package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

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

import java.lang.reflect.Type;
import java.util.ArrayList;

public class QuizHistory extends AppCompatActivity {

    RelativeLayout mainRelLay;
    ScrollView scrlView;
    RelativeLayout listRelLay;

    TextView quizTitle;

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
        quizTitleParams.topMargin = 15;
        quizTitleParams.leftMargin = 10;
        quizTitleParams.rightMargin = 10;
        quizTitleParams.bottomMargin = 20;

        mainRelLay.addView(quizTitle, quizTitleParams);

        scrlView = new ScrollView(this);
        RelativeLayout.LayoutParams scrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        scrlViewParams.addRule(RelativeLayout.BELOW, quizTitle.getId());

        mainRelLay.addView(scrlView, scrlViewParams);

        listRelLay = new RelativeLayout(this);
        listRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        scrlView.addView(listRelLay);

        SharedPreferences sharedPreferences = getSharedPreferences("QuizzesHistory", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("QuizResultList", null);
        Type type = new TypeToken<ArrayList<ArrayList<QuizResult>>>() {}.getType();
        ArrayList<ArrayList<QuizResult>> quizzesHistory = gson.fromJson(json, type);

        if(quizzesHistory != null) {
            ArrayList<Button> quizBtnsList = new ArrayList<Button>();

            for (int i = 0; i < quizzesHistory.size(); i++) {
                Button quizHistoryBtn = new Button(this);
                quizHistoryBtn.setText(quizzesHistory.get(i).get(0).quizTitle);
                quizHistoryBtn.setId(View.generateViewId());

                Drawable saveRsltBtnDrawable = quizHistoryBtn.getBackground();
                saveRsltBtnDrawable = DrawableCompat.wrap(saveRsltBtnDrawable);
                DrawableCompat.setTint(saveRsltBtnDrawable, Color.rgb(0, 153, 255));
                quizHistoryBtn.setBackground(saveRsltBtnDrawable);
                quizHistoryBtn.setTextColor(Color.WHITE);

                quizBtnsList.add(quizHistoryBtn);

                RelativeLayout.LayoutParams quizHistoryBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    quizHistoryBtnParams.addRule(RelativeLayout.BELOW, quizBtnsList.get(i - 1).getId());
                }

                quizHistoryBtnParams.leftMargin = 10;
                quizHistoryBtnParams.rightMargin = 10;
                quizHistoryBtnParams.bottomMargin = 15;

                listRelLay.addView(quizHistoryBtn, quizHistoryBtnParams);
            }

        } else {

        }

        setContentView(mainRelLay);
    }
}