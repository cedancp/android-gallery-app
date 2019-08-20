package com.cedancp.gallery.ui.service;

import com.cedancp.gallery.Config;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static Retrofit retrofit = null;

    public static ImagesAPI getService(){


        if(retrofit==null){

            retrofit=new Retrofit
                    .Builder()
                    .baseUrl(Config.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }

        return retrofit.create(ImagesAPI.class);

    }
}
