package com.flekapp.lnuc.data;

import android.provider.BaseColumns;

class NovelsContract {
    static final class FavoriteNovels implements BaseColumns {
        final static String TABLE_NAME = "favorite_novels";

        final static String COLUMN_ID = BaseColumns._ID;
        final static String COLUMN_NAME = "NAME";
        final static String COLUMN_SHORT_NAME = "SHORT_NAME";
        final static String COLUMN_SOURCE = "SOURCE";
        final static String COLUMN_URL = "URL";
        final static String COLUMN_IMAGE_URL = "IMAGE_URL";
    }

    static final class PublishedChapter implements BaseColumns {
        final static String TABLE_NAME = "chapters";

        final static String COLUMN_ID = BaseColumns._ID;
        final static String COLUMN_NOVEL_ID = "NOVEL_ID";
        final static String COLUMN_NUMBER = "NUMBER";
        final static String COLUMN_TITLE = "TITLE";
        final static String COLUMN_URL = "URL";
        final static String COLUMN_STATUS = "STATUS";
        final static String COLUMN_PUBLISHED_AT = "PUBLISHED_AT";
    }
}
