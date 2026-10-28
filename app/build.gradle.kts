import java.io.File
import java.util.Base64
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

// --- Integration secrets (White/Black). Read from local.properties (dev) or env (CI). ---
// local.properties is NOT committed; see local.properties.example for the keys.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}
fun secret(name: String, default: String = ""): String =
    System.getenv(name) ?: localProperties.getProperty(name) ?: default

// --- Release signing values are read from environment variables only. ---
// Never commit a keystore or passwords. Locally you can export them, and in CI
// they come from GitHub Secrets. See README for the full flow.
val envKeystorePath: String? = System.getenv("ANDROID_KEYSTORE_PATH")
val envKeystorePassword: String? = System.getenv("ANDROID_KEYSTORE_PASSWORD")
val envKeyAlias: String? = System.getenv("ANDROID_KEY_ALIAS")
val envKeyPassword: String? = System.getenv("ANDROID_KEY_PASSWORD")
val hasReleaseSigning: Boolean =
    envKeystorePath != null && envKeystorePassword != null &&
        envKeyAlias != null && envKeyPassword != null

// --- Firebase config (google-services.json) is a secret, never committed. ---
// The google-services plugin refuses to configure without the file, so materialise it
// before it runs: a stub when the secret is absent (the APK builds and runs, but has NO
// working push), the real file decoded from base64 when GOOGLE_SERVICES_JSON is set.
// A missing secret must NOT fail the local build; CI fails it separately, before the build.
val gameApplicationId = "com.jeweljester.game"

fun writeStubGoogleServices(dest: File, packageName: String) {
    dest.parentFile.mkdirs()
    dest.writeText(
        """
        {
          "project_info": {
            "project_number": "000000000000",
            "project_id": "stub-local",
            "storage_bucket": "stub-local.appspot.com"
          },
          "client": [
            {
              "client_info": {
                "mobilesdk_app_id": "1:000000000000:android:0000000000000000000000",
                "android_client_info": { "package_name": "$packageName" }
              },
              "oauth_client": [],
              "api_key": [ { "current_key": "stub-api-key" } ],
              "services": { "appinvite_service": { "other_platform_oauth_client": [] } }
            }
          ],
          "configuration_version": "1"
        }
        """.trimIndent()
    )
}

// Debug always gets a stub: its applicationId ends with `.debug`, and the plugin aborts if
// the json package_name does not match the variant.
writeStubGoogleServices(file("src/debug/google-services.json"), "$gameApplicationId.debug")

// Release: real file from the secret, or a stub as a last resort so the local build does
// not break. The CI check (see workflow) fails the job when the secret is missing.
run {
    val releaseJson = file("src/release/google-services.json")
    val base64 = secret("GOOGLE_SERVICES_JSON")
    if (base64.isNotBlank()) {
        releaseJson.parentFile.mkdirs()
        runCatching {
            releaseJson.writeBytes(Base64.getMimeDecoder().decode(base64.trim()))
        }.onFailure {
            logger.warn("GOOGLE_SERVICES_JSON could not be base64-decoded; writing a Firebase stub instead.")
            writeStubGoogleServices(releaseJson, gameApplicationId)
        }
    } else {
        logger.warn(
            "GOOGLE_SERVICES_JSON is not set — release will build with a Firebase STUB and no " +
                "working push. Set it in local.properties or CI secrets to ship real push."
        )
        writeStubGoogleServices(releaseJson, gameApplicationId)
    }
}

android {
    namespace = "com.jeweljester.game"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.jeweljester.game"
        minSdk = 24
        targetSdk = 36
        versionCode = 5
        versionName = "2.0.3"

        // Adjust attribution + Keitaro offer routing. Values are baked into BuildConfig
        // from local.properties (dev) or GitHub Secrets (CI); empty means White-only.
        buildConfigField("String", "ADJUST_APP_TOKEN", "\"${secret("ADJUST_APP_TOKEN")}\"")
        buildConfigField("String", "OFFER_BASE_URL", "\"${secret("OFFER_BASE_URL")}\"")
        // aso | prily — the campaign group decides the URL shape (sub1 in path for prily).
        buildConfigField("String", "OFFER_ROUTING", "\"${secret("OFFER_ROUTING", "aso")}\"")
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = file(envKeystorePath!!)
                storePassword = envKeystorePassword
                keyAlias = envKeyAlias
                keyPassword = envKeyPassword
            }
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            // A distinct package so a debug build can sit next to a store install, and so
            // the Firebase stub (package_name com.jeweljester.game.debug) matches.
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            // Staged R8: keep OFF for the first verified release build, then flip
            // both flags to true and re-verify (see README > Release stability).
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Fail fast: never let a release artifact be produced with debug signing.
gradle.taskGraph.whenReady {
    val releaseArtifact = allTasks.any {
        it.name == "assembleRelease" || it.name == "bundleRelease"
    }
    if (releaseArtifact && !hasReleaseSigning) {
        throw GradleException(
            "Release build requires signing env vars: ANDROID_KEYSTORE_PATH, " +
                "ANDROID_KEYSTORE_PASSWORD, ANDROID_KEY_ALIAS, ANDROID_KEY_PASSWORD. " +
                "Refusing to fall back to debug signing."
        )
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // White/Black integration: Adjust attribution + Firebase Cloud Messaging.
    implementation(libs.adjust)
    // installreferrer and the AdID provider are declared explicitly: without Play Install
    // Referrer and GAID, Adjust attribution degrades to organic and the tracker gets
    // traffic with no campaign and no sub1..subN.
    implementation(libs.installreferrer)
    implementation(libs.play.services.ads.identifier)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.androidx.webkit)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
