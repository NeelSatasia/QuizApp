package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditQuiz extends AppCompatActivity {

    private RelativeLayout relLay;
    private QuizInfo editableQuiz;
    private EditText quizNameLabel;
    private ArrayList<RelativeLayout> questionsRelLay;
    //private ArrayList<Question> questions;
    private Button addQuestionBtn;
    private Button cancelQuestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_quiz);

        relLay = findViewById(R.id.editQuizRelLay);
        questionsRelLay = new ArrayList<RelativeLayout>();

        addQuestionBtn = new Button(this);
        addQuestionBtn.setId(View.generateViewId());
        addQuestionBtn.setText("Add");

        cancelQuestionBtn = new Button(this);
        cancelQuestionBtn.setId(View.generateViewId());
        cancelQuestionBtn.setText("Cancel");

        SharedPreferences sharedPreferences = getSharedPreferences("EditQuiz", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("EditQuiz", null);
        Type type = new TypeToken<QuizInfo>() {}.getType();
        editableQuiz = gson.fromJson(json, type);

        quizNameLabel = new EditText(this);
        quizNameLabel.setId(View.generateViewId());
        quizNameLabel.setText(editableQuiz.quizName);
        quizNameLabel.setHint("Quiz Name");
        quizNameLabel.setTextSize(30);

        RelativeLayout.LayoutParams quizNameLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        quizNameLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quizNameLay.topMargin = 30;
        relLay.addView(quizNameLabel, quizNameLay);

        for(int i = 0; i < editableQuiz.questionList.size(); i++) {
            RelativeLayout questionRelLay = new RelativeLayout(this);
            questionRelLay.setId(View.generateViewId());

            EditText question = new EditText(this);
            question.setId(View.generateViewId());
            question.setText(editableQuiz.questionList.get(i).question);
            question.setHint("Question " + (i + 1));
            question.setTextSize(20);

            RelativeLayout.LayoutParams questionTextRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            questionTextRelLayParams.leftMargin = 20;
            questionTextRelLayParams.rightMargin = 10;
            questionRelLay.addView(question, questionTextRelLayParams);

            EditText[] options = new EditText[editableQuiz.questionList.get(i).options.length];

            for(int j = 0; j < options.length; j++) {
                options[j] = new EditText(this);
                options[j].setId(View.generateViewId());
                options[j].setText(editableQuiz.questionList.get(i).options[j]);
                options[j].setTextSize(20);

                RelativeLayout.LayoutParams optionRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(j == 0) {
                    optionRelLayParams.addRule(RelativeLayout.BELOW, question.getId());
                } else {
                    optionRelLayParams.addRule(RelativeLayout.BELOW, options[j - 1].getId());
                }
                optionRelLayParams.leftMargin = 30;
                optionRelLayParams.rightMargin = 10;

                questionRelLay.addView(options[j], optionRelLayParams);
            }

            TextView correctAnswerLabel = new TextView(this);
            correctAnswerLabel.setId(View.generateViewId());
            correctAnswerLabel.setText("Correct Answer:");

            RelativeLayout.LayoutParams correctAnsLabRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            correctAnsLabRelLayParams.addRule(RelativeLayout.BELOW, options[options.length - 1].getId());
            correctAnsLabRelLayParams.topMargin = 25;
            questionRelLay.addView(correctAnswerLabel, correctAnsLabRelLayParams);

            RadioButton[] optionRadioBtns = new RadioButton[options.length];

            for(int j = 0; j < optionRadioBtns.length; j++) {
                optionRadioBtns[j] = new RadioButton(this);
                optionRadioBtns[j].setId(View.generateViewId());
                optionRadioBtns[j].setText((j + 1) + "");

                RelativeLayout.LayoutParams optionRadioBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(j == 0) {
                    optionRadioBtnLayParams.addRule(RelativeLayout.RIGHT_OF, correctAnswerLabel.getId());
                } else {
                    optionRadioBtnLayParams.addRule(RelativeLayout.RIGHT_OF, optionRadioBtns[j - 1].getId());
                }

                questionRelLay.addView(optionRadioBtns[j], optionRadioBtnLayParams);
            }

            RelativeLayout.LayoutParams questionRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i == 0) {
                questionRelLayParams.addRule(RelativeLayout.BELOW, quizNameLabel.getId());
            } else {
                questionRelLayParams.addRule(RelativeLayout.BELOW, questionsRelLay.get(questionsRelLay.size() - 1).getId());
            }

            questionsRelLay.add(questionRelLay);
            relLay.addView(questionsRelLay.get(i), questionRelLayParams);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditQuiz.this, MainActivity.class);
        startActivity(intent);
    }
}