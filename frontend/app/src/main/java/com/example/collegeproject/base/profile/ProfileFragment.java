package com.example.collegeproject.base.profile;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collegeproject.R;
import com.example.collegeproject.auth.AuthActivity;
import com.example.collegeproject.helper.SharedPref;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.StandardResponse;
import com.example.collegeproject.response.UserResponse;
import com.example.collegeproject.viewModel.RoomViewModel;
import com.example.collegeproject.viewModel.UserViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private RoomViewModel roomViewModel;
    private Button logOut;
    private CollapsingToolbarLayout collapsingToolbar;
    private Observer<ObjectModel> observerLogout, observerImage, observerRemoveImage, observerUser;
    private ImageView noProfile;
    private CircleImageView profile;
    private String imageFilePath;
    private TextView mail, contact;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        checkUserInfo(v);
        return v;
    }

    private void checkUserInfo(View v) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        observerUser = objectModel -> {
            if (objectModel.isStatus()) {
                StaticVariables.user = ((UserResponse) objectModel.getObj()).getUser();
                setView(v);
            } else {
                Toast.makeText(getContext(), objectModel.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        if (StaticVariables.user == null) {
            userViewModel.getUserInfo().observe(getViewLifecycleOwner(), observerUser);
        } else {
            setView(v);
        }
    }

    private void setView(View v) {
        v.findViewById(R.id.loader).setVisibility(View.GONE);
        roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        observerLogout = objectModel -> {
            if (objectModel.isStatus())
                successful((StandardResponse) objectModel.getObj());
            else
                failed(objectModel.getMessage());
            logOut.setEnabled(true);
        };
        observerImage = objectModel -> {
            if (!objectModel.isStatus()) {
                setProfileImage();
                failed(objectModel.getMessage());
            }
        };
        observerRemoveImage = objectModel -> {
            if (!objectModel.isStatus()) {
                setProfileImage();
                failed(objectModel.getMessage());
            }
        };
        noProfile = v.findViewById(R.id.noImage);
        profile = v.findViewById(R.id.profile);
        setProfileImage();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getContext())
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
            }
        });
        noProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(getContext())
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
            }
        });
        mail = v.findViewById(R.id.mail);
        mail.setText(StaticVariables.user.getEmail());
        contact = v.findViewById(R.id.contact);
        contact.setText(StaticVariables.user.getContactNumber());
        logOut = v.findViewById(R.id.logOut);
        logOut.setEnabled(true);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut.setEnabled(false);
                roomViewModel.clearUserCities();
                userViewModel.logoutLiveData()
                        .observe(getViewLifecycleOwner(), observerLogout);
            }
        });
        collapsingToolbar = v.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(StaticVariables.user.getName().trim().replace(" ", "\n"));
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
    }

    private void setProfileImage() {
        Picasso.get().load(StaticVariables.ProfilePath + StaticVariables.user.get_id()).into(profile, new Callback() {
            @Override
            public void onSuccess() {
                noProfile.setVisibility(View.GONE);
                profile.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                noProfile.setVisibility(View.VISIBLE);
                profile.setVisibility(View.GONE);
            }
        });
    }

    private void successful(StandardResponse model) {
        Intent intent = new Intent(getContext(), AuthActivity.class);
        if (getContext() != null)
            SharedPref.clearData(getContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void failed(String errorString) {
        if (getContext() != null) {
            Toast.makeText(
                    getContext(),
                    errorString,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Remove Picture"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
                    Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
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
                profile.setVisibility(View.GONE);
                noProfile.setVisibility(View.VISIBLE);
                profile.setImageResource(R.color.shimmerGrey);
                userViewModel.removeUserImageLiveData().observe(getViewLifecycleOwner(), observerRemoveImage);
                dialog.dismiss();
                Picasso.get().invalidate(StaticVariables.ProfilePath + StaticVariables.user.get_id());
            }
        });
        builder.show();
    }

    @NotNull
    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    if (imageFilePath != null) {
                        Picasso.get().load(new File(imageFilePath)).placeholder(R.color.shimmerGrey).into(profile, new Callback() {
                            @Override
                            public void onSuccess() {
                                noProfile.setVisibility(View.GONE);
                                profile.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                setProfileImage();
                            }
                        });
                        userViewModel.postUserImageLiveData(getBytes(imageFilePath)).observe(getViewLifecycleOwner(), observerImage);
                    }
                }
                break;
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    if (selectedImage != null) {
                        Cursor cursor = requireContext().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String picturePath = cursor.getString(columnIndex);
                            Picasso.get().load(new File(picturePath)).placeholder(R.color.shimmerGrey).into(profile, new Callback() {
                                @Override
                                public void onSuccess() {
                                    noProfile.setVisibility(View.GONE);
                                    profile.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError(Exception e) {
                                    setProfileImage();
                                }
                            });
                            imageFilePath = cursor.getString(columnIndex);
                            cursor.close();
                            userViewModel.postUserImageLiveData(getBytes(imageFilePath)).observe(getViewLifecycleOwner(), observerImage);
                        }
                    }
                }
                break;
        }
    }

    public byte[] getBytes(String path) {
        Bitmap original = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return out.toByteArray();
    }
}