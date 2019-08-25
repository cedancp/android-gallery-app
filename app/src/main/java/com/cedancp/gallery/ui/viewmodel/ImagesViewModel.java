package com.cedancp.gallery.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cedancp.gallery.ui.model.ImageRepository;
import com.cedancp.gallery.ui.model.ImageResponse;

import java.util.List;

import okhttp3.MultipartBody;

public class ImagesViewModel extends AndroidViewModel {

    private ImageRepository imageRepository;

    public ImagesViewModel(@NonNull Application application) {
        super(application);
        imageRepository =  new ImageRepository();
    }

    public void getImages() {
        imageRepository.getImages();
    }

    public void uploadImage(MultipartBody.Part body, String name, String description) {
        imageRepository.uploadImage(body, name, description);
    }

    public LiveData<List<ImageResponse>> getCurrentImages() {
        return imageRepository.getCurrentImages();
    }
}
