package com.example.quizit;

import java.util.ArrayList;

public class QuizResult {

    String quizTitle;
    ArrayList<Question> questionList;
    String[] timeTook;
    ArrayList<ArrayList<String>> userAnswers;
    boolean [] isUserAnswersCorrect;
    int userCorrectAnswers;

    public QuizResult(String qt, ArrayList<Question> questList, String[] totalTimeTook, ArrayList<ArrayList<String>> userAns, boolean[] isUserAnsCorrect, int userCorrectAns) {
        quizTitle = qt;
        userAnswers = userAns;
        questionList = questList;
        timeTook = totalTimeTook;
        isUserAnswersCorrect = isUserAnsCorrect;
        userCorrectAnswers = userCorrectAns;
    }
}
