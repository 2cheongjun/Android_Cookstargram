package com.example.myapplication.Chat_test;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myapplication.App;
import com.example.myapplication.Chat_test.ChatList.ChatCount;
import com.example.myapplication.Chat_test.ChatList.ChatDate;
import com.example.myapplication.Chat_test.ChatList.chatListActivity;
import com.example.myapplication.Chat_test.ChatList.cookChatInfo;
import com.example.myapplication.Chat_test.ChatList.cookChatInfo.chatComment;
import com.example.myapplication.R;
import com.example.myapplication.Recipe_select.EditActivity;
import com.example.myapplication.mypage.RetrofitServer.ApiClient;
import com.example.myapplication.mypage.RetrofitServer.ApiInterface;
import com.example.myapplication.mypage_ViewBookmark.ViewActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.example.myapplication.Api.Api.BASE_URL;
import static com.example.myapplication.Api.Api.URL_CHAT_IMG_INSERT;


// 클라이언트 소켓통신 1:1 채팅방 -> 채팅리스트를 눌러서 들어갔을때
// 친구마이페이지의 채팅을 누르면 1:1채팅방이 생성되고, 하단 채팅 탭애서 채팅리스트를 확인할수 있다.
public class chatRoom2Activity extends AppCompatActivity {
    private static final String TAG = "chatRoom2Activity /2 ";
    int PICTURE_REQUEST_CODE = 111;
    private Handler mHandler;
    InetAddress serverAddr;
    Socket socket;
    PrintWriter sendMessage;
    // 핸드폰하고,서버가 같은 아이피에 접속이 되어있어야함. 소켓에 텍스트 안써지면, IP를 확인하자
    private String ip = "000.000.0.00"; // 우리집
    private final int port = 6016;
    Button chatbutton;
    ListView mMessageListVIew;
    EditText message; // 채팅내용
    String sendmsg;
    String read;
    int roomOK = 0;
    String friendID = null; // 대화상대 아이디
    private TextView chatName_title; // 방타이틀 이름
    private String myID; // 내아이디
    private String myID2; // 내아이디
    ImageView imageView;
    private String roomIdx; // 채탕방번호
    Bitmap bitmap; // 사진전송을 위한 비트맵
    Bitmap image;
    String volleyImg = null;

    private ClientAdapter mAdapter;
    private ArrayList<Chat> mChatDataList;// 채팅내용

    ImageView iv_back; // 뒤로가기버튼
    ImageView iv_galleryBtn; // 사진전송버튼

    // 레트로핏으로 서버에서 가져올내용
    List<cookChatInfo> chatInfos;

    private String imageFilePath;
    // 볼리전송
    private JSONObject jsonObject;
    private RequestQueue requestQueue;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progressDialog;

    // 인텐트 값을 가져오기 위한 구분자
    private String actionType = "";
    ChatDate chatInfo = null;
    public static final int INCREMENT_PROGRESS = 0;// 핸들러

    private BroadcastReceiver mReceiver; // 브로드캐스트리시버

    // 서비스 인텐트
    private Intent intent;
    private MyService3 mService;
    private RemoteService remoteService;

    private boolean mBound;

    String listRoomIdx = null;
    String listFriend = null;
    boolean isConnected = false; // 소켓연결
    String finalChatNum;

    boolean isRoom = false; // 상대방입장
    private int count = 0; // 안읽음 갯수


    // TODO 1120
    // 쓰레드중지, 소켓 닫기 ,n소켓연결을 한번만해도 되는것으로 변경이되었다.
    @Override
    protected void onStop() {
        super.onStop();

        // 소켓연결 기본상태
        isConnected = false;

//        try {
//            if (read != null) sendMessage.close();
//            if (socket != null) socket.close();
//            Log.e(TAG, "onStop + sendWriter,소켓 닫기");
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.e("read.CLOSE()", e.toString());
//        }
    }


    /**
     * oncreate 시작
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);


        // 핸들러.
        mHandler = new Handler();
        Log.e(TAG, "핸들러" + String.valueOf(mHandler));

        mMessageListVIew = (ListView) findViewById(R.id.list_view);
        mChatDataList = new ArrayList<>();
        Log.e(TAG, "mChatDataList" + mChatDataList);
        mAdapter = new ClientAdapter(mChatDataList);
        mMessageListVIew.setAdapter(mAdapter);
        message = (EditText) findViewById(R.id.message);
        chatbutton = (Button) findViewById(R.id.chatbutton);// 채팅전송버튼
        iv_galleryBtn = findViewById(R.id.iv_galleryBtn);   // 갤러리버튼


        // 로그인한 내 아이디값
        SharedPreferences prefsName = getSharedPreferences("NAME", MODE_PRIVATE);
        myID = prefsName.getString("userID", "userID"); //키값, 디폴트값

        isRoom = false;// 상대방상태

        // TODO 이부분만 수정
        // 리스트에서 가져온 방번호와 친구이름
        if (getIntent() != null) {
            Intent intent = getIntent();
            listRoomIdx = intent.getStringExtra("listRoomIdx");
            friendID = intent.getStringExtra("listFriend");
        }
        // 대화상대이름
        chatName_title = findViewById(R.id.chatName_title);
        chatName_title.setText(friendID + "님과 채팅");


        // TODO 레트로핏으로 채팅내용불러오기
        selectChat(myID, friendID, roomIdx);
        Log.e(TAG, "리스트에서 가져온 친구이름 +방번호 /" + friendID + roomIdx);


        // 스크롤 마지막 채팅으로 이동해있기
        mMessageListVIew.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mMessageListVIew.setSelection(mAdapter.getCount() - 1);


        //TODO 방에 들어오면 roomOK라는 텍스트가 생김 -> 방을 나가면 삭제
        SharedPreferences pref2 = getSharedPreferences("roomOK", MODE_PRIVATE);
        roomOK = pref2.getInt("구분자 생성 roomOK", 1); //키값, 디폴트값
        Log.e(TAG, " roomOK 값 방에 들어옴 /" + roomOK);


        // 뒤로가기버튼
        // 뒤로가기버튼을 누르면 방에 있다는  구분자 삭제 // 서비스 알림처리는?
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 알림에 마지막 메시지 담아보내기, 읽음처리에 관한 내용 필요
                //TODO 방에 들어오면 roomOK라는 텍스트가 생김 -> 방을 나가면 -1로 변경
                SharedPreferences pref2 = getSharedPreferences("roomOK", MODE_PRIVATE);
                roomOK = pref2.getInt("구분자 생성 roomOK", -1); //키값, 디폴트값
                Log.e(TAG, " roomOK 값 방에서 나감 /" + roomOK);

                String room = "room out";
                /*
                 * 방을 나가는 순간 마지막 메시지 저장하기
                 */

                // Todo 마지막글번호 저장하기
//                Chat chatNum = mChatDataList.get(mChatDataList.size() - 1);
//                finalChatNum = chatNum.idx;

                Log.e(TAG, "채팅마지막글번호가져옴@@@@@@@@@@  /" + finalChatNum);

                // 방 나갈때 내가 나가기전 / 마지막 글번호를 셰어드에 저장
                if (roomOK == -1) {
                    Log.e(TAG, "채팅마지막글번호@@@@@@@@@@ 방나감/" + roomOK + "/" + finalChatNum);
                    SharedPreferences pref = getSharedPreferences("FINALCHATNUM", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("FINALCHATNUM", finalChatNum); //키값, 저장값
                    editor.apply();
                }


                finish();
            }
        });


        /**
         * 서버로 부터 받음 // 수신쓰레드 <-------------------------
         */
        class ReceiveThread extends Thread {

            public ReceiveThread() {
            }

            public void run() {

                Log.e(TAG, "Server Thread Run");
                try {
                    // TODO isConnected로 감싸기 추가
                    if (!isConnected) {
                        InetAddress serverAddr = InetAddress.getByName(ip);
                        socket = new Socket(serverAddr, port);

                        Log.e(TAG, " 1.소켓주소 /" + socket + "클라이언트 접속확인" + serverAddr);
                        // 채팅방에 입장 하였습니다. 메시지 보내기 -> OutputStream 서버는 inputStream으로 받는다.
                        sendMessage = new PrintWriter(socket.getOutputStream());
                        Log.e(TAG, " 2.sendMessage 서버로 보냄/" + sendMessage);

                        // 데이터 읽거나 쓸수있다.
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        // 쓰레드를 생성하고 while문을 걸어줘야한다. 서버로 부터 받은 메시지
                        while (true) {

                            read = in.readLine();
                            isConnected = true;

                            Log.e(TAG, "read" + read);

                            if (read != null) {
                                // 서버로부터 수신된메시지가 널이 아니면 , msgUpdate를 한다
                                // 핸들러를 사용해야, UI가 업데이트 된다.
                                mHandler.post(new msgUpdate(read));
                            } else {
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    isConnected = false;
                    Log.e(TAG, " 소켓연결 수신쓰레드 /" + "소켓 연결종료");

                }
            }
        }


//      수신쓰레드 시작
        ReceiveThread receiveThread = new ReceiveThread();
        receiveThread.start();
        Log.e(TAG, " receiveThread 채팅방2 시작 /" + receiveThread);

        // 발신 쓰레드 생성
        class SendThread2 extends Thread {
            public SendThread2() {
            }

            public void run() {
                super.run();
                try { // 입력한 채팅내용 출력
                    Log.e(TAG, "SendThread2()  /");
                    sendMessage = new PrintWriter(socket.getOutputStream());
                    Log.e(TAG, " 2.sendMessage 서버로 보냄/" + sendMessage);

                    // json으로 메시지 보내는 부분
                    JSONObject jsonObject = new JSONObject();

                    Log.e(TAG, " 발신 jsonObject /" + jsonObject);
                    // 상대방프사를 눌러 입장 후 친구아이디가 있을때만 메시지를 보낸다. // json에 채팅 내용을 담아서 printWrite로 보냄

                    if (friendID != null) {
                        jsonObject.put("roomIdx", myID + "&" + friendID);

                        Log.e(TAG, " sendMessage /" + sendMessage);
                        sendMessage.println(jsonObject.toString());
                        Log.e(TAG, "보내는 채팅내용 /" + sendMessage);
                        sendMessage.flush(); // 자바서버로 보내기
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isConnected = false;
                }
            }
        }


        // 서버로 메시지 보내기 // 버튼을 누르면, 서버로 메시지를 보낸다.
        chatbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // edit text에 작성된 글
                sendmsg = message.getText().toString();
                Log.e(TAG, " 입력한 글 /" + sendmsg);

                keyboardHidden();// 키보드내림

                // 발신 쓰레드 생성
                class SendThread extends Thread {
                    public SendThread() {
                    }

                    public void run() {
                        super.run();
                        try { // 입력한 채팅내용 출력
                            sendMessage = new PrintWriter(socket.getOutputStream());
                            Log.e(TAG, " 2.sendMessage 서버로 보냄/" + sendMessage);

                            // json으로 메시지 보내는 부분
                            JSONObject jsonObject = new JSONObject();

                            Log.e(TAG, " 발신 jsonObject /" + jsonObject);
                            // 상대방프사를 눌러 입장 후 친구아이디가 있을때만 메시지를 보낸다. // json에 채팅 내용을 담아서 printWrite로 보냄

                            if (friendID != null) {
                                Log.e(TAG, " 대화상대  /" + friendID);
//                                jsonObject.put("hi", friendID + "님이 들어왔습니다.");

                                jsonObject.put("myID", myID);
                                jsonObject.put("message", sendmsg);// edittext에 작성한내용
                                // 누구의 시간인가? 어떻게 구분할까? ->시간은 구분필요없음
                                jsonObject.put("chatTime", getTime()); // 오후시간대로 표기하게 하기
                                // 방번호를 어떻게 넣을까? 내이름+친구이름으로 방생성
                                jsonObject.put("roomIdx", myID + "&" + friendID);
                                jsonObject.put("friendID", friendID);
//                                    jsonObject.put("check","Y"); // 읽음처리 추가
                                jsonObject.put("imgType", "text");
                                jsonObject.put("list", myID + "&" + sendmsg + "&" + friendID);


                                // 작성하는 내용값이 있으면, 채팅을 보낸다.
                                if (sendMessage != null) {
                                    Log.e(TAG, " sendMessage /" + sendMessage);
                                    sendMessage.println(jsonObject.toString());
                                    Log.e(TAG, "보내는 채팅내용 /" + sendMessage);
                                    sendMessage.flush(); // 자바서버로 보내기
                                }
                                message.setText(""); // 메시지 보내고 채팅창 비우기

                                // TODO 작성내용중 bye가 있으면 소켓연결 종료됨
//                                if (read.equalsIgnoreCase("bye")) {
//                                    sendMessage.write(sendmsg);
//                                    sendMessage.flush();
//                                    message.setText(""); // 메시지 보내고 채팅창 비우기
//                                }

                                message.setText(""); // 메시지 보내고 채팅창 비우기
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            isConnected = false;
                        }
                    }
                }

                // 발신쓰레드 시작
                SendThread sendThread = new SendThread();
                sendThread.start();
                Log.e(TAG, "발신쓰레드 시작" + "서버로 메시지전달");

            }

        });


        // 갤러리버튼을 누르면 doTakeMultiAlbumAction() 실행
        iv_galleryBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                doTakeMultiAlbumAction();
                Log.e(TAG, "Gallery 버튼누름");

            }
        });
    }


    /**
     * 포그라운드 서비스
     */

    // TODO onStartService // 내아이디와 친구아이디,채팅마지막 내용을 서비스로 전달하면,노티가 받는다.
    private void onStartService() {
        // foregroundService ~하면, 포그라운드 서비스 시작해서, 알림메시지 전달하기
        Intent intent = new Intent(this, MyService3.class);
        intent.setAction("startService");
        intent.putExtra("MYID", myID); // 내아이디와 친구아이디를 서비스로 전달하면,노티가 받는다.
        intent.putExtra("FRIENDID", friendID);
        intent.putExtra("LISTROOMID", listRoomIdx);
        Log.e(TAG, "onStartService()  /" + myID + friendID + listRoomIdx);
        // Todo 마지막글 내용
        Chat chatNum = mChatDataList.get(mChatDataList.size() - 1);
        String finalComment = chatNum.message;
        intent.putExtra("finalChat", finalComment);
        // 채팅 내용도 받아오면 좋겠네
        startService(intent);
    }


    public void onStopService() {
        Intent intent = new Intent(this, MyService3.class);
        stopService(intent);
    }


    // 메시지 결과쓰레드?
    private String resultThread() {
        new Thread() {
            public void run() {

                Log.e(TAG, "resultThread 시작");
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    socket = new Socket(serverAddr, port);
                    sendMessage = new PrintWriter(socket.getOutputStream());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while (true) {
                        read = input.readLine();

                        if (read != null) {
                            // 글이 작성이되면, msgUpdate를 한다.
                            mHandler.post(new msgUpdate(read));
                            Log.e(TAG, "결과쓰레드 read" + read);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return null;
    }


    // 어댑터에 추가할 아이템 Chat클래스를 생성한다.
    public static class Chat {
        // 데이터 생성자
        public String tv_firstTime; // 방처음생겼을때 시간
        public String message; // 메시지내용
        public boolean isMe; // 나와 상대구분
        public String youName; // 상대이름
        public String myChatTime; // 나 채팅시간
        public String youChatTime;// 상대 채팅시간
        public String iv_profile_small; // 상대프로필사진
        public String myChatTimeImg; // 말풍선보낸시간
        public String chatImg_1; // 내가 보낸 이미지1
        public String tv_isRead; // 읽음 표시

        // 상대에게 보이는 이미지
        public String chatImg_1_you;
        public String youChatTime_Img;
        public String youName_text_img;
        public String iv_profile_small_img;

        public String roomidx; // 방번호
        public String read; // 읽음처리
        public String idx; // 마지막글번호

    }

    // 리스트 어답터 // 데이터 설정을하고, Adapter가 관리한다.
    private class ClientAdapter extends BaseAdapter {

        private List<Chat> mData;
        private int nListCnt = 0;

        // 외부에서 받을수 있게 생성자를 만든다
        public ClientAdapter(List<Chat> list) {
            mData = list;
        }

        public void setItem(List<Chat> list) {
            mData = list;
            nListCnt = mData.size();
            this.notifyDataSetChanged();
//            Log.e(TAG, "setItem(List<Chat> list) /" + mData);
        }


        @Override
        public int getCount() {  // 아이템 개수
            return mData.size();
        }


        @Override
        public Object getItem(int position) { //~번째 아이템이 있는지 알려줌
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) { // 커서관련사용시에 사용
            return position;
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
            mMessageListVIew.setSelection(mAdapter.getCount() - 1);
        }


        // 나와 상대방을 구분하는 뷰홀더
        // View convertView - 이뷰가 널값이 아니면 재활용할수있다.
        // ViewGroup parent 이뷰를 포함하고 있는 부모 컨테이너 객체이다.
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder(); // 뷰홀더

                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat, parent, false);// 레이아웃장착


                // 채팅 --->> 내가 채팅보냈을때 레이아웃
                viewHolder.layoutMe = (LinearLayout) convertView.findViewById(R.id.layout_me); // 나의레이아웃
                viewHolder.bubbleMe = (TextView) convertView.findViewById(R.id.bubble_me_text); // 내가보낸내용
                viewHolder.myChatTime = (TextView) convertView.findViewById(R.id.myChatTime); // 내가 보낸시간
//                viewHolder.tv_isRead = (TextView) convertView.findViewById(R.id.tv_isRead); // 읽음 표시

                // 상대방이 채팅만보냈을때 레이아웃
                viewHolder.layoutYou = (LinearLayout) convertView.findViewById(R.id.layout_you);
                viewHolder.youName_text = (TextView) convertView.findViewById(R.id.youName_text);   // 대화상대 아이디
                viewHolder.bubbleYou = (TextView) convertView.findViewById(R.id.bubble_you_text); // 대화상대 말풍선
                viewHolder.youChatTime = (TextView) convertView.findViewById(R.id.youChatTime);  // 대화상대가 보낸시간
                viewHolder.iv_profile_small = (ImageView) convertView.findViewById(R.id.iv_profile_small);  // 대화상대 프로필사진

                // 내가 이미지보냈을때 레이아웃  --->>  이미지전송할때 뷰
                viewHolder.layout_me_img = (LinearLayout) convertView.findViewById(R.id.layout_me_img);
                viewHolder.chatImg_1 = (ImageView) convertView.findViewById(R.id.chatImg_1);// 내가 보낸 이미지
                viewHolder.myChatTimeImg = (TextView) convertView.findViewById(R.id.myChatTimeImg);// 이미지말풍선채팅시간

                //대화상대한테 보이는 이미지 --->>   이미지전송할때 뷰
                viewHolder.layout_you_img = (LinearLayout) convertView.findViewById(R.id.layout_you_img); // 상대방이미지
                viewHolder.chatImg_1_you = (ImageView) convertView.findViewById(R.id.chatImg_1_you); // 상대방이미지
                viewHolder.youChatTime_Img = (TextView) convertView.findViewById(R.id.youChatTime_img);// 상대방 이미지말풍선채팅시간
                viewHolder.iv_profile_small_img = (ImageView) convertView.findViewById(R.id.iv_profile_small_img);// 대화상대 프로필사진
                viewHolder.youName_text_img = (TextView) convertView.findViewById(R.id.youName_text_img);// 이미지말풍선채팅시간


                convertView.setTag(viewHolder);// 저장하면, getTag로 가지고 올수있다.
            } else {
                viewHolder = (ViewHolder) convertView.getTag();// 재사용할경우 가져오겠다.
                // 이미 찾은 View를 ViewHolder Pattern을이용해서 convertView의 Tag값이 저장해두는 방식을 쓰면 매번 findViewById()할 필요가 없다.
                // convertView가 최초로 호출 될때 findViewById()로 찾은 View를 Tag에 저장하며,
                // 그 이후로 호출되면 Tag에 저장된 View를 불러오는 방식이다.

            }

            Chat chat = mData.get(position);

            // 이미지 보낼때??????????       // 사진이미지 영역 보이기 어떻게????
            // 사용자가 나일때
            if (chat.isMe) {
                // 내가 메세지를 보낼때 // 메세지 내용이 있으면,
                if (!TextUtils.isEmpty(chat.message)) {
                    viewHolder.bubbleMe.setText(chat.message);

                    viewHolder.layoutMe.setVisibility(View.VISIBLE); // 노랑 말풍선
                    viewHolder.layout_me_img.setVisibility(GONE); // 내 사진영역 가리기
                    viewHolder.myChatTime.setVisibility(View.VISIBLE); // 시간
                    viewHolder.myChatTime.setText(chat.myChatTime); // 시간 넣기
                    viewHolder.myChatTimeImg.setVisibility(GONE); // 이미지시간가리기
                    viewHolder.layoutYou.setVisibility(GONE); // 상대방 레이아웃

                    viewHolder.youName_text.setVisibility(View.VISIBLE); //상대방 이름
                    viewHolder.youName_text.setText(chat.youName); // 상대방이름
                    viewHolder.iv_profile_small.setVisibility(View.VISIBLE); // 상대방 프사
//                    viewHolder.tv_isRead.setVisibility(View.VISIBLE);; // 읽음처리음

                    // 상대방프사 글라이드에 넣기
//                    Uri url = Uri.parse(BASE_URL + "/upload/" + chat.iv_profile_small);
                    SharedPreferences prefsName = getSharedPreferences("FRIENDIMAGE", MODE_PRIVATE);
                    String result = prefsName.getString("frImg", "frImg"); //키값, 디폴트값

                    Uri url = Uri.parse(BASE_URL + "/upload/" + result);
                    Glide.with(convertView)
                            .load(url)
                            .skipMemoryCache(true)
                            .override(200, 200)
                            .centerCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(viewHolder.iv_profile_small);

                    // 상대방이미지 안보이기
                    viewHolder.layout_you_img.setVisibility(GONE);
                    viewHolder.iv_profile_small_img.setVisibility(GONE);
                    viewHolder.youName_text_img.setVisibility(GONE);
                    viewHolder.youChatTime_Img.setVisibility(GONE);
                    viewHolder.chatImg_1_you.setVisibility(GONE);

                } else {

                    // 내가 이미지 보낼때 // 서버로 올린이미지 바로 가져와 끼기0 // 갤러리에서 바로 가져와 넣기x
                    if (!TextUtils.isEmpty(chat.chatImg_1)) {

                        Uri url2 = Uri.parse(BASE_URL + "/img/" + (chat.chatImg_1)); // 서버에서 가져온이미지(내가 업로드)
//                        Log.e(TAG, "볼리에서 가져온 글라이드 URI/" + url2);

                        Glide.with(convertView)
                                .load(url2)
//                                   .load(Uri.parse(chat.chatImg_1))
                                .skipMemoryCache(true)
                                .override(200, 200)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(viewHolder.chatImg_1);
                        viewHolder.chatImg_1.setVisibility(View.VISIBLE); // 사진
//                        }


                        viewHolder.iv_profile_small.setVisibility(View.VISIBLE); // 상대방 프사는 항상보임
                        // 상대방프사 글라이드에 넣기
                        SharedPreferences prefsName = getSharedPreferences("FRIENDIMAGE", MODE_PRIVATE);
                        String result = prefsName.getString("frImg", "frImg"); //키값, 디폴트값
                        Uri url = Uri.parse(BASE_URL + "/upload/" + result);
                        //                        Uri url = Uri.parse(BASE_URL + "/upload/" + chat.iv_profile_small);
                        Glide.with(convertView)
                                .load(url)
                                .skipMemoryCache(true)
                                .override(200, 200)
                                .centerCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(viewHolder.iv_profile_small);


                        viewHolder.myChatTimeImg.setVisibility(View.VISIBLE);// 내가보낸시간보이기
                        viewHolder.myChatTimeImg.setText(chat.myChatTimeImg);

                        // 내말풍선 상대말풍선 가리기
                        viewHolder.layoutMe.setVisibility(GONE);
                        viewHolder.layoutYou.setVisibility(GONE);
                        // 상대방이미지 안보이기
                        viewHolder.layout_you_img.setVisibility(GONE);
                        viewHolder.iv_profile_small_img.setVisibility(GONE);
                        viewHolder.youName_text_img.setVisibility(GONE);
                        viewHolder.youChatTime_Img.setVisibility(GONE);
                        viewHolder.chatImg_1_you.setVisibility(GONE);


                    } else {
                        // 이미지가 없을때 GONE처리
                        viewHolder.chatImg_1.setImageResource(0);
                        viewHolder.chatImg_1.setVisibility(GONE);

                    }

                    // 사진 채팅보낸시간
                    viewHolder.myChatTimeImg.setText(chat.myChatTimeImg);
                }

                // 상대방이 보낼때 메시지내용이 있으면,
            } else {

                if (!TextUtils.isEmpty(chat.message)) {
                    // 상대방이 메시지를 보낼때
                    viewHolder.bubbleYou.setText(chat.message);
                    viewHolder.youName_text.setText(chat.youName);  // 대화상대 이름
                    viewHolder.layoutMe.setVisibility(GONE);
                    viewHolder.layoutYou.setVisibility(View.VISIBLE);  // 흰말풍선 보이기
                    viewHolder.youChatTime.setText(chat.youChatTime);  // 시간
                    viewHolder.iv_profile_small.setVisibility(View.VISIBLE);  // 프사항상보이기
                    viewHolder.chatImg_1.setVisibility(GONE); // 내가 보낸 이미지X
                    viewHolder.chatImg_1_you.setVisibility(View.GONE);// 상대방두 이미지X
                    // 상대방프사 글라이드에 넣기
                    SharedPreferences prefsName = getSharedPreferences("FRIENDIMAGE", MODE_PRIVATE);
                    String result = prefsName.getString("frImg", "frImg"); //키값, 디폴트값

                    Uri url = Uri.parse(BASE_URL + "/upload/" + result);
                    Glide.with(convertView)
                            .load(url)
                            .skipMemoryCache(true)
                            .override(200, 200)
                            .centerCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(viewHolder.iv_profile_small);
                    // 이미지 레이아웃 상대방꺼 가리기
                    viewHolder.iv_profile_small_img.setVisibility(View.GONE); // X
                    viewHolder.youChatTime_Img.setVisibility(GONE); // 보낸시간 보임X
                    viewHolder.myChatTimeImg.setVisibility(GONE); //
                    viewHolder.youName_text_img.setVisibility(GONE); // 이름X

                } else {

                    // 상대방이 보는 이미지
                    // 텍스트를 보내는 레이아웃과 별개로 이미지를 보낼때 뷰를 새로만듬
                    if (!TextUtils.isEmpty(chat.chatImg_1_you)) {

                        viewHolder.layout_you_img.setVisibility(View.VISIBLE); // 이미지 레이아웃 보임
                        viewHolder.youChatTime_Img.setVisibility(View.VISIBLE); // 이미지에붙은시간
                        viewHolder.youChatTime_Img.setText(chat.youChatTime_Img);
                        viewHolder.youName_text_img.setVisibility(View.VISIBLE);// 상대방이름
                        viewHolder.youName_text_img.setText(chat.youName_text_img); // 레트로핏에서 가져와 쓰기


                        viewHolder.layoutMe.setVisibility(GONE); // 내말풍선 안보임
                        viewHolder.layoutYou.setVisibility(GONE);// 상대말풍선 안보임
                        viewHolder.chatImg_1.setVisibility(GONE); // 내가 보낸 이미지X
                        // 사진 채팅보낸시간

                        // TODO 상대방 이미지
                        Uri url3 = Uri.parse(BASE_URL + "/img/" + (chat.chatImg_1_you));

                        Log.e(TAG, "볼리 상대방이 받음 /  /" + url3);
                        // 사진전송이미지
                        viewHolder.chatImg_1_you.setVisibility(View.VISIBLE);
                        Glide.with(convertView)// 이미지 보임
                                .load(url3)
                                .skipMemoryCache(true)
                                .override(200, 200)
                                .centerCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(viewHolder.chatImg_1_you);

                        // 상대방 프사
                        viewHolder.iv_profile_small_img.setVisibility(View.VISIBLE); // 상대방프사보이기
                        // 상대방프사
                        SharedPreferences prefsName = getSharedPreferences("FRIENDIMAGE", MODE_PRIVATE);
                        String result = prefsName.getString("frImg", "frImg"); //키값, 디폴트값

                        Uri url = Uri.parse(BASE_URL + "/upload/" + result);
                        Glide.with(convertView)
                                .load(url)
                                .skipMemoryCache(true)
                                .override(200, 200)
                                .centerCrop()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .into(viewHolder.iv_profile_small_img);


                    } else {
                        // 이미지가 없을때 GONE처리
                        viewHolder.chatImg_1_you.setImageResource(0);
                        viewHolder.chatImg_1_you.setVisibility(GONE);
                    }
                }
            }
            // 리스트뷰 클릭안되게 설정
            convertView.setEnabled(false);

            return convertView;
        }

    }


    // 뷰홀더 내부클래스
    private static class ViewHolder {

        LinearLayout layoutMe;
        LinearLayout layoutYou;
        TextView youName_text;// 상대이름
        TextView bubbleYou;// 상대말풍선
        TextView bubbleMe; // 내 말풍선
        TextView myChatTime;// 내채팅시간
        TextView youChatTime;// 상대방채팅시간
        ImageView iv_profile_small; // 상대방프사
        TextView tv_isRead; // 읽음처리

        // 내 말풍선 이미지전송시
        LinearLayout layout_me_img;
        TextView myChatTimeImg; // 내채팅시간
        ImageView chatImg_1; // 이미지영역1
        public ImageView chatImg_1_1;
        ImageView chatImg_2; // 이미지영역2
//      ImageView chatImg_3; // 이미지영역3

        // 상대방 이미지전송시
        LinearLayout layout_you_img; // 상대방이미지
        ImageView iv_profile_small_img; // 상대방프사
        TextView youName_text_img;// 상대이름
        TextView youChatTime_Img; // 상대채팅시간
        ImageView chatImg_1_you; // 상대이미지영역1

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("IntentReset")
    private void doTakeMultiAlbumAction() {
        // 갤러리에서 이미지 불러오기
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        // 다중이미지설정
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICTURE_REQUEST_CODE);
    }


    // 갤러리 이미지회전
    public static Bitmap rotateImage(Bitmap src, float degree) {
        // create new matrix
        Matrix matrix = new Matrix();
        // setup rotation degree
        matrix.postRotate(degree);
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }

    // TODO 갤러리 onActivityResult
    // 뷰홀더에 어떻게 장착함?????
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //기존 이미지 지우기
//                chatImg_1.setImageResource(0);
//                chatImg_2.setImageResource(0);
//                chatImg_3.setImageResource(0);

                //ClipData 또는 Uri를 가져온다
                Uri uri = data.getData();
                Log.e(TAG, "사진에서 URI 얻어옴");
                ClipData clipData = data.getClipData();
                Log.e(TAG, "clipData" + "에 data,getpClipData를 담음");
                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.

                if (clipData != null) {

                    for (int i = 0; i < 3; i++) {
                        if (i < clipData.getItemCount()) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
//                            Chat chat = new Chat();

//                            try {
//                                //uri 를  list 에 담는다
//                                uriList.add(imageUri);
////                                Log.d(TAG, "uri 확인" + imageUri);
//                                String path = getPathFromUri(imageUri);
////                                Log.d(TAG, "path 확인 : " + path);
//                                pathList.add(path);
////                                Log.d(TAG, "pathList size 확인 : " + pathList.size());
//
//                            } catch (Exception e) {
//
//                                Log.e(TAG, "File select error!", e);
//                            }


                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                                Log.e(TAG, "비트맵으로 가져옴 사진" + bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                image = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // 이미지 간단히 줄이기

                            int width = 1000; // 축소시킬 너비
                            int height = 1778; // 축소시킬 높이
                            float bmpWidth = image.getWidth();
                            float bmpHeight = image.getHeight();

                            if (bmpWidth > width) {
                                // 원하는 너비보다 클 경우의 설정
                                float mWidth = bmpWidth / 100;
                                float scale = width / mWidth;
                                bmpWidth *= (scale / 100);
                                bmpHeight *= (scale / 100);
                            } else if (bmpHeight > height) {
                                // 원하는 높이보다 클 경우의 설정
                                float mHeight = bmpHeight / 100;
                                float scale = height / mHeight;
                                bmpWidth *= (scale / 100);
                                bmpHeight *= (scale / 100);
                            }

                            Bitmap resizedBmp = Bitmap.createScaledBitmap(image, (int) bmpWidth, (int) bmpHeight, true);

                            // 이미지업로드 -> 안에 올리기 가져오기 + 레트로핏조회
                            UploadImage(rotateImage(resizedBmp, 90));


                        }
                    }
                } else if (uri != null) {
//                    chatImg_1.setImageURI(uri);
                }

            }
        }
    }


    // 키보드 내리기
    private void keyboardHidden() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    // TODO 메시지 업데이트 쓰레드
    // 메시지 업데이트 쓰레드 ??++ 여기에 이미지는? 어떻게 넣죠?
    class msgUpdate implements Runnable {
        private String msg;

        public msgUpdate(String str) {
            this.msg = str;
        }

        @Override
        public void run() {

            Chat chat = new Chat();

            Log.e(TAG, "msgUpdate - UI 스레드로 실행");
            // 유저명 + 대화내용+ 시간을 받아와서, 나와 대화상대를 구분하고 뷰홀더에 값을 넣는다.

            try {
                SharedPreferences prefsName3 = getSharedPreferences("NAME", MODE_PRIVATE);
                String myID2 = prefsName3.getString("userID", "userID"); // 내 아이디


                // 메시지가 입력되면, 제이슨값을 받아온다.
                JSONObject jsonObject = new JSONObject(msg);
                Log.e(TAG, "msgUpdate" + jsonObject);

                // 받은값에서 내아이디와, 이미지여부, 메시지를 뽑아낸다.
                String myID = jsonObject.getString("myID");// 내아이디
                String type = jsonObject.getString("imgType");// 이미지구분자
                String finalMessage = jsonObject.getString("message"); // 메시지

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
//                        Log.e(TAG, "상대방메시지  /" + isRoom);

                    }

                }
                // 상대방이름
                chat.youName_text_img = friendID;
                chat.youName = friendID;

                // 시간값
                chat.myChatTime = jsonObject.getString("chatTime");
                chat.youChatTime = jsonObject.getString("chatTime");
                chat.myChatTimeImg = jsonObject.getString("chatTime");
                chat.youChatTime = jsonObject.getString("chatTime");
                chat.youChatTime_Img = jsonObject.getString("chatTime");

//                chat.chatImg_1 = jsonObject.getString("img1");
//                chat.chatImg_2 = jsonObject.getString("img2");
//                chat.chatImg_3 = jsonObject.getString("img3");

                // 방번호를 chat 클래스에 정보를 추가한다. // 읽음 처리 추가
                chat.roomidx = jsonObject.getString("roomIdx");
                chat.read = jsonObject.getString("read");


                Log.e(TAG, "chat.read 값  /" + chat.read);
                Log.e(TAG, "msg업데이트 chat isMe : " + chat.isMe);
                Log.e(TAG, "msg업데이트 chatRoom2Activity : " + !chat.isMe + "");
//

            } catch (JSONException e) {
                e.printStackTrace();
            }

            mChatDataList.add(chat);
            mAdapter.setItem(mChatDataList);
            Log.e(TAG, " mChatDataList에 메시지 갱신");
            mAdapter.notifyDataSetChanged();
            Log.e(TAG, "notifyDataSetChanged");
            // 스크롤 마지막 채팅으로 이동해있기
            mMessageListVIew.setSelection(mAdapter.getCount() - 1);

            mChatDataList.clear();
            selectChat(myID, friendID, roomIdx); // 재조회를 해야 이미지가 사라지지 않았음
        }
    }


    // 현재 시간을 "yyyy-MM-dd hh:mm:ss"로 표시하는 메소드
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String getTime = dateFormat.format(date);
        return getTime;
    }

    // TODO 레트로핏 인터페이스로 서버에서 가져오는 부분
    private void selectChat(String myID, String friendID, String roomIdx) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<cookChatInfo> call = apiInterface.selectChat(myID, friendID, roomIdx);

        call.enqueue(new Callback<cookChatInfo>() {
            @Override
            public void onResponse(@NotNull Call<cookChatInfo> call, @NotNull Response<cookChatInfo> response) {

                cookChatInfo cookChatInfo = response.body();
                if (response.isSuccessful() && response.body() != null) {


                    if (cookChatInfo.isResultCode()) {
                        for (chatComment each : cookChatInfo.getChatComment()) {

                            // 현재 액티비티에서 방번호
                            // 레트로핏 대화 정보에서 방번호를 확인하여 동일할때만 List 정보에 추가한다.

                            // 제이슨 오브젝트 값 받아오기
                            Chat chat = new Chat();
                            chat.isMe = myID.equals(each.getMyID()) ? true : false; // 로그인한아이다와 서버에서 가져온 MYID가 같으면,


                            String type = each.getImgType();
                            if (chat.isMe) {
                                // 나일때 (내가 이미지 보냄)
                                if (type.equals("image")) {
                                    chat.chatImg_1 = each.getFinalMessage();
//                                    Log.e(TAG, " 내가 보낸 이미지 내용 /" +  chat.chatImg_1);
                                } else {
                                    // 가져온값이 채팅이면 -> 지금 채팅 글로 들어가 버림
                                    chat.message = each.getFinalMessage();// 메시지내용
//                                    Log.e(TAG, " 내가 보낸 텍스트 내용 /" +  chat.message);
                                }

                            } else {

                                // 상대방일때 (상대방이 이미지 보냄)
                                chat.youName_text_img = friendID; // 상대방이름
                                if (type.equals("image")) {
                                    chat.chatImg_1_you = each.getFinalMessage();

//                                    Log.e(TAG, " 친구가보낸 이미지 내용 /" +   chat.chatImg_1_you);
                                } else {
                                    // 가져온값이 채팅이면 -> 지금 채팅 글로 들어가 버림
                                    chat.message = each.getFinalMessage();// 메시지내용
//                                    Log.e(TAG, " 친구가보낸 채팅내용 /" +  chat.message);
                                    isRoom = true; // 상대방이 방에 있다.
//                                    Log.e(TAG, "상대방메시지  /" + isRoom);
                                }

                            }

                            // TODO 나와 대화상대 구분하기// 친구저장된값 넣기, 서버에서 가져온값 X

                            chat.youName = friendID; // 상대방아이디

                            chat.iv_profile_small = each.getPostImg(); // 상대방프사
                            // 날짜 가져와서 ~시~분으로 보이게 하기 /오전 오후 표시
                            String str = each.getCreated();// 시간
//                            if(str != null && str.length() >16){
                            String result = str.substring(10, 16); // 문자열통으로가져와 앞뒤 자름
                            chat.myChatTime = result;// 내가 보내는 시간
                            chat.youChatTime = result;// 텍스트만 보낼때 상대방시간
                            chat.youChatTime_Img = result; // 이미지보낼때 붙는 상대방시간
                            chat.myChatTimeImg = result;// 이미지보낼때 붙는 내 시간

                            chat.roomidx = each.getRoomIdx();
//                            Log.e(TAG, "채팅방이름  /" + chat.idx);
                            chat.idx = each.getIdx();
//                            Log.e(TAG, "채팅마지막글번호  /" + chat.idx);

                            mChatDataList.add(chat); // 데이터 리스트에 값을 더함. // 안더하면 사라짐
//                            }

                            // 상대방프사 저장
                            String frImg = each.getPostImg();
                            SharedPreferences pref2 = getSharedPreferences("FRIENDIMAGE", MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref2.edit();
                            editor.putString("frImg", frImg); //키값, 저장값
                            editor.apply();

                        }// foreach문 끝
//

                        // TODO 나와 상대 구분해 알림보내기
//                        ArrayList<Chat> mChatDataList = new ArrayList<>();
                        Chat lastChat = mChatDataList.get(mChatDataList.size() - 1);


//                        listRoomIdx
                        String[] actRoomUserId = listRoomIdx.split("&"); // listRoomIdx에서 가져온값 jun&hoho 라면 &앞에 있는 아이디 actRoomUserId 에 저장
                        String[] roomUserId = lastChat.roomidx.split("&");// roomidx에서 가져온값 &를 기준으로 앞의 ID값 저장

                        Log.e(TAG, " actRoomUserId / 리스트에서 가져온방이름" + actRoomUserId);
                        Log.e(TAG, " roomUserId / 레트로 방이름 " + roomUserId);

                        int userCnt = 0;
                        for (String actUserId : actRoomUserId) { // actUserId와 listRoomIdx에서 가져온 앞의 ID값 비교
                            for (String userId : roomUserId) { // userID와 roomidx에서 가져온 앞의 ID값을 비교한다.
                                if (actUserId.equals(userId)) {  // actUserId와 userId가 같으면, userCnt를 1씩 증가시킨다. // 둘다 일치하면 숫자가 2 == 방번호가 일치한다는것을 의미
                                    userCnt++; // 카운트를 1씩 더한다.
                                }
                            }
                        }

                        Log.e("lee", "chat isMe : " + mChatDataList.get(mChatDataList.size() - 1).isMe);
                        Log.e("lee", "chatRoom2Activity : " + !mChatDataList.get(mChatDataList.size() - 1).isMe);


                        Log.e(TAG, " onStartService()울리기전 /" + count);

                        // 내가 아니고, userCnt가 2일때만, 알림을 보낸다.  내가 방에 없을때는 -1값이 저장됨
                        if (!mChatDataList.get(mChatDataList.size() - 1).isMe && userCnt == 2 && roomOK == -1) {

                            // TODO 알림이 울릴때마다 1씩 증가시켜서 저장한다.=========================================================================================================================

                            onStartService(); // 서비스 호출(알림일 들어있음)

                            // Todo 다른액티비티 실시간새로고침 - 알림이 올때 chatListActivity 새로고침 ********
//                            ((chatListActivity) chatListActivity.CONTEXT).onResume();
//                            count = 0; // 서비스 울리고 나서 카운트값 0으로 초기화
                        }


                        mAdapter.setItem(mChatDataList);
                        mAdapter.notifyDataSetChanged();
                        mMessageListVIew.setAdapter(mAdapter); // 어답터 다시 세팅
                        mMessageListVIew.setSelection(mAdapter.getCount() - 1);


                    } else {
                        assert response.body() != null;
                        Toast.makeText(chatRoom2Activity.this, "null", Toast.LENGTH_SHORT).show();
                        finish();

                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<cookChatInfo> call, @NonNull Throwable t) {

                Toast.makeText(chatRoom2Activity.this, t.getLocalizedMessage() + "문제있어", Toast.LENGTH_SHORT).show();

            }
        });
    }


    // mChatDataList 초기화 함수
    public void clearAllItems() {
        mChatDataList.clear();
    }


    // TODO 볼리로 - 채팅시 갤러리로 가져온 이미지 보내기(이미지이름(name+image),내이름,친구이름,방이름,이미지타입을 서버에 전송하고 가져오기
    private void UploadImage(Bitmap reuduceBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        reuduceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        String image = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        String name = String.valueOf(Calendar.getInstance().getTimeInMillis());

        String volley = null;
        // 보내는 부분
        try {
            jsonObject = new JSONObject();
            jsonObject.put("name", name); // 이미지이름
            jsonObject.put("image", image);// 이미지 이름
            jsonObject.put("myID", myID); // 내이름
            jsonObject.put("roomIdx", volley);// 방이름
//            jsonObject.put("roomIdx", myID + "&" + friendID);
            jsonObject.put("friendID", friendID); // 친구이름
            jsonObject.put("imgType", "image"); // 타입 -이미지


            jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_CHAT_IMG_INSERT, jsonObject,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // 볼리받아오는 부분
                                String imgType = response.getString("imgType");
                                String finalMessage = response.getString("finalMessage");
//                                String getMyID = response.getString("myID");
//                                String getFriendID = response.getString("friendID");


                                // 소켓서버에 이미지를 받았다.
                                // TODO 소켓으로 이미지 이름값을 B에게 던져준다.
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        try { // 입력한 채팅내용 출력

                                            // json으로 메시지 보내는 부분
                                            JSONObject jsonObject = new JSONObject();
                                            // 상대방프사를 눌러 입장 후 친구아이디가 있을때만 메시지를 보낸다. // json에 채팅 내용을 담아서 printWrite로 보냄


                                            if (friendID != null) {
                                                Log.e(TAG, " 대화상대  /" + friendID);
                                            jsonObject.put("myID", myID);
                                            jsonObject.put("message", finalMessage);// edittext에 작성한내용
                                            jsonObject.put("imgType", imgType);
                                            Log.e(TAG, "소켓이미지전송  /" + imgType);
                                            // 누구의 시간인가? 어떻게 구분할까? ->시간은 구분필요없음
                                            jsonObject.put("chatTime", getTime()); // 오후시간대로 표기하게 하기
                                            // 방번호를 어떻게 넣을까? 내이름+친구이름으로 방생성
                                            jsonObject.put("roomIdx", myID + "&" + friendID);
                                            jsonObject.put("friendID", friendID);
                                            jsonObject.put("list", myID + "&" + sendmsg + "&" + friendID);


                                                if (sendMessage != null) {
                                                    sendMessage.println(jsonObject.toString()); // 왜 안감?????
                                                    Log.e(TAG, "보내는 채팅내용 /" + sendMessage);
                                                    sendMessage.flush(); // 자바서버로 보내기
                                                }

                                                message.setText(""); // 메시지 보내고 채팅창 비우기
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }.start(); // 발신쓰레드 시작


//                                        // TODO 핸들러
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        // 스크롤 마지막 채팅으로 이동해있기
                                        mMessageListVIew.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

                                        clearAllItems();//볼리로 업로드하고,조회후싹다 지우고, 다시 가져옴
                                        String roomIdx = myID + "&" + friendID;
                                        selectChat(myID, friendID, roomIdx);// 레트로핏 재조회 // TODO 조회는 하는게 맞음 0
                                    }
                                });// 핸들러끝


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(chatRoom2Activity.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }


            );
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        resultThread();//////////////
        Log.e(TAG, " 볼리이미지업로드후  /" + resultThread());
        // 이미지 업로드 볼리

        jsonObjectRequest.setShouldCache(false); // 매번새로요청
        // 공용 큐
        App.getInstance().getRequestQueue().add(jsonObjectRequest);
    }


    // TODO 레트로핏 마지막 채팅번호 업로드
    private void chatCount(String roomIdx, String finalChatNum) {

        // API 공통꺼 사용중
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        // myID,finalChatNum을 보내고, ChatCount로 값을 받아온다.
        Call<ChatCount> call = apiInterface.chatCount(roomIdx, finalChatNum);

        call.enqueue(new Callback<ChatCount>() {
            @Override
            public void onResponse(@NotNull Call<ChatCount> call, @NotNull Response<ChatCount> response) {


                if (response.isSuccessful() && response.body() != null) {
//                    Toast.makeText(chatRoom2Activity.this, "연결성공", Toast.LENGTH_SHORT).show();

                } else {
                    assert response.body() != null;
                    Toast.makeText(chatRoom2Activity.this, "null", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(Call<ChatCount> call, Throwable t) {
                Toast.makeText(chatRoom2Activity.this, "fail", Toast.LENGTH_SHORT).show();

            }

        });
    }// 래트로핏끝
}



