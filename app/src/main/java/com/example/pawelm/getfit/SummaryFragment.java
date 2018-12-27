package com.example.pawelm.getfit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SummaryFragment extends Fragment {

    public SummaryFragment() {
    }


    LineChart chart;
    FirebaseFirestore firestore;
    private FirebaseAuth sAuth;
    FirebaseUser user;
    long refStamp;
    EditText weight;
    Button addWeightBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chart = view.findViewById(R.id.chart);

        sAuth = FirebaseAuth.getInstance();
        user = sAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        weight = view.findViewById(R.id.current_weight);
        addWeightBtn = view.findViewById(R.id.add_current_weight_btn);
        firestore.collection("Users")
                .document(user.getUid())
                .collection("WeightHistory")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        ArrayList<Long> dates = new ArrayList<>();
                        final ArrayList<Long> weights = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Date date = (Date) document.get("date");
                            Timestamp time = new Timestamp(date.getTime());
                            Long weight = (Long) document.get("weight");
                            dates.add(time.getTime());
                            weights.add(weight);
                        }
                        ArrayList<Long> copy = (ArrayList<Long>) dates.clone();
                        Collections.sort(copy);
                        refStamp = copy.get(0);
                        for (int i = 0; i < dates.size(); i++) {
                            dates.set(i, dates.get(i) - refStamp);
                        }
                        ArrayList<Entry> entries = new ArrayList<>();
                        for (int i = 0; i < dates.size(); i++)
                            entries.add(new Entry(dates.get(i), weights.get(i)));
                        Collections.sort(entries, new Comparator<Entry>() {
                            @Override
                            public int compare(Entry entry, Entry t1) {
                                if (entry.getX() > t1.getX())
                                    return 1;
                                else if (entry.getX() < t1.getX())
                                    return -1;
                                else
                                    return 0;
                            }
                        });
                        LineDataSet data = new LineDataSet(entries, "WAGA");
                        List<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(data);
                        data.setColor(R.color.colorPrimaryDark);
                        data.setLineWidth(2.5f);
                        LineData chartData = new LineData(dataSets);
                        chart.setData(chartData);
                        IAxisValueFormatter formatter = new DayAxisValueFormatter(refStamp);

                        XAxis xAxis = chart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setValueFormatter(formatter);
                        xAxis.setGranularity(86400000f);
                        YAxis yAxisRight = chart.getAxisRight();
                        yAxisRight.setEnabled(false);
                        YAxis yAxisLeft = chart.getAxisLeft();
                        yAxisLeft.setGranularity(1f);
                        chart.invalidate();
                    }
                    addWeightBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!weight.getText().toString().isEmpty()) {
                                Date date = new Date();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);
                                cal.set(Calendar.HOUR_OF_DAY, 0);
                                cal.set(Calendar.MINUTE, 0);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                date = cal.getTime();
                                final int current_weight = Integer.parseInt(weight.getText().toString());
                                User.currentUser.setMass(current_weight);
                                User.currentUser.calculateStats();
                                final CollectionReference collectionReference = firestore.collection("Users").document(user.getUid())
                                        .collection("WeightHistory");
                                final Date finalDate = date;
                                collectionReference.whereEqualTo("date", date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("date", finalDate);
                                            map.put("weight", current_weight);
                                            if (task.getResult().size() == 0) {
                                                collectionReference.add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            setUserVisibleHint(true);
                                                        }
                                                    }
                                                });
                                            } else {
                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                    doc.getReference().update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                setUserVisibleHint(true);
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");
        }
    }
}
