package com.aureusapps.android.providerfile.utils

import android.util.Log
import com.aureusapps.android.providerfile.BuildConfig

internal object Logger {
    private const val TAG = "ProviderFile"

    fun e(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(getTag(tag), msg)
        }
    }

    fun w(tag: String, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.w(getTag(tag), msg)
        }
    }

    private fun getTag(tag: String): String {
        return "$TAG-$tag"
    }
}