package com.seiko.imageloader.component.fetcher

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.seiko.imageloader.option.Options
import dev.icerock.moko.resources.AssetResource
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.resources.ImageResource
import kotlinx.browser.window
import kotlinx.coroutines.await
import okio.Buffer
import org.khronos.webgl.Int8Array

internal actual suspend fun AssetResource.toFetchResult(options: Options): FetchResult? {
    return null
}

internal actual suspend fun ColorResource.toFetchResult(options: Options): FetchResult? {
    return FetchResult.Painter(
        painter = ColorPainter(Color(lightColor.argb)),
    )
}

internal actual suspend fun FileResource.toFetchResult(options: Options): FetchResult? {
    return null
}

internal actual suspend fun ImageResource.toFetchResult(options: Options): FetchResult? {
    val response = window.fetch(fileUrl).await()
    if (response.ok.not()) {
        throw RuntimeException("can't load data from $fileUrl")
    }
    val buffer = response.arrayBuffer().await()
    val bytes = Int8Array(buffer).unsafeCast<ByteArray>()
    return FetchResult.Source(
        source = Buffer().apply {
            write(bytes)
        },
    )
}
