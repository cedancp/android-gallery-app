package com.cedancp.gallery.ui.service;

import com.cedancp.gallery.ui.model.ImageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ImagesAPI {

    @GET("images")
    Call<List<ImageResponse>> getImages();
}
