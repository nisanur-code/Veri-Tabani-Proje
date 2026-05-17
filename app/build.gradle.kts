plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.veritabani"

    /* * DERLEME AYARI (HATAYI ÇÖZEN KISIM)
     * Hocam, kullanılan AndroidX kütüphanelerinin metadata gereksinimlerini
     * karşılamak adına compileSdk sürümü 36 olarak güncellenmiştir.
     */
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.veritabani"
        minSdk = 24

        // targetSdk 34'te kalabilir, bu uygulamanın davranış modunu belirler.
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Temel Android ve Tasarım Kütüphaneleri
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    /* * VERİTABANI VE API ENTEGRASYONU (MODÜL 1 & 2)
     * Uygulamanın Python REST API katmanı ile JSON tabanlı asenkron
     * haberleşmesini sağlayan kütüphaneler:
     */
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.code.gson:gson:2.10.1")

    // Test Kütüphaneleri
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}