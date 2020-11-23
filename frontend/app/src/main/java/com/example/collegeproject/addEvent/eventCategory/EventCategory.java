package com.example.collegeproject.addEvent.eventCategory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.collegeproject.R;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.CategoryModel;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.ArrayList;
import java.util.Objects;

public class EventCategory extends AppCompatActivity implements Adapter.itemClick {

    private ShimmerRecyclerView shimmerRecyclerView;
    private Adapter adapter;
    private ArrayList<CategoryModel> categories, selectedItems;
    private ArrayList<String> selectedIds, selectedIdsInitial;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_category);
        categories = new ArrayList<>();
        if (StaticVariables.eventCategories != null) {
            categories.addAll(StaticVariables.eventCategories);
        }
        selectedItems = new ArrayList<>();
        selectedIdsInitial = new ArrayList<>();
        selectedIds = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Category");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.inflateMenu(R.menu.category_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.save) {
                    save();
                }
                return true;
            }
        });
        Intent baseIntent = getIntent();
        selectedItems = baseIntent.getParcelableArrayListExtra("category");
        for (CategoryModel model : Objects.requireNonNull(selectedItems))
            selectedIdsInitial.add(model.getId());
        selectedIds.addAll(selectedIdsInitial);
        shimmerRecyclerView = findViewById(R.id.category);
        adapter = new Adapter(this, categories, selectedIds, this);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shimmerRecyclerView.setAdapter(adapter);
    }

    @Override
    public void itemClicked(int position) {
        if (selectedIds.contains(categories.get(position).getId())) {
            selectedIds.remove(categories.get(position).getId());
            for (CategoryModel model : selectedItems) {
                if (model.getId().equals(categories.get(position).getId())) {
                    selectedItems.remove(model);
                    break;
                }
            }
        } else {
            selectedIds.add(categories.get(position).getId());
            selectedItems.add(categories.get(position));
        }
        adapter.notifyDataSetChanged();
    }

    void save() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Select at least one category!", Toast.LENGTH_SHORT).show();
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putParcelableArrayListExtra("result", selectedItems);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedIds.size() != selectedIdsInitial.size()) {
            showDialog();
            return;
        }
        for (String id : selectedIds) {
            if (!selectedIdsInitial.contains(id)) {
                showDialog();
                return;
            }
        }
        super.onBackPressed();
    }

    private void showDialog() {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.discard)
                .setMessage("Do you wish to discard the changes you have made?")
                .setCancelable(true)
                .setPositiveButton("Discard", (dialogInterface, which) -> finish())
                .setNegativeButton("Save", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    save();
                })
                .build();
        LottieAnimationView animationView = mDialog.getAnimationView();
//            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mDialog.show();
    }
}