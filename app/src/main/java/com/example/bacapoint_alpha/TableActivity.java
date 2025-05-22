// Updated TableActivity.java - No programmatic header + Custom font
package com.example.bacapoint_alpha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import java.util.ArrayList;

public class TableActivity extends AppCompatActivity {

    DatabaseManager dm;
    private TableLayout tabel4data;
    private Button btnUbah, btnHapus;
    private ArrayList<ArrayList<Object>> tableData;
    private int selectedRowIndex = -1;
    private TableRow selectedRow = null;
    private static final int UPDATE_REQUEST_CODE = 1;
    private static final int DELETE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tabel4data = findViewById(R.id.table_data);
        btnUbah = findViewById(R.id.btnUbah);
        btnHapus = findViewById(R.id.btnHapus);

        // Set the Table's background to white
        tabel4data.setBackgroundColor(Color.WHITE);
        tabel4data.setStretchAllColumns(true);
        tabel4data.setShowDividers(TableLayout.SHOW_DIVIDER_NONE);

        // Initialize and open database
        dm = new DatabaseManager(this);
        dm.open();

        // Initially disable buttons
        btnUbah.setEnabled(false);
        btnHapus.setEnabled(false);
        btnUbah.setAlpha(0.5f);
        btnHapus.setAlpha(0.5f);

        // Button click listeners
        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRowIndex >= 0 && selectedRowIndex < tableData.size()) {
                    ArrayList<Object> selectedData = tableData.get(selectedRowIndex);

                    Intent intent = new Intent(TableActivity.this, UpdateActivity.class);
                    intent.putExtra("ID", (Integer) selectedData.get(0));
                    intent.putExtra("NAMA_BUKU", selectedData.get(1).toString());
                    intent.putExtra("PENERBIT", selectedData.get(2).toString());
                    intent.putExtra("TANGGAL", selectedData.get(3).toString());
                    intent.putExtra("GENRE", selectedData.get(4).toString());

                    startActivityForResult(intent, UPDATE_REQUEST_CODE);
                } else {
                    Toast.makeText(TableActivity.this, "Pilih baris yang ingin diubah", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRowIndex >= 0 && selectedRowIndex < tableData.size()) {
                    ArrayList<Object> selectedData = tableData.get(selectedRowIndex);

                    Intent intent = new Intent(TableActivity.this, DeleteActivity.class);
                    intent.putExtra("ID", (Integer) selectedData.get(0));
                    intent.putExtra("NAMA_BUKU", selectedData.get(1).toString());
                    intent.putExtra("PENERBIT", selectedData.get(2).toString());
                    intent.putExtra("TANGGAL", selectedData.get(3).toString());
                    intent.putExtra("GENRE", selectedData.get(4).toString());

                    startActivityForResult(intent, DELETE_REQUEST_CODE);
                } else {
                    Toast.makeText(TableActivity.this, "Pilih baris yang ingin dihapus", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Update table with data
        updateTable();
    }

    protected void updateTable() {
        try {
            // Clear existing data rows (but keep the XML header)
            // Remove all views except the first one (which is your XML header row)
            while (tabel4data.getChildCount() > 1) {
                tabel4data.removeViewAt(1);
            }

            // Reset selection
            selectedRowIndex = -1;
            selectedRow = null;
            btnUbah.setEnabled(false);
            btnHapus.setEnabled(false);
            btnUbah.setAlpha(0.5f);
            btnHapus.setAlpha(0.5f);

            // Get all data once
            tableData = dm.ambilSemuaBaris();

            if (tableData.isEmpty()) {
                Toast.makeText(this, "Tidak ada data untuk ditampilkan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load custom font (change this to your preferred font)
            Typeface customFont = null;
            try {
                // Try to load Montserrat font (change to your preferred font)
                customFont = ResourcesCompat.getFont(this, R.font.roboto_medium);
            } catch (Exception e) {
                // If custom font fails, use default
                customFont = Typeface.DEFAULT;
            }

            // Define column weights to match your XML header
            float[] weights = {1f, 2f, 2f, 2f, 2f};

            // Process data rows
            for (int rowIndex = 0; rowIndex < tableData.size(); rowIndex++) {
                ArrayList<Object> row = tableData.get(rowIndex);
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                // Set white background for the row
                tableRow.setBackgroundColor(Color.WHITE);

                // Make row clickable
                final int finalRowIndex = rowIndex;
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectRow(finalRowIndex, (TableRow) v);
                    }
                });

                // No spacing between rows
                TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT);
                trParams.setMargins(0, 0, 0, 0);
                tableRow.setLayoutParams(trParams);

                // Process each column in the current row
                for (int i = 0; i < row.size(); i++) {
                    TextView textView = new TextView(this);
                    textView.setText(row.get(i).toString());
                    textView.setPadding(16, 16, 16, 16);
                    textView.setTextSize(12);
                    textView.setTextColor(Color.BLACK);
                    textView.setBackgroundColor(Color.WHITE);
                    textView.setGravity(Gravity.CENTER);

                    // Apply custom font
                    if (customFont != null) {
                        textView.setTypeface(customFont);
                    }

                    // Add to row with appropriate weight
                    TableRow.LayoutParams params = new TableRow.LayoutParams(
                            0, TableRow.LayoutParams.WRAP_CONTENT, weights[i]);
                    params.setMargins(0, 0, 0, 0);
                    textView.setLayoutParams(params);

                    tableRow.addView(textView);
                }

                // Add completed row to table
                tabel4data.addView(tableRow);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error updating table: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void selectRow(int rowIndex, TableRow row) {
        // Deselect previous row
        if (selectedRow != null) {
            selectedRow.setBackgroundColor(Color.WHITE);
            for (int i = 0; i < selectedRow.getChildCount(); i++) {
                selectedRow.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
        }

        // Select new row
        selectedRowIndex = rowIndex;
        selectedRow = row;
        row.setBackgroundColor(Color.parseColor("#E3F2FD")); // Light blue selection
        for (int i = 0; i < row.getChildCount(); i++) {
            row.getChildAt(i).setBackgroundColor(Color.parseColor("#E3F2FD"));
        }

        // Enable buttons
        btnUbah.setEnabled(true);
        btnHapus.setEnabled(true);
        btnUbah.setAlpha(1.0f);
        btnHapus.setAlpha(1.0f);

        Toast.makeText(this, "Baris dipilih. Pilih Ubah atau Hapus.", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == UPDATE_REQUEST_CODE || requestCode == DELETE_REQUEST_CODE) {
                // Refresh the table after update or delete
                updateTable();
                Toast.makeText(this, "Tabel diperbarui", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dm != null) {
            dm.close();
        }
    }
}