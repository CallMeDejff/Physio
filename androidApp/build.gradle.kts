import org.gradle.kotlin.dsl.provider.inLenientMode

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.physio"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.physio"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    //implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.monitor)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database)
    implementation(libs.google.firebase.storage)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.googleid)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.volley)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
    implementation(libs.firebase.ui.auth)
    implementation(libs.facebook.login)
    implementation(libs.coil.compose)
    implementation(libs.play.services.auth)
    implementation("com.vanniktech:android-image-cropper:4.6.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")


    implementation(libs.converter.gson)
    implementation (libs.gson)
    implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc05-k2")

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}