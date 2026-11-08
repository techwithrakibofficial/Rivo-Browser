plugins {
    id("com.android.library")
}
android {
    namespace = "com.rivo.browser.engine.api"
    compileSdk {
        version = release(37) {
            minorApiLevel = 2
        }
    }
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
