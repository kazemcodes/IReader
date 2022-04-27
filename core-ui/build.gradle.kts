plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}
android {

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compose.get()
    }
}


dependencies {
    implementation(project(Modules.coreApi))
    implementation(project(Modules.core))

    implementation(compose.compose.foundation)
    implementation(compose.compose.ui)
    implementation(compose.compose.compiler)
    implementation(compose.compose.activity)
    implementation(compose.compose.material)
    implementation(compose.compose.uiToolingPreview)
    implementation(compose.compose.icons)
    implementation(compose.compose.animations)
    implementation(compose.compose.navigation)
    implementation(compose.compose.paging)
    implementation(compose.compose.hiltNavigation)
    implementation(compose.compose.lifecycle)
    implementation(compose.compose.coil)
    implementation(project(mapOf("path" to ":common_models")))
    androidTestImplementation(compose.compose.uiTestManifest)
    androidTestImplementation(compose.compose.testing)
    androidTestImplementation(compose.compose.composeTooling)

    implementation(accompanist.systemUiController)

    testImplementation(test.junit4)
    testImplementation(test.extJunit)
    testImplementation(test.espresso)
    androidTestImplementation(libs.hilt.androidtest)
}