plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    `maven-publish`

}

android {
    namespace = "com.bitnic.bitnicorm"
    compileSdk = 36


    defaultConfig {
        minSdk = 24



        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    publishing {
        singleVariant("release") {
            withSourcesJar() // создаёт source.jar автоматически (если хочешь отдельно)
        }
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
    kotlinOptions {
        jvmTarget = "11"
    }
//    tasks.register<Jar>("sourceJar") {
//        archiveClassifier.set("sources")
//        from(android.sourceSets["main"].java.srcDirs)
//    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
        //implementation(files("C:\\Users\\123\\AppData\\Local\\Android\\Sdk\\platforms\\android-36\\android.jar"))
    api(libs.gson)
}
//afterEvaluate {
//
//    //gradlew assembleRelease
//    val versionName = "1.2.3"
//    val libName = "bitnicorm"
//
//    // 🔹 основная задача AAR
//    val releaseAar = tasks.named("bundleReleaseAar")
//
//    // 🔹 создаём source.jar с тем же именем и версией
//    val sourceJar = tasks.register<Jar>("sourceJar") {
//        group = "build"
//        archiveBaseName.set(libName)
//        archiveVersion.set(versionName)
//        archiveClassifier.set("sources")
//
//        from(android.sourceSets["main"].java.srcDirs)
//        from("src/main/java")
//
//        destinationDirectory.set(file("${layout.buildDirectory.get()}/outputs/aar"))
//    }
//
//    // 🔹 переименовываем AAR после сборки (чтобы версия была в имени)
//    releaseAar.configure {
//        doLast {
//            val outputDir = file("${layout.buildDirectory.get()}/outputs/aar")
//            val originalAar = outputDir.listFiles()?.find { it.name.endsWith(".aar") }
//            if (originalAar != null) {
//                val targetFile = File(outputDir, "$libName-$versionName.aar")
//                originalAar.renameTo(targetFile)
//                println("✅ AAR renamed to: ${targetFile.name}")
//            }
//        }
//        finalizedBy(sourceJar)
//    }
//}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.ionson100"
                artifactId = "bitnicorm"
                version = "1.2.3" // Your library version

                pom {
                    name.set("bitnicorm")
                    description.set("orm android")
                    url.set("https://github.com/ionson100/orm_android_aar")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("ionson100")
                            name.set("Ion Ionow")
                            email.set("ionson100@gmail.com")
                        }
                    }
                }
            }
        }
    }
}