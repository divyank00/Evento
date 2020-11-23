package com.example.collegeproject.auth;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.collegeproject.R;
import com.example.collegeproject.base.BaseActivity;
import com.example.collegeproject.helper.StaticVariables;
import com.example.collegeproject.models.RegisterModel;
import com.example.collegeproject.network.ObjectModel;
import com.example.collegeproject.response.StandardResponse;
import com.example.collegeproject.response.UserResponse;
import com.example.collegeproject.viewModel.UserViewModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.libizo.CustomEditText;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static androidx.navigation.Navigation.findNavController;
import static com.example.collegeproject.helper.SharedPref.setMail;
import static com.example.collegeproject.helper.SharedPref.setToken;

public class RegisterFragment extends Fragment {

    private UserViewModel registerViewModel;
    private CustomEditText mailEditText, nameEditText, mobileEditText, passwordEditText, confirmPasswordEditText;
    private TextView alreadyUser;
    private Button registerButton;
    private ProgressBar loadingProgressBar;
    private Observer<ObjectModel> observer, observerImage;
    private CircleImageView image;
    private String imageFilePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Register");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findNavController(view).popBackStack();
            }
        });
        mailEditText = view.findViewById(R.id.mail);
        nameEditText = view.findViewById(R.id.name);
        mobileEditText = view.findViewById(R.id.mobile);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        registerButton = view.findViewById(R.id.register);
        loadingProgressBar = view.findViewById(R.id.loading);
        alreadyUser = view.findViewById(R.id.alreadyUser);
        image = view.findViewById(R.id.profile);
        image.setOnClickListener(new View.OnClickListener() {
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
        confirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerButton.performClick();
                return true;
            }
            return false;
        });

        alreadyUser.setText(getText(R.string.already_user));
        alreadyUser.setOnClickListener(v -> {
            if (getView() != null)
                findNavController(getView()).popBackStack();
        });

        observer = objectModel -> {
            if (objectModel.isStatus())
                successful(((UserResponse) objectModel.getObj()));
            else
                failed(objectModel.getMessage());
        };
        observerImage = objectModel -> {
            if (objectModel.isStatus())
                successfulImage((StandardResponse) objectModel.getObj());
            else {
                failed(objectModel.getMessage());
            }
        };
        registerButton.setOnClickListener(v -> {
            closeKeyboard();
            nameEditText.setError(null);
            mailEditText.setError(null);
            mobileEditText.setError(null);
            passwordEditText.setError(null);
            confirmPasswordEditText.setError(null);
            if (Objects.requireNonNull(mailEditText.getText()).toString().trim().isEmpty()) {
                mailEditText.setError(getString(R.string.mandatory));
                return;
            }
            if (!isValidEmail(mailEditText.getText().toString().trim())) {
                mailEditText.setError(getString(R.string.format));
                return;
            }
            if (Objects.requireNonNull(nameEditText.getText()).toString().trim().isEmpty()) {
                nameEditText.setError(getString(R.string.mandatory));
                return;
            }
            if (Objects.requireNonNull(mobileEditText.getText()).toString().trim().isEmpty()) {
                mobileEditText.setError(getString(R.string.mandatory));
                return;
            }
            if (!isValidMobile(mobileEditText.getText().toString().trim())) {
                mobileEditText.setError(getString(R.string.format));
                return;
            }
            if (Objects.requireNonNull(passwordEditText.getText()).toString().trim().isEmpty()) {
                passwordEditText.setError(getString(R.string.mandatory));
                return;
            }
            if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().trim().isEmpty()) {
                confirmPasswordEditText.setError(getString(R.string.mandatory));
                return;
            }
            if (!passwordEditText.getText().toString().trim().equals(confirmPasswordEditText.getText().toString().trim())) {
                confirmPasswordEditText.setError(getString(R.string.password));
                return;
            }
            registerButton.setEnabled(false);
            loadingProgressBar.setVisibility(View.VISIBLE);
            registerViewModel.registerLiveData(new RegisterModel(nameEditText.getText().toString().trim(), mailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim(), mobileEditText.getText().toString().trim()))
                    .observe(getViewLifecycleOwner(), observer);
        });
    }

    private boolean isValidMobile(String mobileNumber) {
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");

        Matcher m = p.matcher(mobileNumber);
        return (m.find() && m.group().equals(mobileNumber));
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

    private void successful(UserResponse model) {
        if (getContext() != null) {
            setToken(getContext(), model.getToken());
            setMail(getContext(),model.getUser().getEmail());
            StaticVariables.user = model.getUser();
            String welcome = getString(R.string.registered);
            if (imageFilePath != null && !imageFilePath.isEmpty()) {
                registerViewModel.postUserImageLiveData(getBytes(imageFilePath)).observe(getViewLifecycleOwner(), observerImage);
            } else {
                Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
                if (getActivity() != null)
                    getActivity().finish();
                startActivity(new Intent(getContext(), BaseActivity.class));
            }
        }
    }

    private void successfulImage(StandardResponse model) {
        loadingProgressBar.setVisibility(View.GONE);
        String welcome = getString(R.string.registered);
        if (getContext() != null) {
            Toast.makeText(getContext(), welcome, Toast.LENGTH_LONG).show();
            if (getActivity() != null)
                getActivity().finish();
            startActivity(new Intent(getContext(), BaseActivity.class));
        }
    }

    private void failed(String errorString) {
        loadingProgressBar.setVisibility(View.GONE);
        if (getContext() != null) {
            Toast.makeText(
                    getContext(),
                    errorString,
                    Toast.LENGTH_SHORT).show();
            registerButton.setEnabled(true);
        }
    }

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
                image.setImageResource(R.color.shimmerGrey);
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
                        Picasso.get().load(new File(imageFilePath)).placeholder(R.color.shimmerGrey).into(image);
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
                            Picasso.get().load(new File(picturePath)).placeholder(R.color.shimmerGrey).into(image);
                            imageFilePath = cursor.getString(columnIndex);
                            cursor.close();
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