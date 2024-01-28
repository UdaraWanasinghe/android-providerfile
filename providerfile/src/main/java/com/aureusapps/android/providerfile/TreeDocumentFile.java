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

package com.aureusapps.android.providerfile;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class TreeDocumentFile extends ProviderFile {
    private Context context;
    private Uri uri;

    TreeDocumentFile(@Nullable ProviderFile parent, Context context, Uri uri) {
        super(parent);
        this.context = context;
        this.uri = uri;
    }

    @Override
    @Nullable
    public ProviderFile createFile(@NonNull String mimeType, @NonNull String displayName) {
        final Uri result = TreeDocumentFile.createFile(context, uri, mimeType, displayName);
        return (result != null) ? new TreeDocumentFile(this, context, result) : null;
    }

    @Nullable
    private static Uri createFile(Context context, Uri self, String mimeType,
            String displayName) {
        try {
            return DocumentsContract.createDocument(context.getContentResolver(), self, mimeType,
                    displayName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Nullable
    public ProviderFile createDirectory(@NonNull String displayName) {
        final Uri result = TreeDocumentFile.createFile(
                context, uri, DocumentsContract.Document.MIME_TYPE_DIR, displayName);
        return (result != null) ? new TreeDocumentFile(this, context, result) : null;
    }

    @NonNull
    @Override
    public Uri getUri() {
        return uri;
    }

    @Override
    @Nullable
    public String getName() {
        return DocumentsContractApi19.getName(context, uri);
    }

    @Override
    @Nullable
    public String getType() {
        return DocumentsContractApi19.getType(context, uri);
    }

    @Override
    public boolean isDirectory() {
        return DocumentsContractApi19.isDirectory(context, uri);
    }

    @Override
    public boolean isFile() {
        return DocumentsContractApi19.isFile(context, uri);
    }

    @Override
    public boolean isVirtual() {
        return DocumentsContractApi19.isVirtual(context, uri);
    }

    @Override
    public long lastModified() {
        return DocumentsContractApi19.lastModified(context, uri);
    }

    @Override
    public long length() {
        return DocumentsContractApi19.length(context, uri);
    }

    @Override
    public boolean canRead() {
        return DocumentsContractApi19.canRead(context, uri);
    }

    @Override
    public boolean canWrite() {
        return DocumentsContractApi19.canWrite(context, uri);
    }

    @Override
    public boolean delete() {
        try {
            return DocumentsContract.deleteDocument(context.getContentResolver(), uri);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean exists() {
        return DocumentsContractApi19.exists(context, uri);
    }

    @NonNull
    @Override
    public ProviderFile[] listFiles() {
        final ContentResolver resolver = context.getContentResolver();
        final Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri,
                DocumentsContract.getDocumentId(uri));
        final ArrayList<Uri> results = new ArrayList<>();

        Cursor c = null;
        try {
            c = resolver.query(childrenUri, new String[] {
                    DocumentsContract.Document.COLUMN_DOCUMENT_ID }, null, null, null);
            while (c.moveToNext()) {
                final String documentId = c.getString(0);
                final Uri documentUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                        documentId);
                results.add(documentUri);
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed query: " + e);
        } finally {
            closeQuietly(c);
        }

        final Uri[] result = results.toArray(new Uri[0]);
        final ProviderFile[] resultFiles = new ProviderFile[result.length];
        for (int i = 0; i < result.length; i++) {
            resultFiles[i] = new TreeDocumentFile(this, context, result[i]);
        }
        return resultFiles;
    }

    private static void closeQuietly(@Nullable AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean renameTo(@NonNull String displayName) {
        try {
            final Uri result = DocumentsContract.renameDocument(
                    context.getContentResolver(), uri, displayName);
            if (result != null) {
                uri = result;
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
