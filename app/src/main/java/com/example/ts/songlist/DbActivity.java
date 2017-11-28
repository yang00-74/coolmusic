package com.example.ts.songlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DbActivity extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);


        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 5);
        Button createDatabase = (Button) findViewById(R.id.create_db);
        Button queryButton = (Button) findViewById(R.id.query_button);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "'%is%'";
                Cursor cursor = db.rawQuery("select * from Book where author like "+s, null);
                if (cursor.moveToFirst()) {
                    String author = cursor.getString(1);
                    Toast.makeText(DbActivity.this, author, Toast.LENGTH_SHORT).show();
                }
            }
        });
        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = dbHelper.getWritableDatabase();

            }
        });
    }

    class MyDatabaseHelper extends SQLiteOpenHelper {

        public static final String CREATE_BOOK = "create table Book("
                + "id integer primary key autoincrement,"
                + "author text,"
                + "price real)";
        public static final String CREATE_CATEGORY = "create table Category("
                + "id integer primary key autoincrement,"
                + "category_name text,"
                + "category_code integer)";

        private Context mContext;

        public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_BOOK);
            db.execSQL(CREATE_CATEGORY);
            Toast.makeText(mContext, "Succeeded", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists Book");
            db.execSQL("drop table if exists Category");

            onCreate(db);
            db.execSQL("insert into Book(author,price) values(?,?)",
                    new String[]{"The World is true", "100.0"});
        }
    }
}
