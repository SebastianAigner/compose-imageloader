package com.seiko.imageloader.demo

import androidx.compose.runtime.CompositionLocalProvider
import club.eridani.compose.jwm.ApplicationWindow
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.cache.memory.maxSizePercent
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.demo.util.LocalResLoader
import com.seiko.imageloader.demo.util.ResLoader
import com.seiko.imageloader.demo.util.commonConfig
import okio.Path.Companion.toOkioPath
import java.io.File

fun main() {
    io.github.humbleui.jwm.App.start {
        ApplicationWindow {
            CompositionLocalProvider(
                LocalImageLoader provides generateImageLoader(),
                LocalResLoader provides ResLoader(),
            ) {
                App()
            }
        }
    }
}


private fun generateImageLoader(): ImageLoader {
    return ImageLoader {
        commonConfig()
        components {
            // add(ImageIODecoder.Factory())
            setupDefaultComponents(imageScope)
        }
        interceptor {
            memoryCacheConfig {
                // Set the max size to 25% of the app's available memory.
                maxSizePercent(0.25)
            }
            diskCacheConfig {
                directory(getCacheDir().resolve("image_cache").toOkioPath())
                maxSizeBytes(512L * 1024 * 1024) // 512MB
            }
        }
    }
}

enum class OperatingSystem {
    Windows, Linux, MacOS, Unknown
}

private val currentOperatingSystem: OperatingSystem
    get() {
        val operSys = System.getProperty("os.name").lowercase()
        return if (operSys.contains("win")) {
            OperatingSystem.Windows
        } else if (operSys.contains("nix") || operSys.contains("nux") ||
            operSys.contains("aix")
        ) {
            OperatingSystem.Linux
        } else if (operSys.contains("mac")) {
            OperatingSystem.MacOS
        } else {
            OperatingSystem.Unknown
        }
    }

private fun getCacheDir() = when (currentOperatingSystem) {
    OperatingSystem.Windows -> File(System.getenv("AppData"), "$APPLICATION_NAME/cache")
    OperatingSystem.Linux -> File(System.getProperty("user.home"), ".cache/$APPLICATION_NAME")
    OperatingSystem.MacOS -> File(System.getProperty("user.home"), "Library/Caches/$APPLICATION_NAME")
    else -> throw IllegalStateException("Unsupported operating system")
}

private const val APPLICATION_NAME = "Compose ImageLoader"

