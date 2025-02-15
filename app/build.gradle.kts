plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.familytree"
    compileSdk = 35

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
        kotlinCompilerExtensionVersion = "2.0.0"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))

    // Firebase
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation(libs.firebaseFirestore)

    // Jetpack Compose
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)
    implementation(libs.material3)
    implementation(libs.uiToolingPreviewAndroid)
    implementation("androidx.activity:activity-compose:1.10.0")
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.material:material-icons-core:1.7.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Other dependencies
    implementation(libs.gson)
    implementation(libs.jewishDate)
    implementation(libs.kotlinxCoroutines)
    implementation("net.sourceforge.jexcelapi:jxl:2.6.12")
    implementation(libs.coreKtx)  // Removed duplicate

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}
