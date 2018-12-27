package com.example.pawelm.getfit;

import com.google.firebase.firestore.FirebaseFirestore;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.Years;

import java.util.Date;

public class User {
    private Integer mass;
    private double height;
    private Integer goal;
    private double activity;
    private boolean gender;
    private Date birthdate;
    private Double BMI;
    private Integer TDEE;


    static User currentUser;

    public User() {
        this(0, 0, 0, 0, false, new Date());
    }

    public User(Integer mass, Integer height, Integer goal, Integer physicalActivity, boolean gender, Date birthdate) {
        this.mass = mass;
        this.height = (double) height / 100;
        this.goal = goal;
        this.gender = gender;
        this.birthdate = birthdate;
        setActivity(physicalActivity);
        calculateStats();
    }

    public void calculateStats() {
        Integer s;
        if (gender)
            s = 5;
        else
            s = -191;
        LocalDate bday = new LocalDate(birthdate);
        LocalDate now = LocalDate.now();
        Years y = Years.yearsBetween(bday, now);

        Integer age = y.getYears();
        BMI = (float) mass / (height * height);
        Integer BMR = 10 * mass + (int) Math.ceil(625 * height) - 5 * age + s;
        TDEE = (int) Math.ceil(BMR * activity);
        if (goal == 0)
            TDEE = TDEE - 200;
        else if (goal == 2)
            TDEE = TDEE + 200;
    }

    public Integer getTDEE() {
        return TDEE;
    }

    public void setHeight(double h) {
        height = h;
    }

    public double getBMI() {
        return BMI;
    }

    public double getactivity() {
        return activity;
    }

    public Integer getGoal() {
        return goal;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public void setActivity(Integer physicalActivity) {
        if (physicalActivity == 0)
            activity = 1.3;
        else if (physicalActivity == 1)
            activity = 1.5;
        else if (physicalActivity == 2)
            activity = 1.8;
        else if (physicalActivity == 3)
            activity = 2.2;
    }

    public Integer getActivity() {
        if (activity == 1.3)
            return 0;
        else if (activity == 1.5)
            return 1;
        else if (activity == 1.8)
            return 2;
        else
            return 3;
    }

    public void setActivity(Double physicalActivity) {
        activity = physicalActivity;
    }

    public Integer getMass() {
        return mass;
    }

    public void setMass(Integer mass) {
        this.mass = mass;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}

