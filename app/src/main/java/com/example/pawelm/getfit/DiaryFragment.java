package com.example.pawelm.getfit;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.joda.time.LocalDate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class DiaryFragment extends Fragment {

    TextView date;
    private TextView caloriesView;
    private FirebaseFirestore firestore;
    private FirebaseUser user;
    private FirebaseAuth sAuth;
    private Integer currentCalories;
    private TableLayout tbreakfast;
    private TableLayout tsecond_bf;
    private TableLayout tlunch;
    private TableLayout tsupper;
    private TableLayout tworkout;
    private MaterialButton workoutBtn;
    private MaterialButton breakfastBtn;
    private MaterialButton second_bfBtn;
    private MaterialButton lunchBtn;
    private MaterialButton supperBtn;
    private Button nextButton;
    private Button previousButton;

    private Timestamp d;
    private Date searchDate;


    public DiaryFragment() {
    }


    public static DiaryFragment newInstance(Date currentDate) {
        DiaryFragment myFragment = new DiaryFragment();

        Bundle args = new Bundle();
        args.putLong("date", currentDate.getTime());
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_diary, container, false);
    }


    private void setUpView(final TableLayout tview, CollectionReference meals, final String type) {
        meals.whereEqualTo("date", d).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        if (document.get("type").equals(type)) {
                            DocumentReference dref = (DocumentReference) document.get("product");

                            final Long quantity = (Long) document.get("quantity");
                            dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String name = documentSnapshot.get("name").toString();
                                    String portion = documentSnapshot.get("portion").toString();
                                    Integer p = Integer.parseInt(portion.replaceAll("[\\D]", ""));
                                    Long calories = (Long) documentSnapshot.get("calories");
                                    TableRow tableRow = new TableRow(getActivity());
                                    tableRow.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    TextView textView = new TextView(getActivity());
                                    textView.setText(name + " x" + quantity + ", " + p * quantity + "g");
                                    currentCalories = currentCalories - (quantity.intValue() * calories.intValue());
                                    caloriesView.setText("Pozostało " + currentCalories + " kcal");
                                    tableRow.addView(textView);
                                    tview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                            intent.putExtra("type", type);
                                            intent.putExtra("date", d);
                                            String myType;
                                            switch (type) {
                                                case "Breakfast":
                                                    myType = "Śniadanie";
                                                    break;
                                                case "secondBreakfast":
                                                    myType = "Drugie Śniadanie";
                                                    break;
                                                case "Lunch":
                                                    myType = "Obiad";
                                                    break;
                                                default:
                                                    myType = "Kolacja";
                                            }
                                            intent.putExtra("myType", myType);
                                            startActivity(intent);
                                        }
                                    });
                                    tview.addView(tableRow);
                                }
                            });
                        }

                    }
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentCalories = 0;
        sAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        user = sAuth.getCurrentUser();
        date = view.findViewById(R.id.date_view);
        if (getArguments() != null) {
            searchDate = new Date(getArguments().getLong("date"));
        } else
            searchDate = new Date();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(searchDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        searchDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(searchDate);
        date.setText(format);
        d = new Timestamp(searchDate.getTime());

        caloriesView = view.findViewById(R.id.calories_view);
        tbreakfast = view.findViewById(R.id.table_breakfast);
        tsecond_bf = view.findViewById(R.id.table_second_bf);
        tlunch = view.findViewById(R.id.table_lunch);
        tsupper = view.findViewById(R.id.table_supper);
        tworkout = view.findViewById(R.id.table_workout);
        breakfastBtn = view.findViewById(R.id.add_breakfast_bnt);
        second_bfBtn = view.findViewById(R.id.add_second_bf_btn);
        lunchBtn = view.findViewById(R.id.add_lunch_btn);
        supperBtn = view.findViewById(R.id.add_supper_btn);
        workoutBtn = view.findViewById(R.id.add_workout_btn);
        nextButton = view.findViewById(R.id.next_day_btn);
        previousButton = view.findViewById(R.id.previus_day_btn);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewProductsActivity.class);
                String type;
                if (view.equals(breakfastBtn))
                    type = "Breakfast";
                else if (view.equals(second_bfBtn))
                    type = "secondBreakfast";
                else if (view.equals(lunchBtn))
                    type = "Lunch";
                else
                    type = "Supper";
                intent.putExtra("type", type);
                intent.putExtra("date", searchDate);
                startActivity(intent);
            }
        };
        breakfastBtn.setOnClickListener(listener);
        second_bfBtn.setOnClickListener(listener);
        lunchBtn.setOnClickListener(listener);
        supperBtn.setOnClickListener(listener);

        workoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewExercisesActivity.class);
                intent.putExtra("date", searchDate);
                startActivity(intent);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.DATE, 1);
                Date nextDay = cal.getTime();
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                ft.replace(R.id.container, newInstance(nextDay)).commit();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cal.add(Calendar.DATE, -1);
                Date nextDay = cal.getTime();
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                ft.replace(R.id.container, newInstance(nextDay)).commit();
            }
        });
        if (User.currentUser != null) {
            currentCalories = User.currentUser.getTDEE();
            caloriesView.setText(getString(R.string.remaining_calories) + currentCalories);

            CollectionReference meals = firestore.collection("Users").document(user.getUid())
                    .collection("Meals");
            setUpView(tbreakfast, meals, "Breakfast");
            setUpView(tsecond_bf, meals, "secondBreakfast");
            setUpView(tsupper, meals, "Supper");
            setUpView(tlunch, meals, "Lunch");
            setUpWorkout();

        }

    }

    private void setUpWorkout() {
        firestore.collection("Users").document(user.getUid())
                .collection("Workouts")
                .whereEqualTo("date", d)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference ref = (DocumentReference) document.get("exercise");
                        final Long quantity = (Long) document.get("quantity");
                        ref.get().addOnSuccessListener(
                                new OnSuccessListener<DocumentSnapshot>() {
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
                                        TableRow tableRow = new TableRow(getActivity());
                                        tableRow.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                                        TextView textView = new TextView(getActivity());
                                        textView.setText(name + ", " + quantity);
                                        currentCalories = currentCalories + quantity.intValue() * burnedCalories.intValue();
                                        caloriesView.setText("Pozostałe kalorie: " + currentCalories);
                                        textView.setMaxLines(5);
                                        textView.setSingleLine(false);
                                        textView.setHorizontallyScrolling(false);
                                        tableRow.addView(textView);
                                        tworkout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                                intent.putExtra("type", "Exercises");
                                                intent.putExtra("date", d);
                                                intent.putExtra("myType", "Ćwiczenia");
                                                startActivity(intent);
                                            }
                                        });
                                        tworkout.addView(tableRow);
                                    }
                                });
                    }
                }
            }
        });
    }


}
