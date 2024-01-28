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

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import com.aureusapps.android.providerfile.DocumentsContractApi19.canRead
import com.aureusapps.android.providerfile.DocumentsContractApi19.canWrite
import com.aureusapps.android.providerfile.DocumentsContractApi19.exists
import com.aureusapps.android.providerfile.DocumentsContractApi19.getName
import com.aureusapps.android.providerfile.DocumentsContractApi19.getType
import com.aureusapps.android.providerfile.DocumentsContractApi19.isDirectory
import com.aureusapps.android.providerfile.DocumentsContractApi19.isFile
import com.aureusapps.android.providerfile.DocumentsContractApi19.isVirtual
import com.aureusapps.android.providerfile.DocumentsContractApi19.lastModified
import com.aureusapps.android.providerfile.DocumentsContractApi19.length

internal class TreeDocumentFile(
    parent: ProviderFile?,
    private val context: Context,
    override var uri: Uri
) : ProviderFile(parent) {

    override fun createFile(mimeType: String, displayName: String): ProviderFile? {
        val result = createFile(context, uri, mimeType, displayName)
        return if (result != null) TreeDocumentFile(this, context, result) else null
    }

    override fun createDirectory(displayName: String): ProviderFile? {
        val result = createFile(
            context, uri, DocumentsContract.Document.MIME_TYPE_DIR, displayName
        )
        return if (result != null) TreeDocumentFile(this, context, result) else null
    }

    override val name: String?
        get() = getName(context, uri)

    override val type: String?
        get() = getType(context, uri)

    override val isDirectory: Boolean
        get() = isDirectory(context, uri)

    override val isFile: Boolean
        get() = isFile(context, uri)

    override val isVirtual: Boolean
        get() = isVirtual(context, uri)

    override fun lastModified(): Long {
        return lastModified(context, uri)
    }

    override fun length(): Long {
        return length(context, uri)
    }

    override fun canRead(): Boolean {
        return canRead(context, uri)
    }

    override fun canWrite(): Boolean {
        return canWrite(context, uri)
    }

    override fun delete(): Boolean {
        return try {
            DocumentsContract.deleteDocument(context.contentResolver, uri)
        } catch (e: Exception) {
            false
        }
    }

    override fun exists(): Boolean {
        return exists(context, uri)
    }

    override fun listFiles(): Array<ProviderFile> {
        val resolver = context.contentResolver
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getDocumentId(uri)
        )
        val results = ArrayList<Uri>()
        var c: Cursor? = null
        try {
            c = resolver.query(
                childrenUri, arrayOf(
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID
                ), null, null, null
            )
            while (c!!.moveToNext()) {
                val documentId = c.getString(0)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(
                    uri,
                    documentId
                )
                results.add(documentUri)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed query: $e")
        } finally {
            closeQuietly(c)
        }
        return results.map { TreeDocumentFile(this, context, it) }.toTypedArray()
    }

    override fun renameTo(displayName: String): Boolean {
        return try {
            val result = DocumentsContract.renameDocument(
                context.contentResolver, uri, displayName
            )
            if (result != null) {
                uri = result
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private fun createFile(
            context: Context, self: Uri, mimeType: String,
            displayName: String
        ): Uri? {
            return try {
                DocumentsContract.createDocument(
                    context.contentResolver, self, mimeType,
                    displayName
                )
            } catch (e: Exception) {
                null
            }
        }

        private fun closeQuietly(closeable: AutoCloseable?) {
            if (closeable != null) {
                try {
                    closeable.close()
                } catch (rethrown: RuntimeException) {
                    throw rethrown
                } catch (ignored: Exception) {
                }
            }
        }
    }
}
