package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
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
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;
import com.byted.camp.todolist.db.TodoContract.FeedEntry;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_ADD = 1002;
    private TodoDbHelper dbHelper;
    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                Log.i("ABD","DELETE 1");
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                Log.i("ABD","DO IT 1");
            }
        });
        recyclerView.setAdapter(notesAdapter);

        dbHelper=new TodoDbHelper(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        dbHelper.onUpgrade(db,1,2);
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
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
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
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
        //things.clear();
        List<Note> things=new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        Log.i(TAG, "1111111111111111:");
        String[] projection = {
                BaseColumns._ID,
                FeedEntry.COLUMN_NAME_INFO,
                FeedEntry.COLUMN_NAME_DATE,
                FeedEntry.COLUMN_NAME_STATE,
                FeedEntry.COLUMN_NAME_CLASS
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_CLASS + " ASC";
        Cursor cursor = db.query(
                FeedEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        Log.i(TAG, "perfrom query data:");
        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(FeedEntry._ID));
            String content = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_INFO));
            String datetemp = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_DATE));
            String statetemp = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_STATE));
            String cla = cursor.getString(cursor.getColumnIndex(FeedEntry.COLUMN_NAME_CLASS));
            Log.i(TAG, "itemId:" + itemId + ", content:" + content+",date:"+datetemp+",Stete:"+statetemp+"  class:"+cla);

            Note temp=new Note(itemId);
            //Log.i(TAG, "date\n\n");
            Date date =getDate(datetemp);
            temp.setDate(date);
            temp.setCla(Integer.valueOf(cla).intValue());
            Log.i(TAG,"date ok"+date);
            if("TODO".equals(statetemp)){
                temp.setState(State.from(0));
            }
            else{
                temp.setState(State.from(1));
            }
            //Log.i(TAG, "state\n\n");
            temp.setContent(content);
            //Log.i(TAG, "concent\n\n");
            things.add(temp);

            //Log.i(TAG, "add ok\n\n");
        }
        cursor.close();
        Log.i("ABD","DO IT 3");
        return things;
    }


    public Date getDate(String str) {
        try {
            java.text.SimpleDateFormat formatter = new SimpleDateFormat(
                    "EEE, d MMM yyyy HH:MM:ss", Locale.ENGLISH);
            return formatter.parse(str);
        } catch (Exception e) {
            // TODO: handle exception
            String s = e.getMessage();
            return null;
        }
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = FeedEntry._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(note.id)};
        // Issue SQL statement.
        int deletedRows = db.delete(FeedEntry.TABLE_NAME, selection, selectionArgs);
        Log.i(TAG, "perform delete data, result:" + deletedRows);
        notesAdapter.refresh(loadNotesFromDatabase());
    }

    private void updateNode(Note note) {
        // TODO 更新数据
        Log.i(TAG,"info1:   "+note.getContent()+"   date1:   "+note.getDate()+"     state1:    "+note.getState());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_STATE, note.getState().toString());

        // Which row to update
        //String selection = FeedEntry.COLUMN_NAME_STATE+"!="+note.getState().toString();
        String selection = FeedEntry._ID + " LIKE ?";;
        String[] selectionArgs = {String.valueOf(note.id)};
        Log.i(TAG,selection);
        int count = db.update(
                FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        Log.i(TAG, "perform update data, result:" + count);
        notesAdapter.refresh(loadNotesFromDatabase());
    }


}
