package com.example.myapplication.Chat_test.ChatList;

import java.util.List;

// 채팅 리스트인터페이스 , API는 마이페이지에 있는것 공통으로 사용중
public interface chatListInterface {

    void onGetResult(List<ChatDate> chatInfos);
    void onErrorLoading(String message);

}


