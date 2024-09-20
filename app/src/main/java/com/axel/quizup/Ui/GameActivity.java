package com.axel.quizup.Ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.axel.quizup.Model.Question;
import com.axel.quizup.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    private TextView questionTextView;
    private TextView option1, option2, option3, option4;
    private TextView timerTextView;
    private DatabaseReference databaseReference;
    private String categoryName;
    private List<Question> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private ImageView cross;
    private Button nextButton;
    private CountDownTimer countDownTimer;
    private static final long TIMER_DURATION = 30000; // 30 seconds
    private static final long TIMER_INTERVAL = 1000; // 1 second
    int score = 0;
    int totalQuestions = 0;
    int showScore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        questionTextView = findViewById(R.id.questionTextView);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        timerTextView = findViewById(R.id.timerTextView);
        nextButton = findViewById(R.id.nextButton);
        cross = findViewById(R.id.cross);
        categoryName = getIntent().getStringExtra("categoryName");
        Log.d("GameActivity", "Category Name: " + categoryName);

        if (categoryName == null) {
            Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Questions").child(categoryName);

        loadQuestions();
        fetchTotalQuestions(categoryName);
        nextButton.setOnClickListener(v -> moveToNextQuestion());
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void fetchTotalQuestions(String category) {
        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference().child("Categories").child(category);

        categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    totalQuestions = snapshot.child("totalQuestion").getValue(Integer.class);
//                    Log.d("GameActivity", "Total Questions: " + totalQuestions);
//                    Toast.makeText(GameActivity.this, "Total Questions: " + totalQuestions, Toast.LENGTH_SHORT).show();

                    // Load questions only after totalQuestions is set
                    loadQuestions();
                } else {
//                    Toast.makeText(GameActivity.this, "Category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(GameActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void loadQuestions() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionList.clear();
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    String questionText = questionSnapshot.child("question").getValue(String.class);
                    String option1 = questionSnapshot.child("options").child("a").getValue(String.class);
                    String option2 = questionSnapshot.child("options").child("b").getValue(String.class);
                    String option3 = questionSnapshot.child("options").child("c").getValue(String.class);
                    String option4 = questionSnapshot.child("options").child("d").getValue(String.class);
                    String correctAnswer = questionSnapshot.child("answer").getValue(String.class);

                    Question question = new Question(questionText, option1, option2, option3, option4, correctAnswer);
                    questionList.add(question);
                }

                Collections.shuffle(questionList);

                if (!questionList.isEmpty()) {
                    displayQuestion(questionList.get(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GameActivity.this, "Failed to load questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuestion(Question question) {
        questionTextView.setText(question.getQuestionText());
        option1.setText(question.getOption1());
        option2.setText(question.getOption2());
        option3.setText(question.getOption3());
        option4.setText(question.getOption4());

        startTimer();

        option1.setOnClickListener(v -> handleAnswerClick(option1, question.getOption1(), question.getCorrectAnswer()));
        option2.setOnClickListener(v -> handleAnswerClick(option2, question.getOption2(), question.getCorrectAnswer()));
        option3.setOnClickListener(v -> handleAnswerClick(option3, question.getOption3(), question.getCorrectAnswer()));
        option4.setOnClickListener(v -> handleAnswerClick(option4, question.getOption4(), question.getCorrectAnswer()));
    }

    private void handleAnswerClick(TextView selectedOptionView, String selectedOption, String correctAnswer) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        disableOptionClicks(); // Disable all options after an answer is selected

//        Toast.makeText(this, "selected Option"+selectedOption, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "correct Ans" +correctAnswer, Toast.LENGTH_SHORT).show();

        if (selectedOption.equals(correctAnswer)) {
            selectedOptionView.setBackgroundResource(R.drawable.right_bg);
            score++;
        } else {
            selectedOptionView.setBackgroundResource(R.drawable.wrong_bg);
            highlightCorrectAnswer(correctAnswer); // Highlight the correct answer
        }
    }

    private void highlightCorrectAnswer(String correctAnswer) {
        if (option1.getText().equals(correctAnswer)) {
            option1.setBackgroundResource(R.drawable.right_bg);
        } else if (option2.getText().equals(correctAnswer)) {
            option2.setBackgroundResource(R.drawable.right_bg);
        } else if (option3.getText().equals(correctAnswer)) {
            option3.setBackgroundResource(R.drawable.right_bg);
        } else if (option4.getText().equals(correctAnswer)) {
            option4.setBackgroundResource(R.drawable.right_bg);
        }
    }

    private void disableOptionClicks() {
        option1.setEnabled(false);
        option2.setEnabled(false);
        option3.setEnabled(false);
        option4.setEnabled(false);
    }
    private void enableOptionClicks() {
        option1.setEnabled(true);
        option2.setEnabled(true);
        option3.setEnabled(true);
        option4.setEnabled(true);
    }
    private void defulatOptionBg(){
        option1.setBackgroundResource(R.drawable.option_bg);
        option2.setBackgroundResource(R.drawable.option_bg);
        option3.setBackgroundResource(R.drawable.option_bg);
        option4.setBackgroundResource(R.drawable.option_bg);
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                timerTextView.setText(String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60));
            }

            @Override
            public void onFinish() {
                moveToNextQuestion();
            }
        }.start();
    }

    private int calculateScore(int totalQuestions , int score){
        int finalscore;
        score = score*100;
        return finalscore = score/totalQuestions;
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            displayQuestion(questionList.get(currentQuestionIndex));
            enableOptionClicks();
            defulatOptionBg();

        } else {
            Toast.makeText(GameActivity.this, "Quiz finished", Toast.LENGTH_SHORT).show();
            disableOptionClicks();
            defulatOptionBg();
            countDownTimer.cancel();
            startwinActivity();
        }

    }

    private void startwinActivity() {
        showScore = calculateScore(totalQuestions,score);
        Intent intent = new Intent(GameActivity.this, WinOrLooseActivity.class);
        intent.putExtra("showScore", showScore);  // Passing the score
//            intent.putExtra("userName", userName);    // Passing the user name
        intent.putExtra("categoryName", categoryName);  // Passing the category name
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startwinActivity();
        super.onBackPressed();

    }
}
