package com.example.collegeproject.addEvent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collegeproject.R;
import com.example.collegeproject.addEvent.eventCategory.EventCategory;
import com.example.collegeproject.addEvent.eventType.EventType;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.CategoryModel;
import com.example.collegeproject.models.PostEventModel;
import com.example.collegeproject.models.TypeModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.libizo.CustomEditText;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Add3 extends AppCompatActivity {

    private CustomEditText quantity, price;
    private ProgressBar outerProgress, innerProgress;
    private FloatingActionButton next;
    private TypeModel eventType;
    private ArrayList<CategoryModel> eventCategory;
    private ArrayList<String> eventCategoryIds;
    private LinearLayout eventTypeLL, eventCategoryLL;
    private TextView eventTypeHeading, eventTypeTV, eventCategoryHeading, eventCategoryTV;
    private PostEventModel postEventModel, initialPostEventModel;
    private boolean isEditMode = false;
    private final AnimatorSet setNext = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add3);
        postEventModel = (PostEventModel) getIntent().getSerializableExtra("eventModel");
        isEditMode = getIntent().getBooleanExtra("editMode", false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Info");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        eventCategory = new ArrayList<>();
        eventCategoryIds = new ArrayList<>();
        eventTypeHeading = findViewById(R.id.eventTypeHeading);
        eventTypeTV = findViewById(R.id.eventTypeTV);
        eventCategoryHeading = findViewById(R.id.eventCategoryHeading);
        eventCategoryTV = findViewById(R.id.eventCategoryTV);
        eventTypeLL = findViewById(R.id.eventType);
        eventCategoryLL = findViewById(R.id.eventCategory);
        quantity = findViewById(R.id.totalTicket);
        price = findViewById(R.id.priceTicket);
        quantity.addTextChangedListener(new textWatcher());
        price.addTextChangedListener(new textWatcher());
        outerProgress = findViewById(R.id.progress);
        next = findViewById(R.id.next);
        addAnimationToNextFAB();
        if (isEditMode) {
            outerProgress.setVisibility(View.GONE);
            next.setBackgroundColor(getResources().getColor(R.color.green));
            next.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tick));
        }
        innerProgress = findViewById(R.id.innerProgress);
        setData();
        eventTypeLL.setOnClickListener(v -> {
            Intent intent = new Intent(Add3.this, EventType.class);
            intent.putExtra("type", (Serializable) eventType);
            startActivityForResult(intent, 200);
        });
        eventCategoryLL.setOnClickListener(v -> {
            Intent intent = new Intent(Add3.this, EventCategory.class);
            intent.putParcelableArrayListExtra("category", eventCategory);
            startActivityForResult(intent, 300);
        });
        next.setOnClickListener(v -> {
            try {
                closeKeyboard();
                eventTypeHeading.setError(null);
                eventCategoryHeading.setError(null);
                quantity.setError(null);
                price.setError(null);
                if (Objects.requireNonNull(quantity.getText()).toString().trim().isEmpty()) {
                    quantity.setError(getString(R.string.mandatory));
                    return;
                }
                if (Objects.requireNonNull(price.getText()).toString().trim().isEmpty()) {
                    price.setError(getString(R.string.mandatory));
                    return;
                }
                if (eventTypeTV.getText().toString().trim().isEmpty()) {
                    eventTypeHeading.setError(getString(R.string.mandatory));
                    return;
                }
                if (eventCategoryTV.getText().toString().trim().isEmpty()) {
                    eventCategoryHeading.setError(getString(R.string.mandatory));
                    return;
                }
                quantity.clearFocus();
                price.clearFocus();
                postEventModel.setAdd3(eventType.getId(), eventCategoryIds, Integer.parseInt(quantity.getText().toString().trim()), Double.parseDouble(price.getText().toString().trim()));
                Intent intent;
                if (isEditMode) {
                    intent = new Intent();
                    intent.putExtra("eventModel", postEventModel);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    intent = new Intent(Add3.this, Review.class);
                    intent.putExtra("eventModel", postEventModel);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                overridePendingTransition(R.anim.enter, R.anim.exit);
            } catch (Exception e) {
                Toast.makeText(Add3.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAnimationToNextFAB() {
        final float from = 1.0f;
        final float to = 1.3f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(next, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(next, View.SCALE_Y, from, to);
        ObjectAnimator translationZ = ObjectAnimator.ofFloat(next, View.TRANSLATION_Z, from, to);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(scaleX, scaleY, translationZ);
        set1.setDuration(100);
        set1.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(next, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(next, View.SCALE_Y, to, from);
        ObjectAnimator translationZBack = ObjectAnimator.ofFloat(next, View.TRANSLATION_Z, to, from);

        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.lineTo(0.5f, 1.3f);
        path.lineTo(0.75f, 0.8f);
        path.lineTo(1.0f, 1.0f);
        PathInterpolator pathInterpolator = new PathInterpolator(path);

        AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(scaleXBack, scaleYBack, translationZBack);
        set2.setDuration(300);
        set2.setInterpolator(pathInterpolator);
        setNext.playSequentially(set1, set2);
        setNext.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                next.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                next.setClickable(false);
            }
        });
    }

    private void setData() {
        if (postEventModel != null) {
            if (postEventModel.getEventType() != null && StaticVariables.eventTypes != null) {
                for (TypeModel model : StaticVariables.eventTypes) {
                    if (model.getId().equals(postEventModel.getEventType())) {
                        eventType = model;
                        setEventType();
                        break;
                    }
                }
            }
            if (postEventModel.getCategories() != null && StaticVariables.eventCategories != null) {
                for (CategoryModel model : StaticVariables.eventCategories) {
                    if (postEventModel.getCategories().contains(model.getId())) {
                        eventCategory.add(model);
                    }
                }
                setEventCategory();
            }
            if (postEventModel.getNoOfSeats() != -1)
                quantity.setText(String.valueOf(postEventModel.getNoOfSeats()));
            if (postEventModel.getPrice() != -1)
                price.setText(String.valueOf(postEventModel.getPrice()));
        }
        try {
            if (postEventModel != null) {
                initialPostEventModel = postEventModel.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    eventType = (TypeModel) data.getSerializableExtra("result");
                    setEventType();
                    updateProgress();
                }
            }
        } else if (requestCode == 300) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    eventCategory = data.getParcelableArrayListExtra("result");
                    setEventCategory();
                    updateProgress();
                }
            }
        }
    }

    private void setEventCategory() {
        if (eventCategory != null) {
            StringBuilder toPut = new StringBuilder();
            int size = eventCategory.size();
            for (int i = 0; i < eventCategory.size(); i++) {
                if (i == 3) {
                    toPut.append("and ").append(size - 3).append(" more");
                    break;
                } else if (size <= 3 && i == size - 1) {
                    toPut.append(eventCategory.get(i).getName());
                } else
                    toPut.append(eventCategory.get(i).getName()).append(",\n");
            }
            eventCategoryTV.setText(toPut.toString());
            eventCategoryIds.clear();
            for (CategoryModel category : eventCategory) {
                eventCategoryIds.add(category.getId());
            }
            eventCategoryHeading.setTextColor(getResources().getColor(R.color.hint));
        } else {
            eventCategoryTV.setText("");
            eventCategoryHeading.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void setEventType() {
        if (eventType != null) {
            eventTypeTV.setText(eventType.getName());
            eventTypeHeading.setTextColor(getResources().getColor(R.color.hint));
        } else {
            eventTypeTV.setText("");
            eventTypeHeading.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private void closeKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    class textWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateProgress();
        }
    }

    private void updateProgress() {
        try {
            int progress = 0;
            if (eventType != null) {
                progress++;
            }
            if (!eventCategory.isEmpty()) {
                progress++;
            }
            if (!Objects.requireNonNull(quantity.getText()).toString().trim().isEmpty()) {
                progress++;
            }
            if (!Objects.requireNonNull(price.getText()).toString().trim().isEmpty()) {
                progress++;
            }
            if (progress == 0) {
                outerProgress.setProgressDrawable(ContextCompat.getDrawable(Add3.this, R.drawable.progress_small));
            } else {
                outerProgress.setProgressDrawable(ContextCompat.getDrawable(Add3.this, R.drawable.progress));
            }
            if (innerProgress.getProgress() < progress) {
                setNext.start();
            }
            innerProgress.setProgress(progress);
            postEventModel.setAdd3(
                    eventType != null ? eventType.getId() : postEventModel.getEventType(),
                    eventCategoryIds,
                    quantity.getText().toString().trim().isEmpty() ? postEventModel.getNoOfSeats() : Integer.parseInt(quantity.getText().toString().trim()),
                    price.getText().toString().trim().isEmpty() ? postEventModel.getPrice() : Double.parseDouble(price.getText().toString().trim())
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        final MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.discard)
                .setMessage("Do you wish to discard the changes you have made?")
                .setCancelable(false)
                .setPositiveButton("Discard", (dialogInterface, which) -> finishActivity())
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .build();
        LottieAnimationView animationView = mDialog.getAnimationView();
//            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mDialog.show();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("eventModel", initialPostEventModel);
        setResult(RESULT_OK, intent);
        finish();
        if (isEditMode) {
            Log.d("Value", "true");
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onBackPressed() {
        if (postEventModel != null && initialPostEventModel != null)
            if (postEventModel.newChanges(initialPostEventModel)) {
                showDialog();
                return;
            }
        finishActivity();
    }
}