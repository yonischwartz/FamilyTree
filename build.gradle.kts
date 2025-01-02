buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.10") // Firebase plugin
    }
}

plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}
