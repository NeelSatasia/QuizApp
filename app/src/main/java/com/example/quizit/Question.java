package com.example.quizit;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {

    String question;
    String[] options;
    ArrayList<Integer> correctAnswers;
    String frCorrectAnswer;
    boolean mcQuestion;

    public Question(String quest, String[] opt, ArrayList<Integer> correctAns, String frCA, boolean mcQues) {
        question = quest;
        options = opt;
        correctAnswers = correctAns;
        mcQuestion = mcQues;
        frCorrectAnswer = frCA;
    }
}
