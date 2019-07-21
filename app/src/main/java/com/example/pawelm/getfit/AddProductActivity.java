package com.example.pawelm.getfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    private EditText calories;
    private EditText product_name;
    private EditText product_portion;
    private MaterialButton add_button;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        calories = findViewById(R.id.product_calories_edit);
        product_name = findViewById(R.id.product_name_edit);
        add_button = findViewById(R.id.add_new_product_btn);
        product_portion = findViewById(R.id.product_portion_edit);
        firestore = FirebaseFirestore.getInstance();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ok = true;
                if (product_name.getText().toString().isEmpty()) {
                    product_name.setError(getString(R.string.can_not_be_empty));
                    ok = false;
                }
                if (calories.getText().toString().isEmpty()) {
                    calories.setError(getString(R.string.can_not_be_empty));
                    ok = false;
                }
                if(product_portion.getText().toString().isEmpty()){
                    product_portion.setError(getString(R.string.can_not_be_empty));
                    ok = false;
                }
                if (ok) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", product_name.getText().toString());
                    map.put("calories", Integer.parseInt(calories.getText().toString()));
                    map.put("portion", Integer.parseInt(calories.getText().toString()) + "g");
                    firestore.collection("Products")
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
        Intent intent = new Intent(AddProductActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
