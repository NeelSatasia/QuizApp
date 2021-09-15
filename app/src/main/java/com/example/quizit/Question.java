package com.example.quizit;

import java.io.Serializable;

public class Question implements Serializable {

    String question;
    String[] options;
    String correctAnswers;

    public Question(String quest, String[] opt, String correctAns) {
        question = quest;
        options = opt;
        correctAnswers = correctAns;
    }
}
