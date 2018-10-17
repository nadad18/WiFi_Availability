package com.harris.mobihoc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{
    mobiHocService benchmarkService;
    public WifiManager wm;
    private ConnectivityManager cm;
    public boolean lightMode = false;
    public static final String TAG = "TAG2";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "ZZZZ: " + "ZZZZ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, mobiHocService.class);
                switch (view.getId()) {

                    case R.id.start:
                        benchmarkService.isRunning = true;
                        //starts service for the given Intent

                        benchmarkService.onCreate();
                        benchmarkService.getWifiNetworks( 1);
                        startService(intent);


                        Log.e(TAG, "mobihoc: " + "startService");
                        Toast.makeText(MainActivity.this,"Scanning Started.", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };
        findViewById(R.id.start).setOnClickListener(listener);

        }

        @Override
    public void onDestroy(){
        super.onDestroy();
        unBindServce();
        if(benchmarkService != null){
            benchmarkService.stop();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        startService(new Intent(MainActivity.this, mobiHocService.class));
        doBindService();

    }

        //super.onResume();
        //void doBindService(){

    //}
    @Override
    public void onPause(){
        super.onPause();
    }

    void unBindServce(){

        stopService(new Intent(MainActivity.this, mobiHocService.class));
        unbindService(ServiceConnector);

    }

    void doBindService(){

        bindService(new Intent(MainActivity.this, mobiHocService.class), ServiceConnector, Context.BIND_AUTO_CREATE);

    }

    ServiceConnection ServiceConnector = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder){
            Log.d(TAG, "ZZZZ: " + "Go to Service");
            benchmarkService = ((mobiHocService.LocalBinder) binder).getService();
            benchmarkService.setMainActivity(MainActivity.this);
            benchmarkService.turnOn();
        }

        @Override
        public void onServiceDisconnected(ComponentName className){

            }

    };
}
