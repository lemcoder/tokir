plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    applyDefaultHierarchyTemplate()
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "tokir-app.js"
            }
        }
    }

    sourceSets {
        /* Main source sets */
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines)
                implementation("org.kobjects.ktxml:core:0.2.3")

            }
        }
        /* Test source sets */
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}