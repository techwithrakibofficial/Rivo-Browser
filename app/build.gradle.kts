plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}
val enableAbiSplits = (project.findProperty("rivo.abiSplits") as String?)?.toBoolean() ?: true
val releaseSigningRequired = (project.findProperty("rivo.releaseSigningRequired") as String?)?.toBoolean() ?: false
val releaseStoreFile = System.getenv("RIVO_UPLOAD_STORE_FILE")
val releaseStorePassword = System.getenv("RIVO_UPLOAD_STORE_PASSWORD")
val releaseKeyAlias = System.getenv("RIVO_UPLOAD_KEY_ALIAS")
val releaseKeyPassword = System.getenv("RIVO_UPLOAD_KEY_PASSWORD")
val releaseSigningConfigured = listOf(releaseStoreFile, releaseStorePassword, releaseKeyAlias, releaseKeyPassword)
    .all { !it.isNullOrBlank() }

if (releaseSigningRequired && !releaseSigningConfigured) {
    throw GradleException("Production signing requested but RIVO_UPLOAD_* environment variables are incomplete.")
}

android {
    namespace = "com.rivo.browser"
    compileSdk {
        version = release(37) {
            minorApiLevel = 2
        }
    }
    defaultConfig {
        applicationId = "com.rivo.browser"
        minSdk = 26
        targetSdk = 36
        versionCode = 5
        versionName = "0.2.4-phase1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    splits {
        abi {
            isEnable = enableAbiSplits
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }
    packaging {
        jniLibs {
            // Keep shared libraries uncompressed and 16 KB ZIP-aligned (AGP >= 8.5.1).
            useLegacyPackaging = false
        }
    }
    signingConfigs {
        if (releaseSigningRequired) {
            create("upload") {
                storeFile = file(requireNotNull(releaseStoreFile))
                // The release workflow validates a PKCS#12 keystore; keep AGP explicit.
                storeType = "PKCS12"
                storePassword = requireNotNull(releaseStorePassword)
                keyAlias = requireNotNull(releaseKeyAlias)
                keyPassword = requireNotNull(releaseKeyPassword)
            }
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".diagnostic"
            buildConfigField("boolean", "CRASH_DIAGNOSTICS_ENABLED", "true")
        }
        getByName("release") {
            buildConfigField("boolean", "CRASH_DIAGNOSTICS_ENABLED", "true")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            if (releaseSigningRequired) {
                signingConfig = signingConfigs.getByName("upload")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation(project(":engine-api"))
    implementation(project(":engine-gecko"))
    implementation(platform("androidx.compose:compose-bom:2026.06.00"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.datastore:datastore-preferences:1.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
}
