plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.exefuer.timelauncher2"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.exefuer.timelauncher2"
        minSdk = 21
        targetSdk = 33
        versionCode = 4
        versionName = "Marshmallow"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        
        create("lunch-community") {
            keyAlias = "Lunch Community"
            keyPassword = "lunch-is-the-best"
            storeFile = file("../lunch-community.keystore")
            storePassword = "lunch-is-the-best"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("lunch-community")
        }
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("lunch-community")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //glide
    implementation("com.github.bumptech.glide:glide:4.13.2")
    //watchface-dev
    implementation(project(":watchface-dev-utils"))
    implementation("com.google.code.gson:gson:2.13.1")
    implementation("com.blankj:utilcodex:1.31.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}