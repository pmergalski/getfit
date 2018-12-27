package com.example.pawelm.getfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {


    private EditText regEmail;
    private EditText regPassword;
    private EditText regRepPassword;
    private MaterialButton regButton;
    private MaterialButton goToLoginButton;
    private ProgressBar regProgress;

    private FirebaseAuth sAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regRepPassword = findViewById(R.id.reg_rep_password);
        regButton = findViewById(R.id.reg_button);
        goToLoginButton = findViewById(R.id.reg_log_button);
        regProgress = findViewById(R.id.reg_progress);
        sAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean allGood = true;
                String mail = regEmail.getText().toString();
                String pass = regPassword.getText().toString();
                String repPass = regRepPassword.getText().toString();

                if (TextUtils.isEmpty(mail)) {
                    regEmail.setError("Nie może być puste");
                    allGood = false;
                }
                if (TextUtils.isEmpty(pass)) {
                    regPassword.setError("Nie może być puste");
                    allGood = false;
                } else {
                    if (!pass.equals(repPass)) {
                        regRepPassword.setError("Hasła muszą być takie same");
                        allGood = false;
                    }
                }

                if (allGood) {
                    regProgress.setVisibility(View.VISIBLE);
                    sAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = sAuth.getCurrentUser();
        if (user != null) {
            sendToMainActivity();
        }
    }

    void sendToMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
