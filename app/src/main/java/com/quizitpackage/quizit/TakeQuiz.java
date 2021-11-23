package com.quizitpackage.quizit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
    ArrayList<ArrayList<String>> userAnswers;
    boolean[] userAnswersCorrect;

    RelativeLayout quizRelLay;
    LinearLayout trackerLay;
    TextView quizNameLabel;
    TextView questionLabel;
    TextView questionTracker;
    TextView quizTimerLabel;
    RelativeLayout questionRelLay;
    ImageButton nextQues;
    ImageButton backQues;
    Button submitQuizBtn;
    ScrollView optionScrlView;

    int currentQuestionIndex = 0;

    boolean finishedQuiz = false;

    CountDownTimer quizTimer;
    long totalTimeInMillis;
    boolean isQuizTimerRunning = false;

    int currentQuest_afterSubmission = 0;

    AlertDialog.Builder confirmADB;
    AlertDialog confirmAD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        loadQuiz();

        questionsOptionsRelLays = new RelativeLayout[quiz.questionList.size()];

        userAnswers = new ArrayList<>();
        userAnswersCorrect = new boolean[quiz.questionList.size()];

        for(int i = 0; i < quiz.questionList.size(); i++) {
            userAnswers.add(new ArrayList<>());

            if(!quiz.questionList.get(i).mcQuestion) {
                userAnswers.get(userAnswers.size() - 1).add("");
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
                                userAnswers.get(h).add(j + "");
                            } else if(userAnswers.get(h).contains(j + "")){
                                userAnswers.get(h).remove(j + "");
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
                        userAnswers.get(h).set(0, frAns.getText().toString());
                    }
                });
            }
        }

        quizRelLay = findViewById(R.id.take_quiz_rel_lay);
        trackerLay = findViewById(R.id.btns);

        quizNameLabel = findViewById(R.id.quizTitleLabel);
        quizNameLabel.setText(quiz.quizName);

        questionLabel = findViewById(R.id.questionLabel);

        questionTracker = findViewById(R.id.questTracker);

        questionRelLay = findViewById(R.id.mainRelLay);

        optionScrlView = findViewById(R.id.mainScrlView);

        nextQues = findViewById(R.id.nextQuesBtn);
        backQues = findViewById(R.id.backQuesBtn);
        submitQuizBtn = findViewById(R.id.sbtBtn);

        quizTimerLabel = findViewById(R.id.timr);

        displayQuestion(currentQuestionIndex);

        nextQues.setOnClickListener(view -> {
            if(currentQuestionIndex + 1 < quiz.questionList.size()) {
                currentQuestionIndex++;
                displayQuestion(currentQuestionIndex);
            }
        });

        backQues.setOnClickListener(view -> {
            if(currentQuestionIndex - 1 >= 0) {
                currentQuestionIndex--;
                displayQuestion(currentQuestionIndex);
            }
        });

        submitQuizBtn.setOnClickListener(view -> submitQuiz());

        long hrInMillis = (long) Integer.parseInt(quiz.timer[0]) * 60 * 60 * 1000;
        long mins = (long) Integer.parseInt(quiz.timer[1]) * 60 * 1000;
        long secs = Integer.parseInt(quiz.timer[2]) * 1000L;

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
        } else {
            RelativeLayout.LayoutParams quizTitleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            quizTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            quizTitleParams.topMargin = 20;
            quizTitleParams.bottomMargin = 30;

            quizNameLabel.setLayoutParams(quizTitleParams);

            RelativeLayout.LayoutParams trackerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            trackerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            trackerParams.addRule(RelativeLayout.BELOW, quizNameLabel.getId());
            trackerParams.bottomMargin = 50;

            trackerLay.setLayoutParams(trackerParams);

            RelativeLayout.LayoutParams questLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            questLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            questLabelParams.addRule(RelativeLayout.BELOW, trackerLay.getId());
            questLabelParams.leftMargin = 20;
            questLabelParams.rightMargin = 20;
            questLabelParams.bottomMargin = 15;

            questionLabel.setLayoutParams(questLabelParams);

            RelativeLayout.LayoutParams optionScrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            optionScrlViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            optionScrlViewParams.addRule(RelativeLayout.BELOW, questionLabel.getId());
            optionScrlViewParams.addRule(RelativeLayout.ALIGN_LEFT, questionLabel.getId());
            optionScrlViewParams.addRule(RelativeLayout.ALIGN_RIGHT, questionLabel.getId());
            optionScrlViewParams.bottomMargin = 30;

            optionScrlView.setLayoutParams(optionScrlViewParams);

            RelativeLayout.LayoutParams sbtBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            sbtBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            sbtBtnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            sbtBtnParams.rightMargin = 30;
            sbtBtnParams.bottomMargin = 20;

            submitQuizBtn.setLayoutParams(sbtBtnParams);
        }
    }

    public void loadQuiz() {
        SharedPreferences sharedPreferences = getSharedPreferences("TakeQuiz", MODE_PRIVATE);
        String quizName = sharedPreferences.getString("Quiz", null);

        SharedPreferences sharedPreferences2 = getSharedPreferences("Quizzes", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences2.getString(quizName, null);
        Type type = new TypeToken<QuizInfo>() {}.getType();
        quiz = gson.fromJson(json, type);
    }

    public void displayQuestion(int index) {
        questionRelLay.removeAllViews();
        questionTracker.setText("Question: " + (index + 1) + " of " + quiz.questionList.size());
        questionLabel.setText(quiz.questionList.get(index).question);

        questionRelLay.addView(questionsOptionsRelLays[index]);
    }

    public void checkAnswers() {
        for(int i = 0; i < userAnswers.size(); i++) {
            if(quiz.questionList.get(i).mcQuestion) {
                for(int j = 0; j < userAnswers.get(i).size(); j++) {
                    if(quiz.questionList.get(i).correctAnswers.size() == userAnswers.get(i).size()) {
                        int userAnswerIndex = Integer.parseInt(userAnswers.get(i).get(j));
                        if (quiz.questionList.get(i).correctAnswers.contains(userAnswerIndex)) {
                            userAnswersCorrect[i] = true;
                        } else {
                            userAnswersCorrect[i] = false;
                            break;
                        }
                    }
                }
            } else {
                if(userAnswers.get(i).get(0).equals(quiz.questionList.get(i).frCorrectAnswer)) {
                    userAnswersCorrect[i] = true;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        confirmADB = new AlertDialog.Builder(this);
        View confirmPopup = getLayoutInflater().inflate(R.layout.confirmationpopup, null);

        confirmADB.setView(confirmPopup);
        confirmAD = confirmADB.create();
        confirmAD.show();

        Button okBtn = confirmPopup.findViewById(R.id.confirm_ok);
        Button cancelBtn = confirmPopup.findViewById(R.id.confirm_cancel);

        okBtn.setOnClickListener(view -> {
            if(isQuizTimerRunning) {
                quizTimer.cancel();
            }

            confirmAD.dismiss();

            TakeQuiz.this.finish();

            Intent intent = new Intent(TakeQuiz.this, MainActivity.class);
            startActivity(intent);
        });

        cancelBtn.setOnClickListener(view -> confirmAD.dismiss());
    }

    public void submitQuiz() {
        if(isQuizTimerRunning) {
            isQuizTimerRunning = false;
            quizTimer.cancel();
        }

        finishedQuiz = true;
        checkAnswers();

        RelativeLayout resultLay = new RelativeLayout(TakeQuiz.this);
        resultLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        ImageButton saveResultBtn = new ImageButton(this);
        saveResultBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle));
        saveResultBtn.setImageResource(R.drawable.ic_save);
        saveResultBtn.setId(View.generateViewId());

        RelativeLayout.LayoutParams saveResultsBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        saveResultsBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        saveResultsBtnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        saveResultsBtnParams.bottomMargin = 30;
        saveResultsBtnParams.rightMargin = 30;

        resultLay.addView(saveResultBtn, saveResultsBtnParams);

        int totalCorrectAnswers = 0;

        for(int i = 0; i < userAnswersCorrect.length; i++) {
            if(userAnswersCorrect[i]) {
                totalCorrectAnswers++;
            }
        }
        int totalCorrectAnswers1 = totalCorrectAnswers;

        saveResultBtn.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPreferences.getString(quiz.quizName, null);
            Type type = new TypeToken<ArrayList<QuizResult>>() {}.getType();
            ArrayList<QuizResult> quizHistory = gson.fromJson(json, type);

            QuizResult newQuizRes = new QuizResult(quiz.quizName, quiz.questionList, userAnswers, userAnswersCorrect, totalCorrectAnswers1);

            if(quizHistory == null) {
                quizHistory = new ArrayList<>();
            }

            quizHistory.add(newQuizRes);

            SharedPreferences sharedPreferences2 = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences2.edit();
            Gson gson2 = new Gson();
            String json2 = gson2.toJson(quizHistory);
            editor.putString(quiz.quizName, json2).commit();
            editor.apply();

            Intent intent = new Intent(TakeQuiz.this, MainActivity.class);
            intent.putExtra("Previous Activity", "TakeQuiz");
            startActivity(intent);
        });

        TextView resultLabel = new TextView(this);
        resultLabel.setText("Result: " + totalCorrectAnswers + " of " + quiz.questionList.size());
        resultLabel.setTextSize(25);
        resultLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams resultLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        resultLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        resultLabelParams.topMargin = 15;
        resultLabelParams.leftMargin = 10;
        resultLabelParams.rightMargin = 10;
        resultLabelParams.bottomMargin = 20;

        resultLay.addView(resultLabel, resultLabelParams);

        LinearLayout topLay = new LinearLayout(this);
        topLay.setId(View.generateViewId());
        topLay.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams topLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        topLayParams.addRule(RelativeLayout.BELOW, resultLabel.getId());
        topLayParams.topMargin = 15;
        topLayParams.leftMargin = 10;
        topLayParams.rightMargin = 10;

        resultLay.addView(topLay, topLayParams);

        ImageButton backBtn = new ImageButton(this);
        backBtn.setBackgroundColor(Color.WHITE);
        backBtn.setImageResource(R.drawable.ic_back);
        backBtn.setId(View.generateViewId());

        LinearLayout.LayoutParams backBtnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        backBtnParams.rightMargin = 30;

        topLay.addView(backBtn, backBtnParams);

        TextView questNumLabel = new TextView(this);
        questNumLabel.setText("Question Tracker");
        questNumLabel.setTextSize(20);
        questNumLabel.setId(View.generateViewId());

        LinearLayout.LayoutParams questNumParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        questNumParams.rightMargin = 30;

        topLay.addView(questNumLabel, questNumParams);

        ImageButton nextBtn = new ImageButton(this);
        nextBtn.setBackgroundColor(Color.WHITE);
        nextBtn.setImageResource(R.drawable.ic_next);
        nextBtn.setId(View.generateViewId());

        topLay.addView(nextBtn);

        questNumLabel.setText("Question: " + (currentQuest_afterSubmission + 1) + " of " + quiz.questionList.size());
        ScrollView[] questionsResScrlView = new ScrollView[quiz.questionList.size()];

        for(int i = 0; i < questionsResScrlView.length; i++) {
            //question view
            questionsResScrlView[i] = new ScrollView(TakeQuiz.this);
            questionsResScrlView[i].setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            RelativeLayout mainQuesRelLay = new RelativeLayout(TakeQuiz.this);
            mainQuesRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

            questionsResScrlView[i].addView(mainQuesRelLay);

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
            questionLabelParams.topMargin = 25;
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
                TextView selectedAnsLabel = new TextView(this);
                selectedAnsLabel.setText("Selected Answer(s):");
                selectedAnsLabel.setTextSize(15);
                selectedAnsLabel.setId(View.generateViewId());

                RelativeLayout.LayoutParams selectedAnsParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                selectedAnsParams.leftMargin = 20;
                selectedAnsParams.bottomMargin = 30;

                answersRelLay.addView(selectedAnsLabel, selectedAnsParams);

                CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(i).options.length];

                for(int k = 0; k < optionsCB.length; k++) {
                    optionsCB[k] = new CheckBox(TakeQuiz.this);
                    optionsCB[k].setText(quiz.questionList.get(i).options[k]);
                    optionsCB[k].setId(View.generateViewId());
                    optionsCB[k].setClickable(false);
                    optionsCB[k].setFocusable(false);

                    if(userAnswers.get(i).contains(k + "")) {
                        optionsCB[k].setChecked(true);
                    }

                    if(quiz.questionList.get(i).correctAnswers.contains((Integer) k)) {
                        optionsCB[k].setTextColor(Color.rgb(0, 128, 0));
                    }

                    if(!quiz.questionList.get(i).correctAnswers.contains((Integer) k) && userAnswers.get(i).contains(k + "")) {
                        optionsCB[k].setTextColor(Color.RED);
                    }

                    RelativeLayout.LayoutParams optionParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if(k > 0) {
                        optionParams.addRule(RelativeLayout.BELOW, optionsCB[k - 1].getId());
                    } else {
                        optionParams.addRule(RelativeLayout.BELOW, selectedAnsLabel.getId());
                    }

                    optionParams.leftMargin = 30;
                    optionParams.rightMargin = 30;
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
                userFrAnswer.setText(userAnswers.get(i).get(0));
                userFrAnswer.setTextSize(20);
                userFrAnswer.setHint("No response");
                userFrAnswer.setId(View.generateViewId());

                RelativeLayout.LayoutParams userFrAnswerLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                userFrAnswerLayParams.addRule(RelativeLayout.BELOW, yourAnsLabel.getId());
                userFrAnswerLayParams.leftMargin = 65;
                userFrAnswerLayParams.bottomMargin = 50;

                answersRelLay.addView(userFrAnswer, userFrAnswerLayParams);

                if(!userAnswersCorrect[i]) {
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

        resultLay.addView(questionsResScrlView[0], questionRelScrlViewParams);

        nextBtn.setOnClickListener(view -> {
            if(currentQuest_afterSubmission + 1 < questionsResScrlView.length) {
                resultLay.removeView(questionsResScrlView[currentQuest_afterSubmission]);
                currentQuest_afterSubmission++;

                RelativeLayout.LayoutParams nextQuestRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                nextQuestRelLayParams.addRule(RelativeLayout.BELOW, topLay.getId());
                nextQuestRelLayParams.topMargin = 15;
                nextQuestRelLayParams.leftMargin = 10;
                nextQuestRelLayParams.rightMargin = 10;

                resultLay.addView(questionsResScrlView[currentQuest_afterSubmission], nextQuestRelLayParams);
                questNumLabel.setText("Question: " + (currentQuest_afterSubmission + 1) + " of " + quiz.questionList.size());
            }
        });

        backBtn.setOnClickListener(view -> {
            if(currentQuest_afterSubmission - 1 >= 0) {
                resultLay.removeView(questionsResScrlView[currentQuest_afterSubmission]);
                currentQuest_afterSubmission--;

                RelativeLayout.LayoutParams previousQuestRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                previousQuestRelLayParams.addRule(RelativeLayout.BELOW, topLay.getId());
                previousQuestRelLayParams.topMargin = 15;
                previousQuestRelLayParams.leftMargin = 10;
                previousQuestRelLayParams.rightMargin = 10;

                resultLay.addView(questionsResScrlView[currentQuest_afterSubmission], previousQuestRelLayParams);
                questNumLabel.setText("Question: " + (currentQuest_afterSubmission + 1) + " of " + quiz.questionList.size());
            }
        });

        setContentView(resultLay);
    }
}