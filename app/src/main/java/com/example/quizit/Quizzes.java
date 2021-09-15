package com.example.quizit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Quizzes extends AppCompatActivity {

    private RelativeLayout relLay;
    private ArrayList<Button> quizzesBtn;
    private ArrayList<QuizInfo> quizzes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_quizzes);

        relLay = findViewById(R.id.userQuizzesRelLay);

        quizzesBtn = new ArrayList<Button>();

        loadData();

        Bundle bundleObj = getIntent().getExtras();
        if(bundleObj != null) {
            quizzes.add((QuizInfo) bundleObj.getSerializable("QuizzesList"));

            saveData();
        }

        for(int i = 0; i < quizzes.size(); i++) {
            Button newQuizBtn = new Button(this);
            newQuizBtn.setText(quizzes.get(i).quizName);
            newQuizBtn.setId(View.generateViewId());

            RelativeLayout.LayoutParams quizBtnLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if(quizzesBtn.size() == 0) {
                quizBtnLay.addRule(RelativeLayout.BELOW, R.id.yourQuizzesLabel);
            } else {
                quizBtnLay.addRule(RelativeLayout.BELOW, quizzesBtn.get(quizzesBtn.size() - 1).getId());
            }
            quizBtnLay.addRule(RelativeLayout.CENTER_HORIZONTAL);
            quizBtnLay.bottomMargin = 10;

            quizzesBtn.add(newQuizBtn);

            relLay.addView(newQuizBtn, quizBtnLay);
        }

        if(quizzes.isEmpty()) {
            TextView noQuizzesLabel = new TextView(this);
            noQuizzesLabel.setText("No Quizzes Found!");
            noQuizzesLabel.setTextSize(15);

            RelativeLayout.LayoutParams noQuizLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            noQuizLabelLay.addRule(RelativeLayout.CENTER_IN_PARENT);
            noQuizLabelLay.addRule(RelativeLayout.BELOW, R.id.yourQuizzesLabel);

            relLay.addView(noQuizzesLabel, noQuizLabelLay);
        }
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quizzes);
        editor.putString("Quizzes List", json);
        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Quizzes List", null);
        Type type = new TypeToken<ArrayList<QuizInfo>>() {}.getType();
        quizzes = gson.fromJson(json, type);

        if(quizzes == null) {
            quizzes = new ArrayList<QuizInfo>();
        }
    }
}
