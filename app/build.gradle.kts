plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.chagnahnn.spotube"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.chagnahnn.spotube"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // room database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    implementation(libs.gson)

    // google cast
    implementation(libs.play.services.cast.framework)
    implementation(libs.mediarouter)

    // media3
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.exoplayer.ima)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)

    // firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    // layout
    implementation(libs.glide)
    implementation(libs.glide.recyclerview.integration) {
        isTransitive = false
    }
    annotationProcessor(libs.glide.annotation)
    implementation(libs.circleindicator)
    implementation(libs.palette)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}