package com.example.myapplication;

import static com.example.myapplication.DBuse.COLUMN_DATE;
import static com.example.myapplication.DBuse.COLUMN_ID_SESSION;
import static com.example.myapplication.DBuse.COLUMN_QUESTION;
import static com.example.myapplication.DBuse.COLUMN_RIGHT_ANS;
import static com.example.myapplication.DBuse.COLUMN_STATE;
import static com.example.myapplication.DBuse.COLUMN_USER_ANS;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestingActivity extends AppCompatActivity {
    Root root;

    ResultTesting results;
    Question question;
    Button btnNext;
    boolean right_ans = false;
    private boolean cheked = false;
    public int index = 0;
    public int count_right_ans = 0;
    public int count_wrong_ans = 0;
    public String Right_ans;
    public String User_ans;
    private final int mlength = 21;
    private ProgressBar tvProgressHorizontal;
    private TextView textViewQuestion, textIndex, text_right_ans;
    private RadioGroup radioGroup;
    String formattedDate;
    private final List<Button> answerButtons = new ArrayList<>();
    DBuse dbHelper = new DBuse(this);


    /////РАБОТА С БД, ЗАПИСЬ ОТВЕТОВ ПОЛЬЗОВАТЕЛЯ

    @SuppressLint("Range")
    protected void dbConrol() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT _id FROM Session ORDER BY _id DESC LIMIT 1", null);
        String id = null;
        if (cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndex("_id"));
        }
        values.put(COLUMN_STATE, count_right_ans - count_wrong_ans);
        values.put(COLUMN_ID_SESSION, id);
        values.put(COLUMN_QUESTION, question.getQuestion());
        values.put(COLUMN_RIGHT_ANS, Right_ans);
        values.put(COLUMN_USER_ANS, User_ans);

        db.insert("Results", null, values);
        cursor.close();
        db.close();
    }

    protected void getPrettyDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("Время - HH:mm // Дата - dd MMMM yyyy'г.'");
        dateFormat.setDateFormatSymbols(new DateFormatSymbols() {{
            setMonths(new String[]{
                    "января", "февраля", "марта", "апреля",
                    "мая", "июня", "июля", "августа",
                    "сентября", "октября", "ноября", "декабря"});
        }});
        formattedDate = dateFormat.format(calendar.getTime());
    }

    protected void createDBsession() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        getPrettyDate();
        values.put(COLUMN_DATE, formattedDate);
        db.insert("Session", null, values);
        db.close();
    }

    /////СРАБАТЫВАЕТ ПРИ НАЧАЛЕ АКТИВНОСТИ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        loadQuestion();
        createDBsession();
    }


    /////ПОДГРУЗКА JSON С ВОПРОСАМИ ИЗ ГИТХАБ
    private void loadQuestion() {
        OkHttpClient client = new OkHttpClient();

        String url = "https://raw.githubusercontent.com/AlexLkv/android_app_tests/main/test.json";

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
                initializeUI();
                setListeners();
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
            question = root.getQuestions().get(index);
            for (int i = 0; i < answerButtons.size(); i++) {
                answerButtons.get(i).setText(question.getAnswers().get(i));
            }
            textViewQuestion.setText(question.getQuestion());
            tvProgressHorizontal.setProgress(index);
            tvProgressHorizontal.setSecondaryProgress(index + 1);
            cheked = false;
            right_ans = false;
            radioGroup.clearCheck();
            Right_ans = question.getAnswers().get((Integer.parseInt(question.getRight_ans()) - 1));
        });
    }

    /////ДЕЙСТВИЯ ПРИ КОНЦЕ ТЕСТИРОВАНИЯ
    public void endTesting() {
        Intent intent = new Intent(TestingActivity.this, ResultTesting.class);
        intent.putExtra("index", index);
        intent.putExtra("count_right_ans", count_right_ans);
        intent.putExtra("count_wrong_ans", count_wrong_ans);
        startActivity(intent);
    }

    /////СЛЕДУЮЩИЙ ВОПРОС
    public void next_question() {
        index++;
        if (index <= mlength && cheked) {
            if (right_ans) {
                count_right_ans++;
                text_right_ans.setText("Верных ответов: " + count_right_ans);
            } else {
                count_wrong_ans++;
                textIndex.setText("Количество ошибок: " + count_wrong_ans);
            }
            dbConrol();
            loadUi();
        } else if (!cheked) {
            Toast.makeText(getApplicationContext(), "Ничего не выбрано", Toast.LENGTH_SHORT).show();
        } else {
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
        next_question();
    }


    /////КНОПКА ПРОПУСКА
    public void skip(View view) {
        cheked = true;
        next_question();
    }

    /////КНОПКА ВЫХОДА
    public void ext(View view) {
        Intent intent = new Intent(TestingActivity.this, MainActivity.class);
        startActivity(intent);
    }
}