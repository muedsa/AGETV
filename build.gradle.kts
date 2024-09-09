// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.gmsGoogleService) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.androidRoom) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.baselineProfile) apply false
}