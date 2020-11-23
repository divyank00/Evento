package com.example.collegeproject.addEvent.eventType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.collegeproject.R;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.TypeModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class EventType extends AppCompatActivity implements Adapter.itemClick {

    private ShimmerRecyclerView shimmerRecyclerView;
    private Adapter adapter;
    private ArrayList<TypeModel> types;
    private TypeModel selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Type");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent baseIntent = getIntent();
        selectedItem = (TypeModel) baseIntent.getSerializableExtra("type");
        shimmerRecyclerView = findViewById(R.id.type);
        types = new ArrayList<>();
        types.addAll(StaticVariables.eventTypes);
        adapter = new Adapter(this, types, selectedItem, this);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shimmerRecyclerView.setAdapter(adapter);
    }

    @Override
    public void itemClicked(int position) {
        selectedItem = types.get(position);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", (Serializable) selectedItem);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}