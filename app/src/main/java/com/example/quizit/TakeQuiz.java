package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TakeQuiz extends AppCompatActivity {

    QuizInfo quiz;

    TextView quizNameLabel;

    TextView questionLabel;

    Button option1Btn;
    Button option2Btn;
    Button option3Btn;
    Button option4Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        loadQuiz();

        quizNameLabel = findViewById(R.id.quizTitleLabel);
        quizNameLabel.setText(quiz.quizName);

        questionLabel = findViewById(R.id.questionLabel);
        questionLabel.setText(quiz.questionList.get(0).question);

        option1Btn = findViewById(R.id.option1Btn);
        option2Btn = findViewById(R.id.option2Btn);
        option3Btn = findViewById(R.id.option3Btn);
        option4Btn = findViewById(R.id.option4Btn);

        option1Btn.setText(quiz.questionList.get(0).options[0]);
        option2Btn.setText(quiz.questionList.get(0).options[1]);
        option3Btn.setText(quiz.questionList.get(0).options[2]);
        option4Btn.setText(quiz.questionList.get(0).options[3]);
    }

    public void loadQuiz() {
        SharedPreferences sharedPreferences = getSharedPreferences("TakeQuiz", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Quiz", null);
        Type type = new TypeToken<QuizInfo>() {}.getType();
        quiz = gson.fromJson(json, type);
    }
}