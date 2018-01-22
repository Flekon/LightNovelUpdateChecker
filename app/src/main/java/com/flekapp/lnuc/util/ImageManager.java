package com.flekapp.lnuc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ImageManager {
    private static final String TAG = ImageManager.class.getSimpleName();

    static class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        Context mContext;
        String resultFileName;

        DownloadImage(Context context, String fileName) {
            mContext = context;
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
            FileOutputStream stream;
            try {
                stream = mContext.openFileOutput(imageName, Context.MODE_PRIVATE);
                b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
            } catch (Exception e) {
                Log.e(TAG, "Exception thrown while save image.");
            }
        }
    }

    private Context mContext;

    public ImageManager(Context context) {
        mContext = context;
    }

    public String getFileNameByUrl(String url) {
        try {
            long start = System.nanoTime();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(url.getBytes());
            byte data[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (int i = data.length - 1; i >= 0; i--) {
                sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
            }
            long end = System.nanoTime();
            Log.d(TAG, "time: " + String.valueOf(end - start));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "Exception thrown while hashing url with sha-256.");
            return null;
        }

    }

    public void saveImage(String url) {
        new DownloadImage(mContext, getFileNameByUrl(url)).execute(url);
    }

    public Bitmap getImage(String url) {
        Bitmap result = null;
        try {
            FileInputStream stream = mContext.openFileInput(getFileNameByUrl(url));
            result = BitmapFactory.decodeStream(stream);
            stream.close();
        } catch (Exception e) {
            Log.e(TAG, "Exception thrown while download image.");
        }
        return result;
    }

    public boolean isFileExist(String url) {
        File file = mContext.getFileStreamPath(getFileNameByUrl(url));
        return file.exists();
    }

    public boolean deleteFile(String url) {
        File file = mContext.getFileStreamPath(getFileNameByUrl(url));
        return file.delete();
    }
}
