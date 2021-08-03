package com.example.notification_create;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    final String CHANNEL_ID = "emm";
    boolean isServiceActivated =false;
    TextView tv1;
    TextView tv2;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        tv1 = findViewById(R.id.TV1);
        tv2 = findViewById(R.id.TV2);
        new Thread(() -> {
            tv2.setText("接收端ip:"+getLocalIpAddress());
        }).start();
    }
    @SuppressLint("DefaultLocale")
    public String getLocalIpAddress() {

            WifiManager wifiManager = (WifiManager) getSystemService(android.content.Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            try {
                return InetAddress.getByName(String.format("%d.%d.%d.%d",
                        (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                        (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff))).toString().split("/")[1];
            } catch (UnknownHostException e) {

                e.printStackTrace();
            }
            return null;

    }
    public void startService(View view){
        if(!isServiceActivated) {
            startService(new Intent(MainActivity.this, create.class));
            isServiceActivated = true;
            Log.d("emm","服务开启");
            tv1.setText("服务状态：开启");
        }
        else{
            Log.d("emm","服务已开启");
        }
    }
    public void stopService(View view){
        if(isServiceActivated) {
            Log.d("emm", "服务关闭");
            stopService(new Intent(MainActivity.this, create.class));
            isServiceActivated=false;
            tv1.setText("服务状态：关闭");
        }
        else{
            Log.d("emm","服务未开启");
        }
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "emm";
            String description = "test";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancelAll();
    }
}