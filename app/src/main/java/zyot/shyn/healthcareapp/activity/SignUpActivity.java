package zyot.shyn.healthcareapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import zyot.shyn.healthcareapp.R;
import zyot.shyn.healthcareapp.base.BaseActivity;
import zyot.shyn.healthcareapp.model.User;
import zyot.shyn.healthcareapp.utils.MyString;

public class SignUpActivity extends BaseActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();

    private EditText emailEt;
    private EditText passEt;
    private EditText cfPassEt;
    private EditText displayNameEt;
    private EditText ageEt;
    private AutoCompleteTextView autoCompleteTextView;
    private Button submitBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference dbReference;

    private static final String[] GENDER_OPTIONS = new String[]{"Male", "Female", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // setting for toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.activity_title_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailEt = findViewById(R.id.email_et);
        passEt = findViewById(R.id.password_et);
        cfPassEt = findViewById(R.id.cf_password_et);
        displayNameEt = findViewById(R.id.display_name_et);
        ageEt = findViewById(R.id.age_et);
        autoCompleteTextView = findViewById(R.id.auto_gender_opt);
        submitBtn = findViewById(R.id.submit_btn);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_gender_dropdown, R.id.gender_opt_text, GENDER_OPTIONS);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setThreshold(1);

        // init firebaseAuth
        mAuth = FirebaseAuth.getInstance();

        submitBtn.setOnClickListener(view -> {
            String email = emailEt.getText().toString();
            String pass = passEt.getText().toString();
            String cfPass = cfPassEt.getText().toString();
            String displayName = displayNameEt.getText().toString();
            String age = ageEt.getText().toString();
            String gender = autoCompleteTextView.getText().toString();

            if (validateInput(email, pass, cfPass, displayName, age, gender)) {
                register(displayName, email, pass, gender, Integer.parseInt(age));
            }
        });
    }

    private boolean validateInput(String email, String pass, String cfPass, String displayName, String age, String gender) {
        if (MyString.isEmpty(email)) {
            Snackbar.make(submitBtn, "Email is empty!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (MyString.isEmpty(pass)) {
            Snackbar.make(submitBtn, "Password is empty!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (MyString.isEmpty(cfPass)) {
            Snackbar.make(submitBtn, "Confirm password is empty!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (MyString.isEmpty(displayName)) {
            Snackbar.make(submitBtn, "Display name is empty!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (MyString.isEmpty(age)) {
            Snackbar.make(submitBtn, "Age is empty!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (MyString.isEmpty(gender)) {
            Snackbar.make(submitBtn, "Gender is empty!", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!MyString.isValidEmail(email)) {
            Snackbar.make(submitBtn, "Email is not valid!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!MyString.isValidPassword(pass)) {
            Snackbar.make(submitBtn, "Password is not valid!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        if (!cfPass.equals(pass)) {
            Snackbar.make(submitBtn, "Confirm password is not wrong!", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void register(final String displayName, String email, String password, String gender, int age) {
        final String userEmail = email;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, task -> {
                    if (task.isSuccessful()) {
                        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        assert firebaseUser != null;
                        String userID = firebaseUser.getUid();

                        Snackbar.make(submitBtn, "Register Successfully", Snackbar.LENGTH_SHORT).show();
                        // Write database
                        dbReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
                        User user = new User(userID, displayName, age, gender);

                        dbReference.setValue(user).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // open Login Activity
                                    mAuth.signOut();
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    intent.setAction("LOGIN_AFTER_REGISTER");
                                    intent.putExtra("user_email", userEmail);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(submitBtn, "You can't register with this email and password!", Snackbar.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                    firebaseUser.delete();
                                }
                            }
                        });
                    } else
                        Snackbar.make(submitBtn, "Register Failed", Snackbar.LENGTH_SHORT).show();
                });


    }
}