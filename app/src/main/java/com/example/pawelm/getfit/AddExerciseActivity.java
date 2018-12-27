package com.example.pawelm.getfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class AddExerciseActivity extends AppCompatActivity {

    EditText calories;
    EditText exercise_name;
    MaterialButton add_button;
    FirebaseAuth sAuth;
    FirebaseFirestore firestore;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        calories = findViewById(R.id.calories_edit);
        exercise_name = findViewById(R.id.name_edit);
        add_button = findViewById(R.id.add_new_exercise_btn);
        sAuth = FirebaseAuth.getInstance();
        user = sAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok = true;
                if (exercise_name.getText().toString().isEmpty()) {
                    exercise_name.setError(getString(R.string.can_not_be_empty));
                    ok = false;
                }
                if (calories.getText().toString().isEmpty()) {
                    calories.setError(getString(R.string.can_not_be_empty));
                    ok = false;
                }
                if (ok) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", exercise_name.getText().toString());
                    map.put("burned_calories", Integer.parseInt(calories.getText().toString()));
                    firestore.collection("Exercises")
                            .document()
                            .set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        goToMainActivity();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(AddExerciseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
