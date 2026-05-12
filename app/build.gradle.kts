plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.shoppingAndNotesListApp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.shoppingAndNotesListApp"
        minSdk = 26
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        animationsDisabled = project.hasProperty("disableAnimations")
    }


    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt") ,
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
    kotlin {
        jvmToolchain(17)
    }
    ksp {
        arg("room.schemaLocation" , "$projectDir/schema")
    }
}

dependencies {

    // Core
    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // UI
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.core:core-splashscreen:1.2.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    // Room
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.7")

    // ADS & billing
    implementation("com.google.android.gms:play-services-ads:25.1.0")
    implementation("com.android.billingclient:billing-ktx:8.3.0")

    // Unit Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.room:room-testing:2.8.4")
    testImplementation(kotlin("test"))

    // Android Test
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")

    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")

    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    androidTestImplementation("androidx.navigation:navigation-testing:2.9.7")

    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")

    // Debug
    debugImplementation("androidx.fragment:fragment-testing:1.8.9")
}