package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EditQuiz extends AppCompatActivity {

    ArrayList<QuizInfo> quizzes;
    RelativeLayout mainRelLay;
    HorizontalScrollView btnsScrollView;
    RelativeLayout btnsRelLay;
    LinearLayout btnsLay;
    ScrollView scrollView;
    RelativeLayout relLay;
    QuizInfo editableQuiz;
    EditText quizNameLabel;
    ArrayList<RelativeLayout> questionsRelLay;
    Button addQuestionBtn;
    Button cancelQuestionBtn;
    Button editQuizBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadData();

        mainRelLay = new RelativeLayout(this);
        mainRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        mainRelLay.setId(View.generateViewId());

        btnsScrollView = new HorizontalScrollView(this);
        btnsScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btnsScrollView.setId(View.generateViewId());

        btnsRelLay = new RelativeLayout(this);
        btnsRelLay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        btnsRelLay.setId(View.generateViewId());

        btnsScrollView.addView(btnsRelLay);

        btnsLay = new LinearLayout(this);
        btnsLay.setId(View.generateViewId());
        btnsLay.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout.LayoutParams btnsLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnsLayParams.topMargin = 20;
        btnsLayParams.bottomMargin = 20;
        btnsRelLay.addView(btnsLay, btnsLayParams);

        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setId(View.generateViewId());

        relLay = new RelativeLayout(this);
        relLay.setLayoutParams((new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)));
        scrollView.addView(relLay);

        mainRelLay.addView(btnsScrollView);

        RelativeLayout.LayoutParams quizRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        quizRelLayParams.addRule(RelativeLayout.BELOW, btnsScrollView.getId());

        mainRelLay.addView(scrollView, quizRelLayParams);

        questionsRelLay = new ArrayList<RelativeLayout>();

        addQuestionBtn = new Button(this);
        addQuestionBtn.setId(View.generateViewId());
        addQuestionBtn.setText("Add Question");

        cancelQuestionBtn = new Button(this);
        cancelQuestionBtn.setId(View.generateViewId());
        cancelQuestionBtn.setText("Cancel Question");
        cancelQuestionBtn.setTextColor(Color.parseColor("#000000"));

        editQuizBtn = new Button(this);
        editQuizBtn.setId(View.generateViewId());
        editQuizBtn.setText("Edit Quiz");

        RelativeLayout.LayoutParams editQuizBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        editQuizBtnLayParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        editQuizBtnLayParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        editQuizBtnLayParams.rightMargin = 20;
        editQuizBtnLayParams.bottomMargin = 20;

        mainRelLay.addView(editQuizBtn, editQuizBtnLayParams);

        SharedPreferences sharedPreferences = getSharedPreferences("EditQuiz", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Quiz", null);
        int quizId = sharedPreferences.getInt("QuizID", -1);
        Type type = new TypeToken<QuizInfo>() {}.getType();
        editableQuiz = gson.fromJson(json, type);

        quizNameLabel = new EditText(this);
        quizNameLabel.setId(View.generateViewId());
        quizNameLabel.setText(editableQuiz.quizName);
        quizNameLabel.setHint("Quiz Name");
        quizNameLabel.setTextSize(30);
        quizNameLabel.setGravity(Gravity.CENTER);

        quizNameLabel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editableQuiz.quizName = quizNameLabel.getText().toString();
            }
        });

        RelativeLayout.LayoutParams quizNameLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        quizNameLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quizNameLay.topMargin = 30;
        quizNameLay.bottomMargin = 20;
        relLay.addView(quizNameLabel, quizNameLay);

        for(int i = 0; i < editableQuiz.questionList.size(); i++) {
            addQuestionLayout(i);
        }

        btnsLay.addView(addQuestionBtn);

        RelativeLayout.LayoutParams cancelBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        cancelBtnLayParams.leftMargin = 10;
        btnsLay.addView(cancelQuestionBtn, cancelBtnLayParams);

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestionLayout(-1);
            }
        });

        cancelQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQuestion();
            }
        });

        editQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i = 0; i < quizzes.size(); i++) {
                    if(i == quizId) {
                        quizzes.remove(i);
                        quizzes.add(i, editableQuiz);
                    }
                }

                saveData();

                Intent intent = new Intent(EditQuiz.this, Quizzes.class);
                startActivity(intent);
            }
        });

        setContentView(mainRelLay);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditQuiz.this, MainActivity.class);
        startActivity(intent);
    }

    public void addQuestionLayout(int index) {
        RelativeLayout questionRelLay = new RelativeLayout(this);
        questionRelLay.setId(View.generateViewId());

        if(index < 0) {
            editableQuiz.questionList.add(new Question("", new String[4], ""));
        }

        EditText question = new EditText(this);
        question.setId(View.generateViewId());
        if(index >= 0) {
            question.setText(editableQuiz.questionList.get(index).question);
        }
        if(index >= 0) {
            question.setHint("Question " + (index + 1));
        } else {
            question.setHint("Question " + (editableQuiz.questionList.size()));
        }
        question.setTextSize(25);

        RelativeLayout.LayoutParams questionTextRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        questionTextRelLayParams.leftMargin = 10;
        questionTextRelLayParams.rightMargin = 10;
        questionRelLay.addView(question, questionTextRelLayParams);

        question.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                editableQuiz.questionList.get(editableQuiz.questionList.size() - 1).question = question.getText().toString();
            }
        });

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

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton[] optionRadioBtns = new RadioButton[optionLen];

        for(int j = 0; j < optionRadioBtns.length; j++) {
            optionRadioBtns[j] = new RadioButton(EditQuiz.this);
            optionRadioBtns[j].setId(View.generateViewId());
            optionRadioBtns[j].setText((j + 1) + "");

            radioGroup.addView(optionRadioBtns[j]);

            if(index >= 0) {
                if(editableQuiz.questionList.get(index).correctAnswers.equals(options[j].getText().toString())) {
                    optionRadioBtns[j].setChecked(true);
                }
            }

            int k = j;
            optionRadioBtns[j].setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (index < 0) {
                        editableQuiz.questionList.get(editableQuiz.questionList.size() - 1).correctAnswers = options[k].getText().toString();
                    } else {
                        editableQuiz.questionList.get(index).correctAnswers = options[k].getText().toString();
                    }
                }
            });

            options[j].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(index < 0) {
                        editableQuiz.questionList.get(editableQuiz.questionList.size() - 1).options[k] = options[k].getText().toString();
                    } else {
                        editableQuiz.questionList.get(index).options[k] = options[k].getText().toString();
                    }

                    if(optionRadioBtns[k].isChecked()) {
                        if(index < 0) {
                            editableQuiz.questionList.get(editableQuiz.questionList.size() - 1).correctAnswers = options[k].getText().toString();
                        } else {
                            editableQuiz.questionList.get(index).correctAnswers = options[k].getText().toString();
                        }
                    }
                }
            });
        }

        RelativeLayout.LayoutParams optionRadioBtnLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        optionRadioBtnLayParams.addRule(RelativeLayout.END_OF, correctAnswerLabel.getId());
        optionRadioBtnLayParams.leftMargin = 10;
        optionRadioBtnLayParams.addRule(RelativeLayout.ALIGN_TOP, correctAnswerLabel.getId());
        optionRadioBtnLayParams.addRule(RelativeLayout.ALIGN_BOTTOM, correctAnswerLabel.getId());

        questionRelLay.addView(radioGroup, optionRadioBtnLayParams);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedRadBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                int optionNum = Integer.parseInt(selectedRadBtn.getText().toString());
                if (index < 0) {
                    editableQuiz.questionList.get(editableQuiz.questionList.size() - 1).correctAnswers = options[optionNum - 1].getText().toString();
                } else {
                    editableQuiz.questionList.get(index).correctAnswers = options[optionNum - 1].getText().toString();
                }
            }
        });

        RelativeLayout.LayoutParams questionRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if(index == 0) {
            questionRelLayParams.addRule(RelativeLayout.BELOW, quizNameLabel.getId());
        } else {
            questionRelLayParams.addRule(RelativeLayout.BELOW, questionsRelLay.get(questionsRelLay.size() - 1).getId());
        }
        questionRelLayParams.bottomMargin = 40;

        questionsRelLay.add(questionRelLay);
        relLay.addView(questionsRelLay.get(questionsRelLay.size() - 1), questionRelLayParams);
    }

    public void deleteQuestion() {
        if(questionsRelLay.size() > 0) {
            editableQuiz.questionList.remove(editableQuiz.questionList.size() - 1);
            relLay.removeView(questionsRelLay.get(questionsRelLay.size() - 1));
            questionsRelLay.remove(questionsRelLay.size() - 1);

            if(questionsRelLay.size() == 0) {
                editQuizBtn.setEnabled(false);
            }
        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("Quizzes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quizzes);
        editor.putString("QuizzesList", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("Quizzes", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("QuizzesList", null);
        Type type = new TypeToken<ArrayList<QuizInfo>>() {}.getType();
        quizzes = gson.fromJson(json, type);
    }
}