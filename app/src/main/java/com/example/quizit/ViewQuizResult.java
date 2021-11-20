package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ViewQuizResult extends AppCompatActivity {

    RelativeLayout mainRelLay;

    QuizResult quiz;

    int currentQuest_afterSubmission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainRelLay = new RelativeLayout(this);
        mainRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        SharedPreferences sharedPreferences2 = getSharedPreferences("Quiz Result", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences2.getString("Quiz", null);
        Type type = new TypeToken<QuizResult>() {}.getType();
        quiz = gson.fromJson(json, type);

        TextView resultLabel = new TextView(this);
        resultLabel.setText("Result: " + quiz.userCorrectAnswers + " of " + quiz.questionList.size());
        resultLabel.setTextSize(25);
        resultLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams resultLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        resultLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        resultLabelParams.topMargin = 30;
        resultLabelParams.leftMargin = 10;
        resultLabelParams.rightMargin = 10;
        resultLabelParams.bottomMargin = 20;

        mainRelLay.addView(resultLabel, resultLabelParams);

        LinearLayout topLay = new LinearLayout(this);
        topLay.setId(View.generateViewId());
        topLay.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams topLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        topLayParams.addRule(RelativeLayout.BELOW, resultLabel.getId());
        topLayParams.topMargin = 15;
        topLayParams.leftMargin = 10;
        topLayParams.rightMargin = 10;

        mainRelLay.addView(topLay, topLayParams);

        Button backBtn = new Button(this);
        backBtn.setText("Back");
        backBtn.setId(View.generateViewId());

        Drawable buttonDrawable = backBtn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, Color.rgb(153, 153, 153));
        backBtn.setBackground(buttonDrawable);
        backBtn.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams backBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        backBtnParams.rightMargin = 10;

        topLay.addView(backBtn, backBtnParams);

        TextView questNumLabel = new TextView(this);
        questNumLabel.setText("Question Tracker");
        questNumLabel.setTextSize(20);
        questNumLabel.setId(View.generateViewId());

        LinearLayout.LayoutParams questNumParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        questNumParams.rightMargin = 10;

        topLay.addView(questNumLabel, questNumParams);

        Button nextBtn = new Button(this);
        nextBtn.setText("Next");
        nextBtn.setId(View.generateViewId());

        Drawable buttonDrawable2 = nextBtn.getBackground();
        buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
        DrawableCompat.setTint(buttonDrawable2, Color.rgb(76, 175, 80));
        nextBtn.setBackground(buttonDrawable2);
        nextBtn.setTextColor(Color.WHITE);

        topLay.addView(nextBtn);

        questNumLabel.setText("Question " + (currentQuest_afterSubmission + 1));
        ScrollView[] questionsResScrlView = new ScrollView[quiz.questionList.size()];

        for(int i = 0; i < questionsResScrlView.length; i++) {
            //question view
            questionsResScrlView[i] = new ScrollView(this);
            questionsResScrlView[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            RelativeLayout mainQuesRelLay = new RelativeLayout(this);
            mainQuesRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            questionsResScrlView[i].addView(mainQuesRelLay);

            TextView questionLabel = new TextView(this);
            questionLabel.setText(quiz.questionList.get(i).question);
            questionLabel.setTextSize(25);
            questionLabel.setTextColor(Color.WHITE);
            questionLabel.setId(View.generateViewId());
            questionLabel.setPadding(20, 10, 10, 10);

            if(quiz.isUserAnswersCorrect[i]) {
                questionLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_right_answer));
            } else {
                questionLabel.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_wrong_answer));
            }

            RelativeLayout.LayoutParams questionLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            questionLabelParams.topMargin = 25;
            questionLabelParams.leftMargin = 20;
            questionLabelParams.rightMargin = 20;

            mainQuesRelLay.addView(questionLabel, questionLabelParams);

            ScrollView answersScrlView = new ScrollView(this);
            answersScrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            answersScrlView.setBackground(ContextCompat.getDrawable(this, R.drawable.custom_answer_result));

            RelativeLayout.LayoutParams ansScrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            ansScrlViewParams.addRule(RelativeLayout.BELOW, questionLabel.getId());
            ansScrlViewParams.addRule(RelativeLayout.ALIGN_LEFT, questionLabel.getId());
            ansScrlViewParams.addRule(RelativeLayout.ALIGN_RIGHT, questionLabel.getId());

            mainQuesRelLay.addView(answersScrlView, ansScrlViewParams);

            RelativeLayout answersRelLay = new RelativeLayout(this);
            answersRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            answersRelLay.setPadding(0, 20, 0, 0);

            answersScrlView.addView(answersRelLay);

            if(quiz.questionList.get(i).mcQuestion) {
                CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(i).options.length];

                for(int k = 0; k < optionsCB.length; k++) {
                    optionsCB[k] = new CheckBox(this);
                    optionsCB[k].setText(quiz.questionList.get(i).options[k]);
                    optionsCB[k].setId(View.generateViewId());
                    optionsCB[k].setClickable(false);
                    optionsCB[k].setFocusable(false);

                    if(quiz.userAnswers.get(i).contains(k + "")) {
                        optionsCB[k].setChecked(true);
                    }

                    for(int u = 0; u < quiz.userAnswers.get(i).size(); u++) {
                        if(quiz.userAnswers.get(i).get(u).equals((Integer) k)) {
                            optionsCB[k].setChecked(true);
                        }
                    }

                    if(quiz.questionList.get(i).correctAnswers.contains((Integer) k)) {
                        optionsCB[k].setTextColor(Color.rgb(0, 128, 0));
                    }

                    if(quiz.questionList.get(i).correctAnswers.contains((Integer) k) == false && quiz.userAnswers.get(i).contains(k + "")) {
                        optionsCB[k].setTextColor(Color.RED);
                    }

                    RelativeLayout.LayoutParams optionParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if(k > 0) {
                        optionParams.addRule(RelativeLayout.BELOW, optionsCB[k - 1].getId());
                    }

                    optionParams.leftMargin = 20;
                    optionParams.rightMargin = 10;
                    optionParams.bottomMargin = 10;

                    answersRelLay.addView(optionsCB[k], optionParams);
                }
            } else {
                TextView yourAnsLabel = new TextView(this);
                yourAnsLabel.setText("Your Answer:");
                yourAnsLabel.setTextSize(20);
                yourAnsLabel.setTextColor(Color.rgb(0, 128, 0));
                yourAnsLabel.setId(View.generateViewId());

                RelativeLayout.LayoutParams yourAnsLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                yourAnsLabelParams.leftMargin = 35;
                yourAnsLabelParams.bottomMargin = 25;

                answersRelLay.addView(yourAnsLabel, yourAnsLabelParams);

                TextView userFrAnswer = new TextView(this);
                userFrAnswer.setText(quiz.userAnswers.get(i).get(0));
                userFrAnswer.setTextSize(20);
                userFrAnswer.setHint("No response");
                userFrAnswer.setId(View.generateViewId());

                RelativeLayout.LayoutParams userFrAnswerLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                userFrAnswerLayParams.addRule(RelativeLayout.BELOW, yourAnsLabel.getId());
                userFrAnswerLayParams.leftMargin = 65;
                userFrAnswerLayParams.bottomMargin = 50;

                answersRelLay.addView(userFrAnswer, userFrAnswerLayParams);

                if(quiz.isUserAnswersCorrect[i] == false) {
                    TextView corrAnsLabel = new TextView(this);
                    corrAnsLabel.setText("Correct Answer:");
                    corrAnsLabel.setTextSize(20);
                    corrAnsLabel.setId(View.generateViewId());
                    corrAnsLabel.setTextColor(Color.rgb(0, 128, 0));
                    yourAnsLabel.setTextColor(Color.RED);

                    RelativeLayout.LayoutParams corrAnsLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    corrAnsLayParams.addRule(RelativeLayout.BELOW, userFrAnswer.getId());
                    corrAnsLayParams.leftMargin = 35;
                    corrAnsLayParams.bottomMargin = 20;

                    answersRelLay.addView(corrAnsLabel, corrAnsLayParams);

                    TextView corrfrAns = new TextView(this);
                    corrfrAns.setText(quiz.questionList.get(i).frCorrectAnswer);
                    corrfrAns.setTextSize(20);

                    RelativeLayout.LayoutParams corrfrAnsParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    corrfrAnsParams.addRule(RelativeLayout.BELOW, corrAnsLabel.getId());
                    corrfrAnsParams.leftMargin = 65;
                    corrfrAnsParams.bottomMargin = 20;

                    answersRelLay.addView(corrfrAns, corrfrAnsParams);
                }
            }
        }

        RelativeLayout.LayoutParams questionRelScrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        questionRelScrlViewParams.addRule(RelativeLayout.BELOW, topLay.getId());
        questionRelScrlViewParams.topMargin = 15;
        questionRelScrlViewParams.leftMargin = 10;
        questionRelScrlViewParams.rightMargin = 10;

        mainRelLay.addView(questionsResScrlView[0], questionRelScrlViewParams);

        nextBtn.setOnClickListener(view -> {
            if(currentQuest_afterSubmission + 1 < questionsResScrlView.length) {
                mainRelLay.removeView(questionsResScrlView[currentQuest_afterSubmission]);
                currentQuest_afterSubmission++;

                RelativeLayout.LayoutParams nextQuestRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                nextQuestRelLayParams.addRule(RelativeLayout.BELOW, topLay.getId());
                nextQuestRelLayParams.topMargin = 15;
                nextQuestRelLayParams.leftMargin = 10;
                nextQuestRelLayParams.rightMargin = 10;

                mainRelLay.addView(questionsResScrlView[currentQuest_afterSubmission], nextQuestRelLayParams);
                questNumLabel.setText("Question " + (currentQuest_afterSubmission + 1));
            }
        });

        backBtn.setOnClickListener(view -> {
            if(currentQuest_afterSubmission - 1 >= 0) {
                mainRelLay.removeView(questionsResScrlView[currentQuest_afterSubmission]);
                currentQuest_afterSubmission--;

                RelativeLayout.LayoutParams previousQuestRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                previousQuestRelLayParams.addRule(RelativeLayout.BELOW, topLay.getId());
                previousQuestRelLayParams.topMargin = 15;
                previousQuestRelLayParams.leftMargin = 10;
                previousQuestRelLayParams.rightMargin = 10;

                mainRelLay.addView(questionsResScrlView[currentQuest_afterSubmission], previousQuestRelLayParams);
                questNumLabel.setText("Question " + (currentQuest_afterSubmission + 1));
            }
        });

        setContentView(mainRelLay);
    }

    @Override
    public void onBackPressed() {
        this.finish();

        Intent intent = new Intent(this, QuizHistory.class);
        startActivity(intent);
    }
}