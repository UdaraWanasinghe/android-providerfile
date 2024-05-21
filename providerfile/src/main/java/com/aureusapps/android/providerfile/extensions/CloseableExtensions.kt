package com.aureusapps.android.providerfile.extensions

import com.aureusapps.android.providerfile.utils.Logger
import java.io.Closeable

internal fun Closeable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        val message = e.message ?: e::class.simpleName ?: "Unknown error"
        Logger.e("Closeable", message)
    }
}