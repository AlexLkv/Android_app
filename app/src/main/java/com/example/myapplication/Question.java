package com.example.myapplication;

import java.util.ArrayList;

public class Question{
    public String level;
    public String id;
    public String right_ans;

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

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public String question;
    public ArrayList<String> answers;
}

