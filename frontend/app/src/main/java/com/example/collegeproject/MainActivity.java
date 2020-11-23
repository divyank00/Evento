package com.example.collegeproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collegeproject.auth.AuthActivity;
import com.example.collegeproject.base.BaseActivity;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.GetTypesAndCategoriesResponse;
import com.example.collegeproject.response.UserResponse;
import com.example.collegeproject.viewModel.GetCategoriesViewModel;
import com.example.collegeproject.viewModel.UserViewModel;

import java.util.ArrayList;

import static com.example.collegeproject.helper.SharedPref.getToken;

public class MainActivity extends AppCompatActivity {

    private GetCategoriesViewModel getCategoriesViewModel;
    private UserViewModel userViewModel;
    private Observer<ObjectModel> observer1, observerUser, observerReboot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StaticVariables.eventCategories = new ArrayList<>();
        StaticVariables.eventTypes = new ArrayList<>();
        getCategoriesViewModel = new ViewModelProvider(this).get(GetCategoriesViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        observer1 = objectModel -> {
            if (objectModel.isStatus()) {
                if (((GetTypesAndCategoriesResponse) objectModel.getObj()).getCategories() != null)
                    StaticVariables.eventCategories = ((GetTypesAndCategoriesResponse) objectModel.getObj()).getCategories();
                if (((GetTypesAndCategoriesResponse) objectModel.getObj()).getTypes() != null)
                    StaticVariables.eventTypes = ((GetTypesAndCategoriesResponse) objectModel.getObj()).getTypes();
                if (getToken(MainActivity.this) == null) {
                    startActivity(new Intent(MainActivity.this, AuthActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, BaseActivity.class));
                }
            } else {
                Toast.makeText(this, objectModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
            finish();
        };
        observerUser = objectModel -> {
            if (objectModel.isStatus()) {
                StaticVariables.user = ((UserResponse) objectModel.getObj()).getUser();
            }
        };
        observerReboot = objectModel -> {
            getCategoriesViewModel.getTypesAndCategoriesLiveData()
                    .observe(MainActivity.this, observer1);
            if (getToken(MainActivity.this) != null) {
                userViewModel.getUserInfo().observeForever(observerUser);
            }
        };
        getCategoriesViewModel.rebootServerLiveData().observe(this, observerReboot);
    }
}

