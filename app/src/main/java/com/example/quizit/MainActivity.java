package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createNewQuiz(View view) {
        Intent intent = new Intent(this, NewQuiz.class);
        startActivity(intent);
    }

    public void goToUserQuizzes(View view) {
        ArrayList<String> list = new ArrayList<String>();
        Intent intent = new Intent(this, Quizzes.class);
        startActivity(intent);

    }
}