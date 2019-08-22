package com.cedancp.gallery.ui.view.home;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cedancp.gallery.R;
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

    private OnFragmentInteractionListener mListener;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        images = new ArrayList<>();
        imagesViewModel = ViewModelProviders.of(this).get(ImagesViewModel.class);

        //TODO: Set up recycler view and adapter

        getImages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
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
    //TODO: Call notifyDataSetChanged on adapter
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
