package com.aureusapps.android.providerfile

import android.content.Context
import android.net.Uri

class MediaProviderFile(
    private val context: Context,
    override val uri: Uri
) : ProviderFile(null) {

    override fun createFile(mimeType: String, displayName: String): ProviderFile? {
        throw UnsupportedOperationException()
    }

    override fun createDirectory(displayName: String): ProviderFile? {
        throw UnsupportedOperationException()
    }

    override val name: String?
        get() = MediaStoreContract.getName(context, uri)

    override val type: String?
        get() = MediaStoreContract.getType(context, uri)

    override val isDirectory: Boolean = false

    override val isFile: Boolean = true

    override val isVirtual: Boolean = false

    override fun lastModified(): Long {
        return MediaStoreContract.lastModified(context, uri)
    }

    override fun length(): Long {
        return MediaStoreContract.length(context, uri)
    }

    override fun canRead(): Boolean {
        return ProviderContract.canRead(context, uri)
    }

    override fun canWrite(): Boolean {
        return ProviderContract.canWrite(context, uri)
    }

    override fun delete(): Boolean {
        return ProviderContract.delete(context, uri)
    }

    override fun exists(): Boolean {
        return MediaStoreContract.exists(context, uri)
    }

    override fun listFiles(): Array<ProviderFile> {
        throw UnsupportedOperationException()
    }

    override fun renameTo(displayName: String): Boolean {
        return MediaStoreContract.rename(context, uri, displayName)
    }

}