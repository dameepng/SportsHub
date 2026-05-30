plugins {
    alias(libs.plugins.android.dynamic.feature)
}

android {
    namespace = "com.example.sportshub.favorite"
    compileSdk = 34

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":app"))
    implementation(fileTree("libs"))
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.rxjava)
    implementation(libs.androidx.lifecycle.reactivestreams.ktx)
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    androidTestImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
}
