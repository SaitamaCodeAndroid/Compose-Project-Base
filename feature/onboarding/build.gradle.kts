@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("monday.android.feature")
    id("monday.android.library.compose")
}

android {
    namespace = "com.season.onboarding"
}

dependencies {
    implementation(libs.androidx.appcompat)
}