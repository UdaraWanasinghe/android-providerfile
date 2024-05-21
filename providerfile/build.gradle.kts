@file:Suppress("UnstableApiUsage")

import com.aureusapps.gradle.PublishLibraryConstants.GROUP_ID
import com.aureusapps.gradle.PublishLibraryConstants.VERSION_NAME

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("com.aureusapps.gradle.update-version")
    id("com.aureusapps.gradle.publish-library")
}

class Props(project: Project) {
    val groupId = project.findProperty(GROUP_ID) as String
    val versionName = project.findProperty(VERSION_NAME) as String
}

val props = Props(project)

android {
    namespace = "${props.groupId}.providerfile"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishLibrary {
    groupId.set(props.groupId)
    artifactId.set("providerfile")
    versionName.set(props.versionName)
    libName.set("ProviderFile")
    libDescription.set("Representation of files backed by different content providers in android.")
    libUrl.set("https://github.com/UdaraWanasinghe/android-providerfile")
    licenseName.set("MIT License")
    licenseUrl.set("https://github.com/UdaraWanasinghe/android-providerfile/blob/main/LICENSE")
    devId.set("UdaraWanasinghe")
    devName.set("Udara Wanasinghe")
    devEmail.set("udara.developer@gmail.com")
    scmConnection.set("scm:git:https://github.com/UdaraWanasinghe/android-providerfile.git")
    scmDevConnection.set("scm:git:ssh://git@github.com/UdaraWanasinghe/android-providerfile.git")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    testImplementation(libs.test.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}