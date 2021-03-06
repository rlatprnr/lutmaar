/*******************************************************************************
 * Copyright 2013 Kumar Bibek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.wots.lutmaar.CustomView.imagechooser.threads;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.wots.lutmaar.BuildConfig;
import com.wots.lutmaar.CustomView.imagechooser.api.ChosenFile;
import com.wots.lutmaar.CustomView.imagechooser.api.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Calendar;

public class FileProcessorThread extends MediaProcessorThread {

    private final static String TAG = "FileProcessorThread";

    private FileProcessorListener listener;

    private ContentResolver cr;

    private String fileDisplayName;

    private long fileSize;

    private String mimeType;

    public FileProcessorThread(String filePath, String foldername,
                               boolean shouldCreateThumbnails) {
        super(filePath, foldername, shouldCreateThumbnails);
    }

    public void setListener(FileProcessorListener listener) {
        this.listener = listener;
    }

    public void setContext(Context context) {
        this.context = context;
        if (filePath.startsWith("content")) {
            cr = context.getContentResolver();
            Cursor cursor = null;
            mimeType = null;
            try {
                InputStream stream = cr.openInputStream(Uri.parse(filePath));
                mimeType = URLConnection.guessContentTypeFromStream(stream);
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }
            if (mimeType == null) {
                mimeType = cr.getType(Uri.parse(filePath));
            }

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "File mime type: " + mimeType);
            }
            try {
                cursor = cr.query(Uri.parse(filePath), null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fileDisplayName = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            Log.i(TAG, "Extension: " + extension);
            if (extension == null) {
                if (mimeType.contains("/")) {
                    String[] parts = mimeType.split("/");
                    setMediaExtension("." + parts[1]);
                } else {
                    setMediaExtension("." + mimeType);
                }
            } else {
                setMediaExtension("." + extension);
            }
        } else if (filePath.startsWith("file")) {
            String extension = null;
            try {
                File file = new File(Uri.parse(filePath).getPath());
                InputStream stream = new FileInputStream(file);
                mimeType = URLConnection.guessContentTypeFromStream(stream);
                cr = context.getContentResolver();
                mimeType = cr.getType(Uri.parse(filePath));
                if (mimeType != null) {
                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                    if (extension != null) {
                        setMediaExtension("." + extension);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Do nothing
            }

            // Default Mime Type
            if (mimeType == null) {
                mimeType = "application/octet-steam";
            }
            if (extension == null) {
                int i = filePath.lastIndexOf('.');
                if (i > 0) {
                    extension = filePath.substring(i + 1);
                }
                if (extension != null && !TextUtils.isEmpty(extension)) {
                    setMediaExtension("." + extension);
                }
            }
            if (fileDisplayName == null || !TextUtils.isEmpty(fileDisplayName)) {
                File file = new File(filePath);
                fileDisplayName = file.getName();
            }
        }
    }

    @Override
    public void run() {
        try {
            manageDiretoryCache(mediaExtension);
            processFile();
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onError(e.getMessage());
            }
        }
    }

    private void processFile() throws Exception {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "Processing File: " + filePath);
        }
        processFileDetails(filePath);
        process();
    }

    protected void processFileDetails(String path)
            throws Exception {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "File Started");
        }
        if (filePath.startsWith("content:")) {
            try {
                Uri uri = Uri.parse(filePath);
                InputStream inputStream = context.getContentResolver()
                        .openInputStream(uri);
                if (fileDisplayName == null) {
                    fileDisplayName = "" + Calendar.getInstance().getTimeInMillis() + mediaExtension;
                }
                if (!fileDisplayName.contains(".") && mediaExtension != null && mediaExtension.length() > 0) {
                    fileDisplayName = fileDisplayName + mediaExtension;
                }
                filePath = FileUtils.getDirectory(foldername) + File.separator
                        + fileDisplayName;

                BufferedOutputStream outStream = new BufferedOutputStream(
                        new FileOutputStream(filePath));
                byte[] buf = new byte[2048];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outStream.write(buf, 0, len);
                }
                inputStream.close();
                outStream.close();
                File fileForSize = new File(filePath);
                fileSize = fileForSize.length();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else if (filePath.startsWith("file:")) {
            filePath = filePath.substring(7);
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "File Done " + filePath);
        }
    }

    @Override
    protected void process() throws Exception {
        super.process();
        if (listener != null) {
            ChosenFile file = new ChosenFile();
            file.setFilePath(filePath);
            file.setExtension(mediaExtension);
            file.setFileName(fileDisplayName);
            file.setFileSize(fileSize);
            file.setMimeType(mimeType);
            listener.onProcessedFile(file);
        }
    }

    @Override
    protected void processingDone(String file, String thumbnail, String thumbnailSmall) {
        // Nothing
    }
}
