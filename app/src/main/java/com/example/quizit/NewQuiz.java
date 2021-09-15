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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class NewQuiz extends AppCompatActivity {

    private ArrayList<RelativeLayout> questionList_rel_lay;
    private ArrayList<QuizInfo> quizzes;
    private ArrayList<Question> questionsList;
    private RelativeLayout layout;
    private TextView quizName;
    private LinearLayout addCancelLayout;
    private Button addBtn;
    private Button cancelBtn;
    private Button createQuizBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        questionList_rel_lay = new ArrayList<RelativeLayout>();
        questionsList = new ArrayList<Question>();
        quizzes = new ArrayList<QuizInfo>();
        layout = findViewById(R.id.newQuizLay);
        quizName = findViewById(R.id.quizNameID);
        addCancelLayout = findViewById(R.id.addCancelLay);
        addBtn = findViewById(R.id.newQuestionbtn);
        cancelBtn = findViewById(R.id.cancelQuestionbtn);
        createQuizBtn = findViewById(R.id.createQuizBtn);
    }

    public void createNewQuestion(View view) {
        RelativeLayout newQuestionRelativeLayout = new RelativeLayout(this);
        newQuestionRelativeLayout.setId(View.generateViewId());

        RelativeLayout.LayoutParams newQuestionRelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        EditText newQuestion = new EditText(this);
        newQuestion.setHint("Question " + (questionList_rel_lay.size() + 1));
        newQuestion.setId(View.generateViewId());
        RelativeLayout.LayoutParams newQuestionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        newQuestionLayout.leftMargin = 20;
        newQuestionLayout.bottomMargin = 10;
        newQuestionRelativeLayout.addView(newQuestion, newQuestionLayout);

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

            optionLayout.leftMargin = 50;
            optionLayout.rightMargin = 10;

            newQuestionRelativeLayout.addView(options[i], optionLayout);
        }

        TextView correctAnswerLabel = new TextView(this);
        correctAnswerLabel.setText("Correct Answer:");
        correctAnswerLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams correctAnswerLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        correctAnswerLabelLay.leftMargin = 50;
        correctAnswerLabelLay.topMargin = 20;
        correctAnswerLabelLay.bottomMargin = 30;
        correctAnswerLabelLay.addRule(RelativeLayout.BELOW, options[options.length - 1].getId());

        newQuestionRelativeLayout.addView(correctAnswerLabel, correctAnswerLabelLay);

        RadioButton[] optionsCheckBoxes = new RadioButton[options.length];

        for(int i = 0; i < optionsCheckBoxes.length; i++) {
            optionsCheckBoxes[i] = new RadioButton(this);
            optionsCheckBoxes[i].setText((i + 1) + "");
            optionsCheckBoxes[i].setId(View.generateViewId());


            RelativeLayout.LayoutParams optionCheckBoxLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            if(i == 0) {
                optionCheckBoxLay.addRule(RelativeLayout.END_OF, correctAnswerLabel.getId());
                optionCheckBoxLay.leftMargin = 20;
            } else {
                optionCheckBoxLay.addRule(RelativeLayout.END_OF, optionsCheckBoxes[i - 1].getId());
                optionCheckBoxLay.leftMargin = 10;
            }
            optionCheckBoxLay.addRule(RelativeLayout.ALIGN_TOP, correctAnswerLabel.getId());
            optionCheckBoxLay.addRule(RelativeLayout.ALIGN_BOTTOM, correctAnswerLabel.getId());
            newQuestionRelativeLayout.addView(optionsCheckBoxes[i], optionCheckBoxLay);
        }

        String[] optionsStrArr = new String[options.length];

        questionsList.add(new Question("", optionsStrArr, ""));

        newQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                questionsList.get(questionsList.size() - 1).question = newQuestion.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                questionsList.get(questionsList.size() - 1).question = newQuestion.getText().toString();
            }
        });

        for(int i = 0; i < options.length; i++) {
            int j = i;
            options[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    questionsList.get(questionsList.size() - 1).options[j] = options[j].getText().toString();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    questionsList.get(questionsList.size() - 1).options[j] = options[j].getText().toString();
                }
            });

        }

        if(questionList_rel_lay.size() > 0) {
            newQuestionRelParams.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 1).getId());
        } else {
            newQuestionRelParams.addRule(RelativeLayout.BELOW, R.id.quizNameID);
        }
        newQuestionRelParams.topMargin = 60;

        questionList_rel_lay.add(newQuestionRelativeLayout);
        layout.addView(questionList_rel_lay.get(questionList_rel_lay.size() - 1), newQuestionRelParams);

        alignButtons();
    }

    public void deleteQuestion(View view) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.newQuizLay);

        layout.removeView(questionList_rel_lay.get(questionList_rel_lay.size() - 1));
        questionList_rel_lay.remove(questionList_rel_lay.size() - 1);

        if(questionList_rel_lay.size() == 0) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            alignButtons();
        }
    }

    public void createNewQuiz(View view) {
        QuizInfo newQuiz = new QuizInfo(quizName.getText().toString(), questionsList);
        quizzes.add(newQuiz);

        Intent intent = new Intent(this, Quizzes.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("QuizzesList", newQuiz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void alignButtons() {
        RelativeLayout.LayoutParams addCancelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addCancelLay.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 1).getId());
        addCancelLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addCancelLay.bottomMargin = 10;
        addCancelLayout.setLayoutParams(addCancelLay);

        RelativeLayout.LayoutParams createQuizLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        createQuizLay.addRule(RelativeLayout.BELOW, addCancelLayout.getId());
        createQuizLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
        createQuizLay.bottomMargin = 100;
        createQuizBtn.setLayoutParams(createQuizLay);
    }
}