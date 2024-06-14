import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt.gradle)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.app.distribution)
}

/*
tasks.register("clean",Delete::class){
    delete(rootProject.buildDir)
}*/

android {
    namespace = "com.mastrosql.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mastrosql.app"
        minSdk = 28
        targetSdk = 34
        versionCode = 2
        versionName = "1.0.2"

        testInstrumentationRunner = "com.mastrosql.app.HiltTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        versionNameSuffix = "1"
        signingConfig = signingConfigs.getByName("debug")

        // Enable room auto-migrations
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    signingConfigs {
        create("release") {
            val propertiesFile = file("../../../src/keystore/release-signing.properties")
            if (!propertiesFile.exists()) {
                //throw IllegalArgumentException("Keystore file not found")
                println("Keystore file not found, verify the path and try again")
            } else {
                val props = loadProperties(propertiesFile)
                keyAlias = props["keyAlias"] as String
                keyPassword = props["keyPassword"] as String
                storeFile = file(props["storeFile"] as String)
                storePassword = props["storePassword"] as String
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            // Load properties from the file
            val propertiesFile = file("../app/config/release.properties")
            val props = loadProperties(propertiesFile)
            signingConfig = signingConfigs.getByName("release")

            // Configure properties from the file
            props.forEach { (key, value) ->
                when (key) {
                    "APPNAME" -> resValue("string", "app_name", value.toString())
                    "APPLICATION_ID_SUFFIX" -> applicationIdSuffix = value.toString()
                    "API_URL" -> buildConfigField("String", key.toString(), value as String)
                    else -> buildConfigField("String", key.toString(), value as String)

                }
            }
        }

        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro"
            )


            // Load properties from the file
            val propertiesFile = file("../app/config/development.properties")
            val props = loadProperties(propertiesFile)

            // Configure properties from the file
            props.forEach { (key, value) ->
                when (key) {
                    "APPNAME" -> resValue("string", "app_name", value.toString())
                    "APPLICATION_ID_SUFFIX" -> applicationIdSuffix = value.toString()
                    "API_URL" -> buildConfigField("String", key.toString(), value as String)
                    else -> buildConfigField("String", key.toString(), value as String)
                }
            }
        }

        create("staging") {
            initWith(getByName("debug"))

            // Load properties from the file
            val propertiesFile = file("../app/config/staging.properties")
            val props = loadProperties(propertiesFile)
            signingConfig = signingConfigs.getByName("release")

            // Configure properties from the file
            props.forEach { (key, value) ->
                when (key) {
                    "APPNAME" -> resValue("string", "app_name", value.toString())
                    "APPLICATION_ID_SUFFIX" -> applicationIdSuffix = value.toString()
                    "API_URL" -> buildConfigField("String", key.toString(), value as String)
                    else -> buildConfigField("String", key.toString(), value as String)
                }
            }
        }


        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

        }


        kotlinOptions {
            jvmTarget = "17"
        }

        buildFeatures {
            compose = true
            aidl = false
            buildConfig = true
            renderScript = false
            shaders = false
        }

        composeOptions {
            kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
                excludes += "/META-INF/DEPENDENCIES"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildToolsVersion = "34.0.0"

    lint {
        checkOnly += "NewApi"
        checkOnly += "HandlerLeak"
    }
}

/**
 * Function to load properties from a file
 */
fun loadProperties(propertiesFile: File): Properties {
    val props = Properties()
    try {
        props.load(propertiesFile.inputStream())
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return props
}


dependencies {

// Zebra
    compileOnly(libs.symbol.emdk)

    /*compileOnly 'com.symbol:emdk:9.1.1'
    implementation 'androidx.core:core-ktx:1.7.0'
    releaseImplementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'com.google.android.material:material:1.5.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.3'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'*/

// Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

// Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

// Hilt Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.gradle.plugin)

// Hilt and instrumented tests.
    androidTestImplementation(libs.hilt.android.testing)
    kaptAndroidTest(libs.hilt.android.compiler)

// Hilt and Robolectric tests.
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)

// Arch Components
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.androidx.lifecycle.compiler)

// Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

// Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.runtime)

//Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

//Retrofit
    implementation(libs.retrofit)

// Retrofit with Gson Converter
    implementation(libs.retrofit.gson)

// Retrofit with Scalar Converter
    //removed because of use of jakeWharton converter below
    //implementation(libs.retrofit.scalars)

// OkHttp
    implementation(libs.squareup.okhttp3)
    implementation(libs.squareup.okhttp3.logging.interceptor)

// jakeWharton
    implementation(libs.jakewharton.retrofit2.kotlinx.serialization.converter)

// Kotlinx Serialization
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.datetime)


//DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

//Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

//Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.app.distribution)

//Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

// Instrumented tests
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.squareup.leakcanary.android)

//WorkManager testing
    androidTestImplementation(libs.androidx.work.testing)

// Local tests: jUnit, coroutines, Android runner
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.test)
    testImplementation(libs.kotlinx.test.junit)

//testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.androidx.test.ext.junit.ktx)
    testImplementation(libs.androidx.test.runner)
    //testImplementation(libs.androidx.test.rules)
    testImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.androidx.work.testing)
    testImplementation(libs.hilt.android.testing)

// Instrumented tests: jUnit rules and runners

    androidTestImplementation(libs.androidx.test.core)
    androidTestImplementation(libs.androidx.test.core.ktx)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    //androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext.junit.ktx)

    // Coil
    //implementation("io.coil-kt:coil-compose:2.4.0")
}
