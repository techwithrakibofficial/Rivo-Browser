plugins { id("com.android.application") }
android {
    namespace = "com.rivo.browser.enginebenchmark"
    compileSdk { version = release(37) { minorApiLevel = 2 } }
    defaultConfig {
        applicationId = "com.rivo.browser.enginebenchmark"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    flavorDimensions += "engine"
    productFlavors {
        create("gecko") { dimension = "engine" }
        create("webview") { dimension = "engine" }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    add("geckoImplementation", project(":engine-gecko"))
    add("webviewImplementation", project(":engine-webview"))
}
