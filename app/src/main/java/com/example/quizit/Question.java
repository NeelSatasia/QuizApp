package com.example.quizit;

public class Question {

    String question;
    String[] options;
    String correctAnswers;

    public Question(String quest, String[] opt, String correctAns) {
        question = quest;
        options = opt;
        correctAnswers = correctAns;
    }
}
