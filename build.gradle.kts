plugins {
    id("com.android.application") version "9.5.0" apply false
    id("com.android.library") version "9.5.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false
}

allprojects {
    dependencyLocking {
        lockAllConfigurations()
    }
    configurations.configureEach {
        resolutionStrategy.force(
            "com.google.guava:guava:33.7.2-android",
            "org.apache.commons:commons-lang3:3.18.0",
            "org.apache.httpcomponents:httpclient:4.5.13",
            "org.bouncycastle:bcpkix-jdk18on:1.85",
            "org.bouncycastle:bcprov-jdk18on:1.85",
            "org.bouncycastle:bcutil-jdk18on:1.85"
        )
    }
}
