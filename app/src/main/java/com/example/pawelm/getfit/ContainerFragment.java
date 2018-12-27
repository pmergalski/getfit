package com.example.pawelm.getfit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import androidx.fragment.app.Fragment;

public class ContainerFragment extends Fragment {

    public ContainerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getFragmentManager().beginTransaction().add(R.id.container, DiaryFragment.newInstance(new Date((Long) getArguments().get("date")))).commit();
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    public static ContainerFragment newInstance(Date currentDate) {
        ContainerFragment myFragment = new ContainerFragment();

        Bundle args = new Bundle();
        args.putLong("date", currentDate.getTime());
        myFragment.setArguments(args);

        return myFragment;
    }
}