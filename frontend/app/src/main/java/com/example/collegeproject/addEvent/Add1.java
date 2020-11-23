package com.example.collegeproject.addEvent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collegeproject.R;
import com.example.collegeproject.models.PostEventModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.libizo.CustomEditText;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.Objects;
import java.util.regex.Pattern;

public class Add1 extends AppCompatActivity {

    private CustomEditText orgName, orgMail, orgMobile, title, desc;
    private ProgressBar outerProgress, innerProgress;
    private FloatingActionButton next;
    private PostEventModel postEventModel, initialPostEventModel;
    private boolean isEditMode = false;
    private final AnimatorSet setNext = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add1);
        postEventModel = (PostEventModel) getIntent().getSerializableExtra("eventModel");
        isEditMode = getIntent().getBooleanExtra("editMode", false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Info");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        orgName = findViewById(R.id.orgName);
        orgMail = findViewById(R.id.orgMail);
        orgMobile = findViewById(R.id.orgMobile);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        orgName.addTextChangedListener(new textWatcher());
        orgMail.addTextChangedListener(new textWatcher());
        orgMobile.addTextChangedListener(new textWatcher());
        title.addTextChangedListener(new textWatcher());
        desc.addTextChangedListener(new textWatcher());
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
        next.setOnClickListener(v -> {
            closeKeyboard();
            orgName.setError(null);
            orgMail.setError(null);
            orgMobile.setError(null);
            title.setError(null);
            desc.setError(null);
            if (Objects.requireNonNull(orgName.getText()).toString().trim().isEmpty()) {
                orgName.setError(getString(R.string.mandatory));
                return;
            }
            if (Objects.requireNonNull(orgMail.getText()).toString().trim().isEmpty()) {
                orgMail.setError(getString(R.string.mandatory));
                return;
            }
            if (!isValidEmail(orgMail.getText().toString().trim())) {
                orgMail.setError(getString(R.string.format));
                return;
            }

            if (Objects.requireNonNull(orgMobile.getText()).toString().trim().isEmpty()) {
                orgMobile.setError(getString(R.string.mandatory));
                return;
            }
            if (!isValidMobile(orgMobile.getText().toString().trim())) {
                orgMobile.setError(getString(R.string.format));
                return;
            }
            if (Objects.requireNonNull(title.getText()).toString().trim().isEmpty()) {
                title.setError(getString(R.string.mandatory));
                return;
            }
            if (Objects.requireNonNull(desc.getText()).toString().trim().isEmpty()) {
                desc.setError(getString(R.string.mandatory));
                return;
            }
            orgName.clearFocus();
            orgMail.clearFocus();
            orgMobile.clearFocus();
            title.clearFocus();
            desc.clearFocus();
            postEventModel.setAdd1(title.getText().toString().trim(), desc.getText().toString().trim(), orgName.getText().toString().trim(), orgMail.getText().toString().trim(), orgMobile.getText().toString().trim());
            Intent intent;
            if (isEditMode) {
                intent = new Intent();
                intent.putExtra("eventModel", postEventModel);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                intent = new Intent(Add1.this, Add2.class);
                intent.putExtra("eventModel", postEventModel);
                startActivityForResult(intent, 100);
            }
            overridePendingTransition(R.anim.enter, R.anim.exit);
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
            if (!postEventModel.getOrganizer().getOrgName().isEmpty())
                orgName.setText(postEventModel.getOrganizer().getOrgName());
            if (!postEventModel.getOrganizer().getOrgEmail().isEmpty())
                orgMail.setText(postEventModel.getOrganizer().getOrgEmail());
            if (!postEventModel.getOrganizer().getOrgContactNo().isEmpty())
                orgMobile.setText(postEventModel.getOrganizer().getOrgContactNo());
            if (!postEventModel.getName().isEmpty())
                title.setText(postEventModel.getName());
            if (!postEventModel.getDescription().isEmpty())
                desc.setText(postEventModel.getDescription());
        } else {
            postEventModel = new PostEventModel();
        }
        try {
            if (postEventModel != null)
                initialPostEventModel = postEventModel.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    postEventModel = (PostEventModel) data.getSerializableExtra("eventModel");
                    setData();
                }
            }
        }
    }

    private boolean isValidMobile(String mobileNumber) {
        return mobileNumber.length() > 9;
//        Pattern p = Pattern.compile("(\\+91)?[7-9][0-9]{9}");
//
//        Matcher m = p.matcher(mobileNumber);
//        return (m.find() && m.group().equals(mobileNumber));
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
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
            if (!Objects.requireNonNull(orgName.getText()).toString().trim().isEmpty()) {
                progress++;
            }
            if (!Objects.requireNonNull(orgMail.getText()).toString().trim().isEmpty() && isValidEmail(orgMail.getText().toString().trim())) {
                progress++;
            }
            if (!Objects.requireNonNull(orgMobile.getText()).toString().trim().isEmpty() && isValidMobile(orgMobile.getText().toString().trim())) {
                progress++;
            }
            if (!Objects.requireNonNull(title.getText()).toString().trim().isEmpty()) {
                progress++;
            }
            if (!Objects.requireNonNull(desc.getText()).toString().trim().isEmpty()) {
                progress++;
            }
            if (progress == 0) {
                outerProgress.setProgressDrawable(ContextCompat.getDrawable(Add1.this, R.drawable.progress_small));
            } else {
                outerProgress.setProgressDrawable(ContextCompat.getDrawable(Add1.this, R.drawable.progress));
            }
            if (innerProgress.getProgress() < progress) {
                setNext.start();
            }
            innerProgress.setProgress(progress);
            postEventModel.setAdd1(
                    title.getText().toString().trim().isEmpty() ? postEventModel.getName() : title.getText().toString().trim(),
                    desc.getText().toString().trim().isEmpty() ? postEventModel.getDescription() : desc.getText().toString().trim(),
                    orgName.getText().toString().trim().isEmpty() ? postEventModel.getOrganizer().getOrgName() : orgName.getText().toString().trim(),
                    orgMail.getText().toString().trim().isEmpty() ? postEventModel.getOrganizer().getOrgEmail() : orgMail.getText().toString().trim(),
                    orgMobile.getText().toString().trim().isEmpty() ? postEventModel.getOrganizer().getOrgContactNo() : orgMobile.getText().toString().trim()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            if (postEventModel != null && initialPostEventModel != null)
                if (postEventModel.newChanges(initialPostEventModel)) {
                    showDialog();
                    return;
                } else {
                    finishActivity();
                }
        } else {
            if (!postEventModel.isEmpty()) {
                showDialog();
                return;
            }
        }
        super.onBackPressed();
    }

    private void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("eventModel", initialPostEventModel);
        setResult(RESULT_OK, intent);
        finish();
        if (isEditMode) {
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
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

}