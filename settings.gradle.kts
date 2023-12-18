pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") }
        maven { url = uri("https://jcenter.bintray.com") }
    }
}

rootProject.name = "Monday"
include(":app")
include(":app-monday-storybook")
include(":core:designsystem")
include(":core:ui")
include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:common")
include(":core:testing")
include(":core:analytics")
include(":core:network")
include(":core:datastore")
include(":core:content-provider")
include(":feature")
include(":feature:settings")
include(":feature:onboarding")
include(":feature:home")
include(":feature:browser-files")
include(":feature:camera")
include(":feature:image-to-file")
include(":feature:export-result")
include(":core:file-storage")
