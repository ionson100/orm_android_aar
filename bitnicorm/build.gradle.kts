plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.bitnic.bitnicorm"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    tasks.register<Jar>("sourceJar") {
        archiveClassifier.set("sources")
        from(android.sourceSets["main"].java.srcDirs)
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    //implementation(files("C:\\Users\\123\\AppData\\Local\\Android\\Sdk\\platforms\\android-36\\android.jar"))
    implementation("com.google.code.gson:gson:2.13.2")
}