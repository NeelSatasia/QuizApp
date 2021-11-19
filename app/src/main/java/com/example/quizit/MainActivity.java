package com.example.quizit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RelativeLayout relLay;
    TextView yourQuizzesLabel;
    ArrayList<Button> quizzesBtn;
    ArrayList<String> quizzes;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    AlertDialog.Builder confirmADB;
    AlertDialog confirmAD;

    Button takeQuizBtn;
    Button editQuizBtn;
    Button deleteQuizBtn;
    Button historyOfQuizBtn;

    TextView noQuizzesLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadQuizzes();
    }

    public void createNewQuiz(View view) {
        Intent intent = new Intent(this, SaveQuiz.class);
        intent.putExtra("Previous Activity", "Main");
        startActivity(intent);
    }

    public void noQuizzesLabel() {
        RelativeLayout.LayoutParams noQuizLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        noQuizLabelLay.addRule(RelativeLayout.CENTER_IN_PARENT);
        noQuizLabelLay.addRule(RelativeLayout.BELOW, yourQuizzesLabel.getId());

        relLay.addView(noQuizzesLabel, noQuizLabelLay);
    }

    public void takeQuiz(String quizName) {
        SharedPreferences sharedPreferences2 = getSharedPreferences("TakeQuiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        editor.putString("Quiz", quizName).commit();
        editor.apply();
    }

    public void editQuiz(String quizName) {
        SharedPreferences sharedPreferences = getSharedPreferences("EditQuiz", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Quiz", quizName);
        editor.apply();
    }

    public void saveData(String quizName) {
        SharedPreferences sharedPreferences = getSharedPreferences("Quizzes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(quizName).commit();
        editor.apply();
    }

    public boolean loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("Quizzes", MODE_PRIVATE);
        if(sharedPreferences.getAll().isEmpty()) {
            return false;
        } else {
            quizzes = new ArrayList<>();

            Map<String, ?> keys = sharedPreferences.getAll();

            for(Map.Entry<String,?> entry : keys.entrySet()){
                quizzes.add(entry.getKey());
            }

            return true;
        }
    }

    public void loadQuizzes() {
        relLay = findViewById(R.id.quizzes_layout);

        yourQuizzesLabel = new TextView(this);
        yourQuizzesLabel.setText("Your Quizzes");
        yourQuizzesLabel.setTextSize(30);
        yourQuizzesLabel.setId(View.generateViewId());

        RelativeLayout.LayoutParams titleLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleLayParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        titleLayParams.topMargin = 20;
        titleLayParams.bottomMargin = 30;

        relLay.addView(yourQuizzesLabel, titleLayParams);

        noQuizzesLabel = new TextView(this);
        noQuizzesLabel.setText("Empty!");
        noQuizzesLabel.setTextSize(20);

        if(loadData()) {
            quizzesBtn = new ArrayList<Button>();

            for (int i = 0; i < quizzes.size(); i++) {
                Button newQuizBtn = new Button(this);
                newQuizBtn.setAllCaps(false);
                newQuizBtn.setText(quizzes.get(i));
                newQuizBtn.setTextColor(Color.WHITE);

                Drawable saveRsltBtnDrawable = newQuizBtn.getBackground();
                saveRsltBtnDrawable = DrawableCompat.wrap(saveRsltBtnDrawable);
                DrawableCompat.setTint(saveRsltBtnDrawable, Color.rgb(51, 173, 255));
                newQuizBtn.setBackground(saveRsltBtnDrawable);
                newQuizBtn.setTextColor(Color.WHITE);

                RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (quizzesBtn.size() == 0) {
                    quizBtnLay.addRule(RelativeLayout.BELOW, yourQuizzesLabel.getId());
                } else {
                    quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(quizzesBtn.size() - 1).getId());
                }
                quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
                quizBtnLay.leftMargin = 20;
                quizBtnLay.rightMargin = 20;
                quizBtnLay.bottomMargin = 15;

                quizzesBtn.add(newQuizBtn);
                quizzesBtn.get(quizzesBtn.size() - 1).setId(View.generateViewId());

                newQuizBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
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
                                Intent intent = new Intent(MainActivity.this, TakeQuiz.class);

                                takeQuiz(newQuizBtn.getText().toString());

                                startActivity(intent);
                            }
                        });

                        editQuizBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(MainActivity.this, SaveQuiz.class);

                                editQuiz(newQuizBtn.getText().toString());

                                intent.putExtra("Previous Activity", "Quizzes");
                                startActivity(intent);
                            }
                        });

                        deleteQuizBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmADB = new AlertDialog.Builder(MainActivity.this);
                                View popupView = getLayoutInflater().inflate(R.layout.confirmationpopup, null);

                                confirmADB.setView(popupView);
                                confirmAD = confirmADB.create();
                                confirmAD.show();

                                Button confirmDeleteBtn = popupView.findViewById(R.id.confirm_delete);
                                Button confirmCancelBtn = popupView.findViewById(R.id.confirm_cancel);

                                confirmDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        SharedPreferences sharedPreferences2 = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences2.edit();
                                        editor.remove(newQuizBtn.getText().toString()).commit();
                                        editor.apply();

                                        quizzes.remove(newQuizBtn.getText().toString());
                                        saveData(newQuizBtn.getText().toString());
                                        quizzesBtn.remove(newQuizBtn);

                                        relLay.removeView(newQuizBtn);

                                        for (int k = 0; k < quizzes.size(); k++) {
                                            RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                            quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                            if (k == 0) {
                                                quizBtnLay.addRule(RelativeLayout.BELOW, yourQuizzesLabel.getId());
                                            } else {
                                                quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(k - 1).getId());
                                            }

                                            quizBtnLay.leftMargin = 10;
                                            quizBtnLay.rightMargin = 10;
                                            quizBtnLay.bottomMargin = 15;

                                            quizzesBtn.get(k).setLayoutParams(quizBtnLay);
                                        }

                                        if(quizzes.isEmpty()) {
                                            noQuizzesLabel();
                                        }

                                        confirmAD.dismiss();
                                        alertDialog.dismiss();
                                    }
                                });

                                confirmCancelBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        confirmAD.dismiss();
                                    }
                                });
                            }
                        });

                        historyOfQuizBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences sharedPreferences = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);

                                if(sharedPreferences.contains(newQuizBtn.getText().toString())) {

                                    SharedPreferences sharedPreferences2 = getSharedPreferences("QuizHistoryName", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                                    editor.putString("Quiz Name", newQuizBtn.getText().toString());
                                    editor.apply();

                                    Intent intent = new Intent(MainActivity.this, QuizHistory.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(MainActivity.this, "No results saved", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                relLay.addView(newQuizBtn, quizBtnLay);
            }
        } else {
            noQuizzesLabel();
        }
    }
}