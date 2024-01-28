package com.aureusapps.android.providerfile.utils

import android.util.Log
import com.aureusapps.android.providerfile.BuildConfig

internal object Logger {

    private const val TAG = "ProviderFile"

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg)
        }
    }

}