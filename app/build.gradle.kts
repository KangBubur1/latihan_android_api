plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.submission_awal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.submission_awal"
        minSdk = 26
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

    buildFeatures {
        viewBinding = true
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

    // WorkManager
    implementation(libs.androidx.work.runtime)
    implementation(libs.android.async.http)

    // DataStore
    implementation(libs.androidx.datastore.preferences)


    // Formatter
    implementation (libs.jsoup)


    implementation(libs.glide) // For image loading and caching
    implementation(libs.retrofit) // For API calls
    implementation(libs.logging.interceptor) // For logging HTTP requests and responses
    implementation(libs.converter.gson) // For converting JSON to Kotlin objects

    // Navigation components
    implementation(libs.androidx.navigation.fragment) // Navigation fragment components
    implementation(libs.androidx.navigation.fragment.ktx) // KTX extensions for navigation fragment
    implementation(libs.androidx.navigation.ui) // Navigation UI components
    implementation(libs.androidx.navigation.ui.ktx) // KTX extensions for navigation UI

    // Room database (Local)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.androidx.core.ktx) // Core KTX features
    implementation(libs.androidx.appcompat) // AppCompat support
    implementation(libs.material) // Material design components
    implementation(libs.androidx.activity) // Activity components
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx) // ConstraintLayout for UI design
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit) // Unit testing
    androidTestImplementation(libs.androidx.junit) // Android unit tests
    androidTestImplementation(libs.androidx.espresso.core) // UI testing
}