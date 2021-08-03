package com.example.notification_create;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class create extends Service {
    final String TAG=create.class.getSimpleName();
    Thread thread;

    public create() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override


    public void onCreate(){
        Log.d(TAG,"oncreate");
        emm();
    }
    public void emm() {
        Log.d("emm","调用");
        thread = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted())
                try {
                    Socket socket=null;
                    ServerSocket serverSocket = new ServerSocket(8886);
                    Log.d("emm","等待连接");
                    socket=serverSocket.accept();
                    if(Thread.currentThread().isInterrupted()){
                        Log.d("emm","退出");
                        socket.close();
                        serverSocket.close();
                        break;
                    }
                    DataInputStream in=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    byte[] bdata = new byte[100];
                    in.read(bdata);
                    in.close();
                    socket.close();
                    serverSocket.close();
                    String[] data=new String(bdata, StandardCharsets.UTF_8).split("&");
                    for(String a:data){
                        Log.d("emmm",a);
                    }
                    send(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
        thread.start();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        thread.interrupt();
    }

    public void send(String[] data) {
        int channel = Integer.parseInt(data[0]);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "emm")
                .setSmallIcon(R.drawable.makefg)
                .setContentTitle(data[1])
                .setContentText(data[2])
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        Log.d("emm","channel:"+data[0]);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(channel, builder.build());
    }

}