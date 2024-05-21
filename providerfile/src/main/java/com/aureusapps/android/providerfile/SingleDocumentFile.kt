package com.aureusapps.android.providerfile

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
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

internal class SingleDocumentFile(
    override val parent: ProviderFile?,
    private val context: Context,
    override val uri: Uri,
) : ProviderFile() {

    override fun createFile(mimeType: String, displayName: String): ProviderFile? {
        throw UnsupportedOperationException()
    }

    override fun createDirectory(displayName: String): ProviderFile? {
        throw UnsupportedOperationException()
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

    override fun listFiles(): List<ProviderFile> {
        throw UnsupportedOperationException()
    }

    override fun renameTo(displayName: String): Boolean {
        throw UnsupportedOperationException()
    }

}
