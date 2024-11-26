plugins {
    kotlin("jvm") version "2.0.21"
    id("checkstyle")
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
}

group = "app.quantun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.11")
    testImplementation(kotlin("test"))
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("org.slf4j:slf4j-api:2.0.16")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

detekt {
    config = files("detekt-config.yml")
    buildUponDefaultConfig = true
}
