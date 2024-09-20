package com.axel.quizup.Ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.axel.quizup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText edPassword, edEmail;
    private Button loginBtn;
    private ProgressBar loadingProgressBar; // Declare ProgressBar
    private boolean isPasswordVisible = false;
    private TextView signUp;

    // Firebase instance
    private FirebaseAuth mAuth;

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Hook up views
        hook();

        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        // Password visibility toggle
        edPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (edPassword.getRight() - edPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (isPasswordVisible) {
                        edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                    } else {
                        edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        edPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                    }
                    isPasswordVisible = !isPasswordVisible;
                    edPassword.setSelection(edPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        // Handle login button click
        loginBtn.setOnClickListener(view -> loginUser());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginUser() {
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the ProgressBar and hide the login button
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingProgressBar.setIndeterminate(true);  // If you want indeterminate style
        loginBtn.setVisibility(View.GONE);  // Hide the login button

        // Authenticate the user using Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide the ProgressBar after login attempt
                    loadingProgressBar.setVisibility(View.GONE);
                    loginBtn.setVisibility(View.VISIBLE);  // Show the login button again

                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        Log.d("LoginActivity", "signInWithEmail:success");

                        // Redirect to MainActivity after successful login
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed. Please check your credentials.", Toast.LENGTH_SHORT).show();

                    }
                });
    }


    private void hook() {
        edEmail = findViewById(R.id.emailEditText);
        edPassword = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        loadingProgressBar = findViewById(R.id.loadingProgressBar); // Initialize ProgressBar
        signUp = findViewById(R.id.signUp);
    }
}
