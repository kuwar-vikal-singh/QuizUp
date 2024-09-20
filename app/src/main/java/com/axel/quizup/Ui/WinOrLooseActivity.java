package com.axel.quizup.Ui;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.axel.quizup.ProgressUi.CircularProgressBar;
import com.axel.quizup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WinOrLooseActivity extends AppCompatActivity {

    TextView playerName,category,titleTxt;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    Button playAgain;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_win_or_loose);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        category =findViewById(R.id.categoryName);
        playerName = findViewById(R.id.playerName);
        playAgain = findViewById(R.id.playAgain);
        titleTxt  = findViewById(R.id.titleTxt);



        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Fetch the user's UID
            String uid = currentUser.getUid();

            // Fetch the user's data from the "Users" node in the database
            fetchUserDetails(uid);
        } else {
            // If no user is logged in, show a default message
            playerName.setText("User");
        }


        Intent intent = getIntent();
        int showScore = intent.getIntExtra("showScore", 0);
        String categoryName = intent.getStringExtra("categoryName");

        CircularProgressBar progressBar = findViewById(R.id.semiCircularProgressBar);

        ObjectAnimator progressAnimator = ObjectAnimator.ofFloat(progressBar, "progress", 0, showScore);
        progressAnimator.setDuration(2000); // 2 seconds duration
        progressAnimator.start();
        category.setText(categoryName+" Quiz");

        titleSet(showScore);

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });




    }

    private void titleSet(int score) {
        if (score >= 90) {
            titleTxt.setText("Excellent");
        } else if (score >= 75) {
            titleTxt.setText("Very Good");
        } else if (score >= 50) {
            titleTxt.setText("Good");
        } else if (score >= 30) {
            titleTxt.setText("Needs Improvement");
        } else {
            titleTxt.setText("Try Again");
        }
    }


    private void fetchUserDetails(String uid) {
        // Reference to the "Users" node in Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Get the user's name
                    String name = snapshot.child("name").getValue(String.class);

                    if (name != null) {
                        // Set the greeting message with the user's name
                        playerName.setText(name);
                    } else {
                        playerName.setText("User");
                    }
                } else {
                    // Handle the case where the user data is not found
//                    Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
//                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "onCancelled: " + error.getMessage());
            }
        });
    }
}