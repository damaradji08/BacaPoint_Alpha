package com.example.bacapoint_alpha;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.View;
import java.util.ArrayList;

public class TableActivity extends AppCompatActivity {

    DatabaseManager dm;
    private TableLayout tabel4data;
    private Button btnUbah, btnHapus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        tabel4data = findViewById(R.id.table_data);

        // Find buttons (if they exist in your layout)
        try {
            btnUbah = findViewById(R.id.btnUbah);
            btnHapus = findViewById(R.id.btnHapus);

            // Add button click listeners here if needed
            // Currently these buttons are in your layout but don't have functionality
        } catch (Exception e) {
            // Button might not exist in the layout
        }

        // Initialize and open database
        dm = new DatabaseManager(this);
        dm.open();

        // Update table with data
        updateTable();
    }

    protected void updateTable() {
        try {
            // Clear existing rows (except header if it exists)
            if (tabel4data.getChildCount() > 0) {
                tabel4data.removeAllViews();
            }

            // Get all data once
            ArrayList<ArrayList<Object>> data = dm.ambilSemuaBaris();

            if (data.isEmpty()) {
                // Show message if no data
                Toast.makeText(this, "Tidak ada data untuk ditampilkan", Toast.LENGTH_SHORT).show();
                return;
            }

            // Loop through all rows once and create table rows
            for (ArrayList<Object> row : data) {
                TableRow tableRow = new TableRow(this);
                tableRow.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));

                // Process each column in the current row
                for (int i = 0; i < row.size(); i++) {
                    TextView textView = new TextView(this);
                    textView.setText(row.get(i).toString());
                    textView.setPadding(16, 16, 16, 16);

                    // Set text size and background
                    textView.setTextSize(12);
                    textView.setBackgroundResource(android.R.color.white);

                    // Add to row with appropriate weight
                    TableRow.LayoutParams params;
                    if (i == 0) { // ID column
                        params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    } else { // Other columns
                        params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database connection
        if (dm != null) {
            dm.close();
        }
    }
}
