plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.familytree"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.familytree"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0" // Match Compose version
    }


    // i just added this
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
}

