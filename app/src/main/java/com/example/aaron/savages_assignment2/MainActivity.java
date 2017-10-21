package com.example.aaron.savages_assignment2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {
    int grade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BoardView board = new BoardView(this);
        setContentView(board);
        System.out.println("Completed onCreate");
        grade = board.score;
    }

}

