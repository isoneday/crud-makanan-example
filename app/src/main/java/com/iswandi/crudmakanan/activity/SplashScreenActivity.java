package com.iswandi.crudmakanan.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.iswandi.crudmakanan.R;
import com.iswandi.crudmakanan.helper.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashScreenActivity extends SessionManager {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//       myIntent(LoginActivity.class);
//                finish();
                sessionManager.checkLogin();
            }
        },4000);
    }
}
