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

import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class SingleDocumentFile extends ProviderFile {
    private Context context;
    private Uri uri;

    SingleDocumentFile(@Nullable ProviderFile parent, Context context, Uri uri) {
        super(parent);
        this.context = context;
        this.uri = uri;
    }

    @Override
    public ProviderFile createFile(@NonNull String mimeType, @NonNull String displayName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProviderFile createDirectory(@NonNull String displayName) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean renameTo(@NonNull String displayName) {
        throw new UnsupportedOperationException();
    }
}
