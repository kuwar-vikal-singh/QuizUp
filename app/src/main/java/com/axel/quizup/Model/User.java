package com.axel.quizup.Model;

public class User {

    public String name, email, password;
    public int score;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String password, int score) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.score = score;
    }

    // Getters and setters (optional, for use in future)
}
