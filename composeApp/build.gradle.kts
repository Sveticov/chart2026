import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.internal.platform.wasm.WasmPlatforms.wasmJs

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias { libs.plugins.serialization }
}

kotlin {
    jvm()

    js(IR) {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            // implementation(libs.ktor.client.cio)
            // implementation(libs.ktor.client.js)
            //implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            implementation(compose.materialIconsExtended)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            /*  implementation(libs.androidx.lifecycle.viewmodelCompose)*/
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }

        val webMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }
        val jsMain by getting {
            dependsOn(webMain)
        }
        @OptIn(ExperimentalWasmDsl::class)
        val wasmJsMain by getting {
            dependsOn(webMain)
        }

    }
}


compose.desktop {
    application {
        mainClass = "org.svetikov.chart2026.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.svetikov.chart2026"
            packageVersion = "1.0.0"
        }
    }
}


