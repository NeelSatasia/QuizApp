package com.example.quizit;

import java.util.ArrayList;

public class QuizResult {

    String quizTitle;
    ArrayList<Question> questionList;
    String[] timeTook;
    ArrayList<Object>[] userAnswers;
    int userCorrectAnswers;

    public QuizResult(String qt, ArrayList<Question> questList, String[] totalTimeTook, ArrayList<Object>[] userAns, int userCorrectAns) {
        quizTitle = qt;
        questionList = questList;
        timeTook = totalTimeTook;
        userAnswers = userAns;
        userCorrectAnswers = userCorrectAns;
    }
}
