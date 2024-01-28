package com.aureusapps.android.providerfile.extensions

import com.aureusapps.android.providerfile.utils.Logger
import java.io.Closeable

internal fun Closeable.closeQuietly() {
    try {
        close()
    } catch (e: Exception) {
        Logger.e(e.message ?: e::class.simpleName ?: "Unknown error")
    }
}