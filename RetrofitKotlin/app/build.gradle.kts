plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.retrofitkotlin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.retrofitkotlin"
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

    buildFeatures{
        viewBinding = true
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

    //RxJava3
    implementation ("io.reactivex.rxjava3:rxjava:3.1.10")

    //RxJava3 Adapter
    implementation ("com.squareup.retrofit2:adapter-rxjava3:2.9.0")

    //RxJavaAndroidIO
    implementation("io.reactivex.rxjava3:rxandroid:3.0.2")

    //Gson
    implementation ("com.google.code.gson:gson:2.13.1")

    //Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
}