package com.example.recordcalls;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class TService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TService", "onCreate");
        if (Build.VERSION.SDK_INT >= 26) {
            String channel_ID = "recordcalls_102";
            NotificationChannel channel = new NotificationChannel(channel_ID, "RecordCalls Application", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this, channel_ID)
                    .setContentTitle("RecordCalls App")
                    .setContentText("Record phone calls")
                    .build();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(102, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(new CallReceiver(), new IntentFilter());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
