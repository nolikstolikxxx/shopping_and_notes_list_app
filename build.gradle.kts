// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    id("com.android.application") version "9.1.0" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    // Use a known-good KSP version; ensure it exists in Gradle plugin portal
    id("com.google.devtools.ksp") version "2.3.2" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}



