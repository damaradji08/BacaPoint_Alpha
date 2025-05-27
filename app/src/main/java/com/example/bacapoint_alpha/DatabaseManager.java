package com.example.bacapoint_alpha;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseManager {

    // Book table columns (existing)
    private static final String ROW_ID = "_id";
    private static final String ROW_BUKU = "nama_buku";
    private static final String ROW_PENERBIT = "penerbit";
    private static final String ROW_TANGGAL = "tanggal_baca";
    private static final String ROW_GENRE = "genre";

    // User table columns (new)
    private static final String USER_ID = "user_id";
    private static final String USER_USERNAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";
    private static final String USER_FULL_NAME = "full_name";
    private static final String USER_CREATED_AT = "created_at";

    // Database info
    private static final String NAMA_DB = "database1";
    private static final String NAMA_TABEL = "tblbacaan";
    private static final String NAMA_TABEL_USER = "tblusers"; // New user table
    private static final int DB_VERSION = 3; // Increment version for new table

    // Create book table (existing)
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + NAMA_TABEL + " (" +
            ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ROW_BUKU + " TEXT, " +
            ROW_PENERBIT + " TEXT, " +
            ROW_TANGGAL + " TEXT, " +
            ROW_GENRE + " TEXT)";

    // Create user table (new)
    private static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + NAMA_TABEL_USER + " (" +
            USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USER_USERNAME + " TEXT UNIQUE, " +
            USER_EMAIL + " TEXT UNIQUE, " +
            USER_PASSWORD + " TEXT, " +
            USER_FULL_NAME + " TEXT, " +
            USER_CREATED_AT + " TEXT)";

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
            db.execSQL(CREATE_USER_TABLE); // Create user table

            // Insert default admin user
            insertDefaultAdmin(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            if (oldVer < 3) {
                // Add user table for version 3
                db.execSQL(CREATE_USER_TABLE);
                insertDefaultAdmin(db);
            }
        }

        private void insertDefaultAdmin(SQLiteDatabase db) {
            try {
                ContentValues values = new ContentValues();
                values.put(USER_USERNAME, "admin");
                values.put(USER_EMAIL, "admin@bacapoint.com");
                values.put(USER_PASSWORD, hashPassword("123456")); // Default password
                values.put(USER_FULL_NAME, "Administrator");
                values.put(USER_CREATED_AT, String.valueOf(System.currentTimeMillis()));

                db.insert(NAMA_TABEL_USER, null, values);
            } catch (Exception e) {
                Log.e("DatabaseManager", "Error creating default admin: " + e.getMessage());
            }
        }
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    // ========== EXISTING BOOK METHODS ==========

    public void addRow(String namaBuku, String penerbit, String tanggalBaca, String genre) {
        if (db == null || !db.isOpen()) {
            open();
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
            open();
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
            open();
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
            open();
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

    // ========== NEW USER MANAGEMENT METHODS ==========

    /**
     * Register a new user
     */
    public boolean registerUser(String username, String email, String password, String fullName) {
        if (db == null || !db.isOpen()) {
            open();
        }

        // Check if username or email already exists
        if (isUsernameExists(username) || isEmailExists(email)) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(USER_USERNAME, username);
        values.put(USER_EMAIL, email);
        values.put(USER_PASSWORD, hashPassword(password));
        values.put(USER_FULL_NAME, fullName);
        values.put(USER_CREATED_AT, String.valueOf(System.currentTimeMillis()));

        try {
            long result = db.insert(NAMA_TABEL_USER, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DB ERROR", "Error registering user: " + e.toString());
            return false;
        }
    }

    /**
     * Authenticate user login
     */
    public boolean authenticateUser(String usernameOrEmail, String password) {
        if (db == null || !db.isOpen()) {
            open();
        }

        String hashedPassword = hashPassword(password);
        Cursor cur = null;

        try {
            // Check if login is by username or email
            String selection = USER_USERNAME + "=? OR " + USER_EMAIL + "=?";
            String[] selectionArgs = {usernameOrEmail, usernameOrEmail};

            cur = db.query(NAMA_TABEL_USER,
                    new String[]{USER_PASSWORD},
                    selection,
                    selectionArgs,
                    null, null, null);

            if (cur != null && cur.moveToFirst()) {
                String storedPassword = cur.getString(0);
                return hashedPassword.equals(storedPassword);
            }
        } catch (Exception e) {
            Log.e("DB ERROR", "Error authenticating user: " + e.toString());
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
        return false;
    }

    /**
     * Get user information by username or email
     */
    public User getUserInfo(String usernameOrEmail) {
        if (db == null || !db.isOpen()) {
            open();
        }

        Cursor cur = null;
        try {
            String selection = USER_USERNAME + "=? OR " + USER_EMAIL + "=?";
            String[] selectionArgs = {usernameOrEmail, usernameOrEmail};

            cur = db.query(NAMA_TABEL_USER,
                    new String[]{USER_ID, USER_USERNAME, USER_EMAIL, USER_FULL_NAME, USER_CREATED_AT},
                    selection,
                    selectionArgs,
                    null, null, null);

            if (cur != null && cur.moveToFirst()) {
                User user = new User();
                user.setId(cur.getInt(0));
                user.setUsername(cur.getString(1));
                user.setEmail(cur.getString(2));
                user.setFullName(cur.getString(3));
                user.setCreatedAt(cur.getString(4));
                return user;
            }
        } catch (Exception e) {
            Log.e("DB ERROR", "Error getting user info: " + e.toString());
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
        return null;
    }

    /**
     * Check if username already exists
     */
    public boolean isUsernameExists(String username) {
        if (db == null || !db.isOpen()) {
            open();
        }

        Cursor cur = null;
        try {
            cur = db.query(NAMA_TABEL_USER,
                    new String[]{USER_ID},
                    USER_USERNAME + "=?",
                    new String[]{username},
                    null, null, null);
            return cur != null && cur.getCount() > 0;
        } catch (Exception e) {
            Log.e("DB ERROR", "Error checking username: " + e.toString());
            return false;
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
    }

    /**
     * Check if email already exists
     */
    public boolean isEmailExists(String email) {
        if (db == null || !db.isOpen()) {
            open();
        }

        Cursor cur = null;
        try {
            cur = db.query(NAMA_TABEL_USER,
                    new String[]{USER_ID},
                    USER_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);
            return cur != null && cur.getCount() > 0;
        } catch (Exception e) {
            Log.e("DB ERROR", "Error checking email: " + e.toString());
            return false;
        } finally {
            if (cur != null && !cur.isClosed()) {
                cur.close();
            }
        }
    }

    /**
     * Hash password using SHA-256
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("DatabaseManager", "Error hashing password", e);
            return password; // Fallback to plain text (not recommended for production)
        }
    }

    /**
     * User data class
     */
    public static class User {
        private int id;
        private String username;
        private String email;
        private String fullName;
        private String createdAt;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}