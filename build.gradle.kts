plugins {
    id("java")
    id("io.qameta.allure") version "2.12.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("org.slf4j:slf4j-api:2.0.13")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.5.6")
    testImplementation("io.qameta.allure:allure-junit5:2.29.0")
    testImplementation("io.qameta.allure:allure-rest-assured:2.29.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    val apiBaseUrl = System.getProperty("api.base-url")
    if (!apiBaseUrl.isNullOrBlank()) {
        systemProperty("api.base-url", apiBaseUrl)
    }

    val apiKey = System.getProperty("api.x-api-key")
    if (!apiKey.isNullOrBlank()) {
        systemProperty("api.x-api-key", apiKey)
    }

    useJUnitPlatform()
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

tasks.test {
    description = "Runs all API tests."
    group = "verification"
}

tasks.register<Test>("smokeTest") {
    description = "Runs smoke API tests (@Tag(\"smoke\"))."
    group = "verification"
    useJUnitPlatform {
        includeTags("smoke")
    }
    shouldRunAfter(tasks.test)
}

tasks.register<Test>("regressionTest") {
    description = "Runs regression API tests (@Tag(\"regression\"))."
    group = "verification"
    useJUnitPlatform {
        includeTags("regression")
    }
    shouldRunAfter(tasks.test)
}

allure {
    report {
        version.set("2.29.0")
    }
    adapter {
        aspectjWeaver.set(true)
        frameworks {
            junit5 {
                adapterVersion.set("2.29.0")
            }
        }
    }
}
