@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("monday.android.library")
    id("monday.android.library.compose")
    id("monday.android.hilt")
}

android {
    namespace = "com.season.analytics"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.kotlinx.coroutines.android)
}