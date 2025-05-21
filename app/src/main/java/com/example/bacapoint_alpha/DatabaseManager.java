package com.example.bacapoint_alpha;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;

public class DatabaseManager {

    private static final String ROW_ID = "_id";
    private static final String ROW_BUKU = "nama_buku";
    private static final String ROW_PENERBIT = "penerbit";
    private static final String ROW_TANGGAL = "tanggal_baca";
    private static final String ROW_GENRE = "genre";

    private static final String NAMA_DB = "database1";
    private static final String NAMA_TABEL = "tblbacaan";
    private static final int DB_VERSION = 2;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + NAMA_TABEL + " (" +
            ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ROW_BUKU + " TEXT, " +
            ROW_PENERBIT + " TEXT, " +
            ROW_TANGGAL + " TEXT, " +
            ROW_GENRE + " TEXT)";

    private final Context context;
    private DatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context ctx) {
        this.context = ctx;
        dbHelper = new DatabaseOpenHelper(context);
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            Log.e("DatabaseManager", "Error opening database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context) {
            super(context, NAMA_DB, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            db.execSQL("DROP TABLE IF EXISTS " + NAMA_TABEL);
            onCreate(db);
        }
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    public void addRow(String namaBuku, String penerbit, String tanggalBaca, String genre) {
        if (db == null || !db.isOpen()) {
            open(); // Make sure database is open
        }

        ContentValues values = new ContentValues();
        values.put(ROW_BUKU, namaBuku);
        values.put(ROW_PENERBIT, penerbit);
        values.put(ROW_TANGGAL, tanggalBaca);
        values.put(ROW_GENRE, genre);

        try {
            db.insert(NAMA_TABEL, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void updateRecord(int id, String namaBuku, String penerbit, String tanggalBaca, String genre) {
        if (db == null || !db.isOpen()) {
            open(); // Make sure database is open
        }

        ContentValues values = new ContentValues();
        values.put(ROW_BUKU, namaBuku);
        values.put(ROW_PENERBIT, penerbit);
        values.put(ROW_TANGGAL, tanggalBaca);
        values.put(ROW_GENRE, genre);

        try {
            db.update(NAMA_TABEL, values, ROW_ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void deleteRecord(int id) {
        if (db == null || !db.isOpen()) {
            open(); // Make sure database is open
        }

        try {
            db.delete(NAMA_TABEL, ROW_ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Object>> ambilSemuaBaris() {
        ArrayList<ArrayList<Object>> dataArray = new ArrayList<>();

        if (db == null || !db.isOpen()) {
            open(); // Make sure database is open
        }

        Cursor cur = null;

        try {
            cur = db.query(NAMA_TABEL,
                    new String[]{ROW_ID, ROW_BUKU, ROW_PENERBIT, ROW_TANGGAL, ROW_GENRE},
                    null, null, null, null, null);

            if (cur != null && cur.moveToFirst()) {
                do {
                    ArrayList<Object> dataList = new ArrayList<>();
                    dataList.add(cur.getInt(0));     // ID
                    dataList.add(cur.getString(1));  // Nama Buku
                    dataList.add(cur.getString(2));  // Penerbit
                    dataList.add(cur.getString(3));  // Tanggal Baca
                    dataList.add(cur.getString(4));  // Genre
                    dataArray.add(dataList);
                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database ERROR", e.toString());
            e.printStackTrace();
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }

        return dataArray;
    }
}