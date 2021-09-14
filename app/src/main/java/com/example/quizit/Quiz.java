package com.example.quizit;

import java.util.ArrayList;

public class Quiz {

    String quizName;
    ArrayList<Question> questionList;

    public Quiz(String quizTitle, ArrayList<Question> questList) {
        questionList = questList;
        quizName = quizTitle;
    }
}
