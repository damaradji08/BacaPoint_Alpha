package com.example.bacapoint_alpha;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText edBuku, edPenerbit, edTanggal, edGenre;
    Button bSimpan, bTabel;
    DatabaseManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Database
        dm = new DatabaseManager(this);
        dm.open(); // Open the database connection

        // Inisialisasi komponen UI
        edBuku = findViewById(R.id.etNamaBuku);
        edPenerbit = findViewById(R.id.etPenerbit);
        edTanggal = findViewById(R.id.etTanggalBaca);
        edGenre = findViewById(R.id.etGenre);

        bSimpan = findViewById(R.id.btnSimpan);
        bTabel = findViewById(R.id.bTabel);

        edTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ambil tanggal hari ini
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Buat DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        (view, year1, monthOfYear, dayOfMonth) -> {
                            // Format dan set ke EditText
                            String tanggalDipilih = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                            edTanggal.setText(tanggalDipilih);
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Aksi tombol "Tambah"
        bSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaBuku = edBuku.getText().toString().trim();
                String penerbit = edPenerbit.getText().toString().trim();
                String tanggal = edTanggal.getText().toString().trim();
                String genre = edGenre.getText().toString().trim();

                if (namaBuku.isEmpty() || penerbit.isEmpty() || tanggal.isEmpty() || genre.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    dm.addRow(namaBuku, penerbit, tanggal, genre);
                    Toast.makeText(MainActivity.this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();

                    // Kosongkan input setelah berhasil simpan
                    edBuku.setText("");
                    edPenerbit.setText("");
                    edTanggal.setText("");
                    edGenre.setText("");
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Aksi tombol "Lihat Tabel"
        bTabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TableActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database when activity is destroyed
        if (dm != null) {
            dm.close();
        }
    }
}