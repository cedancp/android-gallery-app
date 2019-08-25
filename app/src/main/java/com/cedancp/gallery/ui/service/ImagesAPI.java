package com.cedancp.gallery.ui.service;

import com.cedancp.gallery.ui.model.ImageResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ImagesAPI {

    @GET("images")
    Call<List<ImageResponse>> getImages();

    @Multipart
    @POST("images")
    Call<List<ImageResponse>> uploadImage(@Part MultipartBody.Part body, @Part("name") String name, @Part("description") String description);
}
