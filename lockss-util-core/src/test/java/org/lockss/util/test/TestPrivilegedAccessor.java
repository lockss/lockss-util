/*

Copyright (c) 2000-2018, Board of Trustees of Leland Stanford Jr. University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.util.test;

import org.junit.jupiter.api.Test;

public class TestPrivilegedAccessor extends LockssTestCase5 {

  @Test
  public void testParent() throws Exception {
    MyMockParent parent = new MyMockParent("Charlie");
    assertEquals("Charlie", PrivilegedAccessor.getValue(parent, "m_name"));
    PrivilegedAccessor.invokeMethod(parent, "setName", "Timmah!");
    assertEquals("Timmah!", PrivilegedAccessor.getValue(parent,"m_name"));
  }

  @Test
  public void testChild() throws Exception {
    MyMockChild child = new MyMockChild("Charlie", 8);
    assertEquals("Charlie", PrivilegedAccessor.getValue(child, "m_name"));
    assertEquals(new Integer(8),
		 PrivilegedAccessor.getValue(child, "m_number"));

    PrivilegedAccessor.invokeMethod(child, "setName", "Timmah!");
    PrivilegedAccessor.invokeMethod(child, "setNumber", new Integer(3));

    assertEquals("Timmah!", PrivilegedAccessor.getValue(child,"m_name"));
    assertEquals(new Integer(3),
		 PrivilegedAccessor.getValue(child, "m_number"));
  }

  @Test
  public void testChildWithParentReference() throws Exception {
    MyMockParent parent = new MyMockChild("Charlie", 8);
    assertEquals("Charlie", PrivilegedAccessor.getValue(parent, "m_name"));
    assertEquals(new Integer(8),
		 PrivilegedAccessor.getValue(parent, "m_number"));

    Object args[] = {"Timmah!", new Integer(3)};
    PrivilegedAccessor.invokeMethod(parent, "setData", args);

    assertEquals("Timmah!", PrivilegedAccessor.getValue(parent,"m_name"));
    assertEquals(new Integer(3),
		 PrivilegedAccessor.getValue(parent, "m_number"));

    PrivilegedAccessor.invokeMethod(parent, "setName", "prashant");
    assertEquals("prashant", PrivilegedAccessor.getValue(parent,"m_name"));
  }

  @Test
  public void testInvalidField() throws Exception {
    MyMockParent parent = new MyMockParent("Charlie");
    try {
      Object value = PrivilegedAccessor.getValue(parent, "zzz");
      fail("Should throw NoSuchFieldException");
    } catch (NoSuchFieldException e) {
    }
  }

  @Test
  public void testInvalidMethodName() throws Exception {
    MyMockChild child = new MyMockChild("Charlie", 8);
    try {
      PrivilegedAccessor.invokeMethod(child, "zzz", "Timmah!");
      fail("Should throw NoSuchMethodException");
    } catch(NoSuchMethodException e) {
    }
  }

  @Test
  public void testInvalidArguments() throws Exception {
    MyMockChild child = new MyMockChild("Charlie", 8);
    try {
      PrivilegedAccessor.invokeMethod(child, "setData", "Timmah!");
      fail("Should throw NoSuchMethodException");
    } catch (NoSuchMethodException e) {
    }
  }

  @Test
  public void testInstanceParam() throws Exception {
    try {
      new PrivilegedAccessor.Instance(String.class, new Float(5));
      fail("PrivilegedAccessor.Instance should have thrown ClassCastException");
    } catch (ClassCastException e) {
    }
    MyMockParent parent = new MyMockParent();
    Object nullString = new PrivilegedAccessor.Instance(String.class, null);
    Boolean bool =
      (Boolean)PrivilegedAccessor.invokeMethod(parent, "isNullString",
					       nullString);
    assertTrue(bool.booleanValue());
  }

  @Test
  public void testUnambiguousNullArg() throws Exception {
    MyMockParent parent = new MyMockParent();
    Object[] args = {null};
    Boolean bool =
      (Boolean)PrivilegedAccessor.invokeMethod(parent, "isNullString", args);
    assertTrue(bool.booleanValue());
  }

  @Test
  public void testAmbiguousNullArg() throws Exception {
    MyMockChild child = new MyMockChild("Charlie", 8);
    Object[] args1 = {"foo"};
    Object[] args2 = {null};
    assertEquals("child.string",
		 PrivilegedAccessor.invokeMethod(child, "over", args1));
    try {
      PrivilegedAccessor.invokeMethod(child, "over", args2);
      fail("invokeMethod should have thrown an AmbiguousMethodException for null parameter");
    } catch (PrivilegedAccessor.AmbiguousMethodException e) {
    }
  }

  @Test
  public void testUnambiguousArg() throws Exception {
    MyMockParent parent = new MyMockParent();
    MyMockChild child = new MyMockChild("Charlie", 8);
    Object[] args1 = {new Integer(1), new Float(2)};
    Object[] args2 = {new Float(1), new Integer(2)};
    Object[] args3 = {new Float(1), new PrivilegedAccessor.Instance(Number.class, new Float(2.0))};
    assertEquals("parent.string",
		 PrivilegedAccessor.invokeMethod(parent, "over", "foo"));
    assertEquals("child.string",
		 PrivilegedAccessor.invokeMethod(child, "over", "foo"));
    assertEquals("child.number",
		 PrivilegedAccessor.invokeMethod(child, "over",
						 new Integer(1)));
    assertEquals("child.float",
		 PrivilegedAccessor.invokeMethod(child, "over",
						 new Float(1.2)));
    assertEquals("child.number",
		 PrivilegedAccessor.invokeMethod(child, "over",
						 new PrivilegedAccessor.
						   Instance(Number.class,
							    new Float(1.2))));

    assertEquals("child.number.float",
		 PrivilegedAccessor.invokeMethod(child, "over", args1));
    assertEquals("child.float.number",
		 PrivilegedAccessor.invokeMethod(child, "over", args2));
    assertEquals("child.float.number",
		 PrivilegedAccessor.invokeMethod(child, "over", args3));
  }

  @Test
  public void testAambiguousArg() throws Exception {
    MyMockChild child = new MyMockChild("Charlie", 8);
    Object[] args1 = {new Float(1), new Float(2)};
    try {
      PrivilegedAccessor.invokeMethod(child, "over", args1);
      fail("invokeMethod should have thrown an AmbiguousMethodException");
    } catch (PrivilegedAccessor.AmbiguousMethodException e) {
    }
  }

  @Test
  public void testStatic() throws Exception {
    MyMockParent parent = new MyMockParent();
    MyMockChild child = new MyMockChild("Charlie", 8);
    assertEquals("parent.static",
		 PrivilegedAccessor.invokeMethod(parent, "stat", null));
    assertEquals("child.static",
		 PrivilegedAccessor.invokeMethod(child, "stat", null));
    assertEquals("parent.static",
		 PrivilegedAccessor.invokeMethod(new PrivilegedAccessor.
		   Instance(MyMockParent.class, child),
						 "stat", null));
  }

  @Test
  public void testNoArgConstructor() throws Exception {
    ClassWithPrivateConstructor c =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor(ClassWithPrivateConstructor.class);
    assertEquals(0, c.getN());
  }

  @Test
  public void testOneArgConstructor() throws Exception {
    ClassWithPrivateConstructor c =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor(ClassWithPrivateConstructor.class,
					   new Integer(7));
    assertEquals(1, c.getN());
  }

  @Test
  public void testUnambiguousConstructor() throws Exception {
    Object[] args1 = {new Integer(7),
		      new ClassWithPrivateConstructor.Sub()};
    Object[] args2 = {new Integer(7),
		      new ClassWithPrivateConstructor.Super()};

    ClassWithPrivateConstructor c1 =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor(ClassWithPrivateConstructor.class,
					   args1);
    assertEquals(3, c1.getN());
    ClassWithPrivateConstructor c2 =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor(ClassWithPrivateConstructor.class,
					   args2);
    assertEquals(2, c2.getN());
  }

  @Test
  public void testNoArgConstructorByName() throws Exception {
    ClassWithPrivateConstructor c =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor("org.lockss.util.test.ClassWithPrivateConstructor");
    assertEquals(0, c.getN());
  }

  @Test
  public void testOneArgConstructorByName() throws Exception {
    ClassWithPrivateConstructor c =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor("org.lockss.util.test.ClassWithPrivateConstructor",
					   new Integer(7));
    assertEquals(1, c.getN());
  }

  @Test
  public void testUnambiguousConstructorByName() throws Exception {
    Object[] args1 = {new Integer(7),
		      new ClassWithPrivateConstructor.Sub()};
    Object[] args2 = {new Integer(7),
		      new ClassWithPrivateConstructor.Super()};

    ClassWithPrivateConstructor c1 =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor("org.lockss.util.test.ClassWithPrivateConstructor",
					   args1);
    assertEquals(3, c1.getN());
    ClassWithPrivateConstructor c2 =
      (ClassWithPrivateConstructor)
      PrivilegedAccessor.invokeConstructor("org.lockss.util.test.ClassWithPrivateConstructor",
					   args2);
    assertEquals(2, c2.getN());
  }

  // Test utility classes

  public static class MyMockParent {
    private String m_name;

    public MyMockParent() {
    }
    public MyMockParent(String name) {
      m_name = name;
    }

    public String getName() {
      return m_name;
    }

    protected void setName(String newName) {
      m_name = newName;
    }

    public boolean isNullString(String str) {
      return (str == null);
    }
    protected String over(String s) {
      return "parent.string";
    }
    private static String stat() {
      return "parent.static";
    }
  }

  public static class MyMockChild extends MyMockParent {
    private int m_number;

    public MyMockChild(String name, int number) {
      super(name);
      m_number = number;
    }

    public int getNumber() {
      return m_number;
    }

    private void setNumber(Integer number) {
      m_number = number.intValue();
    }
    private void setData(String name, Integer number) {
      setName(name);
      m_number = number.intValue();
    }

    protected String over(String s) {
      return "child.string";
    }
    protected String over(Float s) {
      return "child.float";
    }
    protected String over(Number s) {
      return "child.number";
    }
    protected String over(Float f, Number n) {
      return "child.float.number";
    }
    protected String over(Number n, Float f) {
      return "child.number.float";
    }
    private static String stat() {
      return "child.static";
    }
  }

}

class ClassWithPrivateConstructor {
  private int n;

  private ClassWithPrivateConstructor() {
    n = 0;
  }

  private ClassWithPrivateConstructor(Integer i) {
    n = 1;
  }

  private ClassWithPrivateConstructor(Integer i, Super x) {
    n = 2;
  }

  private ClassWithPrivateConstructor(Integer i, Sub x) {
    n = 3;
  }

  public int getN() {
    return n;
  }

  public static class Super {
  }

  public static class Sub extends Super {
  }
}
