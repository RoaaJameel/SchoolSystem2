package com.example.schoolsystem2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3500; // the time of the splash screen in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logo = findViewById(R.id.schoolLogo);
        TextView schoolName = findViewById(R.id.schoolName);
        String name = "Palestine School";
        final int[] i = {0};

        // animation for showing the logo
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        logo.setVisibility(View.VISIBLE);
        logo.startAnimation(fadeIn);

        // animation for writing the school name letter by letter
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (i[0] <= name.length()) {
                    schoolName.setText(name.substring(0, i[0]));
                    i[0]++;
                    handler.postDelayed(this, 120);
                }
            }
        };
        handler.postDelayed(runnable, 500);

        // moving to the main activity after the splash screen
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // for not returning to the splash screen
        }, SPLASH_DURATION);
    }
}
