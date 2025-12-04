/*
 * LOCKSS Util Core
 *
 * LOCKSS utility library for core functionality.
 */

plugins {
    id("lockss-java-conventions")
}

version = "1.15.0-SNAPSHOT"
description = "LOCKSS utility library for core functionality"

// Export test JAR for other projects
val publishTestJar: Boolean by extra(true)

dependencies {
    // Log4j
    api(libs.log4j.api)
    api(libs.log4j.core)
    api(libs.log4j.web)
    api(libs.log4j.slf4j.impl)
    api(libs.log4j.jcl)
    api(libs.log4j.jul)
    api(libs.log4j.v12.api)

    // SLF4J
    api(libs.slf4j.api)

    // Commons
    api(libs.commons.collections4)
    api(libs.commons.io)
    api(libs.commons.lang3)
    api(libs.commons.compress)

    // JMS
    api(libs.javax.jms.api)

    // Kubernetes
    api(libs.kubernetes.client.java)

    // JUnit 5 bundle (not just in test scope - LockssTestCase5 is in main tree)
    api(platform(project(":lockss-pom-bundles:lockss-junit5-bundle")))
    api(libs.junit.jupiter.engine)
    api(libs.junit.jupiter.params)
    api(libs.hamcrest.library)

    // Test dependencies
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
}
