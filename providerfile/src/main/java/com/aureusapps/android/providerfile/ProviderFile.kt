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
import android.os.Build
import androidx.core.provider.DocumentsContractCompat
import java.io.File

/**
 * Representation of a document backed by either a
 * [android.provider.DocumentsProvider] or a raw file on disk. This is a
 * utility class designed to emulate the traditional [File] interface. It
 * offers a simplified view of a tree of documents, but it has substantial
 * overhead. For optimal performance and a richer feature set, use the
 * [android.provider.DocumentsContract] methods and constants directly.
 *
 *
 * There are several differences between documents and traditional files:
 *
 *  * Documents express their display name and MIME type as separate fields,
 * instead of relying on file extensions. Some documents providers may still
 * choose to append extensions to their display names, but that's an
 * implementation detail.
 *  * A single document may appear as the child of multiple directories, so it
 * doesn't inherently know who its parent is. That is, documents don't have a
 * strong notion of path. You can easily traverse a tree of documents from
 * parent to child, but not from child to parent.
 *  * Each document has a unique identifier within that provider. This
 * identifier is an *opaque* implementation detail of the provider, and
 * as such it must not be parsed.
 *
 *
 *
 * Before using this class, first consider if you really need access to an
 * entire subtree of documents. The principle of least privilege dictates that
 * you should only ask for access to documents you really need. If you only need
 * the user to pick a single file, use [Intent.ACTION_OPEN_DOCUMENT] or
 * [Intent.ACTION_GET_CONTENT]. If you want to let the user pick multiple
 * files, add [Intent.EXTRA_ALLOW_MULTIPLE]. If you only need the user to
 * save a single file, use [Intent.ACTION_CREATE_DOCUMENT]. If you use
 * these APIs, you can pass the resulting [Intent.getData] into
 * [.fromSingleUri] to work with that document.
 *
 *
 * If you really do need full access to an entire subtree of documents, start by
 * launching [Intent.ACTION_OPEN_DOCUMENT_TREE] to let the user pick a
 * directory. Then pass the resulting [Intent.getData] into
 * [.fromTreeUri] to start working with the user selected
 * tree.
 *
 *
 * As you navigate the tree of DocumentFile instances, you can always use
 * [.getUri] to obtain the Uri representing the underlying document for
 * that object, for use with [ContentResolver.openInputStream], etc.
 *
 *
 * To simplify your code on devices running
 * [Build.VERSION_CODES.KITKAT] or earlier, you can use
 * [.fromFile] which emulates the behavior of a
 * [android.provider.DocumentsProvider].
 *
 * @see android.provider.DocumentsProvider
 *
 * @see android.provider.DocumentsContract
 */
abstract class DocumentFile internal constructor(
    /**
     * Return the parent file of this document. Only defined inside of the
     * user-selected tree; you can never escape above the top of the tree.
     *
     *
     * The underlying [android.provider.DocumentsProvider] only defines a
     * forward mapping from parent to child, so the reverse mapping of child to
     * parent offered here is purely a convenience method, and it may be
     * incorrect if the underlying tree structure changes.
     */
    val parentFile: DocumentFile?
) {

    /**
     * Create a new document as a direct child of this directory.
     *
     * @param mimeType MIME type of new document, such as `image/png` or
     * `audio/flac`
     * @param displayName name of new document, without any file extension
     * appended; the underlying provider may choose to append the
     * extension
     * @return file representing newly created document, or null if failed
     * @throws UnsupportedOperationException when working with a single document
     * created from [.fromSingleUri].
     * @see android.provider.DocumentsContract.createDocument
     */
    abstract fun createFile(mimeType: String, displayName: String): DocumentFile?

    /**
     * Create a new directory as a direct child of this directory.
     *
     * @param displayName name of new directory
     * @return file representing newly created directory, or null if failed
     * @throws UnsupportedOperationException when working with a single document
     * created from [.fromSingleUri].
     * @see android.provider.DocumentsContract.createDocument
     */
    abstract fun createDirectory(displayName: String): DocumentFile?

    /**
     * Return a Uri for the underlying document represented by this file. This
     * can be used with other platform APIs to manipulate or share the
     * underlying content. You can use [.isDocumentUri] to
     * test if the returned Uri is backed by a
     * [android.provider.DocumentsProvider].
     *
     * @see Intent.setData
     * @see Intent.setClipData
     * @see ContentResolver.openInputStream
     * @see ContentResolver.openOutputStream
     * @see ContentResolver.openFileDescriptor
     */
    abstract val uri: Uri

    /**
     * Return the display name of this document.
     *
     * @see android.provider.DocumentsContract.Document.COLUMN_DISPLAY_NAME
     */
    abstract val name: String?

    /**
     * Return the MIME type of this document.
     *
     * @see android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
     */
    abstract val type: String?

    /**
     * Indicates if this file represents a *directory*.
     *
     * @return `true` if this file is a directory, `false`
     * otherwise.
     * @see android.provider.DocumentsContract.Document.MIME_TYPE_DIR
     */
    abstract val isDirectory: Boolean

    /**
     * Indicates if this file represents a *file*.
     *
     * @return `true` if this file is a file, `false` otherwise.
     * @see android.provider.DocumentsContract.Document.COLUMN_MIME_TYPE
     */
    abstract val isFile: Boolean

    /**
     * Indicates if this file represents a *virtual* document.
     *
     * @return `true` if this file is a virtual document.
     * @see android.provider.DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT
     */
    abstract val isVirtual: Boolean

    /**
     * Returns the time when this file was last modified, measured in
     * milliseconds since January 1st, 1970, midnight. Returns 0 if the file
     * does not exist, or if the modified time is unknown.
     *
     * @return the time when this file was last modified.
     * @see android.provider.DocumentsContract.Document.COLUMN_LAST_MODIFIED
     */
    abstract fun lastModified(): Long

    /**
     * Returns the length of this file in bytes. Returns 0 if the file does not
     * exist, or if the length is unknown. The result for a directory is not
     * defined.
     *
     * @return the number of bytes in this file.
     * @see android.provider.DocumentsContract.Document.COLUMN_SIZE
     */
    abstract fun length(): Long

    /**
     * Indicates whether the current context is allowed to read from this file.
     *
     * @return `true` if this file can be read, `false` otherwise.
     */
    abstract fun canRead(): Boolean

    /**
     * Indicates whether the current context is allowed to write to this file.
     *
     * @return `true` if this file can be written, `false`
     * otherwise.
     * @see android.provider.DocumentsContract.Document.COLUMN_FLAGS
     *
     * @see android.provider.DocumentsContract.Document.FLAG_SUPPORTS_DELETE
     *
     * @see android.provider.DocumentsContract.Document.FLAG_SUPPORTS_WRITE
     *
     * @see android.provider.DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE
     */
    abstract fun canWrite(): Boolean

    /**
     * Deletes this file.
     *
     *
     * Note that this method does *not* throw `IOException` on
     * failure. Callers must check the return value.
     *
     * @return `true` if this file was deleted, `false` otherwise.
     * @see android.provider.DocumentsContract.deleteDocument
     */
    abstract fun delete(): Boolean

    /**
     * Returns a boolean indicating whether this file can be found.
     *
     * @return `true` if this file exists, `false` otherwise.
     */
    abstract fun exists(): Boolean

    /**
     * Returns an array of files contained in the directory represented by this
     * file.
     *
     * @return an array of files.
     * @throws UnsupportedOperationException when working with a single document
     * created from [.fromSingleUri].
     * @see android.provider.DocumentsContract.buildChildDocumentsUriUsingTree
     */
    abstract fun listFiles(): Array<DocumentFile>

    /**
     * Search through [.listFiles] for the first document matching the
     * given display name. Returns `null` when no matching document is
     * found.
     *
     * @throws UnsupportedOperationException when working with a single document
     * created from [.fromSingleUri].
     */
    fun findFile(displayName: String): DocumentFile? {
        for (doc in listFiles()) {
            if (displayName == doc.name) {
                return doc
            }
        }
        return null
    }

    /**
     * Renames this file to `displayName`.
     *
     *
     * Note that this method does *not* throw `IOException` on
     * failure. Callers must check the return value.
     *
     *
     * Some providers may need to create a new document to reflect the rename,
     * potentially with a different MIME type, so [.getUri] and
     * [.getType] may change to reflect the rename.
     *
     *
     * When renaming a directory, children previously enumerated through
     * [.listFiles] may no longer be valid.
     *
     * @param displayName the new display name.
     * @return true on success.
     * @throws UnsupportedOperationException when working with a single document
     * created from [.fromSingleUri].
     * @see android.provider.DocumentsContract.renameDocument
     */
    abstract fun renameTo(displayName: String): Boolean

    companion object {
        const val TAG = "DocumentFile"

        /**
         * Create a [DocumentFile] representing the filesystem tree rooted at
         * the given [File]. This doesn't give you any additional access to the
         * underlying files beyond what your app already has.
         *
         *
         * [.getUri] will return `file://` Uris for files explored
         * through this tree.
         */
        fun fromFile(file: File): DocumentFile {
            return RawDocumentFile(null, file)
        }

        /**
         * Create a [DocumentFile] representing the single document at the
         * given [Uri]. This is only useful on devices running
         * [Build.VERSION_CODES.KITKAT] or later, and will return
         * `null` when called on earlier platform versions.
         *
         * @param singleUri the [Intent.getData] from a successful
         * [Intent.ACTION_OPEN_DOCUMENT] or
         * [Intent.ACTION_CREATE_DOCUMENT] request.
         */
        fun fromSingleUri(context: Context, singleUri: Uri): DocumentFile? {
            return SingleDocumentFile(null, context, singleUri)
        }

        /**
         * Create a [DocumentFile] representing the document tree rooted at
         * the given [Uri]. This is only useful on devices running
         * [Build.VERSION_CODES.LOLLIPOP] or later, and will return
         * `null` when called on earlier platform versions.
         *
         * @param treeUri the [Intent.getData] from a successful
         * [Intent.ACTION_OPEN_DOCUMENT_TREE] request.
         */
        fun fromTreeUri(context: Context, treeUri: Uri): DocumentFile? {
            return if (Build.VERSION.SDK_INT >= 21) {
                var documentId = DocumentsContractCompat.getTreeDocumentId(treeUri)
                if (DocumentsContractCompat.isDocumentUri(context, treeUri)) {
                    documentId = DocumentsContractCompat.getDocumentId(treeUri)
                }
                requireNotNull(documentId) { "Could not get document ID from Uri: $treeUri" }
                val treeDocumentUri = DocumentsContractCompat.buildDocumentUriUsingTree(treeUri, documentId)
                    ?: throw NullPointerException(
                        "Failed to build documentUri from a tree: $treeUri"
                    )
                TreeDocumentFile(null, context, treeDocumentUri)
            } else {
                null
            }
        }

        /**
         * Test if given Uri is backed by a
         * [android.provider.DocumentsProvider].
         */
        fun isDocumentUri(context: Context, uri: Uri?): Boolean {
            return DocumentsContractCompat.isDocumentUri(context, uri)
        }
    }
}
