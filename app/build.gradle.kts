/**
 * FILE: build.gradle.kts (Module :app)
 * LOKASI: app/build.gradle.kts
 *
 * CARA IMPLEMENTASI:
 * 1. Buka file app/build.gradle.kts di Android Studio
 * 2. REPLACE semua isinya dengan kode ini
 * 3. Klik "Sync Now" di pojok kanan atas
 * 4. Tunggu sampai sync selesai
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "id.ac.ubharajaya.sistemakademik"
    compileSdk = 35

    defaultConfig {
        applicationId = "id.ac.ubharajaya.sistemakademik"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
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

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // ==================== CORE ANDROID ====================
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // ==================== COMPOSE ====================
    val composeBom = "2024.02.00"
    implementation(platform("androidx.compose:compose-bom:$composeBom"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Compose Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // ==================== NAVIGATION ====================
    val navVersion = "2.7.6"
    implementation("androidx.navigation:navigation-compose:$navVersion")

    // ==================== LOCATION SERVICES ====================
    implementation("com.google.android.gms:play-services-location:21.1.0")

    // ==================== ROOM DATABASE ====================
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // ==================== DATASTORE PREFERENCES ====================
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // ==================== IMAGE LOADING (COIL) ====================
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ==================== COROUTINES ====================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ==================== MATERIAL ICONS EXTENDED ====================
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // ==================== JSON ====================
    implementation("org.json:json:20231013")
    implementation("com.google.code.gson:gson:2.10.1")

    // ==================== TESTING ====================
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBom"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

/**
 * PENJELASAN DEPENDENCIES:
 *
 * 1. NAVIGATION COMPOSE
 *    - Untuk navigasi antar screen
 *
 * 2. ROOM DATABASE
 *    - Database lokal untuk menyimpan riwayat absensi
 *    - PENTING: Butuh plugin kapt
 *
 * 3. DATASTORE PREFERENCES
 *    - Untuk menyimpan data login user
 *    - Pengganti SharedPreferences yang lebih modern
 *
 * 4. GOOGLE LOCATION SERVICES
 *    - Untuk mendapatkan koordinat GPS
 *
 * 5. COIL
 *    - Untuk loading dan display image
 *
 * 6. COROUTINES
 *    - Untuk async operations (database, network)
 *
 * 7. MATERIAL ICONS EXTENDED
 *    - Icon-icon tambahan untuk UI
 */