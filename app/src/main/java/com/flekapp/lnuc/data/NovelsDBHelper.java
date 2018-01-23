package com.flekapp.lnuc.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flekapp.lnuc.data.NovelsContract.FavoriteNovels;
import com.flekapp.lnuc.data.NovelsContract.ReleasedChapter;

public class NovelsDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "lnuc.db";
    private static final int DATABASE_VERSION = 1;

    public NovelsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_FAVORITE_NOVELS_TABLE = "CREATE TABLE " + FavoriteNovels.TABLE_NAME + " ("
                + FavoriteNovels.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FavoriteNovels.COLUMN_NAME + " TEXT NOT NULL, "
                + FavoriteNovels.COLUMN_SHORT_NAME + " TEXT NOT NULL, "
                + FavoriteNovels.COLUMN_SOURCE + " TEXT NOT NULL, "
                + FavoriteNovels.COLUMN_URL + " TEXT UNIQUE NOT NULL,"
                + FavoriteNovels.COLUMN_IMAGE_URL + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_FAVORITE_NOVELS_TABLE);

        String SQL_CREATE_CHAPTERS_TABLE = "CREATE TABLE " + ReleasedChapter.TABLE_NAME + " ("
                + ReleasedChapter.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReleasedChapter.COLUMN_NOVEL_ID + " INTEGER NOT NULL, "
                + ReleasedChapter.COLUMN_NUMBER + " TEXT NOT NULL, "
                + ReleasedChapter.COLUMN_TITLE + " TEXT NOT NULL, "
                + ReleasedChapter.COLUMN_URL + " TEXT UNIQUE NOT NULL, "
                + ReleasedChapter.COLUMN_STATUS + " TEXT NOT NULL, "
                + ReleasedChapter.COLUMN_RELEASE_AT + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + ReleasedChapter.COLUMN_NOVEL_ID + ")" +
                " REFERENCES " + FavoriteNovels.TABLE_NAME + "(" + FavoriteNovels.COLUMN_ID + ")" +
                " ON DELETE CASCADE);";

        db.execSQL(SQL_CREATE_CHAPTERS_TABLE);
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
