// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.hiltAndroid) apply false
    alias(libs.plugins.gmsGoogleService) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
}