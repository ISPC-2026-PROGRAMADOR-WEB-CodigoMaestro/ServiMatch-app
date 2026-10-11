package com.ispc.servimatch.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.ispc.servimatch.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = BuildConfig.API_BASE_URL;

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            Context appContext = context.getApplicationContext();

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {

                        SharedPreferences preferences =
                                appContext.getSharedPreferences(
                                        "ServiMatchPrefs",
                                        Context.MODE_PRIVATE
                                );

                        String token = preferences.getString(
                                "jwt_token",
                                null
                        );

                        okhttp3.Request.Builder requestBuilder =
                                chain.request().newBuilder();

                        boolean esLogin = chain.request().url().encodedPath()
                                .equals("/api/login/");

                        if (!esLogin && token != null && !token.isEmpty()) {

                            requestBuilder.header(
                                    "Authorization",
                                    "Bearer " + token
                            );
                        }

                        return chain.proceed(requestBuilder.build());

                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}