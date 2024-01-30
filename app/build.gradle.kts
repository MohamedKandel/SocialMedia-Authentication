plugins {
    id("com.android.application")
}

android {
    namespace = "com.mkandeel.socialmediaauthentication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mkandeel.socialmediaauthentication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.facebook.android:facebook-login:latest.release")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.github.parse-community:ParseTwitterUtils-Android:1.13.0")
    implementation ("com.twitter.sdk.android:twitter:3.3.0")
    implementation ("com.twitter.sdk.android:twitter-core:3.3.0")
    implementation ("com.twitter.sdk.android:tweet-ui:3.3.0")
    implementation ("com.twitter.sdk.android:tweet-composer:3.3.0")
}