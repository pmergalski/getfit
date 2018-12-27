package com.example.pawelm.getfit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity {

    private Spinner genderSpinner;
    private Spinner activitySpinner;
    private Spinner goalSpinner;
    private EditText weightEdit;
    private EditText heightEdit;
    private MaterialButton pickDateBtn;
    private MaterialButton saveBtn;
    private DialogFragment newFragment;
    private FirebaseAuth sAuth;
    private FirebaseFirestore firestore;
    private LocalDate date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        sAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        genderSpinner = findViewById(R.id.gender_spinner);
        activitySpinner = findViewById(R.id.activity_spinner);
        goalSpinner = findViewById(R.id.goal_spinner);
        newFragment = new MyDatePickerFragment();

        final ArrayAdapter<CharSequence> activity_adapter = ArrayAdapter.createFromResource(this,
                R.array.activity, android.R.layout.simple_spinner_item);
        activitySpinner.setAdapter(activity_adapter);

        ArrayAdapter<CharSequence> goals_adapter = ArrayAdapter.createFromResource(this,
                R.array.goals, android.R.layout.simple_spinner_item);
        goalSpinner.setAdapter(goals_adapter);

        ArrayAdapter<CharSequence> gender_adapter = ArrayAdapter.createFromResource(this,
                R.array.genders, android.R.layout.simple_spinner_item);
        genderSpinner.setAdapter(gender_adapter);


        weightEdit = findViewById(R.id.weight_edit);
        heightEdit = findViewById(R.id.height_edit);
        saveBtn = findViewById(R.id.save_button);
        pickDateBtn = findViewById(R.id.date_pick_button);

        if (User.currentUser != null && !User.currentUser.equals(new User())) {
            weightEdit.setText(String.valueOf(User.currentUser.getMass()));
            heightEdit.setText(String.valueOf((int) (User.currentUser.getHeight() * 100)));
            activitySpinner.setSelection(User.currentUser.getActivity());
            if (User.currentUser.getGender())
                genderSpinner.setSelection(0);
            else
                genderSpinner.setSelection(1);
            goalSpinner.setSelection(User.currentUser.getGoal());
            date = LocalDate.fromDateFields(User.currentUser.getBirthdate());
        }

        pickDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newFragment.show(getSupportFragmentManager(), "date picker");
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean allGood = true;
                if (weightEdit.getText().toString().isEmpty()) {
                    weightEdit.setError("podaj wage");
                    allGood = false;
                }
                if (heightEdit.getText().toString().isEmpty()) {
                    heightEdit.setError("podaj wzrost");
                    allGood = false;
                }

                if (((MyDatePickerFragment) newFragment).getDate() != null)
                    date = ((MyDatePickerFragment) newFragment).getDate();
                if ((date != null || (User.currentUser.getBirthdate() != null)) && allGood) {

                    int w = Integer.parseInt(weightEdit.getText().toString());
                    int h = Integer.parseInt(heightEdit.getText().toString());
                    int activity = (int) activitySpinner.getSelectedItemId();
                    boolean gender = (genderSpinner.getSelectedItemId() == 0);
                    int goal = (int) goalSpinner.getSelectedItemId();
                    User.currentUser = new User(w, h, goal, activity, gender, date.toDate());

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("weight", User.currentUser.getMass());
                    userMap.put("height", User.currentUser.getHeight());
                    userMap.put("gender", User.currentUser.getGender());
                    userMap.put("goal", User.currentUser.getGoal());
                    userMap.put("activity", User.currentUser.getactivity());
                    userMap.put("birthdate", User.currentUser.getBirthdate());


                    final String user_id = sAuth.getCurrentUser().getUid();
                    firestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(SetupActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SetupActivity.this, "Wybierz date urodzenia", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
