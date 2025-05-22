package com.example.bacapoint_alpha;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DeleteActivity extends AppCompatActivity {

    DatabaseManager dm;
    TextView tvBuku, tvPenerbit, tvTanggal, tvGenre;
    Button bDelete, bCancel;
    int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        // Initialize database
        dm = new DatabaseManager(this);
        dm.open();

        // Initialize UI components
        tvBuku = findViewById(R.id.tvNamaBukuDelete);
        tvPenerbit = findViewById(R.id.tvPenerbitDelete);
        tvTanggal = findViewById(R.id.tvTanggalBacaDelete);
        tvGenre = findViewById(R.id.tvGenreDelete);
        bDelete = findViewById(R.id.btnDelete);
        bCancel = findViewById(R.id.btnCancelDelete);

        // Get data from intent
        Intent intent = getIntent();
        bookId = intent.getIntExtra("ID", -1);
        String namaBuku = intent.getStringExtra("NAMA_BUKU");
        String penerbit = intent.getStringExtra("PENERBIT");
        String tanggal = intent.getStringExtra("TANGGAL");
        String genre = intent.getStringExtra("GENRE");

        // Display the data to be deleted
        if (bookId != -1) {
            tvBuku.setText("Nama Buku: " + namaBuku);
            tvPenerbit.setText("Penerbit: " + penerbit);
            tvTanggal.setText("Tanggal: " + tanggal);
            tvGenre.setText("Genre: " + genre);
        } else {
            Toast.makeText(this, "Error: Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Delete button click listener
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog
                new AlertDialog.Builder(DeleteActivity.this)
                        .setTitle("Konfirmasi Hapus")
                        .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    dm.deleteRecord(bookId);
                                    Toast.makeText(DeleteActivity.this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();

                                    // Return to table activity
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("DELETED", true);
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                } catch (Exception e) {
                                    Toast.makeText(DeleteActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
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