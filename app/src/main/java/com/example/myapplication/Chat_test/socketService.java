package com.example.myapplication.Chat_test;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


// 백그라운드 서비스 실행 // 사용중임
public class socketService extends Service {
    private static final String TAG = "socketService" + "백그라운드 서비스 실행";

    InetAddress serverAddr;
    Socket socket;

    private String ip = "000.000.0.00"; // 우리집
    private final int port = 6016;

    public socketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스는 한번 실행되면 계속 실행된 상태로 있는다.
        // 따라서 서비스 특성상 intent를 받아서 처리하기에 적합하지않다.
        // intent에 대한 처리는 onStartCommand()에서 처리해준다.
    }

    @SuppressLint("LongLogTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "onStartCommand() called" + "소켓서비스 실행");

        if (intent == null) {
            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
        } else {
            // intent가 null이 아니다.
            // 액티비티에서 intent를 통해 전달한 내용을 뽑아낸다.(if exists)
            new Thread() {
                public void run() {

                    Log.e(TAG, "socket 연결");
                    try {
                        InetAddress serverAddr = InetAddress.getByName(ip);
                        socket = new Socket(serverAddr, port);

                        Log.e(TAG, "소켓값 /" +  socket);

                        // 수신쓰레드 시작
                        ReceiveThread receiveThread = new ReceiveThread();
                        receiveThread.start();

                        Log.e(TAG, "receiveThread 소캣서비스 시작 /" + receiveThread );


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start(); // 수신 쓰레드 시작


            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");
            // etc..

            Log.d(TAG, "전달받은 데이터: " + command + ", " + name);
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy() called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented"); //자동으로 작성되는 코드
    }
}