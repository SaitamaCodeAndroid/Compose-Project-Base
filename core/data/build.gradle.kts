@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("monday.android.library")
    id("monday.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "com.season.data"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {

    implementation(project(":core:analytics"))
    implementation(project(":core:common"))
//    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:content-provider"))
    implementation(project(":core:file-storage"))
    implementation(project(":core:model"))
    implementation(project(":core:network"))
//    implementation(project(":core:notifications"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}