package com.aureusapps.android.providerfile.utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.aureusapps.android.providerfile.extensions.closeQuietly
import com.aureusapps.android.providerfile.utils.ProviderHelper.queryForLong
import com.aureusapps.android.providerfile.utils.ProviderHelper.queryForString

internal object MediaStoreHelper {
    private const val TAG = "MediaStoreHelper"

    fun getName(context: Context, uri: Uri): String? {
        return queryForString(context, uri, MediaStore.MediaColumns.DISPLAY_NAME)
    }

    fun getType(context: Context, uri: Uri): String? {
        return queryForString(context, uri, MediaStore.MediaColumns.MIME_TYPE)
    }

    fun lastModified(context: Context, uri: Uri): Long {
        return queryForLong(context, uri, MediaStore.MediaColumns.DATE_MODIFIED)
    }

    fun length(context: Context, uri: Uri): Long {
        return queryForLong(context, uri, MediaStore.MediaColumns.SIZE)
    }

    @SuppressLint("Recycle")
    fun exists(context: Context, uri: Uri): Boolean {
        val resolver = context.contentResolver
        var cursor: Cursor? = null
        return try {
            cursor = resolver.query(uri, arrayOf(MediaStore.MediaColumns._ID), null, null, null)
            cursor != null && cursor.count > 0
        } catch (e: Exception) {
            val message = e.message ?: e::class.simpleName ?: "Unknown error"
            Logger.e(TAG, message)
            false
        } finally {
            cursor?.closeQuietly()
        }
    }
}