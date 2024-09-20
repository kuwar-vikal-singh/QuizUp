package com.axel.quizup.Ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.quizup.Adapter.CategoryAdapter;
import com.axel.quizup.Model.Category;
import com.axel.quizup.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private DatabaseReference databaseReference;
    private TextView greetingTextView;  // Add a TextView for the greeting
    private FirebaseAuth mAuth;  // FirebaseAuth instance

    private ImageView logoutImg;
    Button btnDilogCancle,btnDilogLogout;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dilog_box);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dilog_bg));
        dialog.setCancelable(false);

        btnDilogCancle = dialog.findViewById(R.id.dilogCancle);
        btnDilogLogout = dialog.findViewById(R.id.dilogLogout);

        mAuth = FirebaseAuth.getInstance();

        greetingTextView = findViewById(R.id.greetingTextView);
        logoutImg = findViewById(R.id.logoutImg);

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Fetch the user's UID
            String uid = currentUser.getUid();

            // Fetch the user's data from the "Users" node in the database
            fetchUserDetails(uid);
        } else {
            // If no user is logged in, show a default message
            greetingTextView.setText("Hi User");
        }

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);

        // Set GridLayoutManager with 2 columns (change to desired number)
        int numberOfColumns = 2;
        categoryRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Categories");

        // Load categories
        loadCategories();

        logoutImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        btnDilogLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mAuth.signOut();
               Intent intent = new Intent(MainActivity.this,LoginActivity.class);
               startActivity(intent);
               finish();
            }
        });
        btnDilogCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
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
                        greetingTextView.setText("Hi " + name);
                    } else {
                        greetingTextView.setText("Hi User");
                    }
                } else {
                    // Handle the case where the user data is not found
                    Toast.makeText(MainActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "onCancelled: " + error.getMessage());
            }
        });
    }

    private void loadCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> categoryList = new ArrayList<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                categoryAdapter = new CategoryAdapter(MainActivity.this, categoryList);
                categoryRecyclerView.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
