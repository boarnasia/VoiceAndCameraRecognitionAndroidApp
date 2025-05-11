// Top-level build file where you can add configuration options common to all sub-projects/modules.
dependencies {
//    implementation(libs.androidx.camera.core)
//    implementation(libs.androidx.camera.camera2)
//    implementation(libs.androidx.camera.lifecycle)
//    implementation(libs.androidx.camera.view)
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}