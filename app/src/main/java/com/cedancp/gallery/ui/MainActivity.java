package com.cedancp.gallery.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.cedancp.gallery.BuildConfig;
import com.cedancp.gallery.Config;
import com.cedancp.gallery.ui.view.home.GalleryFragmentDirections;
import com.cedancp.gallery.ui.view.imagedetail.ImageDetailFragment;
import com.cedancp.gallery.R;
import com.cedancp.gallery.ui.view.home.GalleryFragment;
import com.cedancp.gallery.ui.viewmodel.ImagesViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements
        GalleryFragment.OnFragmentInteractionListener,
        ImageDetailFragment.OnFragmentInteractionListener,
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Permissions
    private static final int RC_HANDLE_STORAGE_PERM = 3;
    private static final int RC_HANDLE_CAMERA_PERM = 4;
    private static final int RC_HANDLE_WRITE_STORAGE_PERM = 5;
    final String[] PERMISSIONS_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    final String[] PERMISSIONS_WRITE_STORAGE = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final String[] PERMISSIONS_CAMERA = new String[]{Manifest.permission.CAMERA};

    private static final int GALLERY_REQUEST_CODE = 10001;
    private static final int CAMERA_REQUEST_CODE = 10002;

    private NavController navController;

    private String currentImagePath;

    private ImagesViewModel imagesViewModel;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        view = findViewById(R.id.activity_main);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        imagesViewModel = ViewModelProviders.of(this).get(ImagesViewModel.class);

        FloatingActionButton fab_gallery = findViewById(R.id.fab_gallery);
        FloatingActionButton fab_camera = findViewById(R.id.fab_camera);
        fab_gallery.setOnClickListener(this);
        fab_camera.setOnClickListener(this);
    }

    /**
     * Opens chooser dialog to pick picture from one source
     */
    private void pickPictureFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Accepted mimetypes jpg and png
        String[] mimetypes = {"image/jpeg\", \"image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * Opens camera for taking a picture
     */
    private void takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestWriteToStoragePermission();
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File tempStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            try {
                File imageTempFile = File.createTempFile("temp_image", ".jpg", tempStorageDir);
                Uri imageURI = FileProvider.getUriForFile(MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        imageTempFile);
                currentImagePath = imageTempFile.getPath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            // Getting image from local storage
            Uri selectedImage = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            goToImageDetail(picturePath);
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            goToImageDetail(currentImagePath);
        }
    }

    private void goToImageDetail(String imagePath) {
        GalleryFragmentDirections.ImageDetailAction action = GalleryFragmentDirections.imageDetailAction();

        action.setImagePath(imagePath);
        if(navController.getCurrentDestination().getId() == R.id.imageDetailFragment) {
            navController.popBackStack();
        }
        navController.navigate(action);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_gallery:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    pickPictureFromGallery();
                }
                break;

            case R.id.fab_camera:
                takePicture();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RC_HANDLE_STORAGE_PERM) {

            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(Config.DEBUG) Log.i(TAG, "Phone storage permissions GRANTED");
                Snackbar.make(view, R.string.permissions_granted,
                        Snackbar.LENGTH_SHORT).show();

                pickPictureFromGallery();

            } else {
                if(Config.DEBUG) Log.i(TAG, "Phone storage permissions NOT GRANTED");
                Snackbar.make(view, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();
            }
        } else if(requestCode == RC_HANDLE_CAMERA_PERM) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(Config.DEBUG) Log.i(TAG, "Phone camera permissions GRANTED");
                Snackbar.make(view, R.string.permissions_granted,
                        Snackbar.LENGTH_SHORT).show();

                takePicture();

            } else {
                if(Config.DEBUG) Log.i(TAG, "Phone camera permissions NOT GRANTED");
                Snackbar.make(view, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();
            }
        } else if(requestCode == RC_HANDLE_WRITE_STORAGE_PERM) {
            if(grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if(Config.DEBUG) Log.i(TAG, "Write camera permissions GRANTED");
                Snackbar.make(view, R.string.permissions_granted,
                        Snackbar.LENGTH_SHORT).show();

                takePicture();

            } else {
                if(Config.DEBUG) Log.i(TAG, "Write storage permissions NOT GRANTED");
                Snackbar.make(view, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Handles the requesting of the storage permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestStoragePermission() {
        if(Config.DEBUG) Log.w(TAG, "Storage permission is not granted. Requesting permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Display a SnackBar with an explanation and a button to trigger the request.

            Snackbar.make(view, R.string.permissions_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permissions_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(PERMISSIONS_STORAGE,
                                    RC_HANDLE_STORAGE_PERM);
                        }
                    })
                    .show();
        } else {
            requestPermissions(PERMISSIONS_STORAGE, RC_HANDLE_STORAGE_PERM);
        }
    }

    /**
     * Handles the requesting of the storage permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        if(Config.DEBUG) Log.w(TAG, "Camera permission is not granted. Requesting permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {

            // Display a SnackBar with an explanation and a button to trigger the request.

            Snackbar.make(view, R.string.permissions_camera,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permissions_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(PERMISSIONS_CAMERA,
                                    RC_HANDLE_CAMERA_PERM);
                        }
                    })
                    .show();
        } else {
            requestPermissions(PERMISSIONS_CAMERA, RC_HANDLE_CAMERA_PERM);
        }
    }

    /**
     * Handles the requesting of write to storage permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestWriteToStoragePermission() {
        if(Config.DEBUG) Log.w(TAG, "Write to storage permission is not granted. Requesting permission");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Display a SnackBar with an explanation and a button to trigger the request.

            Snackbar.make(view, R.string.permissions_write_to_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.permissions_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(PERMISSIONS_WRITE_STORAGE,
                                    RC_HANDLE_WRITE_STORAGE_PERM);
                        }
                    })
                    .show();
        } else {
            requestPermissions(PERMISSIONS_WRITE_STORAGE, RC_HANDLE_WRITE_STORAGE_PERM);
        }
    }

    /**
     * Get shared images view model
     * @return
     */
    @Override
    public ImagesViewModel onGetViewModel() {
        return imagesViewModel;
    }
}
