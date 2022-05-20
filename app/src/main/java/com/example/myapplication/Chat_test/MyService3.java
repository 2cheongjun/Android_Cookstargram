package com.example.myapplication.Chat_test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.Chat_test.ChatList.ChatDate;
import com.example.myapplication.Chat_test.ChatList.chatListActivity;
import com.example.myapplication.Chat_test.ChatList.chatListPresenter;
import com.example.myapplication.R;
import com.example.myapplication.mypage.RetrofitServer.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

// 백그라운드 서비스
public class MyService3 extends Service {
    private static final String TAG = MyService3.class.getSimpleName();

    private Thread mThread;
    private int mCount = 0;

    private static final String CHANNEL_ID = "99";
    private static final int NOTIFICATION_ID = 1;

    String messege = null;
    chatListPresenter presenter;
    private chatListActivity view;

    public MyService3() {
    }


    // 서비스를 실행하는 메소드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 인텐트로 startForeground 액션을 받았다면, ~ 한다.
        if ("startService".equals(intent.getAction())) {
            Log.e(TAG, " onStartCommand  /" + intent.getAction() + "MyService3 서비스시작");

            // 받는 액티비티  - 인텐트는 oncCreate안에 넣기

            String myID = intent.getStringExtra("MYID");
            String friendID = intent.getStringExtra("FRIENDID");
            String listRoomID = intent.getStringExtra("LISTROOMID");
            String finalChat = intent.getStringExtra("finalChat");

            Log.e(TAG, "onStartCommand 인텐트 /" + myID + friendID + listRoomID);

            // 포그라운드 서비스 시작
            startService(myID, friendID, listRoomID,finalChat); // 가져온 내아이디와+친구아이디 건네주기

        }
        return START_NOT_STICKY;// 서비스가 강제종료시 재시작하게 해준다.
    }

    private void startService(String myID, String friendID, String listRoomID, String finalChat) {
        // default 채널 ID로 알림 생성

        Intent notificationIntent = new Intent(this, chatRoom3Activity.class);
        notificationIntent.putExtra("MYID", myID); // 내아이디
        notificationIntent.putExtra("FRIENDID", friendID); //친구아이디
        notificationIntent.putExtra("LISTROOMID", listRoomID); // 방번호
        notificationIntent.putExtra("finalChat", finalChat); // 마지막 글내용


        // 알림을 클릭했을때 무엇을 수행할 것인가?
        // 친구아이디 받아옴
        Log.e(TAG, " notificationIntent /" + myID + friendID + listRoomID);

        // 노티빌더 설정 빌더의 역할: Notification을 생성해준다.
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//위에 하나만있게 설정
//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//위에 하나만있게 설정
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);// noti빌더를 생성한다.
        builder.setSmallIcon(R.mipmap.ic_launcher); // 아이콘 설정
        builder.setContentTitle(friendID + "님이 보낸 메시지"); // 알림 제목 // 보낸사람이름
        builder.setContentText(finalChat); // 친구이름, 채팅내용?????
        Log.e(TAG, " notificationIntent 전달내용 /" + myID + friendID + listRoomID + finalChat);
        builder.setAutoCancel(true); //notification을 탭 했을경우 notification을 없앤다.
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // 오레오에서는 noti매니저와 알림 채널을 매니저에 생성해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // noti매니저 설정
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 채널 설정, 상단에 정해준 채널아이디를 써준다. // high라고 하니까 알림창이 따로뜬다. 디폴트라고 하니까 안떴음
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "오늘은 뭐먹지 채널", manager.IMPORTANCE_HIGH);// 노티형태
            notificationChannel.enableLights(true);// 라이트표시설
            notificationChannel.enableVibration(true); // 진동설정

            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            manager.createNotificationChannel(notificationChannel);
        }

        // noti를 포그라운드로 시작, 0이면 안됨
        startForeground(NOTIFICATION_ID, builder.build());

    }


    // 포그라운드 서비스는 노티피케이션이 반드시 필요하다.
    // 노티를 사용하려면 채널이 필요하다.
    // 상대방이 메시지를 안보고 있을때 마지막 메시지와, 채팅방에 있는 대화상대를 받아와서, 알림창에 띄워준다.


    // 서비스 종료시 호출
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");

        // stopService 에 의해 호출 됨
        // 스레드를 정지시킴

        super.onDestroy();
    }

    // MyService의 레퍼런스를 반환하는 Binder 객체 / Myservice와 액티비와 액티비티를 연결한다.
    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public MyService3 getService() {
            return MyService3.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    // 바인드된 컴포넌트에 카운팅 변수 값을 제공
    public int getCount() {
        return mCount;
    }


}