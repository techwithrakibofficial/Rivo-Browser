plugins {
    id("com.android.library")
}

android {
    namespace = "com.rivo.browser.engine.gecko"
    compileSdk = 37
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":engine-api"))
    implementation("org.mozilla.geckoview:geckoview:159.0.20261022150525")
}
