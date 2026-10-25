plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tempcontrol.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tempcontrol.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 3
        versionName = "1.1.1"
    }

    
    signingConfigs {
        create("release") {
            storeFile = file(
                System.getenv("KEYSTORE_PATH") ?: "tempcontrol-release.p12"
            )
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
            keyAlias = "tempcontrol"
            keyPassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
