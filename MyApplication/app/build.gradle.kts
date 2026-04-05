plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") // Plugin Compose xịn cho Kotlin 2.0
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tien.noteapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tien.noteapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    // 1. FIREBASE (Dùng BOM chuẩn mới)
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // 2. ANDROIDX & COMPOSE
    implementation(platform("androidx.compose:compose-bom:2024.03.00"))
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.activity:activity-ktx:1.9.0")

    // 3. THƯ VIỆN ẢNH COIL
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 3.5 PULL REFRESH
    implementation("com.google.accompanist:accompanist-swiperefresh:0.33.2-alpha")

    // 4. COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // 5. ROOM DATABASE (để lưu cache local)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // 6. DATA STORE (để lưu preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // 7. LOGGING
    implementation("com.jakewharton.timber:timber:5.0.1")

    // DEBUG
    debugImplementation("androidx.compose.ui:ui-tooling")
}