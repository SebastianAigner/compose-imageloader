package com.seiko.imageloader.demo

import android.content.Context

actual class ResLoader(private val contextProvider: () -> Context) {

    // actual fun getString(file: AssetResource): String {
    //     return file.readText(contextProvider())
    // }
}
