package com.cedancp.gallery.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.cedancp.gallery.R;
import com.cedancp.gallery.ui.view.home.GalleryFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.View;

public class MainActivity extends AppCompatActivity implements GalleryFragment.OnFragmentInteractionListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GALLERY_REQUEST_CODE = 10001;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_gallery:
                pickPictureFromGallery();
                break;
        }
    }
}
