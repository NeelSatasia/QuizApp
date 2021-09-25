package com.example.quizit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class NewQuiz extends AppCompatActivity {

    ArrayList<RelativeLayout> questionList_rel_lay;
    ArrayList<Question> questionsList;
    RelativeLayout layout;
    TextView quizName;
    Button addBtn;
    Button cancelBtn;
    Button createQuizBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        questionList_rel_lay = new ArrayList<RelativeLayout>();
        questionsList = new ArrayList<Question>();
        layout = findViewById(R.id.newQuizLay);
        quizName = findViewById(R.id.quizNameID);
        addBtn = findViewById(R.id.newQuestionbtn);
        cancelBtn = findViewById(R.id.cancelQuestionbtn);
        createQuizBtn = findViewById(R.id.createQuizBtn);

        cancelBtn.setEnabled(false);
        createQuizBtn.setEnabled(false);
    }

    public void createNewQuestion(View view) {
        RelativeLayout newQuestionRelativeLayout = new RelativeLayout(this);
        newQuestionRelativeLayout.setId(View.generateViewId());

        questionsList.add(new Question("", new String[4], ""));

        RelativeLayout.LayoutParams newQuestionRelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        EditText newQuestion = new EditText(this);
        newQuestion.setHint("Question " + (questionList_rel_lay.size() + 1));
        newQuestion.setId(View.generateViewId());

        RelativeLayout.LayoutParams newQuestionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        newQuestionLayout.leftMargin = 10;
        newQuestionLayout.bottomMargin = 10;
        newQuestionRelativeLayout.addView(newQuestion, newQuestionLayout);

        newQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                questionsList.get(questionsList.size() - 1).question = newQuestion.getText().toString();
            }
        });

        EditText[] options = new EditText[4];

        for(int i = 0; i < options.length; i++) {
            options[i] = new EditText(this);
            options[i].setHint("Option " + (i + 1));
            options[i].setId(View.generateViewId());

            RelativeLayout.LayoutParams optionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(i - 1 < 0) {
                optionLayout.addRule(RelativeLayout.BELOW, newQuestion.getId());
            } else {
                optionLayout.addRule(RelativeLayout.BELOW, options[i - 1].getId());
            }

            optionLayout.leftMargin = 60;
            optionLayout.rightMargin = 10;

            newQuestionRelativeLayout.addView(options[i], optionLayout);
        }

        TextView correctAnswerLabel = new TextView(this);
        correctAnswerLabel.setText("Correct Answer:");
        correctAnswerLabel.setId(View.generateViewId());
        correctAnswerLabel.setTextSize(20);
        correctAnswerLabel.setPadding(0, 5, 0, 5);

        RelativeLayout.LayoutParams correctAnswerLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        correctAnswerLabelLay.leftMargin = 50;
        correctAnswerLabelLay.topMargin = 30;
        correctAnswerLabelLay.bottomMargin = 30;
        correctAnswerLabelLay.addRule(RelativeLayout.BELOW, options[options.length - 1].getId());

        newQuestionRelativeLayout.addView(correctAnswerLabel, correctAnswerLabelLay);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton[] optionsCheckBoxes = new RadioButton[options.length];

        for(int i = 0; i < optionsCheckBoxes.length; i++) {
            optionsCheckBoxes[i] = new RadioButton(this);
            optionsCheckBoxes[i].setText((i + 1) + "");
            optionsCheckBoxes[i].setId(View.generateViewId());
            radioGroup.addView(optionsCheckBoxes[i]);
            int j = i;

            optionsCheckBoxes[i].setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    questionsList.get(questionsList.size() - 1).correctAnswers = options[j].getText().toString();
                }
            });

            options[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    questionsList.get(questionsList.size() - 1).options[j] = options[j].getText().toString();

                    if(optionsCheckBoxes[j].isChecked()) {
                        questionsList.get(questionsList.size() - 1).correctAnswers = options[j].getText().toString();
                    }
                }
            });
        }

        RelativeLayout.LayoutParams optionCheckBoxLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        optionCheckBoxLay.addRule(RelativeLayout.END_OF, correctAnswerLabel.getId());optionCheckBoxLay.leftMargin = 20;
        optionCheckBoxLay.addRule(RelativeLayout.ALIGN_TOP, correctAnswerLabel.getId());
        optionCheckBoxLay.addRule(RelativeLayout.ALIGN_BOTTOM, correctAnswerLabel.getId());

        newQuestionRelativeLayout.addView(radioGroup, optionCheckBoxLay);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedRadBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                int optionNum = Integer.parseInt(selectedRadBtn.getText().toString());
                questionsList.get(questionsList.size() - 1).correctAnswers = options[optionNum - 1].getText().toString();
            }
        });

        if(questionList_rel_lay.size() > 0) {
            newQuestionRelParams.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 1).getId());
        } else {
            newQuestionRelParams.addRule(RelativeLayout.BELOW, R.id.quizNameID);
        }
        newQuestionRelParams.bottomMargin = 100;

        questionList_rel_lay.add(newQuestionRelativeLayout);
        layout.addView(questionList_rel_lay.get(questionList_rel_lay.size() - 1), newQuestionRelParams);

        if(createQuizBtn.isEnabled() == false) {
            createQuizBtn.setEnabled(true);
        }
    }

    public void deleteQuestion(View view) {
        if(questionList_rel_lay.size() > 0) {
            questionsList.remove(questionsList.size() - 1);
            layout.removeView(questionList_rel_lay.get(questionList_rel_lay.size() - 1));
            questionList_rel_lay.remove(questionList_rel_lay.size() - 1);

            if(questionList_rel_lay.size() == 0) {
                createQuizBtn.setEnabled(false);
            }
        }
    }

    public void createNewQuiz(View view) {
        QuizInfo newQuiz = new QuizInfo(quizName.getText().toString(), questionsList);

        Intent intent = new Intent(this, Quizzes.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("QuizzesList", newQuiz);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}