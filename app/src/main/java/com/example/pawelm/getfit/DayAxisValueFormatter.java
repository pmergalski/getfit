package com.example.pawelm.getfit;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DayAxisValueFormatter implements IAxisValueFormatter {

    private long referenceTimestamp;

    public DayAxisValueFormatter(long referenceTimestamp) {
        this.referenceTimestamp = referenceTimestamp;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long convertedTimestamp = (long) value;

        long originalTimestamp = referenceTimestamp + convertedTimestamp;
        Timestamp timestamp = new Timestamp(originalTimestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String string = dateFormat.format(new Date(timestamp.getTime()));
        return (string);
    }
}