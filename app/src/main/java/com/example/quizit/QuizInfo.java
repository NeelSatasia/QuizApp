package com.example.quizit;

import java.io.Serializable;
import java.util.ArrayList;

public class QuizInfo implements Serializable {

    String quizName;
    ArrayList<Question> questionList;
    String[] timer;
    int id;

    public QuizInfo(String quizTitle, ArrayList<Question> questList, String[] timr, int inputID) {
        questionList = questList;
        quizName = quizTitle;
        timer = timr;
        id = inputID;
    }
}
