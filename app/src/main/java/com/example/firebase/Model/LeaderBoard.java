package com.example.firebase.Model;

public class LeaderBoard {
    String Email;
    String Steps;
    String Time;

    public LeaderBoard() {
    }

    public String getEmail() {
        return Email;
    }

    public String getSteps() {
        return Steps;
    }

    public String getTime() {
        return Time;
    }

    public LeaderBoard(String email, String steps, String time) {
        Email = email;
        Steps = steps;
        Time = time;
    }
}
