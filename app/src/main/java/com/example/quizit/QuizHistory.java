package com.example.quizit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuizHistory extends AppCompatActivity {

    RelativeLayout mainRelLay;
    ScrollView scrlView;
    RelativeLayout listRelLay;

    TextView quizTitle;

    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    AlertDialog.Builder confirmADB;
    AlertDialog confirmAD;

    Button clearAllResultBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainRelLay = new RelativeLayout(this);
        mainRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        SharedPreferences sharedPreferences2 = getSharedPreferences("QuizHistoryName", MODE_PRIVATE);
        String quizName = sharedPreferences2.getString("Quiz Name", "");

        quizTitle = new TextView(this);
        quizTitle.setText(quizName);
        quizTitle.setId(View.generateViewId());
        quizTitle.setTextSize(25);

        RelativeLayout.LayoutParams quizTitleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        quizTitleParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        quizTitleParams.topMargin = 25;
        quizTitleParams.leftMargin = 10;
        quizTitleParams.rightMargin = 10;
        quizTitleParams.bottomMargin = 20;

        mainRelLay.addView(quizTitle, quizTitleParams);

        clearAllResultBtn = new Button(this);
        clearAllResultBtn.setText("Clear All");

        Drawable buttonDrawable2 = clearAllResultBtn.getBackground();
        buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
        DrawableCompat.setTint(buttonDrawable2, Color.RED);
        clearAllResultBtn.setBackground(buttonDrawable2);
        clearAllResultBtn.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams clearAllBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        clearAllBtnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        clearAllBtnParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        clearAllBtnParams.rightMargin = 10;
        clearAllBtnParams.bottomMargin = 10;

        mainRelLay.addView(clearAllResultBtn, clearAllBtnParams);

        scrlView = new ScrollView(this);
        RelativeLayout.LayoutParams scrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        scrlViewParams.addRule(RelativeLayout.BELOW, quizTitle.getId());

        mainRelLay.addView(scrlView, scrlViewParams);

        listRelLay = new RelativeLayout(this);
        listRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        scrlView.addView(listRelLay);

        SharedPreferences sharedPreferences = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(quizName, null);
        Type type = new TypeToken<ArrayList<QuizResult>>() {}.getType();
        ArrayList<QuizResult> quizHistory = gson.fromJson(json, type);

        if(quizHistory != null) {
            ArrayList<Button> quizBtnsList = new ArrayList<Button>();

            for (int i = 0; i < quizHistory.size(); i++) {
                QuizResult quizResult = quizHistory.get(i);

                Button quizHistoryBtn = new Button(this);
                quizHistoryBtn.setAllCaps(false);
                quizHistoryBtn.setText("Result: " + quizHistory.get(i).userCorrectAnswers + " of " + quizHistory.get(i).questionList.size());
                quizHistoryBtn.setId(View.generateViewId());

                Drawable saveRsltBtnDrawable = quizHistoryBtn.getBackground();
                saveRsltBtnDrawable = DrawableCompat.wrap(saveRsltBtnDrawable);
                DrawableCompat.setTint(saveRsltBtnDrawable, Color.rgb(230, 230, 230));
                quizHistoryBtn.setBackground(saveRsltBtnDrawable);
                quizHistoryBtn.setTextColor(Color.BLACK);

                quizBtnsList.add(quizHistoryBtn);

                RelativeLayout.LayoutParams quizHistoryBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    quizHistoryBtnParams.addRule(RelativeLayout.BELOW, quizBtnsList.get(i - 1).getId());
                }

                quizHistoryBtnParams.leftMargin = 25;
                quizHistoryBtnParams.rightMargin = 25;
                quizHistoryBtnParams.bottomMargin = 10;

                listRelLay.addView(quizHistoryBtn, quizHistoryBtnParams);

                quizHistoryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogBuilder = new AlertDialog.Builder(QuizHistory.this);
                        View popupView = getLayoutInflater().inflate(R.layout.quizresultpopup, null);

                        alertDialogBuilder.setView(popupView);
                        alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                        TextView quizNameLabel = popupView.findViewById(R.id.result_quiz_name_label);
                        quizNameLabel.setText(quizName);

                        Button viewQuizResultBtn = popupView.findViewById(R.id.view);
                        Button deleteQuizResultBtn = popupView.findViewById(R.id.delete);

                        viewQuizResultBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SharedPreferences sharedPreferences = getSharedPreferences("Quiz Result", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(quizResult);
                                editor.putString("Quiz", json).commit();
                                editor.apply();

                                Intent intent = new Intent(QuizHistory.this, ViewQuizResult.class);
                                startActivity(intent);
                            }
                        });

                        deleteQuizResultBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmADB = new AlertDialog.Builder(QuizHistory.this);
                                View popupView = getLayoutInflater().inflate(R.layout.confirmationpopup, null);

                                confirmADB.setView(popupView);
                                confirmAD = confirmADB.create();
                                confirmAD.show();

                                Button confirmDeleteBtn = popupView.findViewById(R.id.confirm_delete);
                                Button confirmCancelBtn = popupView.findViewById(R.id.confirm_cancel);

                                confirmDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        quizHistory.remove(quizResult);
                                        quizBtnsList.remove(quizHistoryBtn);

                                        SharedPreferences sharedPreferences = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        if(quizHistory.isEmpty()) {
                                            editor.remove(quizName).commit();
                                        } else {
                                            Gson gson = new Gson();
                                            String json = gson.toJson(quizHistory);
                                            editor.putString(quizName, json).commit();
                                        }

                                        editor.apply();

                                        if(quizHistory.isEmpty()) {
                                            Intent intent = new Intent(QuizHistory.this, Quizzes.class);
                                            startActivity(intent);
                                        }

                                        alertDialog.dismiss();

                                        listRelLay.removeAllViews();

                                        for(int j = 0; j < quizHistory.size(); j++) {
                                            RelativeLayout.LayoutParams quizHistoryBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                            if (j > 0) {
                                                quizHistoryBtnParams.addRule(RelativeLayout.BELOW, quizBtnsList.get(j - 1).getId());
                                            }

                                            quizHistoryBtnParams.leftMargin = 10;
                                            quizHistoryBtnParams.rightMargin = 10;
                                            quizHistoryBtnParams.bottomMargin = 10;

                                            listRelLay.addView(quizBtnsList.get(j), quizHistoryBtnParams);
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
                    }
                });
            }

            clearAllResultBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(quizHistory.isEmpty() == false) {
                        confirmADB = new AlertDialog.Builder(QuizHistory.this);
                        View popupView = getLayoutInflater().inflate(R.layout.confirmationpopup, null);

                        confirmADB.setView(popupView);
                        confirmAD = confirmADB.create();
                        confirmAD.show();

                        Button confirmDeleteBtn = popupView.findViewById(R.id.confirm_delete);
                        Button confirmCancelBtn = popupView.findViewById(R.id.confirm_cancel);

                        confirmDeleteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                quizHistory.clear();

                                SharedPreferences sharedPreferences = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(quizName).commit();
                                editor.apply();

                                listRelLay.removeAllViews();

                                emptyLabel();

                                confirmAD.dismiss();
                            }
                        });

                        confirmCancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                confirmAD.dismiss();
                            }
                        });
                    }
                }
            });
        } else {
            emptyLabel();
        }

        setContentView(mainRelLay);
    }

    public void emptyLabel() {
        TextView emptyLabel = new TextView(this);
        emptyLabel.setText("Empty!");
        emptyLabel.setTextSize(15);

        RelativeLayout.LayoutParams emptyLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        emptyLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        emptyLabelParams.addRule(RelativeLayout.BELOW, quizTitle.getId());
        emptyLabelParams.topMargin = 20;

        listRelLay.addView(emptyLabel, emptyLabelParams);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Quizzes.class);
        startActivity(intent);
    }
}