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

    ArrayList<Object>[] userAnswers;

    TextView quizNameLabel;
    TextView questionLabel;
    TextView questionNumber;
    TextView questionTracker;
    RelativeLayout questionRelLay;
    Button nextQues;
    Button backQues;
    Button submitQuizBtn;

    int currentQuestionIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        loadQuiz();

        userAnswers = new ArrayList[quiz.questionList.size()];

        for(int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = new ArrayList<Object>();

            if(quiz.questionList.get(i).mcQuestion == false) {
                userAnswers[i].add((String) "");
            }
        }

        quizNameLabel = findViewById(R.id.quizTitleLabel);
        quizNameLabel.setText(quiz.quizName);

        questionLabel = findViewById(R.id.questionLabel);
        questionLabel.setText(quiz.questionList.get(0).question);

        questionNumber = findViewById(R.id.quesNum);
        questionTracker = findViewById(R.id.questTracker);

        questionRelLay = findViewById(R.id.mainRelLay);

        nextQues = findViewById(R.id.nextQuesBtn);
        backQues = findViewById(R.id.backQuesBtn);
        submitQuizBtn = findViewById(R.id.sbtBtn);

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

        submitQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswers();
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
        questionTracker.setText((index + 1) + " of " + quiz.questionList.size());
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

                if(userAnswers[index].contains(i)) {
                    optionsCB[i].setChecked(true);
                }

                int j = i;
                optionsCB[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(optionsCB[j].isChecked()) {
                            userAnswers[index].add((Integer) j);
                        } else if(userAnswers[index].contains((Integer) j)){
                            userAnswers[index].remove((Integer) j);
                        }
                    }
                });
            }
        } else {
            EditText frAns = new EditText(this);
            frAns.setHint("Type your answer here");

            RelativeLayout.LayoutParams frLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            frLayParams.leftMargin = 20;

            questionRelLay.addView(frAns, frLayParams);

            frAns.setText((String) userAnswers[index].get(0));

            frAns.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    userAnswers[index].set(0, frAns.getText().toString());
                }
            });
        }
    }

    public void checkAnswers() {
        for(int i = 0; i < userAnswers.length; i++) {
            if(quiz.questionList.get(i).mcQuestion) {
                for(int j = 0; j < userAnswers[i].size(); j++) {
                    if(quiz.questionList.get(i).correctAnswers.contains(userAnswers[i].get(j))) {

                    } else {

                    }
                }
            } else {
                if(userAnswers[i].get(0).equals(quiz.questionList.get(i).frCorrectAnswer) == false) {

                }
            }
        }
    }
}