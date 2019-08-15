package com.honeywell.demos.mybatteryinfo;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyBatteryInfoService extends Service {

    public static final int notify = 60000;  //interval between two services(Here Service run every 1 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling
    Context mContext = this;
    myBattery _battery;
    String TAG="MyBatteryInfoService";

    //sticky service?
    //add to Manifest android:process=":processname"

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show();
        Log.i(TAG,"Background service onCreate method.");
        super.onCreate();

        _battery=new myBattery(mContext);
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG).show();
        Log.i(TAG,"Background service onStartCommand method.");
        //return super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Background service onStartCommand method.");
        mTimer.cancel();    //For Cancel Timer
        Log.d("service is ","Destroyed");
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "sending Battery info broadcast");
                    Bundle batteryinfo = _battery.getBatteryInfo();
                    sendDataToActivity(batteryinfo);
                    writeData(batteryinfo);
                }
            });
        }
    }
    private void sendDataToActivity(Bundle bundle)
    {
        Intent sendLevel = new Intent();
        sendLevel.setAction(myBattery.GET_BATTERY_INFO_MESSAGE);
        sendLevel.putExtras( bundle);
        sendBroadcast(sendLevel);

    }

    void writeData(Bundle bundle) {
        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(MyBatteryInfoService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
            return;

        // Get public external storage folder ( /storage/emulated/0 ).
        File externalDir = Environment.getExternalStorageDirectory();

        // Get /storage/emulated/0/Music folder.
        File downloadsPublicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File newFile = new File(downloadsPublicDir, "batteryinfo.txt");
        try {
            FileWriter fw = new FileWriter(newFile);
            String date=new Date().toString();
            fw.write(date + "\n");
            fw.write(myBattery.dumpBatteryInfo(bundle));
            fw.flush();
            fw.close();
            updateMTP(newFile);
        }catch(IOException ex){

        }
    }
    void updateMTP(File _f){
        Log.d(TAG, "sending Boradcast about file change to MTP...");
        //make the file visible for PC USB attached MTP
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(_f));
        mContext.sendBroadcast(intent);

    }
}
