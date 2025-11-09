plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "de.tiwut.launcher"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "de.tiwut.launcher"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Für Netzwerkanfragen (wie 'requests' in Python)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0") // Um einfachen Text zu empfangen

// Für das Laden und Cachen von Bildern aus dem Internet (ersetzt den IconManager)
    implementation("io.coil-kt:coil:2.5.0")

// Für Listen- und Gitteransichten (ersetzt das AppGridView)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

// Für moderne UI-Komponenten
    implementation("com.google.android.material:material:1.10.0")
}