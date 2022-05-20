package com.example.myapplication.mypage.RetrofitServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 공통으로 사용하는 APICLIENT // 레트로핏
public class ApiClient {
    private static Retrofit retrofit;
    // BaseUrl등록
    private static final String BASE_URL = "http://3.37.202.166/";

    public static Retrofit getApiClient()
    {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        if (retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }
}
