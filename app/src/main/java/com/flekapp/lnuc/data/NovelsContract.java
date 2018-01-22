package com.flekapp.lnuc.data;

import android.provider.BaseColumns;

public class NovelsContract {
    public static final class FavoriteNovels implements BaseColumns {
        public final static String TABLE_NAME = "favorite_novels";

        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "NAME";
        public final static String COLUMN_SHORT_NAME = "SHORT_NAME";
        public final static String COLUMN_SOURCE = "SOURCE";
        public final static String COLUMN_URL = "URL";
        public final static String COLUMN_IMAGE_URL = "IMAGE_URL";
    }

    public static final class ReleasedChapter implements BaseColumns {
        public final static String TABLE_NAME = "chapters";

        public final static String COLUMN_ID = BaseColumns._ID;
        public final static String COLUMN_NOVEL_ID = "NOVEL_ID";
        public final static String COLUMN_NUMBER = "NUMBER";
        public final static String COLUMN_TITLE = "TITLE";
        public final static String COLUMN_URL = "URL";
        public final static String COLUMN_STATUS = "STATUS";
        public final static String COLUMN_RELEASE_AT = "RELEASE_AT";
    }
}
