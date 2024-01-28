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
import android.net.Uri
import android.provider.DocumentsContract
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

internal class SingleDocumentFile(
    parent: ProviderFile?,
    private val context: Context,
    override val uri: Uri
) : ProviderFile(parent) {

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

    override fun listFiles(): Array<ProviderFile> {
        throw UnsupportedOperationException()
    }

    override fun renameTo(displayName: String): Boolean {
        throw UnsupportedOperationException()
    }

}
