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

    public static class ToDoEntry implements BaseColumns{
        public static final String TABLE_NAME="todo";
        public static final String TABLE_NAME_TEMP="note_temp";
        public static final String COLUMN_NAME_CONTENT="content";
        public static final String COLUMN_NAME_DATE="date";
        public static final String COLUMN_NAME_STATE="state";
        public static final String COLUMN_NAME_PRIORITY="priority";
    }

    //建表语句
    public static final String SQL_CREATE_ENTRIES=
            "CREATE TABLE "+ToDoEntry.TABLE_NAME+" ("+
             ToDoEntry._ID+" INTEGER PRIMARY KEY, "+
             ToDoEntry.COLUMN_NAME_CONTENT+" TEXT, "+
             ToDoEntry.COLUMN_NAME_DATE+" INTEGER, "+
             ToDoEntry.COLUMN_NAME_STATE+" INTEGER, "+
             ToDoEntry.COLUMN_NAME_PRIORITY+" INTEGER "+" )";
    //旧表重命名SQL命令
    public static final String SQL_RENAME_TABLE=
            "ALTER TABLE "+ToDoEntry.TABLE_NAME+" RENAME TO "+ToDoEntry.TABLE_NAME_TEMP;
    //旧表数据迁移至新表
    public static final String SQL_MOVE_DATA=
            "INSERT INTO "+ToDoEntry.TABLE_NAME+" ("+ToDoEntry.COLUMN_NAME_CONTENT+","+
             ToDoEntry.COLUMN_NAME_DATE+","+ToDoEntry.COLUMN_NAME_STATE+") "+" SELECT "+
             ToDoEntry.COLUMN_NAME_CONTENT+","+ ToDoEntry.COLUMN_NAME_DATE+","+
             ToDoEntry.COLUMN_NAME_STATE+" FROM "+ToDoEntry.TABLE_NAME_TEMP;
    //删除临时表
    public  static final String SQL_DELETE_ENTRIES=
            "DROP TABLE IF EXISTS "+ToDoEntry.TABLE_NAME_TEMP;

}
