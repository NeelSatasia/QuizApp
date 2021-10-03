package com.example.quizit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SaveQuiz extends AppCompatActivity {

    ArrayList<QuizInfo> quizzes;
    ArrayList<RelativeLayout> questionList_rel_lay;
    ArrayList<EditText> viewsInQuestions;
    ArrayList<Question> questionsList;
    RelativeLayout layout;
    TextView quizName;
    Button addBtn;
    Button cancelBtn;
    Button setTimer;
    Button createQuizBtn;

    AlertDialog.Builder addQuesADB;
    AlertDialog addQuesAD;

    ArrayList<CheckBox> deleteQuesCheckBoxArr;

    int totalDeleteQuestSelected = 0;
    boolean cancelBtnSelected = false;

    int editQuizID = -1;

    boolean selectedMC;

    String userError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        questionList_rel_lay = new ArrayList<RelativeLayout>();
        viewsInQuestions = new ArrayList<EditText>();
        questionsList = new ArrayList<Question>();
        layout = findViewById(R.id.newQuizLay);
        quizName = findViewById(R.id.quizNameID);
        addBtn = findViewById(R.id.newQuestionbtn);
        cancelBtn = findViewById(R.id.cancelQuestionbtn);
        setTimer = findViewById(R.id.quizTimer);
        createQuizBtn = findViewById(R.id.createQuizBtn);

        cancelBtn.setEnabled(false);

        loadQuizzes();
        getEditQuiz();

        if(editQuizID > -1) {
            quizName.setText(quizzes.get(editQuizID).quizName);
            questionsList = quizzes.get(editQuizID).questionList;
            createQuizBtn.setText("Save");
            setTimer.setText("Change Timer");

            uploadQuiz();
        } else {
            questionsList = new ArrayList<Question>();
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuesADB = new AlertDialog.Builder(SaveQuiz.this);
                View addQuesPopup = getLayoutInflater().inflate(R.layout.question_type, null);

                addQuesADB.setView(addQuesPopup);
                addQuesAD = addQuesADB.create();
                addQuesAD.show();

                RadioButton mcRadBtn = addQuesPopup.findViewById(R.id.mc);
                RadioButton frRadBtn = addQuesPopup.findViewById(R.id.fr);

                EditText totalAnswers = addQuesPopup.findViewById(R.id.num);

                Button okBtn = addQuesPopup.findViewById(R.id.okBtn);

                selectedMC = true;

                mcRadBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(mcRadBtn.isChecked()) {
                            selectedMC = true;
                            if(totalAnswers.isEnabled() == false) {
                                totalAnswers.setEnabled(true);
                                totalAnswers.setText("2");
                            }
                        }
                    }
                });

                frRadBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(frRadBtn.isChecked()) {
                            totalAnswers.setText("1");
                            totalAnswers.setEnabled(false);
                            selectedMC = false;

                            if(okBtn.isEnabled() == false) {
                                okBtn.setEnabled(true);
                            }
                        }
                    }
                });

                totalAnswers.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(mcRadBtn.isChecked()) {
                            if (totalAnswers.getText().toString().equals("") || totalAnswers.getText().toString().equals("1")) {
                                okBtn.setEnabled(false);
                            } else if (okBtn.isEnabled() == false) {
                                okBtn.setEnabled(true);
                            }
                        }
                    }
                });

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int totalOptions = Integer.parseInt(totalAnswers.getText().toString());
                        addQuesAD.dismiss();

                        createNewQuestion(-1, totalOptions, selectedMC);
                    }
                });
            }
        });
    }

    public void uploadQuiz() {
        for(int i = 0; i < quizzes.get(editQuizID).questionList.size(); i++) {
            createNewQuestion(i, questionsList.get(i).options.length, questionsList.get(i).mcQuestion);
        }
    }

    public void createNewQuestion(int k, int totalOptions, boolean mcQues) {
            RelativeLayout newQuestionRelativeLayout = new RelativeLayout(SaveQuiz.this);
            newQuestionRelativeLayout.setId(View.generateViewId());

            if(k < 0) {
                questionsList.add(new Question("", new String[totalOptions], new ArrayList<Integer>(), "", mcQues));
            }

            RelativeLayout.LayoutParams newQuestionRelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            EditText newQuestion = new EditText(SaveQuiz.this);
            if(k >= 0) {
                newQuestion.setText(questionsList.get(k).question);
            }
            newQuestion.setHint("Question " + (questionList_rel_lay.size() + 1));
            newQuestion.setId(View.generateViewId());
            viewsInQuestions.add(newQuestion);

            RelativeLayout.LayoutParams newQuestionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            newQuestionLayout.leftMargin = 10;
            newQuestionLayout.bottomMargin = 10;
            newQuestionRelativeLayout.addView(newQuestion, newQuestionLayout);

            newQuestion.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if(k < 0) {
                        questionsList.get(questionsList.size() - 1).question = newQuestion.getText().toString();
                    } else {
                        questionsList.get(k).question = newQuestion.getText().toString();
                    }
                }
            });

            if(mcQues) {
                TextView correctAnswerLabel = new TextView(SaveQuiz.this);
                correctAnswerLabel.setText("Choose Correct Answer:");
                correctAnswerLabel.setId(View.generateViewId());
                correctAnswerLabel.setTextSize(15);
                correctAnswerLabel.setPadding(0, 5, 0, 5);

                RelativeLayout.LayoutParams correctAnswerLabelLay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                correctAnswerLabelLay.leftMargin = 20;
                correctAnswerLabelLay.bottomMargin = 10;
                correctAnswerLabelLay.addRule(RelativeLayout.BELOW, newQuestion.getId());

                newQuestionRelativeLayout.addView(correctAnswerLabel, correctAnswerLabelLay);

                EditText[] options = new EditText[totalOptions];
                CheckBox[] optionsCheckBoxes = new CheckBox[totalOptions];

                for (int i = 0; i < options.length; i++) {
                    optionsCheckBoxes[i] = new CheckBox(SaveQuiz.this);
                    optionsCheckBoxes[i].setText("");
                    optionsCheckBoxes[i].setTextSize(20);
                    optionsCheckBoxes[i].setId(View.generateViewId());

                    options[i] = new EditText(SaveQuiz.this);
                    options[i].setHint("Option " + (i + 1));
                    options[i].setId(View.generateViewId());
                    options[i].setMaxLines(1);
                    options[i].setHorizontallyScrolling(true);

                    if (k >= 0) {
                        options[i].setText(questionsList.get(k).options[i]);
                    }

                    RelativeLayout.LayoutParams checkBoxLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if(i == 0) {
                        checkBoxLayParams.addRule(RelativeLayout.BELOW, correctAnswerLabel.getId());
                    } else {
                        checkBoxLayParams.addRule(RelativeLayout.BELOW, optionsCheckBoxes[i- 1].getId());
                    }

                    checkBoxLayParams.leftMargin = 20;
                    checkBoxLayParams.topMargin = 20;
                    checkBoxLayParams.bottomMargin = 20;

                    newQuestionRelativeLayout.addView(optionsCheckBoxes[i], checkBoxLayParams);

                    RelativeLayout.LayoutParams optionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    optionLayout.addRule(RelativeLayout.END_OF, optionsCheckBoxes[i].getId());
                    optionLayout.addRule(RelativeLayout.ALIGN_BOTTOM, optionsCheckBoxes[i].getId());

                    if(i == 0) {
                        optionLayout.addRule(RelativeLayout.BELOW, correctAnswerLabel.getId());
                    } else {
                        optionLayout.addRule(RelativeLayout.BELOW, options[i - 1].getId());
                    }

                    optionLayout.leftMargin = 5;
                    optionLayout.rightMargin = 10;

                    newQuestionRelativeLayout.addView(options[i], optionLayout);

                    if (k >= 0) {
                        for(int h = 0; h < questionsList.get(k).correctAnswers.size(); h++) {
                            if(questionsList.get(k).correctAnswers.get(h) == i) {
                                optionsCheckBoxes[i].setChecked(true);
                            }
                        }
                    }

                    int j = i;
                    optionsCheckBoxes[i].setOnCheckedChangeListener(new RadioButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(optionsCheckBoxes[j].isChecked()) {
                                if(k < 0) {
                                    questionsList.get(questionsList.size() - 1).correctAnswers.add(j);
                                } else {
                                    questionsList.get(k).correctAnswers.add(j);
                                }
                            } else {
                                if(k < 0) {
                                    questionsList.get(questionsList.size() - 1).correctAnswers.remove((Integer) j);
                                } else {
                                    questionsList.get(k).correctAnswers.remove((Integer) j);
                                }
                            }
                        }
                    });

                    options[i].addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (k < 0) {
                                questionsList.get(questionsList.size() - 1).options[j] = options[j].getText().toString();
                            } else {
                                questionsList.get(k).options[j] = options[j].getText().toString();
                            }
                        }
                    });
                }
            } else {
                EditText frCorrectAnswers = new EditText(this);
                frCorrectAnswers.setHint("Type the correct answer");
                frCorrectAnswers.setId(View.generateViewId());

                if(k >= 0) {
                    frCorrectAnswers.setText(questionsList.get(k).frCorrectAnswer);
                }

                RelativeLayout.LayoutParams frCorrectAnsLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                frCorrectAnsLayParams.addRule(RelativeLayout.BELOW, newQuestion.getId());
                frCorrectAnsLayParams.leftMargin = 30;

                newQuestionRelativeLayout.addView(frCorrectAnswers, frCorrectAnsLayParams);

                frCorrectAnswers.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if(k < 0) {
                            questionsList.get(questionsList.size() - 1).frCorrectAnswer = frCorrectAnswers.getText().toString();
                        } else  {
                            questionsList.get(k).frCorrectAnswer = frCorrectAnswers.getText().toString();
                        }
                    }
                });
            }

            if(k < 0) {
                RelativeLayout.LayoutParams previousQuestRelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                previousQuestRelParams.bottomMargin = 70;
                if (questionList_rel_lay.size() >= 1) {
                    if (questionList_rel_lay.size() == 1) {
                        previousQuestRelParams.addRule(RelativeLayout.BELOW, quizName.getId());
                    } else {
                        previousQuestRelParams.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 2).getId());
                    }

                    questionList_rel_lay.get(questionList_rel_lay.size() - 1).setLayoutParams(previousQuestRelParams);
                }
            }

            if(questionList_rel_lay.size() > 0) {
                newQuestionRelParams.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(questionList_rel_lay.size() - 1).getId());
            } else {
                newQuestionRelParams.addRule(RelativeLayout.BELOW, R.id.quizNameID);
            }
            if(k >= 0) {
                if(k < questionsList.size() - 1) {
                    newQuestionRelParams.bottomMargin = 70;
                } else if(k == questionsList.size() - 1) {
                    newQuestionRelParams.bottomMargin = 230;
                }
            } else {
                newQuestionRelParams.bottomMargin = 230;
            }

            questionList_rel_lay.add(newQuestionRelativeLayout);
            layout.addView(questionList_rel_lay.get(questionList_rel_lay.size() - 1), newQuestionRelParams);

            if(cancelBtn.isEnabled() == false) {
                cancelBtn.setEnabled(true);
            }
    }

    public void deleteQuestion(View view) {

        if(cancelBtnSelected == false) {
            deleteQuesCheckBoxArr = new ArrayList<CheckBox>();

            for (int i = 0; i < questionList_rel_lay.size(); i++) {
                deleteQuesCheckBoxArr.add(new CheckBox(this));
                deleteQuesCheckBoxArr.get(i).setText("Question " + (i + 1) + ":");
                deleteQuesCheckBoxArr.get(i).setTextColor(Color.parseColor("#ff0000"));

                RelativeLayout.LayoutParams deleteQuesCheckBoxParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                deleteQuesCheckBoxParams.addRule(RelativeLayout.ABOVE, questionList_rel_lay.get(i).getId());

                layout.addView(deleteQuesCheckBoxArr.get(i), deleteQuesCheckBoxParams);

                deleteQuesCheckBoxArr.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        totalDeleteQuestSelected++;
                    }
                });
            }

            cancelBtnSelected = true;

            cancelBtn.setText("Delete");

            Drawable buttonDrawable = cancelBtn.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.RED);
            cancelBtn.setBackground(buttonDrawable);

            addBtn.setEnabled(false);

        } else {
            if(totalDeleteQuestSelected > 0) {
                int j = 0;
                while (j < deleteQuesCheckBoxArr.size()) {
                    if (deleteQuesCheckBoxArr.get(j).isChecked()) {
                        layout.removeView(questionList_rel_lay.get(j));
                        questionList_rel_lay.remove(j);
                        layout.removeView(deleteQuesCheckBoxArr.get(j));
                        deleteQuesCheckBoxArr.remove(j);
                        viewsInQuestions.remove(j);
                        questionsList.remove(j);
                    } else {
                        j++;
                    }
                }

                for (int i = 0; i < questionList_rel_lay.size(); i++) {
                    viewsInQuestions.get(i).setHint("Question " + (i + 1));

                    RelativeLayout.LayoutParams quesRelLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if (i == 0) {
                        quesRelLayParams.addRule(RelativeLayout.BELOW, quizName.getId());
                    } else {
                        quesRelLayParams.addRule(RelativeLayout.BELOW, questionList_rel_lay.get(i - 1).getId());
                    }

                    if (i == questionList_rel_lay.size() - 1) {
                        quesRelLayParams.bottomMargin = 220;
                    } else {
                        quesRelLayParams.bottomMargin = 70;
                    }

                    questionList_rel_lay.get(i).setLayoutParams(quesRelLayParams);
                }
            }

            for(int i = 0; i < deleteQuesCheckBoxArr.size(); i++) {
                layout.removeView(deleteQuesCheckBoxArr.get(i));
            }

            deleteQuesCheckBoxArr.clear();
            totalDeleteQuestSelected = 0;

            cancelBtnSelected = false;

            cancelBtn.setText("Delete Question");

            Drawable buttonDrawable = cancelBtn.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            DrawableCompat.setTint(buttonDrawable, Color.rgb(30, 144, 255));
            cancelBtn.setBackground(buttonDrawable);

            addBtn.setEnabled(true);
        }
    }

    public void createNewQuiz(View view) {

        boolean isQuizReadyToBeCreated = true;

        if(quizName.getText().toString().isEmpty() == false && questionsList.size() > 0) {
            for(int i = 0; i < quizzes.size(); i++) {
                if(i != editQuizID && quizzes.get(i).quizName.equals(quizName.getText().toString())) {
                    isQuizReadyToBeCreated = false;
                    userError = "Quiz name already exists!";
                    break;
                }
            }

            if(isQuizReadyToBeCreated) {
                for (int i = 0; i < questionsList.size(); i++) {
                    if (questionsList.get(i).question.isEmpty()) {
                        isQuizReadyToBeCreated = false;
                        userError = "Contains empty field(s)!";
                        break;
                    }

                    if (questionsList.get(i).mcQuestion) {
                        for (int j = 0; j < questionsList.get(i).options.length; j++) {
                            if (questionsList.get(i).options[j].isEmpty()) {
                                isQuizReadyToBeCreated = false;
                                userError = "Contains empty field(s)!";
                                break;
                            }
                        }
                    } else {
                        if (questionsList.get(i).frCorrectAnswer.isEmpty()) {
                            isQuizReadyToBeCreated = false;
                            userError = "Contains empty field(s)!";
                            break;
                        }
                    }
                }
            }
        } else {
            isQuizReadyToBeCreated = false;
            userError =  "Missing requirements!";
        }

        if(isQuizReadyToBeCreated) {
            Intent intent = new Intent(this, Quizzes.class);
            QuizInfo newQuiz = new QuizInfo(quizName.getText().toString(), questionsList);
            if (editQuizID < 0) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("QuizzesList", newQuiz);
                intent.putExtras(bundle);
            } else {
                quizzes.set(editQuizID, newQuiz);

                saveQuizzes();
            }

            startActivity(intent);
        } else {
            Toast.makeText(this, userError, Toast.LENGTH_LONG).show();
        }
    }

    public void getEditQuiz() {
        SharedPreferences sharedPreferences = getSharedPreferences("EditQuiz", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Quiz", null);
        editQuizID = sharedPreferences.getInt("QuizID", -1);
    }

    public void loadQuizzes() {
        SharedPreferences sharedPreferences = getSharedPreferences("Quizzes", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("QuizzesList", null);
        Type type = new TypeToken<ArrayList<QuizInfo>>() {}.getType();
        quizzes = gson.fromJson(json, type);

        if(quizzes == null) {
            quizzes = new ArrayList<QuizInfo>();
        }
    }

    public void saveQuizzes() {
        SharedPreferences sharedPreferences = getSharedPreferences("Quizzes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(quizzes);
        editor.putString("QuizzesList", json);
        editor.apply();
    }
}