package com.example.collegeproject.addevent;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.collegeproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.libizo.CustomEditText;

public class Add extends AppCompatActivity {

    private CustomEditText orgName, title, desc;
    private FloatingActionButton next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        orgName = findViewById(R.id.orgName);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                nameEditText.setError(null);
                mailEditText.setError(null);
                mobileEditText.setError(null);
                passwordEditText.setError(null);
                confirmPasswordEditText.setError(null);
                if (mailEditText.getText().toString().trim().isEmpty()) {
                    mailEditText.setError(getString(R.string.mandatory));
                    return;
                }
                if (!isValidEmail(mailEditText.getText().toString().trim())) {
                    mailEditText.setError(getString(R.string.format));
                    return;
                }
                if (nameEditText.getText().toString().trim().isEmpty()) {
                    nameEditText.setError(getString(R.string.mandatory));
                    return;
                }
                if (mobileEditText.getText().toString().trim().isEmpty()) {
                    mobileEditText.setError(getString(R.string.mandatory));
                    return;
                }
                if (!isValidMobile(mobileEditText.getText().toString().trim())) {
                    mobileEditText.setError(getString(R.string.format));
                    return;
                }
                if (passwordEditText.getText().toString().trim().isEmpty()) {
                    passwordEditText.setError(getString(R.string.mandatory));
                    return;
                }
                if (confirmPasswordEditText.getText().toString().trim().isEmpty()) {
                    confirmPasswordEditText.setError(getString(R.string.mandatory));
                    return;
                }
                if (!passwordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())) {
                    confirmPasswordEditText.setError(getString(R.string.password));
                    return;
                }
                registerButton.setEnabled(true);
            }
        });

        private void closeKeyboard() {
            if (getActivity() != null) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputManager != null) {
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
    }
}