import com.android.build.api.dsl.ApkSigningConfig
import java.util.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "ru.rustore.sdk.reviewexample"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfigs {
            // Замените на свою подпись!
            val debugStoreFile = rootProject.file("cert/release.keystore")
            val debugPropsFile = rootProject.file("cert/release.properties")
            val debugProps = Properties()
            debugPropsFile.inputStream().use(debugProps::load)
            val debugSigningConfig = getByName<ApkSigningConfig>("debug") {
                storeFile = debugStoreFile
                keyAlias = debugProps.getProperty("key_alias")
                keyPassword = debugProps.getProperty("key_password")
                storePassword = debugProps.getProperty("store_password")
            }
            signingConfig = debugSigningConfig
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName<ApkSigningConfig>("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("ru.rustore.sdk:bom:2025.02.01"))
    implementation("ru.rustore.sdk:review")

    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.fragment:fragment-ktx:1.5.5")
}
