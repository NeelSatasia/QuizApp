package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TakeQuiz extends AppCompatActivity {

    QuizInfo quiz;

    TextView quizNameLabel;
    TextView questionLabel;
    TextView questionNumber;
    RelativeLayout questionRelLay;
    Button nextQues;
    Button backQues;

    int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        loadQuiz();

        quizNameLabel = findViewById(R.id.quizTitleLabel);
        quizNameLabel.setText(quiz.quizName);

        questionLabel = findViewById(R.id.questionLabel);
        questionLabel.setText(quiz.questionList.get(0).question);

        questionNumber = findViewById(R.id.quesNum);

        questionRelLay = findViewById(R.id.mainRelLay);

        nextQues = findViewById(R.id.nextQuesBtn);
        backQues = findViewById(R.id.backQuesBtn);

        displayQuestion(currentQuestionIndex);

        nextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentQuestionIndex + 1 < quiz.questionList.size()) {
                    currentQuestionIndex++;
                    displayQuestion(currentQuestionIndex);
                }
            }
        });

        backQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentQuestionIndex - 1 >= 0) {
                    currentQuestionIndex--;
                    displayQuestion(currentQuestionIndex);
                }
            }
        });
    }

    public void loadQuiz() {
        SharedPreferences sharedPreferences = getSharedPreferences("TakeQuiz", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Quiz", null);
        Type type = new TypeToken<QuizInfo>() {}.getType();
        quiz = gson.fromJson(json, type);
    }

    public void displayQuestion(int index) {
        questionRelLay.removeAllViews();

        questionNumber.setText((index + 1) + ".");
        questionLabel.setText(quiz.questionList.get(index).question);

        if(quiz.questionList.get(index).mcQuestion) {
            CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(index).options.length];

            for(int i = 0; i < optionsCB.length; i++) {
                optionsCB[i] = new CheckBox(this);
                optionsCB[i].setText(quiz.questionList.get(index).options[i]);
                optionsCB[i].setId(View.generateViewId());

                RelativeLayout.LayoutParams optionLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                if(i < optionsCB.length - 1) {
                    optionLayParams.bottomMargin = 20;
                } else {
                    optionLayParams.bottomMargin = 200;
                }

                if(i > 0) {
                    optionLayParams.addRule(RelativeLayout.BELOW, optionsCB[i - 1].getId());
                }

                questionRelLay.addView(optionsCB[i], optionLayParams);

                optionsCB[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    }
                });
            }
        } else {
            EditText frAns = new EditText(this);
            frAns.setHint("Type your answer here");

            RelativeLayout.LayoutParams frLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            frLayParams.leftMargin = 20;

            questionRelLay.addView(frAns, frLayParams);

            frAns.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
}