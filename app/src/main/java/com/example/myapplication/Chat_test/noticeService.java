package com.example.myapplication.Chat_test;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;

// 서비스
public class noticeService extends Service {
    private static final String TAG = noticeService.class.getSimpleName();

    private Thread mThread;
    private int mCount = 0;

    String friendID = null;
    String roomIdx = null;
    private static final String CHANNEL_ID = "99";
    private static final int NOTIFICATION_ID = 1;

    public noticeService() {
    }


    // 서비스를 실행하는 메소드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 인텐트로 startForeground 액션을 받았다면, ~ 한다.
        if ("startForeground".equals(intent.getAction())) {
            // 포그라운드 서비스 시작
            roomIdx = intent.getStringExtra("roomIdx"); // 못가져옴

            Log.e(TAG, " onStartCommand  /" + intent.getAction() + friendID +"채팅방에서 가져온 친구이름");

            startForegroundService();

        } else if (mThread == null) { // 일반 서비스를 시작한다.
            // 스레드 초기화 및 시작
            mThread = new Thread("My Thread") {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        try {
                            mCount++;
                            // 1초 마다 쉬기
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // 스레드에 인터럽트가 걸리면
                            // 오래 걸리는 처리 종료
                            break;
                        }
                        // 1초 마다 로그 남기기
                        Log.d("My Service", "서비스 동작 중 " + mCount);
                    }
                }
            };
            mThread.start();

        }
        return START_NOT_STICKY;// 서비스가 강제종료시 재시작하게 해준다.
    }

    // 서비스 종료시 호출
    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");

        // stopService 에 의해 호출 됨
        // 스레드를 정지시킴
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }

        super.onDestroy();
    }

    // MyService의 레퍼런스를 반환하는 Binder 객체 / Myservice와 액티비와 액티비티를 연결한다.
    private IBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public noticeService getService() {
            return noticeService.this;
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

    // 포그라운드 서비스는 노티피케이션이 반드시 필요하다.
    // 노티를 사용하려면 채널이 필요하다.
    // 상대방이 메시지를 안보고 있을때 마지막 메시지와, 채팅방에 있는 대화상대를 받아와서, 알림창에 띄워준다.
    private void startForegroundService() {
        // default 채널 ID로 알림 생성

        Intent notificationIntent = new Intent(this, chatRoom2Activity.class);
        // 알림을 클릭했을때 무엇을 수행할 것인가?
        // 친구아이디 받아옴

        // 노티빌더 설정 빌더의 역할: Notification을 생성해준다.
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//위에 하나만있게 설정
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);// noti빌더를 생성한다.
        builder.setSmallIcon(R.mipmap.ic_launcher); // 아이콘 설정
        builder.setContentTitle("오늘은 뭐먹지 "); // 알림 제목 // 보낸사람이름
        builder.setContentText("메시지가 왔습니다."); // 친구이름, 채팅내용?????
        builder.setAutoCancel(true); //notification을 탭 했을경우 notification을 없앤다.
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        // 오레오에서는 noti매니저와 알림 채널을 매니저에 생성해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // noti매니저 설정
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // 채널 설정, 상단에 정해준 채널아이디를 써준다. // high라고 하니까 알림창이 따로뜬다. 디폴트라고 하니까 안떴음
//            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"오늘은 뭐먹지 채널",manager.IMPORTANCE_HIGH);// 노티형태
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"오늘은 뭐먹지 채널",manager.IMPORTANCE_HIGH);// 노티형태
            notificationChannel.enableLights(true);// 라이트표시설
//            notificationChannel.enableVibration(false); // 진동설정
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            manager.createNotificationChannel(notificationChannel);
        }

        // noti를 포그라운드로 시작, 0이면 안됨
        startForeground(NOTIFICATION_ID, builder.build());
    }
}