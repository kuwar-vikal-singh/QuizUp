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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.axel.quizup.Model.User;
import com.axel.quizup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText edName, edEmail, edPassword;
    private Button regBtn;
    private ProgressBar loadingProgressBar;
    private boolean isPasswordVisible = false;
    private TextView login;

    // Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in, redirect to MainActivity
            startActivity(new Intent(SignupActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_signup);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        hook();  // Initialize views

        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(view -> onBackPressed());

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

        regBtn.setOnClickListener(view -> {
            registerUser();
        });
    }

    private void registerUser() {
        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the ProgressBar and disable the button
        loadingProgressBar.setVisibility(View.VISIBLE);
        regBtn.setEnabled(false);

        // Register the user in Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // User registration successful
                Log.d("SignupActivity", "User registered successfully");

                // Save user info to Firebase Realtime Database
                saveUserDetails(task.getResult().getUser().getUid(), name, email);

                Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                // Redirect to MainActivity after registration
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                finish();
            } else {
                // User registration failed
                Log.e("SignupActivity", "User registration failed", task.getException());
                Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }

            // Hide the ProgressBar and re-enable the button
            loadingProgressBar.setVisibility(View.GONE);
            regBtn.setEnabled(true);
        });
    }

    private void saveUserDetails(String userId, String name, String email) {
        // Create a user object
        User user = new User(name, email, "", 0);  // Default score 0, password not saved

        // Save the user details to the database
        databaseRef.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("SignupActivity", "User data saved successfully");
            } else {
                Log.e("SignupActivity", "Failed to save user data", task.getException());
            }
        });
    }

    private void hook() {
        edName = findViewById(R.id.nameEditText);
        edEmail = findViewById(R.id.emailEditText);
        edPassword = findViewById(R.id.passwordEditText);
        regBtn = findViewById(R.id.registerButton);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
    }
}
