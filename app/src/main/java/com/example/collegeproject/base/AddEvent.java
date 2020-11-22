package com.example.collegeproject.base;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.collegeproject.R;
import com.example.collegeproject.addevent.Add;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEvent extends Fragment {

    FloatingActionButton add;

    public AddEvent() {
        // Required empty public constructor
    }

    public static AddEvent newInstance() {
        return new AddEvent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        add = v.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Add.class));
            }
        });
        return v;
    }
}