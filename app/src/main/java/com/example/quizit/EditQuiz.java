package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditQuiz extends AppCompatActivity {

    ScrollView scrollView;
    RelativeLayout relLay;
    QuizInfo editableQuiz;
    EditText quizNameLabel;
    ArrayList<RelativeLayout> questionsRelLay;
    Button addQuestionBtn;
    Button cancelQuestionBtn;
    Button editQuizBtn;
    LinearLayout addCancelLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.edit_quiz);

        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        relLay = new RelativeLayout(this);
        relLay.setLayoutParams((new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)));
        scrollView.addView(relLay);

        questionsRelLay = new ArrayList<RelativeLayout>();

        addQuestionBtn = new Button(this);
        addQuestionBtn.setId(View.generateViewId());
        addQuestionBtn.setText("Add");

        cancelQuestionBtn = new Button(this);
        cancelQuestionBtn.setId(View.generateViewId());
        cancelQuestionBtn.setText("Cancel");

        addCancelLay = new LinearLayout(this);
        addCancelLay.setId(View.generateViewId());
        addCancelLay.setOrientation(LinearLayout.HORIZONTAL);

        addCancelLay.addView(addQuestionBtn);
        addCancelLay.addView(cancelQuestionBtn);

        editQuizBtn = new Button(this);
        editQuizBtn.setId(View.generateViewId());
        editQuizBtn.setText("Edit");

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
        quizNameLabel.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams quizNameLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        quizNameLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quizNameLay.topMargin = 30;
        quizNameLay.bottomMargin = 20;
        relLay.addView(quizNameLabel, quizNameLay);

        for(int i = 0; i < editableQuiz.questionList.size(); i++) {
            addQuestionLayout(i);
        }

        RelativeLayout.LayoutParams addCancelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addCancelLayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if(questionsRelLay.size() == 0) {
            addCancelLayParams.addRule(RelativeLayout.BELOW, quizNameLabel.getId());
        } else {
            addCancelLayParams.addRule(RelativeLayout.BELOW, questionsRelLay.get(questionsRelLay.size() - 1).getId());
        }

        relLay.addView(addCancelLay, addCancelLayParams);

        RelativeLayout.LayoutParams editQuizBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editQuizBtnLayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        editQuizBtnLayParams.addRule(RelativeLayout.BELOW, addCancelLay.getId());
        editQuizBtnLayParams.bottomMargin = 100;

        relLay.addView(editQuizBtn, editQuizBtnLayParams);

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestionLayout(-1);
                alignButtons();
            }
        });

        cancelQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alignButtons();
            }
        });

        editQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setContentView(scrollView);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditQuiz.this, MainActivity.class);
        startActivity(intent);
    }

    public void addQuestionLayout(int index) {
        RelativeLayout questionRelLay = new RelativeLayout(this);
        questionRelLay.setId(View.generateViewId());

        EditText question = new EditText(this);
        question.setId(View.generateViewId());
        if(index >= 0) {
            question.setText(editableQuiz.questionList.get(index).question);
        }
        if(index >= 0) {
            question.setHint("Question " + (index + 1));
        } else {
            question.setHint("Question " + (editableQuiz.questionList.size() + 1));
        }
        question.setTextSize(25);

        RelativeLayout.LayoutParams questionTextRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        questionTextRelLayParams.leftMargin = 10;
        questionTextRelLayParams.rightMargin = 10;
        questionRelLay.addView(question, questionTextRelLayParams);

        int optionLen = 0;
        if(index >= 0) {
            optionLen = editableQuiz.questionList.get(index).options.length;
        } else {
            optionLen = 4;
        }

        EditText[] options = new EditText[optionLen];

        for(int j = 0; j < options.length; j++) {
            options[j] = new EditText(this);
            options[j].setId(View.generateViewId());
            if(index >= 0) {
                options[j].setText(editableQuiz.questionList.get(index).options[j]);
            }
            options[j].setHint("Option " + (j + 1));
            options[j].setTextSize(20);

            RelativeLayout.LayoutParams optionRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(j == 0) {
                optionRelLayParams.addRule(RelativeLayout.BELOW, question.getId());
            } else {
                optionRelLayParams.addRule(RelativeLayout.BELOW, options[j - 1].getId());
            }
            optionRelLayParams.leftMargin = 60;
            optionRelLayParams.rightMargin = 10;

            questionRelLay.addView(options[j], optionRelLayParams);
        }

        TextView correctAnswerLabel = new TextView(this);
        correctAnswerLabel.setId(View.generateViewId());
        correctAnswerLabel.setText("Correct Answer:");
        correctAnswerLabel.setTextSize(15);
        correctAnswerLabel.setPadding(0,5,0,5);

        RelativeLayout.LayoutParams correctAnsLabRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        correctAnsLabRelLayParams.addRule(RelativeLayout.BELOW, options[options.length - 1].getId());
        correctAnsLabRelLayParams.addRule(RelativeLayout.ALIGN_LEFT, options[options.length - 1].getId());
        correctAnsLabRelLayParams.topMargin = 25;
        questionRelLay.addView(correctAnswerLabel, correctAnsLabRelLayParams);

        RadioButton[] optionRadioBtns = new RadioButton[optionLen];

        for(int j = 0; j < optionRadioBtns.length; j++) {
            optionRadioBtns[j] = new RadioButton(EditQuiz.this);
            optionRadioBtns[j].setId(View.generateViewId());
            optionRadioBtns[j].setText((j + 1) + "");

            RelativeLayout.LayoutParams optionRadioBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(j == 0) {
                optionRadioBtnLayParams.addRule(RelativeLayout.END_OF, correctAnswerLabel.getId());
                optionRadioBtnLayParams.leftMargin = 10;
            } else {
                optionRadioBtnLayParams.addRule(RelativeLayout.END_OF, optionRadioBtns[j - 1].getId());
            }
            optionRadioBtnLayParams.addRule(RelativeLayout.ALIGN_TOP, correctAnswerLabel.getId());
            optionRadioBtnLayParams.addRule(RelativeLayout.ALIGN_BOTTOM, correctAnswerLabel.getId());

            questionRelLay.addView(optionRadioBtns[j], optionRadioBtnLayParams);
        }

        RelativeLayout.LayoutParams questionRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(index == 0) {
            questionRelLayParams.addRule(RelativeLayout.BELOW, quizNameLabel.getId());
        } else {
            questionRelLayParams.addRule(RelativeLayout.BELOW, questionsRelLay.get(questionsRelLay.size() - 1).getId());
        }
        questionRelLayParams.bottomMargin = 40;

        questionsRelLay.add(questionRelLay);
        relLay.addView(questionsRelLay.get(questionsRelLay.size() - 1), questionRelLayParams);

        if(index == -1) {
            editableQuiz.questionList.add(new Question("", new String[optionLen], ""));
        }
    }

    public void alignButtons() {
        RelativeLayout.LayoutParams addCancelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addCancelLayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        if(questionsRelLay.size() == 0) {
            addCancelLayParams.addRule(RelativeLayout.BELOW, quizNameLabel.getId());
        } else {
            addCancelLayParams.addRule(RelativeLayout.BELOW, questionsRelLay.get(questionsRelLay.size() - 1).getId());
        }

        addCancelLay.setLayoutParams(addCancelLayParams);

        RelativeLayout.LayoutParams editQuizBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editQuizBtnLayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        editQuizBtnLayParams.addRule(RelativeLayout.BELOW, addCancelLay.getId());
        editQuizBtnLayParams.bottomMargin = 100;

        editQuizBtn.setLayoutParams(editQuizBtnLayParams);
    }
}