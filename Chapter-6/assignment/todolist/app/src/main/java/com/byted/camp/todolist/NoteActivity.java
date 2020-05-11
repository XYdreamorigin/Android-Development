package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract.FeedEntry;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.util.Date;

import static com.byted.camp.todolist.ui.NoteViewHolder.SIMPLE_DATE_FORMAT;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";
    private EditText editText;
    private Button addBtn;
    private TodoDbHelper dbHelper;
    private RadioGroup nRg1;
    private RadioButton btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        setTitle(R.string.take_a_note);

        editText = findViewById(R.id.edit_text);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }
        dbHelper=new TodoDbHelper(this);
        addBtn = findViewById(R.id.btn_add);
        nRg1 = findViewById(R.id.rg_1);
        btn= findViewById(R.id.rb_3);
        nRg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                btn= radioGroup.findViewById(i);
                Toast.makeText(NoteActivity.this, "重要程度："+btn.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
        dbHelper.close();
    }

    private boolean saveNote2Database(String content) {
        // TODO 插入一条新数据，返回是否插入成功
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.i(TAG, "here:" + "ok");
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_INFO, content);
        Log.i(TAG, "Note added:" + content);
        Date date = new Date(System.currentTimeMillis());
        State state=State.from(0);
        values.put(FeedEntry.COLUMN_NAME_DATE, SIMPLE_DATE_FORMAT.format(date));
        Log.i(TAG, "date:" + SIMPLE_DATE_FORMAT.format(date));
        values.put(FeedEntry.COLUMN_NAME_STATE, state.toString());
        Log.i(TAG, "state:" + state);
        Log.i(TAG, "cla:" + btn.getText().toString());
        values.put(FeedEntry.COLUMN_NAME_CLASS, btn.getText().toString());
        Log.i(TAG, "cla:" + btn.getText().toString());
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedEntry.TABLE_NAME, null, values);
        Log.i(TAG, "newrowid:" + newRowId);
        return newRowId!=-1;

    }
}
