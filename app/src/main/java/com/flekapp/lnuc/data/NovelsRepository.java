package com.flekapp.lnuc.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.flekapp.lnuc.data.NovelsContract.FavoriteNovels;
import com.flekapp.lnuc.data.NovelsContract.ReleasedChapter;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.entity.Source;
import com.flekapp.lnuc.util.ImageManager;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class NovelsRepository {
    public static Map<Integer, Novel> getFavoritesFromDB(@NonNull Context context) {
        Map<Integer, Novel> novels = new LinkedHashMap<>();

        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "SELECT * FROM " + FavoriteNovels.TABLE_NAME + " fn " +
                        "LEFT JOIN (" +
                            "SELECT a." + ReleasedChapter.COLUMN_NOVEL_ID + ", " +
                                        ReleasedChapter.COLUMN_NUMBER + " lastChapterNumber, " +
                                        ReleasedChapter.COLUMN_TITLE + " lastChapterTitle, " +
                                        ReleasedChapter.COLUMN_RELEASE_AT + " lastReleaseAt " +
                                    "FROM " + ReleasedChapter.TABLE_NAME + " A INNER JOIN (" +
                                            "SELECT " + ReleasedChapter.COLUMN_ID + ", " + ReleasedChapter.COLUMN_NOVEL_ID +
                                                " FROM " + ReleasedChapter.TABLE_NAME +
                                                " GROUP BY " + ReleasedChapter.COLUMN_NOVEL_ID + "" +
                                                " HAVING " + ReleasedChapter.COLUMN_RELEASE_AT + " = MAX(" + ReleasedChapter.COLUMN_RELEASE_AT + ")) B" +
                                                " ON A." + ReleasedChapter.COLUMN_ID + " = B." + ReleasedChapter.COLUMN_ID +
                                                    " AND A." + ReleasedChapter.COLUMN_NOVEL_ID + " = B." + ReleasedChapter.COLUMN_NOVEL_ID +
                            ") c ON c." + ReleasedChapter.COLUMN_NOVEL_ID + " = fn." + FavoriteNovels.COLUMN_ID + ";";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(FavoriteNovels._ID));
            String name = cursor.getString(cursor
                    .getColumnIndex(FavoriteNovels.COLUMN_NAME));
            String shortName = cursor.getString(cursor
                    .getColumnIndex(FavoriteNovels.COLUMN_SHORT_NAME));
            String source = cursor.getString(cursor
                    .getColumnIndex(FavoriteNovels.COLUMN_SOURCE));
            String url = cursor.getString(cursor
                    .getColumnIndex(FavoriteNovels.COLUMN_URL));
            String imageUrl = cursor.getString(cursor
                    .getColumnIndex(FavoriteNovels.COLUMN_IMAGE_URL));
            String lastChapterNumber = cursor.getString(cursor
                    .getColumnIndex("lastChapterNumber"));
            String lastChapterTitle = cursor.getString(cursor
                    .getColumnIndex("lastChapterTitle"));
            Long lastChapterReleaseAt = null;
            if (!cursor.isNull(cursor.getColumnIndex("lastReleaseAt"))) {
                lastChapterReleaseAt = cursor.getLong(cursor
                        .getColumnIndex("lastReleaseAt"));
            }

            Novel novel = new Novel();
            novel.setId(id);
            novel.setName(name);
            novel.setShortName(shortName);
            novel.setUrl(url);
            novel.setImageUrl(imageUrl);
            novel.setSource(Source.getByName(source));
            novel.setLastChapterNumber(lastChapterNumber);
            novel.setLastChapterTitle(lastChapterTitle);
            // TODO check if date not null...
            if (lastChapterReleaseAt != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(lastChapterReleaseAt);
                novel.setLastUpdate(calendar.getTime());
            }
            novels.put(id, novel);
        }
        cursor.close();

        return novels;
    }

    public static boolean addFavorite(@NonNull Context context, @NonNull Novel novel) {
        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FavoriteNovels.COLUMN_NAME, novel.getName());
        values.put(FavoriteNovels.COLUMN_SHORT_NAME, novel.getShortName());
        values.put(FavoriteNovels.COLUMN_SOURCE, novel.getSource().getName());
        values.put(FavoriteNovels.COLUMN_URL, novel.getUrl());
        values.put(FavoriteNovels.COLUMN_IMAGE_URL, novel.getImageUrl());

        new ImageManager(context).saveImage(novel.getImageUrl());

        long newRowId = db.insert(FavoriteNovels.TABLE_NAME, null, values);

        return newRowId != -1;
    }

    public static boolean deleteFavorite(@NonNull Context context, @NonNull Novel novel) {
        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String[] params = new String[] { String.valueOf(novel.getId()) };

        new ImageManager(context).deleteFile(novel.getImageUrl());

        return db.delete(FavoriteNovels.TABLE_NAME,FavoriteNovels.COLUMN_ID + " = ?", params) > 0;
    }

    public static Map<Integer, Chapter> getChaptersFromDB(@NonNull Context context) {
        Map<Integer, Chapter> chapters = new LinkedHashMap<>();

        Map<Integer, Novel> novels = NovelsRepository.getFavoritesFromDB(context);

        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "SELECT * FROM " + ReleasedChapter.TABLE_NAME +
                            " order by " + ReleasedChapter.COLUMN_RELEASE_AT + " desc, "
                                        + ReleasedChapter.COLUMN_NUMBER + " desc" +
                            " LIMIT 100;";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(ReleasedChapter._ID));
            int novelId = cursor.getInt(cursor
                    .getColumnIndex(ReleasedChapter.COLUMN_NOVEL_ID));
            String number = cursor.getString(cursor
                    .getColumnIndex(ReleasedChapter.COLUMN_NUMBER));
            String title = cursor.getString(cursor
                    .getColumnIndex(ReleasedChapter.COLUMN_TITLE));
            String url = cursor.getString(cursor
                    .getColumnIndex(ReleasedChapter.COLUMN_URL));
            String status = cursor.getString(cursor
                    .getColumnIndex(ReleasedChapter.COLUMN_STATUS));
            Long releaseAt = cursor.getLong(cursor
                    .getColumnIndex(ReleasedChapter.COLUMN_RELEASE_AT));

            Chapter chapter = new Chapter();
            chapter.setId(id);
            chapter.setNumber(number);
            chapter.setTitle(title);
            chapter.setUrl(url);
            chapter.setNovel(novels.get(novelId));
            chapter.setStatus(status);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(releaseAt);
            chapter.setReleaseDate(calendar.getTime());
            chapters.put(id, chapter);
        }
        cursor.close();

        return chapters;
    }

    public static boolean addChapter(@NonNull Context context, @NonNull Chapter chapter) {
        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ReleasedChapter.COLUMN_NOVEL_ID, chapter.getNovel().getId());
        values.put(ReleasedChapter.COLUMN_NUMBER, chapter.getNumber());
        values.put(ReleasedChapter.COLUMN_TITLE, chapter.getTitle());
        values.put(ReleasedChapter.COLUMN_URL, chapter.getUrl());
        values.put(ReleasedChapter.COLUMN_STATUS, chapter.getStatus());
        values.put(ReleasedChapter.COLUMN_RELEASE_AT, chapter.getReleaseDate().getTime());

        long newRowId = db.insert(ReleasedChapter.TABLE_NAME, null, values);

        return newRowId != -1;
    }
}
