plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.mob.notescan"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.mob.notescan"
        minSdk = 28
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("com.google.mlkit:text-recognition:16.0.1")
    //implementation(libs.play.services.mlkit.text.recognition)
    //implementation("com.vanniktech:android-image-cropper:4.3.3")
    //implementation(libs.vanniktech.android.image.cropper)
    //implementation(libs.android.image.cropper)
    implementation(libs.play.services.cast.framework)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("org.json:json:20210307")  // Do pracy z JSON
    //implementation(libs.opencv.android)  // implemetacja openCV
    //implementation(libs.opencv.android)


}