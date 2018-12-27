package com.example.pawelm.getfit;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FirebaseAuth sAuth;
    private FirebaseFirestore firestore;
    FirebaseUser user;
    ViewPager vp;
    TabLayout tabs;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        sAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        if (getIntent().getExtras() != null)
            date = (Date) getIntent().getExtras().get("date");
        else
            date = new Date();
        adapter.addFragment(ContainerFragment.newInstance(date), "Dziennik");
        adapter.addFragment(new SummaryFragment(), "Podsumowanie");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = sAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            firestore.collection("Users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User.currentUser = new User();
                            Integer mass = Integer.parseInt(document.get("weight").toString());
                            User.currentUser.setMass(mass);
                            Integer goal = Integer.parseInt(document.get("goal").toString());
                            User.currentUser.setGoal(goal);
                            Double activity = Double.parseDouble(document.get("activity").toString());
                            User.currentUser.setActivity(activity);
                            Double height = Double.parseDouble(document.get("height").toString());
                            User.currentUser.setHeight(height);
                            Boolean gender = Boolean.parseBoolean(document.get("gender").toString());
                            User.currentUser.setGender(gender);
                            Date d = ((Date) (document.get("birthdate")));
                            User.currentUser.setBirthdate(d);
                            User.currentUser.setActivity(activity);
                            User.currentUser.calculateStats();
                            vp = findViewById(R.id.view_pager);
                            setupViewPager(vp);
                            tabs = findViewById(R.id.tabs);
                            tabs.setupWithViewPager(vp);
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_log_out) {

            sAuth.signOut();
            User.currentUser = null;
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.edit_data)
            startActivity(new Intent(MainActivity.this, SetupActivity.class));

        else if (id == R.id.add_product)
            startActivity(new Intent(MainActivity.this, AddProductActivity.class));

        else if (id == R.id.add_exercise)
            startActivity(new Intent(MainActivity.this, AddExerciseActivity.class));


        return super.onOptionsItemSelected(item);
    }
}
