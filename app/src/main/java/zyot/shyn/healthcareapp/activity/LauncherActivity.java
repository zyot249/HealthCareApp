package zyot.shyn.healthcareapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.base.BaseActivity;

public class LauncherActivity extends BaseActivity {
    private final static int SPLASH_DISPLAY_TIME = 1000;

    SharedPreferences sp;
    private String password = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laucher);
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        password = sp.getString("password", "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (password.equals("")) { // nếu không có pass
                    intent = new Intent(LauncherActivity.this, SignInActivity.class);
                } else { // nếu có pass
                    // start LockScreen
                    intent = new Intent(LauncherActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, SPLASH_DISPLAY_TIME);
    }
}
