package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

        quizTitle = new TextView(this);
        quizTitle.setText("Quiz Title");
        quizTitle.setId(View.generateViewId());

        RelativeLayout.LayoutParams quizTitleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
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

        setContentView(mainRelLay);
    }
}