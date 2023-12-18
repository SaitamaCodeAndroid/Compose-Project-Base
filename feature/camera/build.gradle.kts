@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("monday.android.feature")
    id("monday.android.library.compose")
}

android {
    namespace = "com.season.camera"

    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }

    androidResources {
        noCompress += "tflite"
        noCompress += "lite"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))

    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.material)

    implementation("com.github.yalantis:ucrop:2.2.6-native")
    implementation("com.github.pqpo:SmartCropper:v2.1.3")
}