package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.debug.DebugActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;
    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //初始化数据库
        todoDbHelper=new TodoDbHelper(this);
        sqLiteDatabase=todoDbHelper.getWritableDatabase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
        sqLiteDatabase = null;
        todoDbHelper.close();
        todoDbHelper = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        if(sqLiteDatabase==null){
            return Collections.emptyList();
        }
        List<Note> result=new LinkedList<>();
        Cursor cursor=null;
        try{
            cursor=sqLiteDatabase.query(TodoContract.ToDoEntry.TABLE_NAME,null,null,null,null,null,TodoContract.ToDoEntry.COLUMN_NAME_PRIORITY+" DESC");
            while(cursor.moveToNext())
            {
                long id=cursor.getLong(cursor.getColumnIndex(TodoContract.ToDoEntry._ID));
                String content=cursor.getString(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_CONTENT));
                long dateMs=cursor.getLong(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_DATE));
                int intState=cursor.getInt(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_STATE));
                int intPriority=cursor.getInt(cursor.getColumnIndex(TodoContract.ToDoEntry.COLUMN_NAME_PRIORITY));
                Note note=new Note(id);
                note.setContent(content);
                note.setDate(new Date(dateMs));
                note.setState(State.from(intState));
                note.setPriority(Priority.from(intPriority));
                result.add(note);
            }
        }
        finally {
            if(cursor!=null){
                cursor.close();
            }
        }
        return result;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        if(sqLiteDatabase==null){
            return;
        }
        Long id=note.id;
        String selection= TodoContract.ToDoEntry._ID+" LIKE ?";
        String[] selectArgs={Long.toString(id)};
        int deleteRows=sqLiteDatabase.delete(TodoContract.ToDoEntry.TABLE_NAME,selection,selectArgs);
        if(deleteRows>0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private void updateNode(Note note) {
        // 更新数据
        if(sqLiteDatabase==null){
            return;
        }
        Long id=note.id;
        State state=note.getState();
        ContentValues values=new ContentValues();
        values.put(TodoContract.ToDoEntry.COLUMN_NAME_STATE,Integer.toString(state.intValue));
        String selection=TodoContract.ToDoEntry._ID+" LIKE ?";
        String[] selectArgs={Long.toString(id)};
        int count=sqLiteDatabase.update(TodoContract.ToDoEntry.TABLE_NAME,values,selection,selectArgs);
        if(count>0){
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }
}
