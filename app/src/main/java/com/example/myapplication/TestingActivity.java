package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestingActivity extends AppCompatActivity {
    Question question;
    boolean right_ans = false;
    private boolean cheked = false;
    private int index = 0;
    private int count_right_ans = 0;
    private final int mlength = 10;
    private TextView textViewQuestion, textIndex, text_right_ans;
    private RadioGroup radioGroup;
    private List<Button> answerButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        initializeUI();
        loadQuestion();
        setListeners();
    }

    private void initializeUI() {
        radioGroup = findViewById(R.id.radioGroup);
        text_right_ans = findViewById(R.id.right_ans);
        textViewQuestion = findViewById(R.id.question);
        textIndex = findViewById(R.id.index);
        answerButtons.addAll(Arrays.asList(findViewById(R.id.btn_ans_1), findViewById(R.id.btn_ans_2), findViewById(R.id.btn_ans_3), findViewById(R.id.btn_ans_4)));
    }

    private void setListeners() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            cheked = true;
            switch (checkedId) {
                case -1:
                    cheked = false;
                    break;
                case R.id.btn_ans_1:
                    right_ans = question.getRight_ans().equals("1");
                    break;
                case R.id.btn_ans_2:
                    right_ans = question.getRight_ans().equals("2");
                    break;
                case R.id.btn_ans_3:
                    right_ans = question.getRight_ans().equals("3");
                    break;
                case R.id.btn_ans_4:
                    right_ans = question.getRight_ans().equals("4");
                    break;
            }
        });
    }

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

                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    question = new Gson().fromJson(jsonObject.getJSONArray("questions").getJSONObject(index).toString(), Question.class);
                    runOnUiThread(() -> {
                        for (int i = 0; i < answerButtons.size(); i++) {
                            answerButtons.get(i).setText(question.getAnswers().get(i));
                        }
                        textViewQuestion.setText(question.getQuestion());
                        textIndex.setText("Номер вопроса: " + (index + 1));
                        text_right_ans.setText("Верных ответов: " + count_right_ans);
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private static class Question {
        private String level;
        private String id;
        private String right_ans;
        private String question;
        private List<String> answers = new ArrayList<>();

        private Question() {
        }

        public String getLevel() {
            return level;
        }

        public String getId() {
            return id;
        }

        public String getRight_ans() {
            return right_ans;
        }

        public String getQuestion() {
            return question;
        }

        public List<String> getAnswers() {
            return answers;
        }
    }

    public void next(View view) {

        AlertDialog.Builder builder = null;
        if (index < mlength && cheked) {
            if (right_ans) {
                count_right_ans++;
                text_right_ans.setText("Верных ответов: " + count_right_ans);
            }
            textIndex.setText("Номер вопроса: " + (index + 1));
            index++;
            loadQuestion();
            cheked = false;
            radioGroup.clearCheck();
        } else if (!cheked) {
            Toast.makeText(getApplicationContext(), "Ничего не выбрано", Toast.LENGTH_SHORT).show();
        }else {
        builder = new AlertDialog.Builder(TestingActivity.this);
        builder.setTitle("Поздравляем! Вы прошли тестирование");
        builder.setMessage("Количество вопросов: " + (index + 1) + "\n" + "Количество верных ответов: " + count_right_ans );

        builder.setPositiveButton("Ок, пон",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(TestingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
        builder.show();

    }

}}