package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestingActivity extends AppCompatActivity {
    Root root;

    Question question;
    Button btnNext;
    boolean right_ans = false;
    private boolean cheked = false;
    public int index = 0;
    public int count_right_ans = 0;
    public int count_wrong_ans = 0;
    public String Right_ans;
    public String User_ans;
    private int mlength;
    private ProgressBar tvProgressHorizontal;
    private TextView textViewQuestion, textIndex, text_right_ans;
    private RadioGroup radioGroup;
    private final List<Button> answerButtons = new ArrayList<>();
    DBMatches mDBConnector;
    int Index_lvl = 1;
    String self_lvl = "A1";

    /////СРАБАТЫВАЕТ ПРИ НАЧАЛЕ АКТИВНОСТИ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        mDBConnector = new DBMatches(this);
        initializeUI();
        loadQuestion();
    }


    /////ПОДГРУЗКА JSON С ВОПРОСАМИ ИЗ ГИТХАБ
    private void loadQuestion() {
        OkHttpClient client = new OkHttpClient();
        String url = null;
        switch (Index_lvl) {
            case 1:
                mDBConnector.createDBsession();
                url = "https://gitfront.io/r/Alexlkv/PCToDPrZ4WBS/android-app-tests/raw/TestA.json";
                break;
            case 2:
                url = "https://gitfront.io/r/Alexlkv/PCToDPrZ4WBS/android-app-tests/raw/TestB.json";
                break;
            case 3:
                url = "https://gitfront.io/r/Alexlkv/PCToDPrZ4WBS/android-app-tests/raw/TestC.json";
                break;
        }
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                Gson gson = new Gson();
                root = gson.fromJson(jsonData, Root.class);
                question = root.getQuestions().get(index);
                setListeners();
                index = 0;
                right_ans = false;
                loadUi();

            }
        });
    }


    /////ИНИЦИАЛИЗАЦИЯ ЭЛЕМЕНТОВ
    private void initializeUI() {
        btnNext = findViewById(R.id.btn_next);
        tvProgressHorizontal = findViewById(R.id.pb_horizontal);
        radioGroup = findViewById(R.id.radioGroup);
        text_right_ans = findViewById(R.id.right_ans);
        textViewQuestion = findViewById(R.id.question);
        textIndex = findViewById(R.id.index);
        answerButtons.addAll(Arrays.asList(findViewById(R.id.btn_ans_1), findViewById(R.id.btn_ans_2), findViewById(R.id.btn_ans_3), findViewById(R.id.btn_ans_4)));
    }


    /////ПОДКЛЮЧЕНИЕ КНОПОК С ВАРИАНТАМИ ОТВЕТОВ
    private void setListeners() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            cheked = true;
            switch (checkedId) {
                case -1:
                    cheked = false;
                    btnNext.setBackgroundColor(Color.parseColor("#9e2a2b"));
                    btnNext.setTextColor(Color.parseColor("#ffffff"));
                    break;
                case R.id.btn_ans_1:
                    right_ans = question.getRight_ans().equals("1");
                    btnNext.setBackgroundColor(Color.parseColor("#90a955"));
                    btnNext.setTextColor(Color.parseColor("#000000"));
                    User_ans = question.getAnswers().get(0);
                    break;
                case R.id.btn_ans_2:
                    right_ans = question.getRight_ans().equals("2");
                    btnNext.setBackgroundColor(Color.parseColor("#90a955"));
                    btnNext.setTextColor(Color.parseColor("#000000"));
                    User_ans = question.getAnswers().get(1);
                    break;
                case R.id.btn_ans_3:
                    right_ans = question.getRight_ans().equals("3");
                    btnNext.setBackgroundColor(Color.parseColor("#90a955"));
                    btnNext.setTextColor(Color.parseColor("#000000"));
                    User_ans = question.getAnswers().get(2);
                    break;
                case R.id.btn_ans_4:
                    right_ans = question.getRight_ans().equals("4");
                    btnNext.setBackgroundColor(Color.parseColor("#90a955"));
                    btnNext.setTextColor(Color.parseColor("#000000"));
                    User_ans = question.getAnswers().get(3);
                    break;
            }
        });
    }

    /////ФОРМИРОВАНИЕ ВОПРОСА, ОТРИСОВКА ИНТЕРФЕЙСА
    @SuppressLint("SetTextI18n")
    public void loadUi() {
        runOnUiThread(() -> {
            mlength = root.getQuestions().size();
            question = root.getQuestions().get(index);
            for (int i = 0; i < answerButtons.size(); i++) {
                answerButtons.get(i).setText(question.getAnswers().get(i));
            }
            textViewQuestion.setText(question.getQuestion());
            tvProgressHorizontal.setProgress(index);
            tvProgressHorizontal.setSecondaryProgress(index + 1);
            tvProgressHorizontal.setMax(mlength);
            cheked = false;
            right_ans = false;
            radioGroup.clearCheck();
            Right_ans = question.getAnswers().get((Integer.parseInt(question.getRight_ans()) - 1));
        });
    }

    /////ДЕЙСТВИЯ ПРИ КОНЦЕ ТЕСТИРОВАНИЯ
    public void endTesting() {
        mDBConnector.upd_db(count_right_ans, count_wrong_ans, self_lvl);
        Intent intent = new Intent(TestingActivity.this, ResultTesting.class);

        intent.putExtra("id", mDBConnector.getId());
        startActivity(intent);
    }


    public void upd_lvl() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestingActivity.this);
        builder.setTitle("Поздравляем! Вы достигли уровня: " + self_lvl + "!");
        builder.setPositiveButton("Хорошо",
                (dialog, which) -> showMessage("Продолжение..."));
        builder.show();
    }

    public void upd_lvlOFhard() {
        if (Index_lvl != 3) {
            Index_lvl++;

            AlertDialog.Builder builder = new AlertDialog.Builder(TestingActivity.this);
            builder.setTitle("Поздравляем! Вы достигли уровня: " + self_lvl + "!");
            builder.setMessage("Уровень сложности вопросов будет изменен.");
            builder.setCancelable(false);
            builder.setPositiveButton("Хорошо",

                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            showMessage("Продолжение...");
                            loadQuestion();
                        }
                    });
            builder.show();

        }
    }

    /////СЛЕДУЮЩИЙ ВОПРОС
    public void next_question() {
        index++;
        if (index < mlength && cheked) {
            if (right_ans) {
                count_right_ans++;
                text_right_ans.setText("Верных ответов: " + count_right_ans);
            } else {
                count_wrong_ans++;
                textIndex.setText("Количество ошибок: " + count_wrong_ans);
            }

            String lvl = question.getLevel();
            if (count_right_ans == root.getA2() && Objects.equals(lvl, "A1") && !Objects.equals(self_lvl, "A2")) {
                self_lvl = "A2";
                upd_lvl();
            } else if ((count_right_ans == root.getB1()) && Objects.equals(lvl, "A1") && !Objects.equals(self_lvl, "B1")) {
                self_lvl = "B1";
                upd_lvlOFhard();
            } else if (count_right_ans == root.getB2() && Objects.equals(lvl, "B") && !Objects.equals(self_lvl, "B2")) {
                self_lvl = "B2";
                upd_lvl();
            } else if (count_right_ans == root.getC1() && Objects.equals(lvl, "B") && !Objects.equals(self_lvl, "C1")) {
                self_lvl = "C1";
                upd_lvlOFhard();
            } else if (count_right_ans == root.getC2() && Objects.equals(lvl, "C") && !Objects.equals(self_lvl, "C2")) {
                self_lvl = "C2";
                endTesting();
            }
            mDBConnector.dbConrol(count_right_ans, count_wrong_ans, question.getQuestion(), Right_ans, User_ans);
            loadUi();
        } else if (!cheked) {
            Toast.makeText(getApplicationContext(), "Ничего не выбрано", Toast.LENGTH_SHORT).show();
        } else {
            mDBConnector.dbConrol(count_right_ans, count_wrong_ans, question.getQuestion(), Right_ans, User_ans);
            endTesting();
        }
    }


    /////КНОПКА ДАЛЕЕ
    @SuppressLint("SetTextI18n")
    public void next(View view) {
        next_question();
    }

    /////КНОПКА НЕ ЗНАЮ
    public void idk(View view) {
        cheked = true;
        User_ans = null;
        next_question();
    }


    /////КНОПКА ВЫХОДА
    public void ext(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TestingActivity.this);
        builder.setTitle("Вы точно хотите покинуть тестирование?");
        builder.setMessage("В случае отмены тестирования прогресс будет утерян");
        builder.setNegativeButton("Нет",
                (dialog, which) -> showMessage("Продолжение..."));
        builder.setPositiveButton("Да",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        mDBConnector.deleteSession();
                        Intent intent = new Intent(TestingActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });
        builder.show();

    }

    private void showMessage(String textInMessage) {
        Toast.makeText(getApplicationContext(), textInMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Чтобы покинуть тестирование, используйте кнопку", Toast.LENGTH_SHORT).show();
    }
}