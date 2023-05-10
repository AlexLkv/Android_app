package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TestingActivity extends AppCompatActivity {

    private int index = 0;
    private int mlength = 10;
    private List<String> answers = new ArrayList<>();

    private TextView textViewQuestion;
    private List<Button> answerButtons = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        textViewQuestion = findViewById(R.id.question);

        answerButtons.add(findViewById(R.id.btn_ans_1));
        answerButtons.add(findViewById(R.id.btn_ans_2));
        answerButtons.add(findViewById(R.id.btn_ans_3));
        answerButtons.add(findViewById(R.id.btn_ans_4));

        loadQuestion();
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
                    Question question = new Gson().fromJson(jsonObject.getJSONArray("questions").getJSONObject(index).toString(), Question.class);

                    answers.clear();
                    answers.addAll(question.getAnswers());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < answerButtons.size(); i++) {
                                answerButtons.get(i).setText(answers.get(i));
                            }
                            textViewQuestion.setText(question.getQuestion());
                        }
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
        private List<String> answers;

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

        if (index < mlength) {
            index++;
        } else index = 0;
        loadQuestion();
    }
}