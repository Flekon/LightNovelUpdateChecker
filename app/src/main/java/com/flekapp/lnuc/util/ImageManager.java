package com.flekapp.lnuc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.os.Environment.MEDIA_MOUNTED;
import static android.os.Environment.getExternalStorageState;

public class ImageManager {
    private static final String TAG = ImageManager.class.getSimpleName();

    static class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private File mImagesDir;
        String resultFileName;

        DownloadImage(File cacheDir, String fileName) {
            mImagesDir = cacheDir;
            resultFileName = fileName;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(result, resultFileName);
        }

        private Bitmap downloadImageBitmap(String url) {
            Bitmap bitmap = null;
            try {
                InputStream stream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(stream);
                stream.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown while download image.");
            }
            return bitmap;
        }

        private void saveImage(Bitmap b, String imageName) {
            try {
                File file = new File(mImagesDir, imageName);
                FileOutputStream stream = new FileOutputStream(file);
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown while save image.");
            }
        }
    }

    private File mImagesDir;

    public ImageManager(Context context) {
        String sdState = getExternalStorageState();
        if (MEDIA_MOUNTED.equals(sdState)) {
            mImagesDir = context.getExternalFilesDir("images");
            if (mImagesDir == null) {
                mImagesDir = context.getCacheDir();
            }
        } else {
            mImagesDir = context.getCacheDir();
        }
        if(!mImagesDir.exists()) {
            if (!mImagesDir.mkdirs()) {
                Log.e(TAG, "Can't create images directory.");
            }
        }
    }

    private String getFileNameByUrl(@NonNull String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(url.getBytes());
            byte data[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = data.length - 1; i >= 0; i--) {
                sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception thrown while hashing url with sha-256.");
            return url;
        }
    }

    public void saveImage(String url) {
        new DownloadImage(mImagesDir, getFileNameByUrl(url)).execute(url);
    }

    public Bitmap getImage(String url) {
        Bitmap result = null;
        try {
            File file = new File(mImagesDir, getFileNameByUrl(url));
            FileInputStream stream = new FileInputStream(file);
            result = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception thrown while get image.");
        }
        return result;
    }

    public boolean isImageExist(String url) {
        File file = new File(mImagesDir, getFileNameByUrl(url));
        return file.isFile();
    }

    public boolean deleteImage(String url) {
        File file = new File(mImagesDir, getFileNameByUrl(url));
        return file.delete();
    }
}
