package com.flekapp.lnuc.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flekapp.lnuc.data.NovelsContract.FavoriteNovels;
import com.flekapp.lnuc.data.NovelsContract.ReleasedChapter;
import com.flekapp.lnuc.data.entity.Chapter;
import com.flekapp.lnuc.data.entity.Novel;
import com.flekapp.lnuc.data.entity.Source;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class NovelsRepository {
    public static Map<Integer, Novel> getFavoritesFromDB(@NonNull Context context) {
        Map<Integer, Novel> novels = new LinkedHashMap<>();

        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "SELECT * FROM " + FavoriteNovels.TABLE_NAME + " fn LEFT JOIN" +
                " (SELECT " + ReleasedChapter.COLUMN_NOVEL_ID + ", MAX(" + ReleasedChapter.COLUMN_NUMBER + ") lastChapter, MAX(" + ReleasedChapter.COLUMN_RELEASE_AT + ") lastReleaseAt" +
                " FROM " + ReleasedChapter.TABLE_NAME + " group by " + ReleasedChapter.COLUMN_NOVEL_ID + ") c ON c."+ ReleasedChapter.COLUMN_NOVEL_ID + " = fn." + FavoriteNovels.COLUMN_ID + ";";
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
                    .getColumnIndex("lastChapter"));
            Long lastChapterReleaseAt = cursor.getLong(cursor
                    .getColumnIndex("lastReleaseAt"));

            Novel novel = new Novel();
            novel.setId(id);
            novel.setName(name);
            novel.setShortName(shortName);
            novel.setUrl(url);
            novel.setImageUrl(imageUrl);
            novel.setSource(Source.getByName(source));
            novel.setLastChapter(lastChapterNumber);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(lastChapterReleaseAt);
            novel.setLastUpdate(calendar.getTime());
            novels.put(id, novel);
        }
        cursor.close();

        return novels;
    }

    // INSERT INTO favorite_novels (NAME, SHORT_NAME, SOURCE, URL) VALUES ("TEST", "TEST","TEST","http://test.ru")
    // INSERT INTO chapters (NOVEL_ID, NUMBER, TITLE, URL, STATUS, RELEASE_AT) VALUES (1, "#1111", "Test chapter 1", "http://test.ru/ch1", "tr", 123456789)
    // SELECT * FROM 'favorite_novels' fn INNER JOIN (SELECT novel_id, MAX(number), MAX(release_at) FROM chapters group by NOVEL_ID) c ON c.novel_id = fn._id;
    public static void addFavorite(@NonNull SQLiteDatabase db, @NonNull Novel novel) {
        ContentValues values = new ContentValues();
        values.put(FavoriteNovels.COLUMN_NAME, novel.getName());
        values.put(FavoriteNovels.COLUMN_SHORT_NAME, novel.getShortName());
        values.put(FavoriteNovels.COLUMN_SOURCE, novel.getSource().getName());
        values.put(FavoriteNovels.COLUMN_URL, novel.getUrl());
        values.put(FavoriteNovels.COLUMN_IMAGE_URL, novel.getImageUrl());

        long newRowId = db.insert(FavoriteNovels.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(NovelsRepository.class.getSimpleName(), "Error on add novel to favorite");
        }
    }

    public static Map<Integer, Chapter> getChaptersFromDB(@NonNull Context context) {
        Map<Integer, Chapter> chapters = new LinkedHashMap<>();

        Map<Integer, Novel> novels = NovelsRepository.getFavoritesFromDB(context);

        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String query = "SELECT * FROM " + ReleasedChapter.TABLE_NAME +
                " order by " + ReleasedChapter.COLUMN_RELEASE_AT + " desc;";
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

    public static void addChapter(@NonNull Context context, @NonNull Chapter chapter) {
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

        if (newRowId == -1) {
            Log.e(NovelsRepository.class.getSimpleName(), "Error on add new chapter to db");
        }
    }

    public static void initFavorites(@NonNull Context context) {
        NovelsDBHelper mDbHelper = new NovelsDBHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Novel againstTheGods = new Novel();
        againstTheGods.setName("Against The Gods");
        againstTheGods.setShortName("ATG");
        againstTheGods.setUrl("https://lnmtl.com/novel/against-the-gods");
        againstTheGods.setSource(Source.LNMTL);
        addFavorite(db, againstTheGods);

        Novel chaoticSwordGod = new Novel();
        chaoticSwordGod.setName("Chaotic Sword God");
        chaoticSwordGod.setShortName("CSG");
        chaoticSwordGod.setUrl("https://lnmtl.com/novel/chaotic-sword-god");
        chaoticSwordGod.setSource(Source.LNMTL);
        addFavorite(db, chaoticSwordGod);
    }
}
