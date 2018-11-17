// See https://gradle.org and https://github.com/gradle/kotlin-dsl

// Apply the java plugin to add support for Java
plugins {
    java
    application
}

repositories {
    jcenter()
}

val loggingDependencies = listOf(
        "org.slf4j:slf4j-api:1.7.25",
        "org.apache.logging.log4j:*:2.11.1")

dependencies {
    // Annotations for better code documentation
    compile("com.intellij:annotations:12.0")

    //nio
    compile("ru.odnoklassniki:one-nio:1.0.2")

    //RocksDB
    compile("org.rocksdb:rocksdbjni:5.4.5")

    //log4j
    loggingDependencies.map { compile(it) }

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")

    // HTTP client for unit tests
    testCompile("org.apache.httpcomponents:fluent-hc:4.5.3")
}

tasks {
    "test"(Test::class) {
        maxHeapSize = "128m"
        useJUnitPlatform()
    }
}

application {
    // Define the main class for the application
    mainClassName = "ru.mail.polis.Server"

    // And limit Xmx
    applicationDefaultJvmArgs = listOf("-Xmx128m")
}
