@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("monday.android.feature")
    id("monday.android.library.compose")
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.season.home"
}

dependencies {
    implementation(project(":feature:settings"))
    implementation(project(":feature:browser-files"))
    implementation(project(":feature:image-to-file"))

    implementation(project(":core:ui"))

    implementation(libs.androidx.appcompat)
    implementation(libs.accompanist.permissions)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

}