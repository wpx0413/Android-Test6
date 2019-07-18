package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.Priority;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;


public class NoteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;
    private RadioGroup radioGroup;
    private AppCompatRadioButton lowRadioBtn;

    private TodoDbHelper todoDbHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        //初始化数据库
        todoDbHelper=new TodoDbHelper(this);
        sqLiteDatabase=todoDbHelper.getWritableDatabase();

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        radioGroup = findViewById(R.id.radio_group);
        lowRadioBtn = findViewById(R.id.btn_low);
        lowRadioBtn.setChecked(true);

        addBtn = findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(NoteActivity.this,
                            "No content to add", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean succeed = saveNote2Database(content.toString().trim());
                if (succeed) {
                    Toast.makeText(NoteActivity.this,
                            "Note added", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                } else {
                    Toast.makeText(NoteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
        sqLiteDatabase = null;
        todoDbHelper.close();
        todoDbHelper = null;
    }

    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        if (sqLiteDatabase == null || TextUtils.isEmpty(content)) {
            return false;
        }

        ContentValues values=new ContentValues();
        values.put(TodoContract.ToDoEntry.COLUMN_NAME_CONTENT,content);
        values.put(TodoContract.ToDoEntry.COLUMN_NAME_DATE, System.currentTimeMillis());
        values.put(TodoContract.ToDoEntry.COLUMN_NAME_STATE, State.TODO.intValue);
        values.put(TodoContract.ToDoEntry.COLUMN_NAME_PRIORITY,getSelectedPriority().intValue);
        long newRowId=sqLiteDatabase.insert(TodoContract.ToDoEntry.TABLE_NAME,null,values);
        if(newRowId!=-1){
            return true;
        }
        else{
            return false;
        }
    }

    //选择优先级
    private Priority getSelectedPriority() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.btn_high:
                return Priority.HIGH;
            case R.id.btn_mid:
                return Priority.MID;
            default:
                return Priority.LOW;
        }
    }
}
