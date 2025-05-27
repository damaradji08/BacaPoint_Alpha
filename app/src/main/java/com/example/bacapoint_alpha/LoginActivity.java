package com.example.bacapoint_alpha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize SharedPreferences for storing login state
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Check if user is already logged in
        if (isUserLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleForgotPassword();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegister();
            }
        });
    }

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Reset any previous errors
        etUsername.setError(null);
        etPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Validate password
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Kata sandi tidak boleh kosong");
            focusView = etPassword;
            cancel = true;
        } else if (password.length() < 6) {
            etPassword.setError("Kata sandi minimal 6 karakter");
            focusView = etPassword;
            cancel = true;
        }

        // Validate username/email
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Username atau email tidak boleh kosong");
            focusView = etUsername;
            cancel = true;
        }

        if (cancel) {
            // There was an error; focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show loading state
            btnLogin.setEnabled(false);
            btnLogin.setText("Memuat...");

            // Perform login validation
            performLogin(username, password);
        }
    }

    private void performLogin(String username, String password) {
        // TODO: Replace this with actual authentication logic
        // This could be a call to your API, Firebase Auth, etc.

        // Simulate network delay
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Demo credentials - replace with actual authentication
                if (isValidCredentials(username, password)) {
                    // Save login state
                    saveLoginState(username);

                    Toast.makeText(LoginActivity.this, "Login berhasil!", Toast.LENGTH_SHORT).show();
                    navigateToMainActivity();
                } else {
                    // Reset button state
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Masuk");

                    Toast.makeText(LoginActivity.this, "Username atau kata sandi salah", Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                }
            }
        }, 1500); // 1.5 second delay to simulate network request
    }

    private boolean isValidCredentials(String username, String password) {
        // TODO: Replace with actual authentication logic
        // This is just a demo - DO NOT use in production

        // Demo credentials
        return (username.equals("admin") && password.equals("123456")) ||
                (username.equals("user@example.com") && password.equals("password"));
    }

    private void saveLoginState(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("username", username);
        editor.putLong("loginTime", System.currentTimeMillis());
        editor.apply();
    }

    private boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
        Toast.makeText(this, "Fitur lupa kata sandi akan segera hadir", Toast.LENGTH_SHORT).show();

        // You can navigate to a forgot password activity or show a dialog
        // Intent intent = new Intent(this, ForgotPasswordActivity.class);
        // startActivity(intent);
    }

    private void navigateToRegister() {
        // TODO: Navigate to registration activity
        Toast.makeText(this, "Navigasi ke halaman pendaftaran", Toast.LENGTH_SHORT).show();

        // Intent intent = new Intent(this, RegisterActivity.class);
        // startActivity(intent);
    }

    private void navigateToMainActivity() {
        // TODO: Replace with your main activity class
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Disable back button on login screen or show exit confirmation
        showExitConfirmation();
    }

    private void showExitConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Keluar Aplikasi")
                .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    finishAffinity(); // Close all activities
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    // Method to logout (call this from other activities when needed)
    public static void logout(android.content.Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}