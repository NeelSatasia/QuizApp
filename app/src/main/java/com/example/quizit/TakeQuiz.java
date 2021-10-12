package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TakeQuiz extends AppCompatActivity {

    QuizInfo quiz;

    RelativeLayout[] questionsOptionsRelLays;
    ArrayList<Object>[] userAnswers;
    Boolean[] userAnswersCorrect;

    TextView quizNameLabel;
    TextView questionLabel;
    TextView questionTracker;
    TextView quizTimerLabel;
    RelativeLayout questionRelLay;
    Button nextQues;
    Button backQues;
    Button submitQuizBtn;

    int currentQuestionIndex = 0;

    ScrollView resultScrlView;

    boolean finishedQuiz = false;
    boolean reviewingQuestionAfterQuiz = false;

    CountDownTimer quizTimer;
    long totalTimeInMillis;
    boolean isQuizTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        loadQuiz();

        questionsOptionsRelLays = new RelativeLayout[quiz.questionList.size()];

        userAnswers = new ArrayList[quiz.questionList.size()];
        userAnswersCorrect = new Boolean[userAnswers.length];

        for(int i = 0; i < quiz.questionList.size(); i++) {
            userAnswers[i] = new ArrayList<Object>();

            if(quiz.questionList.get(i).mcQuestion == false) {
                userAnswers[i].add((String) "");
            }

            userAnswersCorrect[i] = false;

            questionsOptionsRelLays[i] = new RelativeLayout(this);
            questionsOptionsRelLays[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            if(quiz.questionList.get(i).mcQuestion) {
                CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(i).options.length];

                for(int k = 0; k < optionsCB.length; k++) {
                    optionsCB[k] = new CheckBox(this);
                    optionsCB[k].setText(quiz.questionList.get(i).options[k]);
                    optionsCB[k].setId(View.generateViewId());

                    RelativeLayout.LayoutParams optionLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    optionLayParams.bottomMargin = 20;

                    if(k > 0) {
                        optionLayParams.addRule(RelativeLayout.BELOW, optionsCB[k - 1].getId());
                    }

                    questionsOptionsRelLays[i].addView(optionsCB[k], optionLayParams);

                    int h = i;
                    int j = k;
                    optionsCB[k].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(optionsCB[j].isChecked()) {
                                userAnswers[h].add((Integer) j);
                            } else if(userAnswers[h].contains((Integer) j)){
                                userAnswers[h].remove((Integer) j);
                            }
                        }
                    });
                }
            } else {
                EditText frAns = new EditText(this);
                frAns.setHint("Type your answer here");
                frAns.setBackgroundResource(R.drawable.custom_input);
                frAns.setPadding(15, 15, 15, 15);

                RelativeLayout.LayoutParams frLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                frLayParams.rightMargin = 40;

                questionsOptionsRelLays[i].addView(frAns, frLayParams);

                int h = i;
                frAns.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        userAnswers[h].set(0, frAns.getText().toString());
                    }
                });
            }
        }

        quizNameLabel = findViewById(R.id.quizTitleLabel);
        quizNameLabel.setText(quiz.quizName);

        questionLabel = findViewById(R.id.questionLabel);

        questionTracker = findViewById(R.id.questTracker);

        questionRelLay = findViewById(R.id.mainRelLay);

        nextQues = findViewById(R.id.nextQuesBtn);
        backQues = findViewById(R.id.backQuesBtn);
        submitQuizBtn = findViewById(R.id.sbtBtn);

        quizTimerLabel = findViewById(R.id.timr);

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
                submitQuiz();
            }
        });

        long hrInMillis = Integer.parseInt(quiz.timer[0]) * 60 * 60 * 1000;
        long mins = Integer.parseInt(quiz.timer[1]) * 60 * 1000;
        long secs = Integer.parseInt(quiz.timer[2]) * 1000;

        totalTimeInMillis = hrInMillis + mins + secs;

        if(totalTimeInMillis > 0) {
            quizTimer = new CountDownTimer(totalTimeInMillis, 1000) {
                @Override
                public void onTick(long l) {
                    totalTimeInMillis = l;

                    int hours = (int) (totalTimeInMillis / (1000 * 60 * 60)) % 24;
                    int minutes = (int) (totalTimeInMillis / (1000 * 60)) % 60;
                    int seconds = (int) (totalTimeInMillis / 1000) % 60;

                    String timerStr = "";

                    if(hours < 10) {
                        timerStr += "0" + hours + ":";
                    } else {
                        timerStr += hours + ":";
                    }

                    if(minutes < 10) {
                        timerStr += "0" + minutes + ":";
                    } else {
                        timerStr += minutes + ":";
                    }

                    if(seconds < 10) {
                        timerStr += "0" + seconds;
                    } else {
                        timerStr += seconds;
                    }

                    quizTimerLabel.setText(timerStr);

                    if(totalTimeInMillis <= 300000) { //if within 5 minutes
                        quizTimerLabel.setTextColor(Color.RED);
                    }
                }

                @Override
                public void onFinish() {
                    submitQuiz();
                }
            }.start();

            isQuizTimerRunning = true;
        }
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
        questionTracker.setText((index + 1) + " of " + quiz.questionList.size());
        questionLabel.setText(quiz.questionList.get(index).question);

        questionRelLay.addView(questionsOptionsRelLays[index]);
    }

    public void checkAnswers() {
        for(int i = 0; i < userAnswers.length; i++) {
            if(quiz.questionList.get(i).mcQuestion) {
                for(int j = 0; j < userAnswers[i].size(); j++) {
                    if(quiz.questionList.get(i).correctAnswers.size() == userAnswers[i].size()) {
                        if (quiz.questionList.get(i).correctAnswers.contains(userAnswers[i].get(j))) {
                            userAnswersCorrect[i] = true;
                        } else {
                            userAnswersCorrect[i] = false;
                            break;
                        }
                    }
                }
            } else {
                if(userAnswers[i].get(0).equals(quiz.questionList.get(i).frCorrectAnswer)) {
                    userAnswersCorrect[i] = true;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(reviewingQuestionAfterQuiz) {
            reviewingQuestionAfterQuiz = false;
            setContentView(resultScrlView);
        } else {
            if(isQuizTimerRunning) {
                quizTimer.cancel();
            }

            Intent intent = new Intent(TakeQuiz.this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void submitQuiz() {
        if(isQuizTimerRunning) {
            quizTimer.cancel();
        }

        finishedQuiz = true;
        checkAnswers();

        resultScrlView = new ScrollView(TakeQuiz.this);
        resultScrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        RelativeLayout resultLay = new RelativeLayout(TakeQuiz.this);
        resultLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        resultScrlView.addView(resultLay);

        TextView resultLabel = new TextView(TakeQuiz.this);
        resultLabel.setText("Result");
        resultLabel.setTextSize(40);
        resultLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams resultLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        resultLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        resultLabelParams.topMargin = 75;
        resultLabelParams.bottomMargin = 10;

        resultLay.addView(resultLabel, resultLabelParams);

        TextView scoreLabel = new TextView(TakeQuiz.this);

        int totalCorrectAnswers = 0;

        for(int i = 0; i < userAnswersCorrect.length; i++) {
            if(userAnswersCorrect[i]) {
                totalCorrectAnswers++;
            }
        }

        scoreLabel.setText("Score: " + totalCorrectAnswers + " of " + quiz.questionList.size());
        scoreLabel.setTextSize(25);
        scoreLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams scoreLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        scoreLabelParams.addRule(RelativeLayout.BELOW, resultLabel.getId());
        scoreLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        scoreLabelParams.bottomMargin = 50;

        resultLay.addView(scoreLabel, scoreLabelParams);

        Button[] questionsButtons = new Button[quiz.questionList.size()];
        ScrollView[] questionsResScrlView = new ScrollView[questionsButtons.length];

        for(int i = 0; i < questionsButtons.length; i++) {
            questionsButtons[i] = new Button(TakeQuiz.this);
            questionsButtons[i].setText("Question " + (i + 1));
            questionsButtons[i].setTextColor(Color.WHITE);
            questionsButtons[i].setId(View.generateViewId());

            Drawable buttonDrawable = questionsButtons[i].getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);

            if(userAnswersCorrect[i]) {
                DrawableCompat.setTint(buttonDrawable, Color.rgb(60, 179, 113));
            } else {
                DrawableCompat.setTint(buttonDrawable, Color.rgb(178, 34, 34));
            }

            questionsButtons[i].setBackground(buttonDrawable);

            RelativeLayout.LayoutParams questBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            questBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            if(i == 0) {
                questBtnParams.addRule(RelativeLayout.BELOW, scoreLabel.getId());
            } else {
                questBtnParams.addRule(RelativeLayout.BELOW, questionsButtons[i - 1].getId());
            }

            questBtnParams.leftMargin = 10;
            questBtnParams.rightMargin = 10;
            questBtnParams.bottomMargin = 10;

            resultLay.addView(questionsButtons[i], questBtnParams);


            //question view
            questionsResScrlView[i] = new ScrollView(TakeQuiz.this);
            questionsResScrlView[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            RelativeLayout mainQuesRelLay = new RelativeLayout(TakeQuiz.this);
            mainQuesRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            questionsResScrlView[i].addView(mainQuesRelLay);

            TextView questNumLabel = new TextView(TakeQuiz.this);
            questNumLabel.setText("Question " + (i + 1));
            questNumLabel.setTextSize(20);
            questNumLabel.setId(View.generateViewId());

            RelativeLayout.LayoutParams questNumLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            questNumLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            questNumLabelParams.topMargin = 25;
            questNumLabelParams.bottomMargin = 30;

            mainQuesRelLay.addView(questNumLabel, questNumLabelParams);

            TextView questionLabel = new TextView(TakeQuiz.this);
            questionLabel.setText(quiz.questionList.get(i).question);
            questionLabel.setTextSize(25);
            questionLabel.setTextColor(Color.WHITE);
            questionLabel.setId(View.generateViewId());
            questionLabel.setPadding(20, 10, 10, 10);

            if(userAnswersCorrect[i]) {
                questionLabel.setBackground(ContextCompat.getDrawable(TakeQuiz.this, R.drawable.custom_right_answer));
            } else {
                questionLabel.setBackground(ContextCompat.getDrawable(TakeQuiz.this, R.drawable.custom_wrong_answer));
            }

            RelativeLayout.LayoutParams questionLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            questionLabelParams.addRule(RelativeLayout.BELOW, questNumLabel.getId());
            questionLabelParams.leftMargin = 20;
            questionLabelParams.rightMargin = 20;

            mainQuesRelLay.addView(questionLabel, questionLabelParams);

            ScrollView answersScrlView = new ScrollView(TakeQuiz.this);
            answersScrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            answersScrlView.setBackground(ContextCompat.getDrawable(TakeQuiz.this, R.drawable.custom_answer_result));

            RelativeLayout.LayoutParams ansScrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            ansScrlViewParams.addRule(RelativeLayout.BELOW, questionLabel.getId());
            ansScrlViewParams.addRule(RelativeLayout.ALIGN_LEFT, questionLabel.getId());
            ansScrlViewParams.addRule(RelativeLayout.ALIGN_RIGHT, questionLabel.getId());

            mainQuesRelLay.addView(answersScrlView, ansScrlViewParams);

            RelativeLayout answersRelLay = new RelativeLayout(TakeQuiz.this);
            answersRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            answersRelLay.setPadding(0, 20, 0, 0);

            answersScrlView.addView(answersRelLay);

            if(quiz.questionList.get(i).mcQuestion) {
                CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(i).options.length];

                for(int k = 0; k < optionsCB.length; k++) {
                    optionsCB[k] = new CheckBox(TakeQuiz.this);
                    optionsCB[k].setText(quiz.questionList.get(i).options[k]);
                    optionsCB[k].setId(View.generateViewId());
                    optionsCB[k].setClickable(false);
                    optionsCB[k].setFocusable(false);

                    if(quiz.questionList.get(i).correctAnswers.contains((Integer) k)) {
                        optionsCB[k].setChecked(true);
                        optionsCB[k].setTextColor(Color.rgb(0, 128, 0));
                        optionsCB[k].setTextSize(20);
                    }

                    if(quiz.questionList.get(i).correctAnswers.contains((Integer) k) == false && userAnswers[i].contains((Integer) k)) {
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
                TextView yourAnsLabel = new TextView(TakeQuiz.this);
                yourAnsLabel.setText("Your Answer:");
                yourAnsLabel.setTextSize(20);
                yourAnsLabel.setTextColor(Color.rgb(0, 128, 0));
                yourAnsLabel.setId(View.generateViewId());

                RelativeLayout.LayoutParams yourAnsLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                yourAnsLabelParams.leftMargin = 35;
                yourAnsLabelParams.bottomMargin = 25;

                answersRelLay.addView(yourAnsLabel, yourAnsLabelParams);

                TextView userFrAnswer = new TextView(TakeQuiz.this);
                userFrAnswer.setText((String) userAnswers[i].get(0));
                userFrAnswer.setTextSize(20);
                userFrAnswer.setHint("No response");
                userFrAnswer.setId(View.generateViewId());

                RelativeLayout.LayoutParams userFrAnswerLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                userFrAnswerLayParams.addRule(RelativeLayout.BELOW, yourAnsLabel.getId());
                userFrAnswerLayParams.leftMargin = 65;
                userFrAnswerLayParams.bottomMargin = 50;

                answersRelLay.addView(userFrAnswer, userFrAnswerLayParams);

                if(userAnswersCorrect[i] == false) {
                    TextView corrAnsLabel = new TextView(TakeQuiz.this);
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

                    TextView corrfrAns = new TextView(TakeQuiz.this);
                    corrfrAns.setText(quiz.questionList.get(i).frCorrectAnswer);
                    corrfrAns.setTextSize(20);

                    RelativeLayout.LayoutParams corrfrAnsParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    corrfrAnsParams.addRule(RelativeLayout.BELOW, corrAnsLabel.getId());
                    corrfrAnsParams.leftMargin = 65;
                    corrfrAnsParams.bottomMargin = 20;

                    answersRelLay.addView(corrfrAns, corrfrAnsParams);
                }
            }

            int j = i;
            questionsButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reviewingQuestionAfterQuiz = true;
                    setContentView(questionsResScrlView[j]);
                }
            });
        }


        setContentView(resultScrlView);
    }
}