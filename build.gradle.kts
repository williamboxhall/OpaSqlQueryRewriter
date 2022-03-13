import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    implementation("com.github.tim-patterson.antlr-kotlin:antlr-kotlin-runtime-jvm:$antlr_kotlin_version")
//    implementation("org.antlr:antlr4-runtime:4.9.3")
    implementation("com.github.jsqlparser:jsqlparser:4.3")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.10")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
