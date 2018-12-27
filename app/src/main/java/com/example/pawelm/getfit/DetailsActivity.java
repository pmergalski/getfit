package com.example.pawelm.getfit;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class DetailsActivity extends AppCompatActivity {

    TextView dateText;
    TableLayout products_table;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth sAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sAuth = FirebaseAuth.getInstance();
        user = sAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_details);
        dateText = findViewById(R.id.date_text_view);
        products_table = findViewById(R.id.products_table);
        Bundle extras = getIntent().getExtras();
        final String type = (String) extras.get("type");
        Timestamp time = (Timestamp) extras.get("date");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String myType = (String) extras.getString("myType");
        dateText.setText(myType + " z dnia: " + simpleDateFormat.format(time));

        if (type.equals("Exercises")) {
            firestore.collection("Users").document(user.getUid())
                    .collection("Workouts").
                    whereEqualTo("date", time).get().
                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    final DocumentReference ref = (DocumentReference) document.get("exercise");
                                    final Long quantity = (Long) document.get("quantity");
                                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String name = documentSnapshot.get("name").toString();
                                            Object burned = documentSnapshot.get("burned_calories");
                                            Double burnedCalories;
                                            if (burned.getClass().equals(Long.class)) {
                                                Long temp = (Long) burned;
                                                burnedCalories = temp.doubleValue();
                                            } else
                                                burnedCalories = (Double) burned;
                                            LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                            final View v = mInflater.inflate(R.layout.details_table_row, null);
                                            TextView textView = v.findViewById(R.id.table_row_text_view);

                                            textView.setText(name + ", " + quantity + ", spalone " + burnedCalories * quantity + " kcal");
                                            Button button = v.findViewById(R.id.table_row_delete_button);
                                            button.setText("-");
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            products_table.removeView(v);
                                                        }
                                                    });
                                                }


                                            });
                                            products_table.addView(v);
                                        }
                                    });
                                }
                            }
                        }
                    });
        } else {
            firestore.collection("Users").document(user.getUid())
                    .collection("Meals").
                    whereEqualTo("date", time).get().
                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                    if (!document.get("type").equals(type))
                                        continue;
                                    final DocumentReference ref = (DocumentReference) document.get("product");
                                    final Long quantity = (Long) document.get("quantity");
                                    ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String name = documentSnapshot.get("name").toString();
                                            Long calories = (Long) documentSnapshot.get("calories");
                                            LayoutInflater mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                                            final View v = mInflater.inflate(R.layout.details_table_row, null);
                                            TextView textView = v.findViewById(R.id.table_row_text_view);

                                            textView.setText(name + " x" + quantity + ",  " + calories * quantity + " kcal");
                                            Button button = v.findViewById(R.id.table_row_delete_button);
                                            button.setText("-");
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            products_table.removeView(v);
                                                        }
                                                    });
                                                }
                                            });
                                            products_table.addView(v);
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }
}
