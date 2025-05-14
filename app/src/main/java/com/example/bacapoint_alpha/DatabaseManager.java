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
    private static final String ROW_NAMA = "nama";
    private static final String ROW_BUKU = "nama_buku";
    private static final String ROW_PENERBIT = "penerbit";
    private static final String ROW_TANGGAL = "tanggal_baca";
    private static final String ROW_POIN = "poin";

    private static final String NAMA_DB = "database1";
    private static final String NAMA_TABEL = "tblbacaan";
    private static final int DB_VERSION = 1;

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + NAMA_TABEL + " (" +
            ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ROW_NAMA + " TEXT, " +
            ROW_BUKU + " TEXT, " +
            ROW_PENERBIT + " TEXT, " +
            ROW_TANGGAL + " TEXT, " +
            ROW_POIN + " INTEGER)";

    private final Context context;
    private DatabaseOpenHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context ctx) {
        this.context = ctx;
        dbHelper = new DatabaseOpenHelper(context);
        // Jangan langsung buka DB di sini
    }

    public void open() {
        setDb(dbHelper.getWritableDatabase());
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
        dbHelper.close();
    }

    public void addRow(String nama, String namaBuku, String penerbit, String tanggalBaca, int poin) {
        ContentValues values = new ContentValues();
        values.put(ROW_NAMA, nama);
        values.put(ROW_BUKU, namaBuku);
        values.put(ROW_PENERBIT, penerbit);
        values.put(ROW_TANGGAL, tanggalBaca);
        values.put(ROW_POIN, poin);

        try {
            db.insert(NAMA_TABEL, null, values);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void updateRecord(int id, String nama, String namaBuku, String penerbit, String tanggalBaca, int poin) {
        ContentValues values = new ContentValues();
        values.put(ROW_NAMA, nama);
        values.put(ROW_BUKU, namaBuku);
        values.put(ROW_PENERBIT, penerbit);
        values.put(ROW_TANGGAL, tanggalBaca);
        values.put(ROW_POIN, poin);

        try {
            db.update(NAMA_TABEL, values, ROW_ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public void deleteRecord(int id) {
        try {
            db.delete(NAMA_TABEL, ROW_ID + "=" + id, null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Object>> ambilSemuaBaris() {
        ArrayList<ArrayList<Object>> dataArray = new ArrayList<>();
        Cursor cur;

        try {
            cur = db.query(NAMA_TABEL,
                    new String[]{ROW_ID, ROW_NAMA, ROW_BUKU, ROW_PENERBIT, ROW_TANGGAL, ROW_POIN},
                    null, null, null, null, null);

            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                ArrayList<Object> dataList = new ArrayList<>();
                dataList.add(cur.getInt(0));     // id
                dataList.add(cur.getString(1));  // nama
                dataList.add(cur.getString(2));  // nama buku
                dataList.add(cur.getString(3));  // penerbit
                dataList.add(cur.getString(4));  // tanggal baca
                dataList.add(cur.getInt(5));     // poin
                dataArray.add(dataList);
                cur.moveToNext();
            }
            cur.close();
        } catch (Exception e) {
            Log.e("Database ERROR", e.toString());
            e.printStackTrace();
        }
        return dataArray;
    }
}
