package com.cedancp.gallery.ui.model;

import androidx.lifecycle.MutableLiveData;

import com.cedancp.gallery.ui.service.ImagesAPI;
import com.cedancp.gallery.ui.service.RetrofitInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageRepository {

    private static ImageRepository imageRepository;
    private ImagesAPI imagesAPI;
    private MutableLiveData<List<ImageResponse>> imagesData = new MutableLiveData<>();

    public static ImageRepository getInstance() {
        if (imageRepository == null) {
            imageRepository = new ImageRepository();
        }

        return imageRepository;
    }

    public MutableLiveData<List<ImageResponse>> getImages() {
        imagesAPI = RetrofitInstance.getService();

        imagesAPI.getImages().enqueue(new Callback<List<ImageResponse>>() {
            @Override
            public void onResponse(Call<List<ImageResponse>> call, Response<List<ImageResponse>> response) {
                if(response.isSuccessful()) {
                    imagesData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ImageResponse>> call, Throwable t) {
                imagesData.setValue(new ArrayList<ImageResponse>());
            }
        });

        return imagesData;
    }
}
