package com.aureusapps.android.providerfile

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import com.aureusapps.android.providerfile.extensions.closeQuietly
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.canRead
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.canWrite
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.exists
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.getName
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.getType
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.isDirectory
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.isFile
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.isVirtual
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.lastModified
import com.aureusapps.android.providerfile.utils.DocumentsProviderHelper.length
import com.aureusapps.android.providerfile.utils.Logger

internal class TreeDocumentFile(
    override val parent: ProviderFile?,
    private val context: Context,
    override var uri: Uri,
) : ProviderFile() {

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

    @SuppressLint("Recycle")
    override fun listFiles(): List<ProviderFile> {
        val resolver = context.contentResolver
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            uri,
            DocumentsContract.getDocumentId(uri)
        )
        val results = ArrayList<TreeDocumentFile>()
        var c: Cursor? = null
        try {
            c = resolver.query(
                childrenUri,
                arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID),
                null,
                null,
                null
            )
            if (c != null) {
                while (c.moveToNext()) {
                    val documentId = c.getString(0)
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(uri, documentId)
                    results.add(
                        TreeDocumentFile(this, context, documentUri)
                    )
                }
            }
        } catch (e: Exception) {
            Logger.w(TAG, "Failed query: $e")
        } finally {
            c?.closeQuietly()
        }
        return results
    }

    override fun renameTo(displayName: String): Boolean {
        return try {
            val result = DocumentsContract.renameDocument(
                context.contentResolver,
                uri,
                displayName
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
            context: Context,
            uri: Uri,
            mimeType: String,
            displayName: String,
        ): Uri? {
            return try {
                DocumentsContract.createDocument(
                    context.contentResolver,
                    uri,
                    mimeType,
                    displayName
                )
            } catch (e: Exception) {
                null
            }
        }

    }
}
