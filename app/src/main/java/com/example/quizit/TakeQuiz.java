package com.example.quizit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TakeQuiz extends AppCompatActivity {

    QuizInfo quiz;

    ArrayList<Object>[] userAnswers;
    Boolean[] userAnswersCorrect;

    TextView quizNameLabel;
    TextView questionLabel;
    TextView questionNumber;
    TextView questionTracker;
    RelativeLayout questionRelLay;
    Button nextQues;
    Button backQues;
    Button submitQuizBtn;

    int currentQuestionIndex = 0;

    boolean finishedQuiz = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        loadQuiz();

        userAnswers = new ArrayList[quiz.questionList.size()];
        userAnswersCorrect = new Boolean[userAnswers.length];

        for(int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = new ArrayList<Object>();

            if(quiz.questionList.get(i).mcQuestion == false) {
                userAnswers[i].add((String) "");
            }
        }

        quizNameLabel = findViewById(R.id.quizTitleLabel);
        quizNameLabel.setText(quiz.quizName);

        questionLabel = findViewById(R.id.questionLabel);
        questionLabel.setText(quiz.questionList.get(0).question);

        questionNumber = findViewById(R.id.quesNum);
        questionTracker = findViewById(R.id.questTracker);

        questionRelLay = findViewById(R.id.mainRelLay);

        nextQues = findViewById(R.id.nextQuesBtn);
        backQues = findViewById(R.id.backQuesBtn);
        submitQuizBtn = findViewById(R.id.sbtBtn);

        displayQuestion(currentQuestionIndex);

        nextQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentQuestionIndex + 1 < quiz.questionList.size()) {
                    currentQuestionIndex++;
                    displayQuestion(currentQuestionIndex);
                }
            }
        });

        backQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentQuestionIndex - 1 >= 0) {
                    currentQuestionIndex--;
                    displayQuestion(currentQuestionIndex);
                }
            }
        });

        submitQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishedQuiz = true;
                checkAnswers();

                ScrollView resultScrlView = new ScrollView(TakeQuiz.this);
                resultScrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                RelativeLayout resultLay = new RelativeLayout(TakeQuiz.this);
                resultLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                resultScrlView.addView(resultLay);

                TextView resultLabel = new TextView(TakeQuiz.this);
                resultLabel.setText("Result");
                resultLabel.setTextSize(40);
                resultLabel.setId(View.generateViewId());

                RelativeLayout.LayoutParams resultLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                resultLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                resultLabelParams.topMargin = 75;
                resultLabelParams.bottomMargin = 10;

                resultLay.addView(resultLabel, resultLabelParams);

                TextView scoreLabel = new TextView(TakeQuiz.this);

                int totalCorrectAnswers = 0;

                for(int i = 0; i < userAnswersCorrect.length; i++) {
                    if(userAnswersCorrect[i]) {
                        totalCorrectAnswers++;
                    }
                }

                scoreLabel.setText("Score: " + totalCorrectAnswers + " of " + quiz.questionList.size());
                scoreLabel.setTextSize(25);
                scoreLabel.setId(View.generateViewId());

                RelativeLayout.LayoutParams scoreLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                scoreLabelParams.addRule(RelativeLayout.BELOW, resultLabel.getId());
                scoreLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                scoreLabelParams.bottomMargin = 50;

                resultLay.addView(scoreLabel, scoreLabelParams);

                Button[] questionsButtons = new Button[quiz.questionList.size()];

                for(int i = 0; i < questionsButtons.length; i++) {
                    questionsButtons[i] = new Button(TakeQuiz.this);
                    questionsButtons[i].setText("Question " + (i + 1));
                    questionsButtons[i].setId(View.generateViewId());

                    Drawable buttonDrawable = questionsButtons[i].getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);

                    if(userAnswersCorrect[i]) {
                        DrawableCompat.setTint(buttonDrawable, Color.GREEN);
                    } else {
                        DrawableCompat.setTint(buttonDrawable, Color.RED);
                    }

                    questionsButtons[i].setBackground(buttonDrawable);

                    RelativeLayout.LayoutParams questBtnParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    questBtnParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                    if(i == 0) {
                        questBtnParams.addRule(RelativeLayout.BELOW, scoreLabel.getId());
                    } else {
                        questBtnParams.addRule(RelativeLayout.BELOW, questionsButtons[i - 1].getId());
                    }

                    questBtnParams.bottomMargin = 10;

                    resultLay.addView(questionsButtons[i], questBtnParams);

                    int j = i;
                    questionsButtons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ScrollView mainQuestScrlView = new ScrollView(TakeQuiz.this);
                            mainQuestScrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                            RelativeLayout mainQuesRelLay = new RelativeLayout(TakeQuiz.this);
                            mainQuesRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                            mainQuestScrlView.addView(mainQuesRelLay);

                            TextView questNumLabel = new TextView(TakeQuiz.this);
                            questNumLabel.setText("Question " + (j + 1));
                            questNumLabel.setTextSize(20);
                            questNumLabel.setId(View.generateViewId());

                            RelativeLayout.LayoutParams questNumLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            questNumLabelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                            questNumLabelParams.topMargin = 25;
                            questNumLabelParams.bottomMargin = 30;

                            mainQuesRelLay.addView(questNumLabel, questNumLabelParams);

                            TextView questionLabel = new TextView(TakeQuiz.this);
                            questionLabel.setText(quiz.questionList.get(j).question);
                            questionLabel.setTextSize(25);
                            questionLabel.setId(View.generateViewId());

                            RelativeLayout.LayoutParams questionLabelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                            questionLabelParams.addRule(RelativeLayout.BELOW, questNumLabel.getId());
                            questionLabelParams.leftMargin = 10;
                            questionLabelParams.rightMargin = 10;
                            questionLabelParams.bottomMargin = 20;

                            mainQuesRelLay.addView(questionLabel, questionLabelParams);

                            ScrollView answersScrlView = new ScrollView(TakeQuiz.this);
                            answersScrlView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                            RelativeLayout.LayoutParams ansScrlViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                            ansScrlViewParams.addRule(RelativeLayout.BELOW, questionLabel.getId());

                            mainQuesRelLay.addView(answersScrlView, ansScrlViewParams);

                            RelativeLayout answersRelLay = new RelativeLayout(TakeQuiz.this);
                            answersRelLay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

                            answersScrlView.addView(answersRelLay);

                            if(quiz.questionList.get(j).mcQuestion) {
                                CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(j).options.length];

                                for(int i = 0; i < optionsCB.length; i++) {
                                    optionsCB[i] = new CheckBox(TakeQuiz.this);
                                    optionsCB[i].setText(quiz.questionList.get(j).options[i]);
                                    optionsCB[i].setId(View.generateViewId());
                                    optionsCB[i].setClickable(false);
                                    optionsCB[i].setFocusable(false);

                                    RelativeLayout.LayoutParams optionParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                                    if(i > 0) {
                                        optionParams.addRule(RelativeLayout.BELOW, optionsCB[i - 1].getId());
                                    }

                                    optionParams.leftMargin = 20;
                                    optionParams.rightMargin = 10;
                                    optionParams.bottomMargin = 10;

                                    answersRelLay.addView(optionsCB[i], optionParams);
                                }
                            }

                            setContentView(mainQuestScrlView);
                        }
                    });
                }


                setContentView(resultScrlView);
            }
        });
    }

    public void loadQuiz() {
        SharedPreferences sharedPreferences = getSharedPreferences("TakeQuiz", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Quiz", null);
        Type type = new TypeToken<QuizInfo>() {}.getType();
        quiz = gson.fromJson(json, type);
    }

    public void displayQuestion(int index) {
        questionRelLay.removeAllViews();

        questionNumber.setText((index + 1) + ".");
        questionTracker.setText((index + 1) + " of " + quiz.questionList.size());
        questionLabel.setText(quiz.questionList.get(index).question);

        if(quiz.questionList.get(index).mcQuestion) {
            CheckBox[] optionsCB = new CheckBox[quiz.questionList.get(index).options.length];

            for(int i = 0; i < optionsCB.length; i++) {
                optionsCB[i] = new CheckBox(this);
                optionsCB[i].setText(quiz.questionList.get(index).options[i]);
                optionsCB[i].setId(View.generateViewId());

                RelativeLayout.LayoutParams optionLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                if(i < optionsCB.length - 1) {
                    optionLayParams.bottomMargin = 20;
                } else {
                    optionLayParams.bottomMargin = 200;
                }

                if(i > 0) {
                    optionLayParams.addRule(RelativeLayout.BELOW, optionsCB[i - 1].getId());
                }

                questionRelLay.addView(optionsCB[i], optionLayParams);

                if(userAnswers[index].contains(i)) {
                    optionsCB[i].setChecked(true);
                }

                int j = i;
                optionsCB[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(optionsCB[j].isChecked()) {
                            userAnswers[index].add((Integer) j);
                        } else if(userAnswers[index].contains((Integer) j)){
                            userAnswers[index].remove((Integer) j);
                        }
                    }
                });
            }
        } else {
            EditText frAns = new EditText(this);
            frAns.setHint("Type your answer here");

            RelativeLayout.LayoutParams frLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            frLayParams.leftMargin = 20;

            questionRelLay.addView(frAns, frLayParams);

            frAns.setText((String) userAnswers[index].get(0));

            frAns.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    userAnswers[index].set(0, frAns.getText().toString());
                }
            });
        }
    }

    public void checkAnswers() {
        for(int i = 0; i < userAnswers.length; i++) {
            if(quiz.questionList.get(i).mcQuestion) {
                for(int j = 0; j < userAnswers[i].size(); j++) {
                    if(quiz.questionList.get(i).correctAnswers.contains(userAnswers[i].get(j))) {
                        userAnswersCorrect[i] = true;
                    } else {
                        userAnswersCorrect[i] = false;
                        break;
                    }
                }
            } else {
                if(userAnswers[i].get(0).equals(quiz.questionList.get(i).frCorrectAnswer)) {
                    userAnswersCorrect[i] = true;
                } else {
                    userAnswersCorrect[i] = false;
                }
            }
        }
    }
}