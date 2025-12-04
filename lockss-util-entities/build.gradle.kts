/*
 * LOCKSS Util Entities
 *
 * LOCKSS utility library for Web Services entities.
 */

plugins {
    id("lockss-java-conventions")
}

version = "1.7.0-SNAPSHOT"
description = "LOCKSS utility library for Web Services entities"

// Export test JAR for other projects
val publishTestJar: Boolean by extra(true)

dependencies {
    // Internal dependencies
    api(project(":lockss-util:lockss-util-core"))

    // Jakarta
    api("jakarta.activation:jakarta.activation-api:2.1.2")
    api("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
    api("jakarta.xml.ws:jakarta.xml.ws-api:3.0.1")

    // Jackson
    api("com.fasterxml.jackson.core:jackson-annotations:${libs.versions.jackson.get()}")

    // JAX-WS (legacy)
    api("javax.xml.ws:jaxws-api:2.3.1")

    // Test dependencies
    testImplementation(platform(project(":lockss-pom-bundles:lockss-junit5-bundle")))
    testImplementation(libs.junit.jupiter.engine)
}
