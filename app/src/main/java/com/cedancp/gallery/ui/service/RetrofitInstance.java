package com.cedancp.gallery.ui.service;

import com.cedancp.gallery.Config;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.cedancp.gallery.Config.DEBUG;

public class RetrofitInstance {

    private static Retrofit retrofit = null;

    public static ImagesAPI getService(){


        if(retrofit == null){

            if (DEBUG) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.level(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(Config.API_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } else {
                retrofit = new Retrofit
                        .Builder()
                        .baseUrl(Config.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        }

        return retrofit.create(ImagesAPI.class);

    }
}
