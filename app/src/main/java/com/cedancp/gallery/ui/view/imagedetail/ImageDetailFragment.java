package com.cedancp.gallery.ui.view.imagedetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cedancp.gallery.BuildConfig;
import com.cedancp.gallery.Config;
import com.cedancp.gallery.R;
import com.cedancp.gallery.databinding.FragmentImageDetailBinding;
import com.cedancp.gallery.ui.MainActivity;
import com.cedancp.gallery.ui.model.ImageResponse;
import com.cedancp.gallery.ui.view.home.GalleryAdapter;
import com.cedancp.gallery.ui.viewmodel.ImagesViewModel;
import com.cedancp.gallery.utils.ImageEditor;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageDetailFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_PARAM1 = "imagePath";

    private FragmentImageDetailBinding fragmentImageDetailBinding;
    private ImagesViewModel imagesViewModel;

    private String imagePath;
    private Bitmap currentBitmap;

    private ImageView iv_preview;
    private ImageButton bt_rotate;
    private ImageButton bt_upload;

    private OnFragmentInteractionListener mListener;

    public ImageDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @param imagePath Path to selected image.
     * @return A new instance of fragment ImageDetailFragment.
     */
    public static ImageDetailFragment newInstance(String imagePath) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePath = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Setting up data binding
        fragmentImageDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_image_detail, container, false);

        imagesViewModel = mListener.onGetViewModel();


        iv_preview = fragmentImageDetailBinding.ivPreview;
        currentBitmap = getImageBitmap();
        iv_preview.setImageBitmap(currentBitmap);

        bt_rotate = fragmentImageDetailBinding.btRotate;
        bt_upload = fragmentImageDetailBinding.btUpload;

        bt_rotate.setOnClickListener(this);
        bt_upload.setOnClickListener(this);

        return fragmentImageDetailBinding.getRoot();
    }


    private Bitmap getImageBitmap() {
        Uri imageURI = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                new File(imagePath));
        try {
            return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageURI);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void uploadImage() {
        File tempStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageTempFile = File.createTempFile("temp_image", ".jpg", tempStorageDir);
            Uri imageURI = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    imageTempFile);
            OutputStream os;
            os = new FileOutputStream(imageTempFile);
            currentBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            if (imageTempFile.exists()) {
                RequestBody requestFile = RequestBody.create(imageTempFile, MediaType.parse("multipart/form-data"));
                MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile", imageTempFile.getName(), requestFile);

                imagesViewModel.uploadImage(body, "testApp", "testApp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_rotate:
                currentBitmap = ImageEditor.rotateImage(currentBitmap);
                iv_preview.setImageBitmap(currentBitmap);
                break;

            case R.id.bt_upload:
                uploadImage();
                Navigation.findNavController(view).popBackStack();
                Snackbar.make(view, R.string.image_upload,
                        Snackbar.LENGTH_SHORT)
                        .show();
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        ImagesViewModel onGetViewModel();
    }
}
