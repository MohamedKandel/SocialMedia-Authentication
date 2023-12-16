pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
            setUrl("https://maven.fabric.io/public")
            setUrl("https://jcenter.bintray.com")

        }
    }
}

rootProject.name = "Social Media Authentication"
include(":app")
 