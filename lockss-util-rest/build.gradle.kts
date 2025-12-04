/*
 * LOCKSS Util REST
 *
 * LOCKSS utility library for REST.
 */

plugins {
    id("lockss-java-conventions")
}

version = "1.7.0-SNAPSHOT"
description = "LOCKSS utility library for REST"

// Export test JAR for other projects
val publishTestJar: Boolean by extra(true)

// Get versions from catalog
val springVersion = "6.1.3"
val springBootVersion = "3.2.2"
val javaxMailVersion = "1.6.2"
val httpclientVersion = "4.5.3"
val mockserverVersion = "5.15.0"

dependencies {
    // Internal dependencies
    api(project(":lockss-util:lockss-util-core"))
    api(project(":lockss-util:lockss-util-entities"))

    // Jackson
    api(libs.jackson.databind)
    api(libs.jackson.dataformat.yaml)

    // Spring
    api(libs.spring.web)
    api("org.springframework:spring-webmvc:$springVersion")
    api("org.springframework:spring-test:$springVersion")
    api("org.springframework.boot:spring-boot:$springBootVersion")

    // JavaMail
    api("com.sun.mail:javax.mail:$javaxMailVersion")

    // Jakarta Validation (for Spring Boot 3.x)
    api("jakarta.validation:jakarta.validation-api:3.0.2")

    // Swagger
    api("io.swagger:swagger-annotations:1.5.10")
    api("io.swagger.core.v3:swagger-annotations:2.2.16")

    // CXF (4.x for jakarta namespace)
    api("org.apache.cxf:cxf-core:4.0.4")

    // HTTP Client
    api("org.apache.httpcomponents:httpclient:$httpclientVersion")
    api("org.apache.httpcomponents.client5:httpclient5:5.3")

    // Commons
    api("commons-fileupload:commons-fileupload:1.4")

    // Guava
    api("com.google.guava:guava:31.1-jre")

    // Test dependencies
    testImplementation(platform(project(":lockss-pom-bundles:lockss-junit5-bundle")))
    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.mockserver.netty)
    testImplementation(libs.mockserver.junit.rule)
    testImplementation("org.mock-server:mockserver-spring-test-listener:$mockserverVersion")
    testImplementation("org.mock-server:mockserver-junit-jupiter-no-dependencies:$mockserverVersion")
}
