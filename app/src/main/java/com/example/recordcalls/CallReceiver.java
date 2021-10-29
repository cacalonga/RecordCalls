package com.example.recordcalls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

public class CallReceiver extends BroadcastReceiver {
    private AtomicBoolean working = new AtomicBoolean(true);
    public static boolean recording = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (!am.isSpeakerphoneOn())
                am.setSpeakerphoneOn(true);
            int sb2value = am.getStreamMaxVolume(am.STREAM_VOICE_CALL);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, sb2value, 0);
            Log.e("PHONESTATE", "answer");
            recording = true;
            Log.d("recording", "value: " + recording);
            new Thread(runnable).start();
        } else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Log.e("PHONESTATE", "close");
            recording = false;
            Log.d("recording", "value: " + recording);
        }
    }

    private Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LocalDateTime dt = LocalDateTime.now();
            String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + dt.toString()
                    .replace(":", "_").replace(".", "_") + ".amr";
            MediaRecorder recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                recorder.prepare();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
            Log.d("recorder", "start");
            while (working.get()) {
                if (recording) {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("recording", "value: " + recording);
                } else {
                    recorder.stop();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("recorder", "stop");
                    working.set(false);
                }
            }
        }
    };
}
