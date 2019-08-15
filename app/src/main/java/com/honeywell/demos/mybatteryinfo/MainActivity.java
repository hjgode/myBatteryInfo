package com.honeywell.demos.mybatteryinfo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.KeyEventDispatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    Context context=this;
    BatteryLevelReceiver receiver;
    String TAG="mybatteryinfo";
    ComponentName myservice=null;
    TextView textView;

    static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION=11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.textView);

        receiver = new BatteryLevelReceiver();
        registerReceiver(receiver, new IntentFilter(myBattery.GET_BATTERY_INFO_MESSAGE));  //<----Register

        myservice = startService();

        // Check whether this app has write external storage permission or not.
        int writeExternalStoragePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // If do not grant write external storage permission.
        if(writeExternalStoragePermission!= PackageManager.PERMISSION_GRANTED)
        {
            // Request user to grant write external storage permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }

    }

    ComponentName startService(){
        if(myservice!=null)
            return myservice;
        // Start android service.
        Intent startServiceIntent = new Intent(context, MyBatteryInfoService.class);
        myservice = startService(startServiceIntent);
        //startForegroundService(startServiceIntent);
        return myservice;
    }
    void stopService(){
        // Stop android service.
        Intent stopServiceIntent = new Intent(context, MyBatteryInfoService.class);
        if(stopService(stopServiceIntent))
            myservice=null;
    }
    class BatteryLevelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(myBattery.GET_BATTERY_INFO_MESSAGE))
            {
                Bundle batteryinfo = intent.getExtras();
                Log.i(TAG,"received bundle: " + batteryinfo.toString());
                textView.setText(myBattery.dumpBatteryInfo(batteryinfo));
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            int grantResultsLength = grantResults.length;
            if (grantResultsLength > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "You grant write external storage permission. Please click original button again to continue.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "You denied write external storage permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class BootReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Log.i(TAG, "BootReceiver called");
            startService();
        }
    }
}
