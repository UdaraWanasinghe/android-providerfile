import com.aureusapps.gradle.PublishLibraryConstants.GROUP_ID

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.aureusapps.gradle.update-version")
}

class Props(project: Project) {
    private val groupId = project.findProperty(GROUP_ID)
    val packageName = "$groupId.providerfile.example"
}

val props = Props(project)

android {
    namespace = props.packageName
    compileSdk = 34

    defaultConfig {
        applicationId = props.packageName
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    testImplementation(libs.junit)

    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
}