package com.example.myapplication.Chat_test;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class RemoteService extends Service {
    private final String TAG = "RemoteService";
    public static final int MSG_CLIENT_CONNECT = 1;
    public static final int MSG_CLIENT_DISCONNECT = 2;
    public static final int MSG_ADD_VALUE = 3;
    public static final int MSG_ADDED_VALUE = 4;

    InetAddress serverAddr;
    Socket socket;
    PrintWriter sendMessage;
    String read;
    private Handler mHandler;
    private String ip = "000.000.0.00"; // 우리집

    private final int port = 6016;

    private ArrayList<Messenger> mClientCallbacks = new ArrayList<Messenger>();
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    int mValue = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    // 수신쓰레드
    public void receive(String send2){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(send2.equals(null)){
                    for(int i = mClientCallbacks.size()-1; i >=0; i--){
                        try{

                            InetAddress serverAddr = InetAddress.getByName(ip);
                            socket = new Socket(serverAddr, port);

                            sendMessage = new PrintWriter(socket.getOutputStream());

                            // 데이터 읽거나 쓸수있다.
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            // 쓰레드를 생성하고 while문을 걸어줘야한다. 서버로 부터 수신된 메시지
                            while (true) {

                                read = in.readLine();

                                Log.e(TAG, "read" + read);

                            }


                        }catch (Exception e){

                        }
                    }

                }
            }
        }).start();
    }


// 클라이언트에서 들어오는 메시지의 처리
    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage( Message msg ){
            switch( msg.what ){

                case MSG_CLIENT_CONNECT:
                    Log.d(TAG, "Received MSG_CLIENT_CONNECT message from client");
                    mClientCallbacks.add(msg.replyTo);
                    break;
                case MSG_CLIENT_DISCONNECT:
                    Log.d(TAG, "Received MSG_CLIENT_DISCONNECT message from client");
                    mClientCallbacks.remove(msg.replyTo);
                    break;
                    // 메시지 값에 따라
//                case MSG_ADD_VALUE:
//                    Bundle bundle = msg.getData();
//                    String response = bundle.getString("response");
//                    Log.d(TAG, "response : " + response);
//                    break;

                case MSG_ADD_VALUE:
                    Bundle bundle = msg.getData();
                    String send = bundle.getString("send");
                    Log.d(TAG, "response : " + bundle.getString("send"));
                    receive(send);
                    break;
                default:
                    super.handleMessage(msg);
                        }
                    }


            }
        }
