package com.example.myapplication.Chat_test.ChatList;

import android.util.Log;

import com.example.myapplication.mypage.RetrofitServer.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatListPresenter {

    private static final String TAG = "chatListPresenter";
    private chatListActivity view;
    private com.example.myapplication.mypage.RetrofitServer.ApiClient ApiClient;

    public chatListPresenter(chatListActivity view) {
        this.view = view;
    }

    // 보내는 곳
    public void getData(String myID) {
        //view.showLoading();
        // userID 넣고 서버 Select해오기

        // 공통으로 쓰는  API
        ApiInterface apiInterface = (com.example.myapplication.mypage.RetrofitServer.ApiClient.getApiClient().create(ApiInterface.class));
        Call<List<ChatDate>> call = apiInterface.chatList(myID);
        call.enqueue(new Callback<List<ChatDate>>() {
            @Override
            public void onResponse(@NotNull Call<List<ChatDate>> call, @NotNull Response<List<ChatDate>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    view.onGetResult(response.body());

                    Log.e(TAG, "Presenter" + "레트로핏 API 채팅목록 가져옴" + String.valueOf(response.body()));
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<ChatDate>> call, @NotNull Throwable t) {
//                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        });
    }
}