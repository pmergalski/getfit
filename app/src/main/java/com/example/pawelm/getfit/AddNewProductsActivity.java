package com.example.pawelm.getfit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddNewProductsActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText editText;
    TableLayout table;
    FirebaseFirestore firestore;
    private FirebaseAuth sAuth;
    FirebaseUser user;
    MaterialButton searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        toolbar = findViewById(R.id.add_toolbar);
        toolbar.setTitle(getString(R.string.add_new_products));
    }

    @Override
    protected void onStart() {
        super.onStart();

        editText = findViewById(R.id.search_view);
        searchBtn = findViewById(R.id.search_btn);
        table = findViewById(R.id.tableLayout);
        Intent intent = getIntent();
        final String type = intent.getExtras().getString("type");
        sAuth = FirebaseAuth.getInstance();
        user = sAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                table.removeAllViews();
                String name = editText.getText().toString();
                firestore.collection("Products").orderBy("name")
                        .startAt(name).endAt(name + '\uf8ff')
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (final QueryDocumentSnapshot document : task.getResult()) {

                                LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                View v = mInflater.inflate(R.layout.table_row, null);
                                TextView textView = v.findViewById(R.id.table_row_view);
                                textView.setText(document.get("name").toString() + ", " + document.get("calories") + " kalorii");
                                textView.setSelected(true);
                                final EditText quantity = v.findViewById(R.id.table_row_edit);
                                Button button = v.findViewById(R.id.table_row_button);
                                button.setText(R.string.add);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String quantityString = (quantity.getText().toString());
                                        if (!quantityString.isEmpty()) {
                                            Integer quantityInt = Integer.parseInt(quantityString);
                                            final Date date = (Date) getIntent().getExtras().get("date");
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("type", type);
                                            map.put("quantity", quantityInt);
                                            map.put("date", date);
                                            map.put("product", document.getReference());

                                            firestore.collection("Users")
                                                    .document(user.getUid())
                                                    .collection("Meals")
                                                    .document()
                                                    .set(map)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Intent intent = new Intent(AddNewProductsActivity.this, MainActivity.class);
                                                                intent.putExtra("date", date);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } else
                                            quantity.setError(getResources().getString(R.string.can_not_be_empty));
                                    }
                                });
                                table.addView(v);
                            }
                        }
                    }
                });
            }
        });
    }
}
