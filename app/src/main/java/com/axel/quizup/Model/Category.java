package com.axel.quizup.Model;

public class Category {
    private String categoryName;
    private String image;
    private String description;
    private int totalQuestion;  // Add this line

    public Category() {}  // Empty constructor for Firebase

    public Category(String categoryName, String image, String description, int totalQuestion) {
        this.categoryName = categoryName;
        this.image = image;
        this.description = description;
        this.totalQuestion = totalQuestion;  // Update constructor
    }

    public String getCategoryName() { return categoryName; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public int getTotalQuestion() { return totalQuestion; }  // Add getter
    public void setTotalQuestion(int totalQuestion) { this.totalQuestion = totalQuestion; }  // Add setter
}
