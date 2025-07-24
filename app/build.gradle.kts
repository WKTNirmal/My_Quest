plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.wktnirmal.myquest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wktnirmal.myquest"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Utility Library for Maps SDK for Android
    // You do not need to add a separate dependency for the Maps SDK for Android
    // since this library builds in the compatible version of the Maps SDK.
    implementation("com.google.maps.android:android-maps-utils:3.10.0")

    implementation ("com.google.android.gms:play-services-location:21.2.0") // Use the latest version
    implementation ("com.google.android.gms:play-services-maps:18.2.0")     // For Google Maps
}