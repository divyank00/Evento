package com.example.collegeproject.addEvent;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.example.collegeproject.R;
import com.example.collegeproject.addEvent.map.WorkaroundMapFragment;
import com.example.collegeproject.base.BaseActivity;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.CategoryModel;
import com.example.collegeproject.models.PostEventModel;
import com.example.collegeproject.models.TypeModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.PostEventResponse;
import com.example.collegeproject.response.StandardResponse;
import com.example.collegeproject.viewModel.PostEventViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Review extends AppCompatActivity implements OnMapReadyCallback {

    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private ImageView image;
    private FloatingActionButton addImage, addEvent;
    private LinearLayout desc, orgName, orgMail, orgContact, dateOfEvent, locationOfEvent, typeOfEvent, categoryOfEvent, totalTicket, priceTicket;
    private TextView descTV, orgNameTV, orgMailTV, orgContactTV, startDateOfEventTV, endDateOfEventTV, locationOfEventTV, typeOfEventTV, categoryOfEventTV, totalTicketTV, priceTicketTV;
    private PostEventModel postEventModel;
    private String imageFilePath;
    private PostEventViewModel postEventViewModel;
    private Observer<ObjectModel> observer, observer1;
    private ProgressBar progress, progressRotating;
    private final AnimatorSet setUpload = new AnimatorSet(), setSave = new AnimatorSet();
    private GoogleMap mMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        init();
        setData();
        setOnClickListeners();
    }

    private void init() {
        postEventViewModel = new ViewModelProvider(this).get(PostEventViewModel.class);
        observer = objectModel -> {
            if (objectModel.isStatus())
                successful((PostEventResponse) objectModel.getObj());
            else {
                failed(objectModel.getMessage());
            }
        };
        observer1 = objectModel -> {
            if (objectModel.isStatus())
                successful((StandardResponse) objectModel.getObj());
            else {
                failed(objectModel.getMessage());
            }
        };
        postEventModel = (PostEventModel) getIntent().getSerializableExtra("eventModel");
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        appBar = findViewById(R.id.appBar);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    toolbar.setBackgroundColor(ContextCompat.getColor(Review.this, R.color.colorPrimary));
                    addImage.hide();
                } else {
                    toolbar.setBackgroundColor(ContextCompat.getColor(Review.this, android.R.color.transparent));
                    addImage.show();
                }

            }
        });
        toolbar = findViewById(R.id.toolbar);
        addImage = findViewById(R.id.addImage);
        addEvent = findViewById(R.id.confirm);
        addAnimationToImageFAV();
        addAnimationToSaveFAB();
        image = findViewById(R.id.image);
        desc = findViewById(R.id.desc);
        orgName = findViewById(R.id.orgName);
        orgMail = findViewById(R.id.orgMail);
        orgContact = findViewById(R.id.orgContact);
        dateOfEvent = findViewById(R.id.dateOfEvent);
        locationOfEvent = findViewById(R.id.locationOfEvent);
        typeOfEvent = findViewById(R.id.typeOfEvent);
        categoryOfEvent = findViewById(R.id.categoryOfEvent);
        totalTicket = findViewById(R.id.totalTicket);
        priceTicket = findViewById(R.id.priceTicket);
        descTV = findViewById(R.id.descTV);
        orgNameTV = findViewById(R.id.orgNameTV);
        orgMailTV = findViewById(R.id.orgMailTV);
        orgContactTV = findViewById(R.id.orgContactTV);
        startDateOfEventTV = findViewById(R.id.startDateOfEventTV);
        endDateOfEventTV = findViewById(R.id.endDateOfEventTV);
        locationOfEventTV = findViewById(R.id.locationOfEventTV);
        typeOfEventTV = findViewById(R.id.typeOfEventTV);
        categoryOfEventTV = findViewById(R.id.categoryOfEventTV);
        totalTicketTV = findViewById(R.id.totalTicketTV);
        priceTicketTV = findViewById(R.id.priceTicketTV);
        progress = findViewById(R.id.progress);
        progressRotating = findViewById(R.id.progressRotating);
        collapsingToolbar.setTitleEnabled(true);
        if (postEventModel != null && !postEventModel.getName().isEmpty())
            collapsingToolbar.setTitle(postEventModel.getName());
        else
            collapsingToolbar.setTitle(getString(R.string.prompt_event_title));
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
    }

    private void setOnClickListeners() {
        addEvent.setOnClickListener(v -> {
            addEvent.setEnabled(false);
            addImage.setEnabled(false);
            setSave.start();
            postEventViewModel.postEventLiveData(postEventModel).observe(Review.this, observer);
        });
        addImage.setOnClickListener(v -> {
            setUpload.start();
            Dexter.withContext(Review.this)
                    .withPermissions(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        selectImage();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        });
        collapsingToolbar.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add1.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        desc.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add1.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        orgName.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add1.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        orgMail.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add1.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        orgContact.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add1.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        dateOfEvent.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add2.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        locationOfEvent.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add2.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        typeOfEvent.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add3.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        categoryOfEvent.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add3.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        totalTicket.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add3.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
        priceTicket.setOnClickListener(v -> {
            Intent intent = new Intent(Review.this, Add3.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
    }

    private void addAnimationToImageFAV() {
        final float from = 1.0f;
        final float to = 1.3f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(addImage, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(addImage, View.SCALE_Y, from, to);
        ObjectAnimator translationZ = ObjectAnimator.ofFloat(addImage, View.TRANSLATION_Z, from, to);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(scaleX, scaleY, translationZ);
        set1.setDuration(100);
        set1.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(addImage, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(addImage, View.SCALE_Y, to, from);
        ObjectAnimator translationZBack = ObjectAnimator.ofFloat(addImage, View.TRANSLATION_Z, to, from);

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
        setUpload.playSequentially(set1, set2);
        setUpload.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addImage.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                addImage.setClickable(false);
            }
        });
    }

    private void addAnimationToSaveFAB() {
        final float from = 1.0f;
        final float to = 1.3f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(addEvent, View.SCALE_X, from, to);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(addEvent, View.SCALE_Y, from, to);
        ObjectAnimator translationZ = ObjectAnimator.ofFloat(addEvent, View.TRANSLATION_Z, from, to);

        AnimatorSet set1 = new AnimatorSet();
        set1.playTogether(scaleX, scaleY, translationZ);
        set1.setDuration(100);
        set1.setInterpolator(new AccelerateInterpolator());

        set1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progress.setVisibility(View.GONE);
            }
        });
        ObjectAnimator scaleXBack = ObjectAnimator.ofFloat(addEvent, View.SCALE_X, to, from);
        ObjectAnimator scaleYBack = ObjectAnimator.ofFloat(addEvent, View.SCALE_Y, to, from);
        ObjectAnimator translationZBack = ObjectAnimator.ofFloat(addEvent, View.TRANSLATION_Z, to, from);

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
        set2.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressRotating.setVisibility(View.VISIBLE);
            }
        });
        setSave.playSequentially(set1, set2);

        setSave.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                addEvent.setClickable(true);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                addEvent.setClickable(false);
            }
        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Review.this);
        builder.setTitle("Choose Event Picture!");
        builder.setItems(options, (dialog, item) -> {
            if (item == 0) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoURI = FileProvider.getUriForFile(Review.this, "com.example.android.fileprovider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            0);
                }
            } else if (item == 1) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(pickPhoto, 1);

            } else if (item == 2) {
                imageFilePath = null;
                image.setImageBitmap(null);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        postEventModel = (PostEventModel) data.getSerializableExtra("eventModel");
                        setData();
                    }
                }
                break;
            case 0:
                if (resultCode == RESULT_OK) {
                    if (imageFilePath != null) {
                        Picasso.get().load(new File(imageFilePath)).into(image);
                    }
                }
                break;
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                            imageFilePath = cursor.getString(columnIndex);
                            cursor.close();
                        }
                    }

                }
                break;
        }
    }

    private void setData() {
        if (postEventModel != null) {
            if (!postEventModel.getDescription().isEmpty())
                descTV.setText(postEventModel.getDescription());
            if (!postEventModel.getOrganizer().getOrgName().isEmpty())
                orgNameTV.setText(postEventModel.getOrganizer().getOrgName());
            if (!postEventModel.getOrganizer().getOrgEmail().isEmpty())
                orgMailTV.setText(postEventModel.getOrganizer().getOrgEmail());
            if (!postEventModel.getOrganizer().getOrgContactNo().isEmpty())
                orgContactTV.setText(postEventModel.getOrganizer().getOrgContactNo());
            if (postEventModel.getStartTime() != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(postEventModel.getStartTime());
                startDateOfEventTV.setText(getDate(calendar));
            }
            if (postEventModel.getEndTime() != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(postEventModel.getEndTime());
                endDateOfEventTV.setText(getDate(calendar));
            }
            if (!postEventModel.getLocation().getCity().isEmpty())
                locationOfEventTV.setText(postEventModel.getLocation().getCity());
            if (postEventModel.getLocation().getCity() != null && !postEventModel.getLocation().getCity().equals("")) {
                WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                if (mapFragment != null) {
                    mapFragment.getMapAsync(this);
                }
            }
            if (postEventModel.getEventType() != null && StaticVariables.eventTypes != null) {
                for (TypeModel model : StaticVariables.eventTypes) {
                    if (model.getId().equals(postEventModel.getEventType())) {
                        typeOfEventTV.setText(model.getName());
                        break;
                    }
                }
            }
            List<CategoryModel> eventCategory = new ArrayList<>();
            if (postEventModel.getCategories() != null && StaticVariables.eventCategories != null) {
                for (CategoryModel model : StaticVariables.eventCategories) {
                    if (postEventModel.getCategories().contains(model.getId())) {
                        eventCategory.add(model);
                    }
                }
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
                categoryOfEventTV.setText(toPut.toString());
            }
            if (postEventModel.getNoOfSeats() != -1)
                totalTicketTV.setText(String.valueOf(postEventModel.getNoOfSeats()));
            if (postEventModel.getPrice() != -1)
                priceTicketTV.setText(String.valueOf(postEventModel.getPrice()));
        }
    }

    private String getDate(Calendar calendar) {
        String myFormat = "EEEE, dd MMMM, yyyy\nhh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(calendar.getTime()).replace("AM", "am").replace("PM", "pm");
    }

    private void showDialog() {
        final MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setAnimation(R.raw.discard)
                .setMessage("Do you wish to discard the changes you have made?")
                .setCancelable(false)
                .setPositiveButton("Discard", (dialogInterface, which) -> {
                    Intent intent = new Intent(Review.this, BaseActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.dismiss())
                .build();
        LottieAnimationView animationView = mDialog.getAnimationView();
//            animationView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mDialog.show();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }

    private void successful(PostEventResponse model) {
        if (imageFilePath != null && !imageFilePath.isEmpty()) {
            postEventViewModel.postEventImageLiveData(model.getEventId(), getBytes(imageFilePath)).observe(Review.this, observer1);
        } else {
            Toast.makeText(this.getApplicationContext(), "Event Posted!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Review.this, BaseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    private void successful(StandardResponse model) {
        Toast.makeText(this.getApplicationContext(), "Event Posted!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Review.this, BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void failed(String errorString) {
        Toast.makeText(
                this,
                errorString,
                Toast.LENGTH_SHORT).show();
        addEvent.setEnabled(true);
        addImage.setEnabled(true);
        progress.setVisibility(View.VISIBLE);
        progressRotating.setVisibility(View.GONE);
    }

    public byte[] getBytes(String path) {
        Bitmap original = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return out.toByteArray();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        NestedScrollView parentSV = findViewById(R.id.parentSV);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        parentSV.requestDisallowInterceptTouchEvent(true);
                    }
                });
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (marker != null)
            marker.remove();
        if (postEventModel.getLocation().getCity() != null && !postEventModel.getLocation().getCity().isEmpty()) {
            LatLng loc = new LatLng(Double.parseDouble(postEventModel.getLocation().getLatitude()), Double.parseDouble(postEventModel.getLocation().getLongitude()));
            marker = mMap.addMarker(new MarkerOptions().position(loc).title("Event Location"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 12.0f));
        }
        mMap.setOnMapClickListener(latLng -> {
            Intent intent = new Intent(Review.this, Add2.class);
            intent.putExtra("eventModel", postEventModel);
            intent.putExtra("editMode", true);
            startActivityForResult(intent, 100);
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        });
    }
}