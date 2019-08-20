package com.cedancp.gallery.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cedancp.gallery.ui.model.ImageRepository;
import com.cedancp.gallery.ui.model.ImageResponse;

import java.util.List;

public class ImagesViewModel extends AndroidViewModel {

    private ImageRepository imageRepository;

    public ImagesViewModel(@NonNull Application application) {
        super(application);
        imageRepository =  new ImageRepository();
    }

    public LiveData<List<ImageResponse>> getImages() {
        return imageRepository.getImages();
    }
}
