package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    // TODO 定义数据库名、版本；创建数据库
    public static final int DATABASE_VERSION = 2;//数据库版本2.0
    public static final String DATABASE_NAME = "todo.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TodoContract.SQL_CREATE_ENTRIES);
        Log.d("help", "onCreate: ");
    }
    /********数据库版本版本更新*******/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         for(int i=oldVersion;i<newVersion;i++){
             Log.d("help", "onUpgrade: ");
             switch (i){
                 case 1:
                     try{
                         db.execSQL(TodoContract.SQL_RENAME_TABLE);
                         db.execSQL(TodoContract.SQL_CREATE_ENTRIES);
                         db.execSQL(TodoContract.SQL_MOVE_DATA);
                         db.execSQL(TodoContract.SQL_DELETE_ENTRIES);
                     }
                     catch (Exception e){
                         e.printStackTrace();
                     }
                     break;
                 default:
                     break;
             }
         }
    }
}
