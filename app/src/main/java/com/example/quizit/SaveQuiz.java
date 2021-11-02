package com.example.quizit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SaveQuiz extends AppCompatActivity {

    ArrayList<QuizInfo> quizzes;
    ArrayList<RelativeLayout> questionList_rel_lay;
    ArrayList<TextView> viewsInQuestions;
    ArrayList<Question> questionsList;
    String[] timer = new String[3];
    boolean passwordProtected = false;
    RelativeLayout layout;
    TextView quizName;
    Button addBtn;
    Button cancelBtn;
    Button setTimer;
    Button createQuizBtn;

    AlertDialog.Builder addQuesADB;
    AlertDialog addQuesAD;

    AlertDialog.Builder setTimerADB;
    AlertDialog setTimerAD;

    ArrayList<CheckBox> deleteQuesCheckBoxArr;
    int selectedDeleteQuests;

    int editQuizID = -1;

    boolean selectedMC;

    String userError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quiz);

        questionList_rel_lay = new ArrayList<RelativeLayout>();
        viewsInQuestions = new ArrayList<TextView>();
        deleteQuesCheckBoxArr = new ArrayList<CheckBox>();
        questionsList = new ArrayList<Question>();
        layout = findViewById(R.id.newQuizLay);
        quizName = findViewById(R.id.quizNameID);
        addBtn = findViewById(R.id.newQuestionbtn);
        cancelBtn = findViewById(R.id.cancelQuestionbtn);
        setTimer = findViewById(R.id.quizTimer);
        createQuizBtn = findViewById(R.id.createQuizBtn);

        cancelBtn.setEnabled(false);

        Drawable buttonDrawable = cancelBtn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, Color.rgb(230, 230, 230));
        cancelBtn.setBackground(buttonDrawable);
        cancelBtn.setTextColor(Color.rgb(166, 166, 166));

        loadQuizzes();
        getEditQuiz();

        for(int i = 0; i < timer.length; i++) {
            timer[i] = "00";
        }

        if(editQuizID > -1) {
            quizName.setText(quizzes.get(editQuizID).quizName);
            questionsList = quizzes.get(editQuizID).questionList;
            timer = quizzes.get(editQuizID).timer;
            createQuizBtn.setText("Save");

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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteQuestion();
            }
        });

        setTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimerADB = new AlertDialog.Builder(SaveQuiz.this);
                View timerPopup = getLayoutInflater().inflate(R.layout.quiz_timer_layout, null);

                setTimerADB.setView(timerPopup);
                setTimerAD = setTimerADB.create();
                setTimerAD.show();

                Spinner hrs = timerPopup.findViewById(R.id.hrs);
                Spinner mins = timerPopup.findViewById(R.id.mins);
                Spinner secs = timerPopup.findViewById(R.id.secs);

                String[] hours = new String[25];
                int defaultHrIndex = 0;

                for(int i = 0; i < hours.length; i++) {
                    if(i < 10) {
                        hours[i] = "0" + i;
                    } else {
                        hours[i] = i + "";
                    }

                    if(timer[0].equals(hours[i])) {
                        defaultHrIndex = i;
                    }
                }

                ArrayAdapter<String> hrsAdapter = new ArrayAdapter<String>(SaveQuiz.this, android.R.layout.simple_spinner_dropdown_item, hours);
                hrs.setAdapter(hrsAdapter);
                hrs.setSelection(defaultHrIndex);

                hrs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String hrsStr = adapterView.getSelectedItem().toString();
                        timer[0] = hrsStr;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                String[] minutes = new String[61];
                int defaultMinIndex = 0;

                for(int i = 0; i < minutes.length; i++) {
                    if(i < 10) {
                        minutes[i] = "0" + i;
                    } else {
                        minutes[i] = i + "";
                    }

                    if(timer[1].equals(minutes[i])) {
                        defaultMinIndex = i;
                    }
                }

                ArrayAdapter<String> minsAdapter = new ArrayAdapter<String>(SaveQuiz.this, android.R.layout.simple_spinner_dropdown_item, minutes);
                mins.setAdapter(minsAdapter);
                mins.setSelection(defaultMinIndex);

                mins.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String minsStr = adapterView.getSelectedItem().toString();
                        timer[1] = minsStr;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                String[] seconds = new String[61];
                int defaultSecIndex = 0;

                for(int i = 0; i < seconds.length; i++) {
                    if(i < 10) {
                        seconds[i] = "0" + i;
                    } else {
                        seconds[i] = i + "";
                    }

                    if(timer[2].equals(seconds[i])) {
                        defaultSecIndex = i;
                    }
                }

                ArrayAdapter<String> secsAdapter = new ArrayAdapter<String>(SaveQuiz.this, android.R.layout.simple_spinner_dropdown_item, seconds);
                secs.setAdapter(secsAdapter);
                secs.setSelection(defaultSecIndex);

                secs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String secsStr = adapterView.getSelectedItem().toString();
                        timer[2] = secsStr;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                Button resetBtn = timerPopup.findViewById(R.id.resetBtn);

                resetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hrs.setSelection(0);
                        mins.setSelection(0);
                        secs.setSelection(0);

                        for(int i = 0; i < timer.length; i++) {
                            timer[i] = "00";
                        }
                    }
                });

                Button setTimerBtn = timerPopup.findViewById(R.id.setTimrBtn);

                setTimerBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setTimerAD.dismiss();
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
        newQuestionRelativeLayout.setBackgroundColor(Color.rgb(242, 242, 242));
        newQuestionRelativeLayout.setPadding(20, 20, 20, 20);
        newQuestionRelativeLayout.setBackgroundResource(R.drawable.custom_input_question);

        if(k < 0) {
            questionsList.add(new Question("", new String[totalOptions], new ArrayList<Integer>(), "", mcQues));
        }

        RelativeLayout.LayoutParams newQuestionRelParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        CheckBox questionCheckBox = new CheckBox(this);
        questionCheckBox.setText("Question " + (questionList_rel_lay.size() + 1));
        questionCheckBox.setId(View.generateViewId());

        RelativeLayout.LayoutParams questionCBParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        questionCBParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        questionCBParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        questionCBParams.bottomMargin = 10;

        newQuestionRelativeLayout.addView(questionCheckBox, questionCBParams);

        EditText newQuestion = new EditText(SaveQuiz.this);
        if(k >= 0) {
            newQuestion.setText(questionsList.get(k).question);
        }
        newQuestion.setHint("Enter question");
        newQuestion.setId(View.generateViewId());
        newQuestion.setBackgroundResource(R.drawable.custom_question_textview);
        newQuestion.setPadding(15, 15, 15, 15);

        RelativeLayout.LayoutParams newQuestionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        newQuestionLayout.addRule(RelativeLayout.BELOW, questionCheckBox.getId());
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
                options[i].setBackgroundResource(R.drawable.custom_input);
                options[i].setPadding(15, 15, 15, 15);

                if (k >= 0) {
                    options[i].setText(questionsList.get(k).options[i]);
                }

                RelativeLayout.LayoutParams checkBoxLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                if(i == 0) {
                    checkBoxLayParams.addRule(RelativeLayout.BELOW, correctAnswerLabel.getId());
                } else {
                    checkBoxLayParams.addRule(RelativeLayout.BELOW, optionsCheckBoxes[i- 1].getId());
                }

                newQuestionRelativeLayout.addView(optionsCheckBoxes[i], checkBoxLayParams);

                RelativeLayout.LayoutParams optionLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                optionLayout.addRule(RelativeLayout.END_OF, optionsCheckBoxes[i].getId());

                if(i == 0) {
                    optionLayout.addRule(RelativeLayout.BELOW, correctAnswerLabel.getId());
                } else {
                    optionLayout.addRule(RelativeLayout.BELOW, options[i - 1].getId());
                }

                optionLayout.leftMargin = 20;
                if(i < optionsCheckBoxes.length - 1) {
                    optionLayout.bottomMargin = 20;
                }

                newQuestionRelativeLayout.addView(options[i], optionLayout);

                RelativeLayout.LayoutParams reCheckBoxLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                reCheckBoxLayParams.addRule(RelativeLayout.ALIGN_TOP, options[i].getId());
                reCheckBoxLayParams.addRule(RelativeLayout.ALIGN_BOTTOM, options[i].getId());

                optionsCheckBoxes[i].setLayoutParams(reCheckBoxLayParams);

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
            frCorrectAnswers.setBackgroundResource(R.drawable.custom_input);
            frCorrectAnswers.setPadding(15, 15, 15, 15);

            if(k >= 0) {
                frCorrectAnswers.setText(questionsList.get(k).frCorrectAnswer);
            }

            RelativeLayout.LayoutParams frCorrectAnsLayParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            frCorrectAnsLayParams.addRule(RelativeLayout.BELOW, newQuestion.getId());
            frCorrectAnsLayParams.leftMargin = 30;
            frCorrectAnsLayParams.topMargin = 20;

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

        questionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(questionCheckBox.isChecked()) {
                    if(addBtn.isEnabled()) {
                        Drawable buttonDrawable = addBtn.getBackground();
                        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                        DrawableCompat.setTint(buttonDrawable, Color.rgb(230, 230, 230));
                        addBtn.setBackground(buttonDrawable);
                        addBtn.setTextColor(Color.rgb(166, 166, 166));

                        addBtn.setEnabled(false);
                    }
                    if(cancelBtn.isEnabled() == false) {
                        Drawable buttonDrawable = cancelBtn.getBackground();
                        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                        DrawableCompat.setTint(buttonDrawable, Color.RED);
                        cancelBtn.setBackground(buttonDrawable);
                        cancelBtn.setTextColor(Color.WHITE);

                        cancelBtn.setEnabled(true);
                    }

                    selectedDeleteQuests++;
                } else {
                    selectedDeleteQuests--;

                    if(selectedDeleteQuests == 0) {
                        if (addBtn.isEnabled() == false) {
                            Drawable buttonDrawable = addBtn.getBackground();
                            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                            DrawableCompat.setTint(buttonDrawable, Color.rgb(30, 144, 255));
                            addBtn.setBackground(buttonDrawable);
                            addBtn.setTextColor(Color.WHITE);

                            addBtn.setEnabled(true);
                        }

                        if(cancelBtn.isEnabled()) {
                            Drawable buttonDrawable = cancelBtn.getBackground();
                            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                            DrawableCompat.setTint(buttonDrawable, Color.rgb(230, 230, 230));
                            cancelBtn.setBackground(buttonDrawable);
                            cancelBtn.setTextColor(Color.rgb(166, 166, 166));

                            cancelBtn.setEnabled(false);
                        }
                    }
                }
            }
        });

        deleteQuesCheckBoxArr.add(questionCheckBox);
    }

    public void deleteQuestion() {
        int j = 0;
        while (j < deleteQuesCheckBoxArr.size()) {
            if(deleteQuesCheckBoxArr.get(j).isChecked()) {
                layout.removeView(questionList_rel_lay.get(j));
                questionList_rel_lay.remove(j);
                questionsList.remove(j);
                deleteQuesCheckBoxArr.remove(j);
            } else {
                j++;
            }
        }

        selectedDeleteQuests = 0;

        for (int i = 0; i < questionList_rel_lay.size(); i++) {
            deleteQuesCheckBoxArr.get(i).setText("Question " + (i + 1));

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

        Drawable buttonDrawable = cancelBtn.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, Color.rgb(230, 230, 230));
        cancelBtn.setBackground(buttonDrawable);

        cancelBtn.setEnabled(false);

        Drawable addbuttonDrawable = addBtn.getBackground();
        addbuttonDrawable = DrawableCompat.wrap(addbuttonDrawable);
        DrawableCompat.setTint(addbuttonDrawable, Color.rgb(30, 144, 255));
        addBtn.setBackground(addbuttonDrawable);
        addBtn.setTextColor(Color.WHITE);

        addBtn.setEnabled(true);
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
                        if(questionsList.get(i).correctAnswers.isEmpty()) {
                            isQuizReadyToBeCreated = false;
                            userError = "Missing requirements!";
                            break;
                        }

                        for (int j = 0; j < questionsList.get(i).options.length; j++) {
                            if (questionsList.get(i).options[j].isEmpty()) {
                                isQuizReadyToBeCreated = false;
                                userError = "Contains empty field(s)!";
                                break;
                            }
                        }
                    } else if (questionsList.get(i).frCorrectAnswer.isEmpty()) {
                            isQuizReadyToBeCreated = false;
                            userError = "Contains empty field(s)!";
                            break;
                    }
                }
            }
        } else {
            isQuizReadyToBeCreated = false;
            userError =  "Missing requirements!";
        }

        if(isQuizReadyToBeCreated) {
            Intent intent = new Intent(this, Quizzes.class);
            QuizInfo newQuiz = new QuizInfo(quizName.getText().toString(), questionsList, timer, passwordProtected);
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