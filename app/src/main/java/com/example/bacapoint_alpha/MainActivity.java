package com.example.bacapoint_alpha;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText edNama, edBuku, edPenerbit, edTanggal, edPoin;
    Button bSimpan, bTabel;
    DatabaseManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi Database
        dm = new DatabaseManager(this);

        // Inisialisasi komponen UI
        edNama = findViewById(R.id.etNamaPengguna);
        edBuku = findViewById(R.id.etNamaBuku);
        edPenerbit = findViewById(R.id.etPenerbit);
        edTanggal = findViewById(R.id.etTanggalBaca);
        edPoin = findViewById(R.id.etPoin);

        bSimpan = findViewById(R.id.btnSimpan);
        bTabel = findViewById(R.id.bTabel);

        // Aksi tombol "Tambah"
        bSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = edNama.getText().toString().trim();
                String namaBuku = edBuku.getText().toString().trim();
                String penerbit = edPenerbit.getText().toString().trim();
                String tanggal = edTanggal.getText().toString().trim();
                String poinStr = edPoin.getText().toString().trim();

                if (nama.isEmpty() || namaBuku.isEmpty() || penerbit.isEmpty() || tanggal.isEmpty() || poinStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Semua kolom harus diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int poin = Integer.parseInt(poinStr);
                    dm.addRow(nama, namaBuku, penerbit, tanggal, poin);
                    Toast.makeText(MainActivity.this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();

                    // Kosongkan input setelah berhasil simpan
                    edNama.setText("");
                    edBuku.setText("");
                    edPenerbit.setText("");
                    edTanggal.setText("");
                    edPoin.setText("");
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Poin harus berupa angka!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Aksi tombol "Lihat Tabel"
        bTabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TableActivity2.class);
                startActivity(intent);
            }
        });
    }
}
