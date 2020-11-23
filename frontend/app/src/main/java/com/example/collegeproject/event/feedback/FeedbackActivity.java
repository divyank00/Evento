package com.example.collegeproject.event.feedback;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.collegeproject.R;
import com.example.collegeproject.models.GetEventsModel;
import com.example.collegeproject.models.PostReviewModel;
import com.example.collegeproject.models.ReviewModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.StandardResponse;
import com.example.collegeproject.viewModel.PostReviewViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.libizo.CustomEditText;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.RotationRatingBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FeedbackActivity extends AppCompatActivity {

    private ShimmerRecyclerView shimmerRecyclerView;
    private CustomEditText editText;
    private FloatingActionButton post;
    private GetEventsModel model;
    private RotationRatingBar simpleRatingBar;
    private PostReviewViewModel postReviewViewModel;
    private Observer<ObjectModel> observer;
    private ProgressBar progressBar;
    private Adapter adapter;
    private List<ReviewModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        postReviewViewModel = new ViewModelProvider(this).get(PostReviewViewModel.class);
        observer = objectModel -> {
            if (objectModel.isStatus()) {
                successful((StandardResponse) objectModel.getObj());
            } else
                failed(objectModel.getMessage());
            post.setEnabled(true);
            simpleRatingBar.setIsIndicator(false);
            editText.setEnabled(true);
            progressBar.setVisibility(View.GONE);
        };
        model = (GetEventsModel) getIntent().getSerializableExtra("eventModel");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Reviews & Feedback");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        list = new ArrayList<>();
        list.addAll(model.getReviews());
        shimmerRecyclerView = findViewById(R.id.feedback);
        shimmerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, list);
        shimmerRecyclerView.setAdapter(adapter);
        post = findViewById(R.id.post);
        progressBar = findViewById(R.id.progress);
        editText = findViewById(R.id.editText);
        simpleRatingBar = findViewById(R.id.simpleRatingBar);
        simpleRatingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating, boolean fromUser) {
                if (rating != 0 && Objects.requireNonNull(editText.getText()).length() != 0) {
                    post.setEnabled(true);
                } else {
                    post.setEnabled(false);
                }
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0 && simpleRatingBar.getRating() != 0) {
                    post.setEnabled(true);
                } else {
                    post.setEnabled(false);
                }
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postFeedback();
            }
        });
    }

    void postFeedback() {
        post.setEnabled(false);
        simpleRatingBar.setIsIndicator(true);
        editText.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        postReviewViewModel.postReviewLiveData(new PostReviewModel((int) simpleRatingBar.getRating(), Objects.requireNonNull(editText.getText()).toString(), model.get_id()))
                .observe(FeedbackActivity.this, observer);
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void successful(StandardResponse resp) {
        ReviewModel rev = new ReviewModel((int) simpleRatingBar.getRating(), Objects.requireNonNull(editText.getText()).toString());
        double avg = (model.getTotalStar() + simpleRatingBar.getRating()) / (model.getTotalRating() + 1);
        model.setAvgRating(avg);
        model.setTotalRating(model.getTotalRating() + 1);
        model.setTotalStar(model.getTotalStar() + simpleRatingBar.getRating());
        model.getReviews().add(rev);
        list.add(0, rev);
        adapter.notifyItemInserted(0);
        editText.setText("");
        simpleRatingBar.setRating(0);
        Toast.makeText(this, "Feedback Posted!", Toast.LENGTH_SHORT).show();
    }

    private void failed(String errorString) {
        Toast.makeText(
                this,
                errorString,
                Toast.LENGTH_SHORT).show();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("eventModel", model);
        setResult(RESULT_OK, intent);
        finish();
    }
}