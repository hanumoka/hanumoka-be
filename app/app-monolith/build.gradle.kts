plugins {
    kotlin("jvm")
}

group = "com.hanumoka"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
}