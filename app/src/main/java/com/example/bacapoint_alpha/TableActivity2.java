package com.example.bacapoint_alpha;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

public class TableActivity2 extends AppCompatActivity {

    DatabaseManager dm;
    private EditText eId, eNama, eBuku, ePenerbit, eTanggal, ePoin;
    private Button bBaru, bSimpan, bUbah, bHapus;
    private LinearLayout tabel4data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crudtable); // Pastikan layout ini ada

        dm = new DatabaseManager(this);

        // Inisialisasi elemen UI
        eNama = findViewById(R.id.etNamaPengguna);
        eBuku = findViewById(R.id.etNamaBuku);
        ePenerbit = findViewById(R.id.etPenerbit);
        eTanggal = findViewById(R.id.etTanggalBaca);
        ePoin = findViewById(R.id.etPoin);
        bSimpan = findViewById(R.id.btnSimpan);
        tabel4data = findViewById(R.id.table_data);

        // Event button bisa kamu tambahkan di sini jika diperlukan

        updateTable();
    }

    protected void simpanTable() {
        try {
            String nama = eNama.getText().toString();

            dm.addRow(
                    nama,
                    eBuku.getText().toString(),
                    ePenerbit.getText().toString(),
                    eTanggal.getText().toString(),
                    Integer.parseInt(ePoin.getText().toString())
            );

            Toast.makeText(this, nama + " berhasil disimpan", Toast.LENGTH_SHORT).show();
            updateTable();
            kosongkanField();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal simpan: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    protected void ubahTable() {
        try {
            int id = Integer.parseInt(eId.getText().toString());
            String nama = eNama.getText().toString();

            dm.updateRecord(id,
                    nama,
                    eBuku.getText().toString(),
                    ePenerbit.getText().toString(),
                    eTanggal.getText().toString(),
                    Integer.parseInt(ePoin.getText().toString()));

            Toast.makeText(this, nama + " berhasil diubah", Toast.LENGTH_SHORT).show();
            updateTable();
            kosongkanField();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal ubah: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    protected void hapusTable() {
        try {
            int id = Integer.parseInt(eId.getText().toString());
            dm.deleteRecord(id);

            Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
            updateTable();
            kosongkanField();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal hapus: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    protected void kosongkanField() {
        eId.setText("");
        eNama.setText("");
        eBuku.setText("");
        ePenerbit.setText("");
        eTanggal.setText("");
        ePoin.setText("");
    }

    protected void updateTable() {
        tabel4data.removeAllViews();
        ArrayList<ArrayList<Object>> data = dm.ambilSemuaBaris();

        for (ArrayList<Object> baris : data) {
            TableRow tabelBaris = new TableRow(this);

            for (Object obj : baris) {
                TextView txt = new TextView(this);
                txt.setTextSize(16);
                txt.setPadding(10, 10, 10, 10);
                txt.setText(obj.toString());
                tabelBaris.addView(txt);
            }

            tabel4data.addView(tabelBaris);
        }
    }
}
