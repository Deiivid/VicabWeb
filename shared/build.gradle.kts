import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

// Contenido y textos de la web (datos puros, sin UI).
plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
        browser { testTask { enabled = false } }
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser { testTask { enabled = false } }
        nodejs()
    }
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
