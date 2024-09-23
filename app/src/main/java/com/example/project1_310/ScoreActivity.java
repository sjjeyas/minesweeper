package com.example.project1_310;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity{
    public int score;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        Bundle b = getIntent().getExtras();
        assert b != null;
        score = b.getInt("score");
        if (score > 0){
            String scoreOutput = String.format("You used %2d seconds. You won!", score);
            scoreView.setText(scoreOutput);
        }
    }

    public void playAgain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
