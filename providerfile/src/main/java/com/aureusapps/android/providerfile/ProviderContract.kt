package com.aureusapps.android.providerfile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import com.aureusapps.android.providerfile.extensions.closeQuietly
import com.aureusapps.android.providerfile.utils.Logger

object ProviderContract {

    @SuppressLint("Recycle")
    fun queryForString(
        context: Context,
        uri: Uri,
        column: String,
        defaultValue: String? = null
    ): String? {
        val resolver = context.contentResolver
        var cursor: Cursor? = null
        return try {
            cursor = resolver.query(uri, arrayOf(column), null, null, null)
            if (cursor?.moveToFirst() == true)  {
                cursor.getString(0) ?: defaultValue
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            Logger.e(e.message ?: e::class.simpleName ?: "Unknown error")
            defaultValue
        } finally {
            cursor?.closeQuietly()
        }
    }

    @SuppressLint("Recycle")
    fun queryForLong(
        context: Context,
        uri: Uri,
        column: String,
        defaultValue: Long = 0
    ): Long {
        val resolver = context.contentResolver
        var cursor: Cursor? = null
        return try {
            cursor = resolver.query(uri, arrayOf(column), null, null, null)
            if (cursor?.moveToFirst() == true)  {
                cursor.getLong(0)
            } else {
                defaultValue
            }
        } catch (e: Exception) {
            Logger.e(e.message ?: e::class.simpleName ?: "Unknown error")
            defaultValue
        } finally {
            cursor?.closeQuietly()
        }
    }

    fun canRead(context: Context, uri: Uri): Boolean {
        return context.checkCallingOrSelfUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun canWrite(context: Context, uri: Uri): Boolean {
        return context.checkCallingOrSelfUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun delete(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.delete(uri, null, null) > 0
        } catch (e: Exception) {
            Logger.e(e.message ?: e::class.simpleName ?: "Unknown error")
            false
        }
    }

}