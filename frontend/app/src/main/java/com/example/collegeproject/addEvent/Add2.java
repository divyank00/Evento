package com.example.collegeproject.addEvent;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collegeproject.R;
import com.example.collegeproject.addEvent.map.GPSTracker;
import com.example.collegeproject.addEvent.map.LocationTracker;
import com.example.collegeproject.addEvent.map.WorkaroundMapFragment;
import com.example.collegeproject.models.CoordinateModel;
import com.example.collegeproject.models.PostEventModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.collegeproject.addEvent.map.GPSTracker.GPS_REQUEST;

public class Add2 extends AppCompatActivity implements OnMapReadyCallback {

    private ProgressBar outerProgress, innerProgress;
    private FloatingActionButton next;
    private LinearLayout startDate, startTime, endDate, endTime;
    private TextView startDateTV, startTimeTV, endDateTV, endTimeTV;
    private final Calendar startCalender = Calendar.getInstance();
    private final Calendar endCalender = Calendar.getInstance();
    private boolean startDateSet = false, startTimeSet = false, endDateSet = false, endTimeSet = false;
    private PostEventModel postEventModel, initialPostEventModel;
    private boolean isEditMode = false;
    private final AnimatorSet setNext = new AnimatorSet();
    private GoogleMap mMap;
    private LatLng venue, myLoc;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add2);
        postEventModel = (PostEventModel) getIntent().getSerializableExtra("eventModel");
        isEditMode = getIntent().getBooleanExtra("editMode", false);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Event Info");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        init();
        requestPermission();
    }

    private void init() {
        outerProgress = findViewById(R.id.progress);
        next = findViewById(R.id.next);
        addAnimationToNextFAB();
        if (isEditMode) {
            outerProgress.setVisibility(View.GONE);
            next.setBackgroundColor(getResources().getColor(R.color.green));
            next.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tick));
        }
        innerProgress = findViewById(R.id.innerProgress);
        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);
        endDate = findViewById(R.id.endDate);
        endTime = findViewById(R.id.endTime);
        startDateTV = findViewById(R.id.startDateTV);
        startTimeTV = findViewById(R.id.startTimeTV);
        endDateTV = findViewById(R.id.endDateTV);
        endTimeTV = findViewById(R.id.endTimeTV);
        setData();
        startDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Add2.this, (view, year, month, dayOfMonth) -> {
                startCalender.set(Calendar.YEAR, year);
                startCalender.set(Calendar.MONTH, month);
                startCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDateSet = true;
                updateProgress();
                updateDateUI(startDateTV, startCalender);
                if (startTimeTV.getText().equals("Time")) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(Add2.this, (timePicker, selectedHour, selectedMinute) -> {
                        startCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                        startCalender.set(Calendar.MINUTE, selectedMinute);
                        startTimeSet = true;
                        updateProgress();
                        updateTimeUI(startTimeTV, startCalender);
                    }, startCalender.get(Calendar.HOUR_OF_DAY),
                            startCalender.get(Calendar.MINUTE),
                            false);
                    timePickerDialog.show();
                }
            },
                    startCalender.get(Calendar.YEAR),
                    startCalender.get(Calendar.MONTH),
                    startCalender.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
        startTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(Add2.this, (timePicker, selectedHour, selectedMinute) -> {
                startCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                startCalender.set(Calendar.MINUTE, selectedMinute);
                startTimeSet = true;
                updateProgress();
                updateTimeUI(startTimeTV, startCalender);
            }, startCalender.get(Calendar.HOUR_OF_DAY),
                    startCalender.get(Calendar.MINUTE),
                    false);
            timePickerDialog.show();
        });
        endDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(Add2.this, (view, year, month, dayOfMonth) -> {
                endCalender.set(Calendar.YEAR, year);
                endCalender.set(Calendar.MONTH, month);
                endCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDateSet = true;
                updateProgress();
                updateDateUI(endDateTV, endCalender);
                if (endTimeTV.getText().equals("Time")) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(Add2.this, (timePicker, selectedHour, selectedMinute) -> {
                        endCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                        endCalender.set(Calendar.MINUTE, selectedMinute);
                        endTimeSet = true;
                        updateProgress();
                        updateTimeUI(endTimeTV, endCalender);
                    }, endCalender.get(Calendar.HOUR_OF_DAY),
                            endCalender.get(Calendar.MINUTE),
                            false);
                    timePickerDialog.show();
                }
            },
                    endCalender.get(Calendar.YEAR),
                    endCalender.get(Calendar.MONTH),
                    endCalender.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
        endTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(Add2.this, (timePicker, selectedHour, selectedMinute) -> {
                endCalender.set(Calendar.HOUR_OF_DAY, selectedHour);
                endCalender.set(Calendar.MINUTE, selectedMinute);
                endTimeSet = true;
                updateProgress();
                updateTimeUI(endTimeTV, endCalender);
            }, endCalender.get(Calendar.HOUR_OF_DAY),
                    endCalender.get(Calendar.MINUTE),
                    false);
            timePickerDialog.show();
        });
        next.setOnClickListener(v -> {
            startDateTV.setError(null);
            startTimeTV.setError(null);
            endDateTV.setError(null);
            endTimeTV.setError(null);
            if (!startDateSet) {
                startDateTV.setError(getString(R.string.mandatory));
                return;
            }
            if (!startTimeSet) {
                startTimeTV.setError(getString(R.string.mandatory));
                return;
            }
            if (!endDateSet) {
                endDateTV.setError(getString(R.string.mandatory));
                return;
            }
            if (!endTimeSet) {
                endTimeTV.setError(getString(R.string.mandatory));
                return;
            }
            if (endCalender.getTimeInMillis() <= startCalender.getTimeInMillis()) {
                Toast.makeText(Add2.this, "Event can't end before the start!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (venue == null) {
                Toast.makeText(this, "Select Venue!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Geocoder geocoder = new Geocoder(Add2.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(venue.latitude, venue.longitude, 1);
                String cityName = addresses.get(0).getLocality();
                if (cityName != null && !cityName.isEmpty()) {
                    Toast.makeText(Add2.this, "Event is in " + cityName + "!", Toast.LENGTH_SHORT).show();
                    postEventModel.setAdd2(startCalender.getTimeInMillis(), endCalender.getTimeInMillis(), new CoordinateModel(String.valueOf(venue.latitude), String.valueOf(venue.longitude), cityName));
                    Intent intent;
                    if (isEditMode) {
                        intent = new Intent();
                        intent.putExtra("eventModel", postEventModel);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        intent = new Intent(Add2.this, Add3.class);
                        intent.putExtra("eventModel", postEventModel);
                        startActivityForResult(intent, 100);
                    }
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                } else {
                    Toast.makeText(Add2.this, "City cannot be identified!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("Error", e.toString());
                Toast.makeText(Add2.this, "City cannot be identified!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestPermission() {
        Dexter.withContext(Add2.this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    enableGPS();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private void enableGPS() {
        new GPSTracker(this).turnGPSOn(new GPSTracker.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                if (isGPSEnable) {
                    getLocation();
                }
            }
        });
    }

    private void getLocation() {
        LocationTracker.requestSingleUpdate(this,
                location -> {
                    myLoc = new LatLng(location.latitude, location.longitude);
                    WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(this);
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
            if (postEventModel.getStartTime() != 0) {
                startCalender.setTimeInMillis(postEventModel.getStartTime());
                startDateSet = true;
                startTimeSet = true;
                updateDateUI(startDateTV, startCalender);
                updateTimeUI(startTimeTV, startCalender);
            }
            if (postEventModel.getEndTime() != 0) {
                endCalender.setTimeInMillis(postEventModel.getEndTime());
                endDateSet = true;
                endTimeSet = true;
                updateDateUI(endDateTV, endCalender);
                updateTimeUI(endTimeTV, endCalender);
            }
            if(postEventModel.getLocation().getCity()!=null && !postEventModel.getLocation().getCity().equals("")){
                venue = new LatLng(Double.parseDouble(postEventModel.getLocation().getLatitude()), Double.parseDouble(postEventModel.getLocation().getLongitude()));
            }
        }
        try {
            if (postEventModel != null) {
                initialPostEventModel = postEventModel.clone();
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    private void updateDateUI(TextView textView, Calendar calendar) {
        String myFormat = "dd/MMM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(calendar.getTime()));
    }

    private void updateTimeUI(TextView textView, Calendar calendar) {
        String myFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(calendar.getTime()).replace("AM", "am").replace("PM", "pm"));
    }

    private void updateProgress() {
        try {
            int progress = 0;
            if (startDateSet) {
                progress++;
            }
            if (startTimeSet) {
                progress++;
            }
            if (endCalender.getTimeInMillis() > startCalender.getTimeInMillis()) {
                if (endDateSet) {
                    progress++;
                }
                if (endTimeSet) {
                    progress++;
                }
            }
            if (venue != null) {
                progress++;
            }
            if (progress == 0) {
                outerProgress.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.progress_small));
            } else {
                outerProgress.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.progress));
            }
            if (innerProgress.getProgress() < progress) {
                setNext.start();
            }
            innerProgress.setProgress(progress);
            postEventModel.setAdd2(
                    startCalender.getTimeInMillis(),
                    endCalender.getTimeInMillis(),
                    new CoordinateModel(
                            venue == null ? postEventModel.getLocation().getLatitude() : String.valueOf(venue.latitude),
                            venue == null ? postEventModel.getLocation().getLongitude() : String.valueOf(venue.longitude),
                            postEventModel.getLocation().getCity()
                    )
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onBackPressed() {
        if (postEventModel != null && initialPostEventModel != null)
            if (postEventModel.newChanges(initialPostEventModel)) {
                showDialog();
                return;
            }
        finishActivity();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ScrollView parentSV = findViewById(R.id.parentSV);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        parentSV.requestDisallowInterceptTouchEvent(true);
                    }
                });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if(postEventModel.getLocation().getCity()!=null && !postEventModel.getLocation().getCity().isEmpty()) {
            LatLng loc = new LatLng(Double.parseDouble(postEventModel.getLocation().getLatitude()),Double.parseDouble(postEventModel.getLocation().getLongitude()));
            marker = mMap.addMarker(new MarkerOptions().position(loc).title("Event Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
        }else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 12.0f));
        }
        mMap.setOnMapClickListener(latLng -> {
            if (marker != null)
                marker.remove();
            marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Event Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12.0f));
            venue = latLng;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_REQUEST:
                if (resultCode == RESULT_OK)
                    getLocation();
                break;
            case 100:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        postEventModel = (PostEventModel) data.getSerializableExtra("eventModel");
                        setData();
                    }
                    break;
                }
        }
    }

}