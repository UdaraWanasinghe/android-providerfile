/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aureusapps.android.providerfile

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.IOException
import java.util.Locale

internal class RawDocumentFile(parent: ProviderFile?, private var file: File) : ProviderFile(parent) {

    override fun createFile(mimeType: String, displayName: String): ProviderFile? {
        // Tack on extension when valid MIME type provided
        var name = displayName
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        if (extension != null) {
            name += ".$extension"
        }
        val target = File(file, name)
        return try {
            if (target.createNewFile()) {
                RawDocumentFile(this, target)
            } else {
                null
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to createFile: $e")
            null
        }
    }

    override fun createDirectory(displayName: String): ProviderFile? {
        val target = File(file, displayName)
        return if (target.isDirectory || target.mkdir()) {
            RawDocumentFile(this, target)
        } else {
            null
        }
    }

    override val uri: Uri
        get() = Uri.fromFile(file)

    override val name: String
        get() = file.name

    override val type: String?
        get() = if (file.isDirectory) {
            null
        } else {
            getTypeForName(file.name)
        }
    override val isDirectory: Boolean
        get() = file.isDirectory

    override val isFile: Boolean
        get() = file.isFile

    override val isVirtual: Boolean
        get() = false

    override fun lastModified(): Long {
        return file.lastModified()
    }

    override fun length(): Long {
        return file.length()
    }

    override fun canRead(): Boolean {
        return file.canRead()
    }

    override fun canWrite(): Boolean {
        return file.canWrite()
    }

    override fun delete(): Boolean {
        deleteContents(file)
        return file.delete()
    }

    override fun exists(): Boolean {
        return file.exists()
    }

    override fun listFiles(): List<ProviderFile> {
        val results = ArrayList<ProviderFile>()
        val files = file.listFiles()
        if (files != null) {
            for (file in files) {
                results.add(RawDocumentFile(this, file))
            }
        }
        return results
    }

    override fun renameTo(displayName: String): Boolean {
        val target = File(file.parentFile, displayName)
        return if (file.renameTo(target)) {
            file = target
            true
        } else {
            false
        }
    }

    companion object {

        private fun getTypeForName(name: String): String {
            val lastDot = name.lastIndexOf('.')
            if (lastDot >= 0) {
                val extension = name.substring(lastDot + 1).lowercase(Locale.getDefault())
                val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                if (mime != null) {
                    return mime
                }
            }
            return "application/octet-stream"
        }

        private fun deleteContents(dir: File): Boolean {
            val files = dir.listFiles()
            var success = true
            if (files != null) {
                for (file in files) {
                    if (file.isDirectory) {
                        success = success and deleteContents(file)
                    }
                    if (!file.delete()) {
                        Log.w(TAG, "Failed to delete $file")
                        success = false
                    }
                }
            }
            return success
        }

    }

}