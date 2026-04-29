/*
 * Copyright 2015-2018 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.lockss.util.test;

import static org.junit.platform.commons.util.AnnotationUtils.*;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.params.support.AnnotationConsumerInitializer;
import org.junit.jupiter.params.support.ParameterDeclaration;
import org.junit.jupiter.params.support.ParameterDeclarations;
import org.junit.platform.commons.util.*;
import org.lockss.log.L4JLogger;
import org.slf4j.*;

/**
 * @since 5.0
 */
class VariantTestExtension implements TestTemplateInvocationContextProvider {
  
  private static L4JLogger log = L4JLogger.getLogger();

  @Override
  public boolean supportsTestTemplate(ExtensionContext context) {
    if (!context.getTestMethod().isPresent()) {
      return false;
    }

    Method testMethod = context.getTestMethod().get();
    if (!isAnnotated(testMethod, VariantTest.class)) {
      return false;
    }

    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext>
    provideTestTemplateInvocationContexts(ExtensionContext context) {
    Method templateMethod = context.getRequiredTestMethod();
    VariantTestNameFormatter formatter = createNameFormatter(templateMethod);
    // Collect eagerly so we can validate before returning the stream.
    // In JUnit 6.0.3 the stream's onClose() handler may fire before
    // elements are consumed, so a lazy check via onClose() no longer works.
    List<TestTemplateInvocationContext> contexts =
      findRepeatableAnnotations(templateMethod, ArgumentsSource.class)
      .stream()
      .map(ArgumentsSource::value)
      .map(ReflectionUtils::newInstance)
      .map(provider -> AnnotationConsumerInitializer.initialize(templateMethod,
								provider))
      .flatMap(provider -> arguments(provider, templateMethod, context))
      .map(Arguments::get)
      .flatMap(a -> Stream.of(a))
      .map(vName -> createInvocationContext(formatter, vName.toString()))
      .toList();
    Preconditions.condition(!contexts.isEmpty(),
			     "Configuration error: You must provide at least one argument for this @VariantTest");
    return contexts.stream();
  }

  private TestTemplateInvocationContext
    createInvocationContext(VariantTestNameFormatter formatter, String vName) {
    return new VariantTestInvocationContext(formatter, vName);
  }

  private VariantTestNameFormatter createNameFormatter(Method templateMethod) {
    VariantTest variantTest = findAnnotation(templateMethod,
					     VariantTest.class).get();
    String name =
      Preconditions.notBlank(variantTest.name().trim(),
			     () -> String.format("Configuration error: @VariantTest on method [%s] must be declared with a non-empty name.",
						 templateMethod));
    return new VariantTestNameFormatter(templateMethod, name);
  }

  protected static Stream<? extends Arguments>
    arguments(ArgumentsProvider provider, Method method,
	      ExtensionContext context) {
    try {
      return provider.provideArguments(methodParameterDeclarations(method),
				       context);
    }
    catch (Exception e) {
      throw ExceptionUtils.throwAsUncheckedException(e);
    }
  }

  /**
   * Build a {@link ParameterDeclarations} for the given method.
   * VariantTest methods typically have no formal parameters (variant
   * names are delivered via {@code setUpVariant}), but the JUnit 6
   * {@code ArgumentsProvider} API requires a non-null declarations
   * object.
   */
  private static ParameterDeclarations methodParameterDeclarations(Method method) {
    Parameter[] params = method.getParameters();
    List<ParameterDeclaration> declarations =
      java.util.stream.IntStream.range(0, params.length)
      .mapToObj(i -> {
	  final Parameter p = params[i];
	  final int index = i;
	  return new ParameterDeclaration() {
	    @Override public AnnotatedElement getAnnotatedElement() { return p; }
	    @Override public Class<?> getParameterType() { return p.getType(); }
	    @Override public int getParameterIndex() { return index; }
	    @Override public Optional<String> getParameterName() {
	      return p.isNamePresent()
		? Optional.of(p.getName()) : Optional.empty();
	    }
	  };
	})
      .collect(java.util.stream.Collectors.toList());

    return new ParameterDeclarations() {
      @Override public List<ParameterDeclaration> getAll() { return declarations; }
      @Override public Optional<ParameterDeclaration> getFirst() {
	return declarations.isEmpty() ? Optional.empty() : Optional.of(declarations.get(0));
      }
      @Override public Optional<ParameterDeclaration> get(int index) {
	return index >= 0 && index < declarations.size()
	  ? Optional.of(declarations.get(index)) : Optional.empty();
      }
      @Override public AnnotatedElement getSourceElement() { return method; }
      @Override public String getSourceElementDescription() {
	return "method '" + method.getName() + "'";
      }
    };
  }

}
