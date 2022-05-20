package com.example.myapplication.Chat_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


class ReceiveThread extends Thread {
    String read;
    Context context;
    private static final String TAG = "ReceiveThread";
    private String ip = "000.000.0.00"; // 우리집

    Socket socket;
    private final int port = 6016;
    PrintWriter sendMessage;
    private Handler mHandler;


    public ReceiveThread() {
    }

    public void run() {

        Log.e(TAG, "수신쓰레드 클래스 시작");
        try {

            // 채팅방에 입장 하였습니다. 메시지 보내기 -> OutputStream 서버는 inputStream으로 받는다.
//            sendMessage = new PrintWriter(socket.getOutputStream());
//            Log.e(TAG, " 2.sendMessage 서버로 보냄/" + sendMessage);

            InetAddress serverAddr = InetAddress.getByName(ip);
            socket = new Socket(serverAddr, port);

            // 데이터 읽거나 쓸수있다.
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 쓰레드를 생성하고 while문을 걸어줘야한다. 서버로 부터 수신된 메시지
            while (true) {

                read = in.readLine();

                Log.e(TAG, "read" + read);

                if (read != null) {
//                     서버로부터 수신된메시지가 널이 아니면 , msgUpdate를 한다
//                     핸들러를 사용해야, UI가 업데이트 된다.
                    Log.e(TAG, " 서버로 부터 수신된메시지  /" + read);
                    mHandler.post(new msgUpdate(read, context));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "소켓 연결종료");

        }
    }


    class msgUpdate implements Runnable {
        private static final String TAG = "msgUPdate";
        private String msg;
        private Context context;

        public msgUpdate(String str, Context context) {
            this.msg = str;
            this.context = context;
//            this.read =read;
        }


        @Override
        public void run() {

            chatRoom2Activity.Chat chat = new chatRoom2Activity.Chat();

            Log.e(TAG, "msgUpdate 쓰레드");
            // 유저명 + 대화내용+ 시간을 받아와서, 나와 대화상대를 구분하고 뷰홀더에 값을 넣는다.

            try {

                SharedPreferences prefsName3 = context.getSharedPreferences("NAME", Context.MODE_PRIVATE);
                String myID2 = prefsName3.getString("userID", "userID"); // 내 아이디

                Log.e(TAG, " myID2 가져온값 /" + myID2);


                // 메시지가 입력되면, 제이슨값을 받아온다.
                JSONObject jsonObject = new JSONObject(msg);
                Log.e(TAG, "msgUpdate" + jsonObject);

                // 받은값에서 내아이디와, 이미지여부, 메시지를 뽑아낸다.
                String myID = jsonObject.getString("myID");// 내아이디
                String type = jsonObject.getString("imgType");// 이미지구분자
                String finalMessage = jsonObject.getString("message");


                Log.e(TAG, "msg UPdate finalMessage /" + finalMessage);

                // JSON으로 가져온 내아이디와, 셰어드에 저장된 내아이디가 같으면 true
                chat.isMe = myID.equals(myID2) ? true : false;

                if (chat.isMe) {
                    // 나일때 (내가 이미지 보냄)OK
                    if (type.equals("image")) {
                        chat.chatImg_1 = finalMessage;
                        Log.e(TAG, "volleyImg / 조회@@@@@@" + chat.chatImg_1);
                    }
                    if (type.equals("text")) { // OK
                        chat.message = finalMessage;
                        Log.e(TAG, "text / 조회@@@@@@" + chat.chatImg_1);
                    }

                } else {

                    // 대화상대방일때, 이미지값이 있으면,OK
                    if (type.equals("image")) {
                        chat.chatImg_1_you = finalMessage;
                        Log.e(TAG, "image / 상대가보낸image" + chat.chatImg_1);

                    }
                    if (type.equals("text")) { // 내용이 두번씩 감.
                        chat.message = finalMessage;// 메시지내용
                        Log.e(TAG, "image / 상대가보낸text" + chat.chatImg_1);

                    }

                }
                // 상대방이름

                // 시간값
                chat.myChatTime = jsonObject.getString("chatTime");
                chat.youChatTime = jsonObject.getString("chatTime");
                chat.myChatTimeImg = jsonObject.getString("chatTime");
                chat.youChatTime = jsonObject.getString("chatTime");
                chat.youChatTime_Img = jsonObject.getString("chatTime");


                // 방번호를 chat 클래스에 정보를 추가한다.
                chat.roomidx = jsonObject.getString("roomIdx");

                Log.e(TAG, "msg업데이트 chat isMe : " + chat.isMe);
                Log.e(TAG, "msg업데이트 chatRoom2Activity : " + !chat.isMe + "");
//
//                onStartService(!chat.isMe);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


//class SendThread extends Thread {
//    private static final String TAG = "SendThread";
//
//    public SendThread() {
//    }
//
//    public void run() {
//        super.run();
//        try { // 입력한 채팅내용 출력
//
//            // json으로 메시지 보내는 부분
//            JSONObject jsonObject = new JSONObject();
//
//            Log.e(TAG, " 발신 jsonObject /" + jsonObject);
//            // 상대방프사를 눌러 입장 후 친구아이디가 있을때만 메시지를 보낸다. // json에 채팅 내용을 담아서 printWrite로 보냄
//
//            if (friendID != null) {
//                Log.e(TAG, " 대화상대  /" + friendID);
//
//                jsonObject.put("myID", myID);
//                jsonObject.put("message", sendmsg);// edittext에 작성한내용
//                // 누구의 시간인가? 어떻게 구분할까? ->시간은 구분필요없음
//                jsonObject.put("chatTime", getTime()); // 오후시간대로 표기하게 하기
//                // 방번호를 어떻게 넣을까? 내이름+친구이름으로 방생성
//                jsonObject.put("roomIdx", myID + "&" + friendID);
//                jsonObject.put("friendID", friendID);
////                                    jsonObject.put("check","Y"); // 읽음처리 추가
//                jsonObject.put("imgType", "text");
//                jsonObject.put("list", myID + "&" + sendmsg + "&" + friendID);
//                jsonObject.put("room", "in");
//
//
//                // 작성하는 내용값이 있으면, 채팅을 보낸다.
//                if (sendMessage != null) {
//                    Log.e(TAG, " sendMessage /" + sendMessage);
//                    sendMessage.println(jsonObject.toString());
////                                    sendMessage.println(myID + "&" + friendID + "&"+sendmsg);
//                    Log.e(TAG, "보내는 채팅내용 /" + sendMessage);
//                    sendMessage.flush(); // 자바서버로 보내기
//                }
//
//                message.setText(""); // 메시지 보내고 채팅창 비우기
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}

