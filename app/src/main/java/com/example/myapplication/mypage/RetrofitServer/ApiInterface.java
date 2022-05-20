package com.example.myapplication.mypage.RetrofitServer;


import com.example.myapplication.Chat_test.ChatList.ChatCount;
import com.example.myapplication.Chat_test.ChatList.ChatDate;
import com.example.myapplication.Chat_test.ChatList.cookChatInfo;
import com.example.myapplication.Recipe_select.Note;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

// 인터페이스는 공통으로 사용할수 있다.
// 보내는 부분
public interface ApiInterface {

    // userID만 보냄  // 마이페이지 - 게시글수 서버에서 가져오는 값
    // 구독리스트를 눌러서 보내는 것
    // 프사를 눌러서 서버로 보내는 것
    @FormUrlEncoded
    @POST("post/myPostCount.php")
    Call<Count> getCount(
            @Field("userID") String userID,
            @Field("followID") String followID
    );

    // 구독한 게시물 // 서버로 보내는값 게시물 구독하기, 해제하기
    @FormUrlEncoded
    @POST("mypage/subscribe.php")
    Call<subscribe> getSubscribe(
            @Field("myID") String myID,
            @Field("userID") String userID,
            @Field("check") boolean check
    );

    // myID 보내고 // 구독자목록 가져오기
    @FormUrlEncoded
    @POST("subcribe_select.php")
    Call<List<Note>> getNotes(
            @Field("myID") String myID);


    // 노트작성내용 // 서버로 보내는값
    @FormUrlEncoded
    @POST("save.php")
    Call<Note> saveNote(
            @Field("title") String title,
            @Field("note") String note
    );


    // 1:1 채팅방에서 보내는 메시지 내용
    @FormUrlEncoded
    @POST("chatList_select_message.php")
    Call<cookChatInfo> selectChat(
            @Field("myID")  String myID,
            @Field("friendID") String friendID,
            @Field("friendID") String roomIdx);

    // 채팅방목록 불러오기는 내아이디만 보내면됨
    @FormUrlEncoded
    @POST("chatRoomList_select.php")
    Call<List<ChatDate>> chatList(
            @Field("myID") String myID
            );


    // 채팅방버튼 누르면, 채팅방생성하기
    @FormUrlEncoded
    @POST("mypage/makeChatRoomList.php")
    Call<MakeChatRoom> makeChatRoom(
            @Field("myID") String myID,
            @Field("userID") String userID,
            @Field("roomName") String roomName
    );

    // TODO 마지막채팅번호 보내기
    @FormUrlEncoded
    @POST("mypage/finalChatNum.php")
    Call<ChatCount> chatCount(

            @Field("roomIdx") String roomIdx,
            @Field("finalChatNum") String finalChatNum
    );
}
