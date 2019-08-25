package com.cedancp.gallery.ui.view.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cedancp.gallery.Config;
import com.cedancp.gallery.R;
import com.cedancp.gallery.databinding.FragmentGalleryBinding;
import com.cedancp.gallery.ui.model.ImageResponse;
import com.cedancp.gallery.ui.viewmodel.ImagesViewModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GalleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class GalleryFragment extends Fragment {

    private static final String TAG = GalleryFragment.class.getSimpleName();

    private ImagesViewModel imagesViewModel;
    private List<ImageResponse> images;
    private FragmentGalleryBinding fragmentGalleryBinding;
    private GalleryAdapter galleryAdapter;

    private RecyclerView rv_images;

    private OnFragmentInteractionListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        images = new ArrayList<>();

        // Setting up data binding
        fragmentGalleryBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_gallery, container, false);

        rv_images = fragmentGalleryBinding.rvImages;
        galleryAdapter = new GalleryAdapter(images);

        rv_images.setLayoutManager(new GridLayoutManager(getActivity(), Config.GALLERY_GRID_COLUMNS));

        // TODO: Set animator

        rv_images.setAdapter(galleryAdapter);

        images = new ArrayList<>();
        imagesViewModel = ViewModelProviders.of(this).get(ImagesViewModel.class);

        getImages();
        // Inflate the layout for this fragment
        return fragmentGalleryBinding.getRoot();
    }

    /**
     * Sets images observer received from the API
     */
    private void getImages() {
        imagesViewModel.getImages().observe(getActivity(), new Observer<List<ImageResponse>>() {
            @Override
            public void onChanged(List<ImageResponse> imageResponses) {
                images = imageResponses;

                // Updates recycler view adapter
                updateGallery();
            }
        });
    }

    /**
     * Updates recycler view
     */
    private void updateGallery() {
        if(galleryAdapter != null) {
            galleryAdapter.updateImages(images);
            galleryAdapter.notifyDataSetChanged();
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
