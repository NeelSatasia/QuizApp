package com.example.quizit;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizInfo implements Serializable {

    String quizName;
    ArrayList<Question> questionList;

    public QuizInfo(String quizTitle, ArrayList<Question> questList) {
        questionList = questList;
        quizName = quizTitle;
    }
}
