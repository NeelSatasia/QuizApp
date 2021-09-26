package com.example.quizit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.LineNumberReader;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.util.ArrayList;

public class Quizzes extends AppCompatActivity {

    RelativeLayout relLay;
    TextView yourQuizzesLabel;
    ArrayList<Button> quizzesBtn;
    ArrayList<QuizInfo> quizzes;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    Button takeQuizBtn;
    Button editQuizBtn;
    Button deleteQuizBtn;

    TextView noQuizzesLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_quizzes);

        relLay = findViewById(R.id.userQuizzesRelLay);
        yourQuizzesLabel = findViewById(R.id.yourQuizzesLabel);

        noQuizzesLabel = new TextView(this);
        noQuizzesLabel.setText("No Quizzes Found!");
        noQuizzesLabel.setTextSize(15);

        loadData();

        Bundle bundleObj = getIntent().getExtras();
        if(bundleObj != null) {
            quizzes.add((QuizInfo) bundleObj.getSerializable("QuizzesList"));

            saveData();
        }

        quizzesBtn = new ArrayList<Button>();

        for(int i = 0; i < quizzes.size(); i++) {
            Button newQuizBtn = new Button(this);
            newQuizBtn.setText(quizzes.get(i).quizName);


            RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(quizzesBtn.size() == 0) {
                quizBtnLay.addRule(RelativeLayout.BELOW, R.id.yourQuizzesLabel);
            } else {
                quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(quizzesBtn.size() - 1).getId());
            }
            quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
            quizBtnLay.bottomMargin = 10;

            quizzesBtn.add(newQuizBtn);
            quizzesBtn.get(quizzesBtn.size() - 1).setId(View.generateViewId());
            int j = i;
            newQuizBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialogBuilder = new AlertDialog.Builder(Quizzes.this);
                    View popupView = getLayoutInflater().inflate(R.layout.quizoptionpopup, null);

                    alertDialogBuilder.setView(popupView);
                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    takeQuizBtn = popupView.findViewById(R.id.takeQuizBtn);
                    editQuizBtn = popupView.findViewById(R.id.editQuizBtn);
                    deleteQuizBtn = popupView.findViewById(R.id.deleteQuizBtn);

                    takeQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    editQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Quizzes.this, SaveQuiz.class);

                            for(int i = 0; i < quizzes.size(); i++) {
                                if(newQuizBtn.getText().toString().equals(quizzes.get(i).quizName)) {
                                    editQuiz(quizzes.get(i), i);
                                }
                            }
                            startActivity(intent);
                        }
                    });

                    deleteQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int k = 0; k < quizzes.size(); k++) {
                                if(quizzes.get(k).quizName.equals(newQuizBtn.getText().toString())) {
                                    quizzes.remove(k);
                                    quizzesBtn.remove(k);
                                    break;
                                }
                            }
                            relLay.removeView(newQuizBtn);

                            for(int k = 0; k < quizzes.size(); k++) {
                                RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                if(k == 0) {
                                    quizBtnLay.addRule(RelativeLayout.BELOW, R.id.yourQuizzesLabel);
                                } else {
                                    quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(k - 1).getId());
                                }

                                quizzesBtn.get(k).setLayoutParams(quizBtnLay);
                            }

                            saveData();

                            noQuizzesLabel();

                            alertDialog.dismiss();
                        }
                    });
                }
            });

            relLay.addView(newQuizBtn, quizBtnLay);
        }

        noQuizzesLabel();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Quizzes.this, MainActivity.class);
        startActivity(intent);
    }

    public void noQuizzesLabel() {
        if(quizzes.isEmpty()) {
            RelativeLayout.LayoutParams noQuizLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            noQuizLabelLay.addRule(RelativeLayout.CENTER_IN_PARENT);
            noQuizLabelLay.addRule(RelativeLayout.BELOW, R.id.yourQuizzesLabel);

            relLay.addView(noQuizzesLabel, noQuizLabelLay);
        }
    }

    public void editQuiz(QuizInfo quiz, int index) {
        SharedPreferences sharedPreferences = getSharedPreferences("EditQuiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quiz);
        editor.putString("Quiz", json);
        editor.putInt("QuizID", index);
        editor.apply();
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

        if(quizzes == null) {
            quizzes = new ArrayList<QuizInfo>();
        }
    }
}
