import com.season.monday.convention.FlavorDimension
import com.season.monday.convention.AppFlavor

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("monday.android.application")
    id("monday.android.application.compose")
}

android {
    namespace = "com.season.mondaystorybook"

    defaultConfig {
        applicationId = "com.season.mondaystorybook"
        versionCode = 1
        versionName = "1.0.0"

        // The UI catalog does not depend on content from the app, however, it depends on modules
        // which do, so we must specify a default value for the contentType dimension.
        missingDimensionStrategy(FlavorDimension.contentType.name, AppFlavor.demo.name)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:ui"))
    implementation(libs.androidx.activity.compose)
}
