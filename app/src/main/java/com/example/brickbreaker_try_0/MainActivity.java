package com.example.brickbreaker_try_0;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button restartGameButton = (Button) findViewById(R.id.restartGameButton);
        Button releaseBallButton = (Button) findViewById(R.id.releaseBallButton);

        restartGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameStatus.RestartGame();
            }
        });

        releaseBallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameStatus.ReleaseBall();
            }
        });
    }
}