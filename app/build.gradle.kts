plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.voiceandcamerarecognition"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.voiceandcamerarecognition"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // ローカルユニットテスト (JVM 上で実行)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.core)
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("com.google.truth:truth:1.4.4")
    // モック化ライブラリ (Mockito + android-mockito-core または MockK)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.robolectric)

    // ML Kit
    implementation(libs.mlkit.image.labeling)
    implementation(libs.mlkit.face.detection)
    
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

composeCompiler {
    /**
     * NOTE: ドキュメントに追加した方が良さそうな感じで書いてあるので追加した
     * どういう効果があるかはわからない。不要なら消しても良いと思う。
     * @see https://developer.android.com/develop/ui/compose/compiler?hl=ja
     */
    reportsDestination = layout.buildDirectory.dir("compose_compiler")
    stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
}
