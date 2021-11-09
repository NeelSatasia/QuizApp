package com.example.quizit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.LineNumberReader;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.util.ArrayList;

public class Quizzes extends AppCompatActivity {

    ScrollView scrlView;

    RelativeLayout relLay;
    TextView yourQuizzesLabel;
    ArrayList<Button> quizzesBtn;
    ArrayList<QuizInfo> quizzes;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    Button takeQuizBtn;
    Button editQuizBtn;
    Button deleteQuizBtn;
    Button historyOfQuizBtn;

    TextView noQuizzesLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scrlView = new ScrollView(this);
        scrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        relLay = new RelativeLayout(this);
        relLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        relLay.setPadding(10, 0, 10, 0);

        scrlView.addView(relLay);

        yourQuizzesLabel = new TextView(this);
        yourQuizzesLabel.setText("Your Quizzes");
        yourQuizzesLabel.setTextSize(30);
        yourQuizzesLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams titleLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleLayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        titleLayParams.topMargin = 75;
        titleLayParams.bottomMargin = 50;

        relLay.addView(yourQuizzesLabel, titleLayParams);

        noQuizzesLabel = new TextView(this);
        noQuizzesLabel.setText("Empty!");
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
            newQuizBtn.setAllCaps(false);
            newQuizBtn.setText(quizzes.get(i).quizName);
            newQuizBtn.setTextColor(Color.WHITE);

            Drawable saveRsltBtnDrawable = newQuizBtn.getBackground();
            saveRsltBtnDrawable = DrawableCompat.wrap(saveRsltBtnDrawable);
            DrawableCompat.setTint(saveRsltBtnDrawable, Color.rgb(51, 173, 255));
            newQuizBtn.setBackground(saveRsltBtnDrawable);
            newQuizBtn.setTextColor(Color.WHITE);

            RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(quizzesBtn.size() == 0) {
                quizBtnLay.addRule(RelativeLayout.BELOW, yourQuizzesLabel.getId());
            } else {
                quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(quizzesBtn.size() - 1).getId());
            }
            quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
            quizBtnLay.leftMargin = 10;
            quizBtnLay.rightMargin = 10;
            quizBtnLay.bottomMargin = 15;

            quizzesBtn.add(newQuizBtn);
            quizzesBtn.get(quizzesBtn.size() - 1).setId(View.generateViewId());

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
                    historyOfQuizBtn = popupView.findViewById(R.id.histBtn);

                    takeQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Quizzes.this, TakeQuiz.class);

                            for(int i = 0; i < quizzes.size(); i++) {
                                if(newQuizBtn.getText().toString().equals(quizzes.get(i).quizName)) {
                                    takeQuiz(quizzes.get(i));
                                    break;
                                }
                            }

                            startActivity(intent);
                        }
                    });

                    editQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Quizzes.this, SaveQuiz.class);

                            for(int i = 0; i < quizzes.size(); i++) {
                                if(newQuizBtn.getText().toString().equals(quizzes.get(i).quizName)) {
                                    editQuiz(quizzes.get(i), i);
                                    break;
                                }
                            }

                            intent.putExtra("Previous Activity", "Quizzes");
                            startActivity(intent);
                        }
                    });

                    deleteQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int k = 0; k < quizzes.size(); k++) {
                                if(quizzes.get(k).quizName.equals(newQuizBtn.getText().toString())) {
                                    SharedPreferences sharedPreferences2 = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                                    editor.remove(quizzes.get(k).quizName).commit();
                                    editor.apply();

                                    quizzes.remove(k);
                                    quizzesBtn.remove(k);

                                    break;
                                }
                            }
                            relLay.removeView(newQuizBtn);

                            for(int k = 0; k < quizzes.size(); k++) {
                                RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                if(k == 0) {
                                    quizBtnLay.addRule(RelativeLayout.BELOW, yourQuizzesLabel.getId());
                                } else {
                                    quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(k - 1).getId());
                                }

                                quizBtnLay.leftMargin = 10;
                                quizBtnLay.rightMargin = 10;
                                quizBtnLay.bottomMargin = 15;

                                quizzesBtn.get(k).setLayoutParams(quizBtnLay);

                                quizzes.get(k).id = k;
                            }

                            saveData();

                            noQuizzesLabel();

                            alertDialog.dismiss();
                        }
                    });

                    historyOfQuizBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences sharedPreferences2 = getSharedPreferences("QuizHistoryName", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences2.edit();
                            editor.putString("Quiz Name", newQuizBtn.getText().toString());
                            editor.apply();

                            Intent intent = new Intent(Quizzes.this, QuizHistory.class);
                            startActivity(intent);
                        }
                    });
                }
            });

            relLay.addView(newQuizBtn, quizBtnLay);
        }

        noQuizzesLabel();

        setContentView(scrlView);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Quizzes.this, MainActivity.class);
        intent.putExtra("Previous Activity", "Quizzes");
        startActivity(intent);
    }

    public void noQuizzesLabel() {
        if(quizzes.isEmpty()) {
            RelativeLayout.LayoutParams noQuizLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            noQuizLabelLay.addRule(RelativeLayout.CENTER_IN_PARENT);
            noQuizLabelLay.addRule(RelativeLayout.BELOW, yourQuizzesLabel.getId());

            relLay.addView(noQuizzesLabel, noQuizLabelLay);
        }
    }

    public void takeQuiz(QuizInfo quiz) {
        SharedPreferences sharedPreferences = getSharedPreferences("TakeQuiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quiz);
        editor.putString("Quiz", json);
        editor.apply();
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
