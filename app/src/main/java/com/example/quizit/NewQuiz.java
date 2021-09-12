package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NewQuiz extends AppCompatActivity {

    private ArrayList<RelativeLayout> questionList_rel_lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        questionList_rel_lay = new ArrayList<RelativeLayout>();
    }

    public void createNewQuestion(View view) {

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.newQuizLay);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addCancelLay);

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

        CheckBox[] optionsCheckBoxes = new CheckBox[options.length];

        for(int i = 0; i < optionsCheckBoxes.length; i++) {
            optionsCheckBoxes[i] = new CheckBox(this);
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

        if(questionList_rel_lay.size() > 0) {
            newQuestionRelParams.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 1).getId());
        } else {
            newQuestionRelParams.addRule(RelativeLayout.BELOW, R.id.quizNameID);
        }
        newQuestionRelParams.topMargin = 60;

        questionList_rel_lay.add(newQuestionRelativeLayout);
        layout.addView(newQuestionRelativeLayout, newQuestionRelParams);

        RelativeLayout.LayoutParams addCancelLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addCancelLayout.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 1).getId());
        addCancelLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
        addCancelLayout.bottomMargin = 100;
        linearLayout.setLayoutParams(addCancelLayout);
    }
}