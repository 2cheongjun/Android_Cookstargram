package com.example.myapplication.Recipe_select;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.mypage.RetrofitServer.ApiClient;
import com.example.myapplication.mypage.RetrofitServer.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubscribePresenter {

    private static final Object TAG = "SubscribePresenter";
    private SubscribeView view;
    private com.example.myapplication.mypage.RetrofitServer.ApiClient ApiClient;
    ImageView iv_imgNo;


    public SubscribePresenter(SubscribeView view) {
        this.view = view;
    }

    // 보내는 곳
    void getData(String myID) {
//        view.showLoading();
        // userID 넣고 서버 Select해오기

        ApiInterface apiInterface = com.example.myapplication.mypage.RetrofitServer.ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Note>> call = apiInterface.getNotes(myID);
        call.enqueue(new Callback<List<Note>>() {
            @Override
            public void onResponse(@NotNull Call<List<Note>> call, @NotNull Response<List<Note>> response) {
//                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {

                    view.onGetResult(response.body());

                    Log.e("SubscribePresenter", String.valueOf(response.body()));


                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Note>> call, @NotNull Throwable t) {
//                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        });
    }
}
