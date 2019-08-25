package com.cedancp.gallery.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.cedancp.gallery.Config;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements GalleryFragment.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Permissions
    private static final int RC_HANDLE_STORAGE_PERM = 3;
    final String[] PERMISSIONS_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int GALLERY_REQUEST_CODE = 10001;

    private NavController navController;

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

        FloatingActionButton fab = findViewById(R.id.fab_gallery);
        fab.setOnClickListener(this);
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
            File imageFile = new File(picturePath);
            c.close();
            RequestBody requestFile = RequestBody.create(imageFile, MediaType.parse("multipart/form-data"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile", imageFile.getName(), requestFile);

            imagesViewModel.uploadImage(body, "testApp", "testApp");
        }
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_HANDLE_STORAGE_PERM ) {

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (Config.DEBUG) Log.i(TAG, "Phone storage permissions GRANTED");
                Snackbar.make(view, R.string.permissions_granted,
                        Snackbar.LENGTH_SHORT).show();

                pickPictureFromGallery();

            } else {
                if (Config.DEBUG) Log.i(TAG, "Phone storage permissions NOT GRANTED");
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
     * Get shared images view model
     * @return
     */
    @Override
    public ImagesViewModel onGetViewModel() {
        return imagesViewModel;
    }
}
