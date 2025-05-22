package com.example.bacapoint_alpha;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Calendar;

public class UpdateActivity extends AppCompatActivity {

    DatabaseManager dm;
    EditText edBuku, edPenerbit, edTanggal, edGenre;
    Button bUpdate, bCancel;
    int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize database
        dm = new DatabaseManager(this);
        dm.open();

        // Initialize UI components
        edBuku = findViewById(R.id.etNamaBukuUpdate);
        edPenerbit = findViewById(R.id.etPenerbitUpdate);
        edTanggal = findViewById(R.id.etTanggalBacaUpdate);
        edGenre = findViewById(R.id.etGenreUpdate);
        bUpdate = findViewById(R.id.btnUpdate);
        bCancel = findViewById(R.id.btnCancelUpdate);

        // Get data from intent
        Intent intent = getIntent();
        bookId = intent.getIntExtra("ID", -1);
        String namaBuku = intent.getStringExtra("NAMA_BUKU");
        String penerbit = intent.getStringExtra("PENERBIT");
        String tanggal = intent.getStringExtra("TANGGAL");
        String genre = intent.getStringExtra("GENRE");

        // Pre-fill the form with existing data
        if (bookId != -1) {
            edBuku.setText(namaBuku);
            edPenerbit.setText(penerbit);
            edTanggal.setText(tanggal);
            edGenre.setText(genre);
        } else {
            Toast.makeText(this, "Error: Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Date picker for tanggal field
        edTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateActivity.this,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            String tanggalDipilih = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                            edTanggal.setText(tanggalDipilih);
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Update button click listener
        bUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaBuku = edBuku.getText().toString().trim();
                String penerbit = edPenerbit.getText().toString().trim();
                String tanggal = edTanggal.getText().toString().trim();
                String genre = edGenre.getText().toString().trim();

                if (namaBuku.isEmpty() || penerbit.isEmpty() || tanggal.isEmpty() || genre.isEmpty()) {
                    Toast.makeText(UpdateActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    dm.updateRecord(bookId, namaBuku, penerbit, tanggal, genre);
                    Toast.makeText(UpdateActivity.this, "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();

                    // Return to table activity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(UpdateActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Cancel button click listener
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Just close the activity
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dm != null) {
            dm.close();
        }
    }
}