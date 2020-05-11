package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }
    /* Inner class that defines the table contents */
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_DATE + " DATE," +
                    FeedEntry.COLUMN_NAME_STATE + " STATE,"+
                    FeedEntry.COLUMN_NAME_CLASS + " CLASS,"+
                    FeedEntry.COLUMN_NAME_INFO + " INFO)";



    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "todolists";
        public static final String COLUMN_NAME_INFO = "info";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_STATE = "state";
        public static final String COLUMN_NAME_CLASS = "class";
    }


}
