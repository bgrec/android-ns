pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
//        maven {
//            url = uri("https://zebratech.jfrog.io/artifactory/EMDK-Android/")
//        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://zebratech.jfrog.io/artifactory/EMDK-Android/")
        }
    }
}
rootProject.name = "MastroAndroidApplication"

include(":app")
