package zyot.shyn.healthcareapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.base.BaseActivity;
import zyot.shyn.healthcareapp.utils.MyString;

public class ResetPasswordActivity extends BaseActivity {
    private EditText emailEt;
    private Button resetBtn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailEt = findViewById(R.id.reset_email_et);
        resetBtn = findViewById(R.id.reset_btn);

        mAuth = FirebaseAuth.getInstance();

        resetBtn.setOnClickListener(view -> {
            final String email = emailEt.getText().toString();
            if (MyString.isEmpty(email)) {
                Snackbar.make(resetBtn, "Please enter your email!", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (!MyString.isValidEmail(email)) {
                Snackbar.make(resetBtn, "Email is not valid!", Snackbar.LENGTH_LONG).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Snackbar.make(resetBtn, "Please check your email", Snackbar.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, SignInActivity.class);
                    intent.setAction("LOGIN_AFTER_RESET_PASSWORD");
                    intent.putExtra("user_email", email);
                    startActivity(intent);
                } else {
                    String error = task.getException().getMessage();
                    Snackbar.make(resetBtn, "Error: " + error, Snackbar.LENGTH_SHORT).show();
                }
            });

        });
    }
}