package com.harris.mobihoc;

import android.Manifest;
import android.content.pm.PackageManager;


import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.InputStream;
import java.net.URLConnection;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;

import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiInfo;
import android.os.Binder;
import android.os.IBinder;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.os.Environment.DIRECTORY_DCIM;

public class mobiHocService extends Service {

    public static final String TAG = "TAG2";

    boolean isRunning  = true;
    private MainActivity activity;
    private IBinder localBinder = new LocalBinder();
    //private boolean running = false;
    int bandwidth;
    String Mode;
    String utilization;
    LocationManager lm;
    //// Create Files to write the data //////////////////

    File folder = Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM);
    File Wi_Fi = new File(folder, "All_data.txt");
    File wifi_scan = new File(folder, "Individual_AP_Tributes.txt");
    File level_GPS = new File(folder, "Extra_Information.txt");

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override

    public void onCreate()
    {
        isRunning = true;
        Toast.makeText(this, "Scanning Started.", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "YYYYY: " + "OnCreate");
        initializeLocationManager();

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
        try {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (lm == null) {
            lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "MyService Completed or Stopped.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        activity = null;
        return false;
        }

        public void turnOn() {

        Thread go = new Thread(new trialThread());
        go.setPriority(Thread.MAX_PRIORITY);
        go.start();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    class trialThread implements Runnable {
        @Override
        public void run() {



            Log.d(TAG, "YYYYY: " + "Runnable");
            while (isRunning) {

                try {

                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

                    if (!hasPermissions(activity, PERMISSIONS)) {
                        ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
                    }

                    getWifiNetworks(1);
                     /// 1 minutes between scans /////////////////
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private void printInfo(File file, String Info, String trial) {
        BufferedWriter fos = null;
        try {
            fos = new BufferedWriter(new FileWriter(file, true));
            fos.write(Info);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getGPScoordinates ()

{
    /////////// Current Time //////////////////////////////

    long currentDateTime = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
    Date currentDate = new Date(currentDateTime);
    LocationManager lm = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
    Location GPS_GPS = lm.getLastKnownLocation(lm.GPS_PROVIDER);
    Location GPS_Network = lm.getLastKnownLocation(lm.NETWORK_PROVIDER);
    Location GPS_Passive = lm.getLastKnownLocation(lm.PASSIVE_PROVIDER);

    if ( GPS_GPS!= null)

    {
        double GPSLat_GPS = GPS_GPS.getLatitude();
        double GPSLong_GPS = GPS_GPS.getLongitude();
        String GPSLat_GGPS = Double.toString(GPSLat_GPS);
        String GPSLong_GGPS = Double.toString(GPSLong_GPS);
        printInfo(wifi_scan, sdf.format(currentDate) + ",", "");
        printInfo(wifi_scan, GPSLat_GGPS + ",", "");
        printInfo(wifi_scan, GPSLong_GGPS + ",", "");
    }

    else if (GPS_Network != null) {

        double GPSLat_Wifi = GPS_Network.getLatitude();
        double GPSLong_Wifi = GPS_Network.getLongitude();
        String GPSLat_WWifi = Double.toString(GPSLat_Wifi);
        String GPSLong_WWif = Double.toString(GPSLong_Wifi);

        printInfo(wifi_scan, sdf.format(currentDate) + ",", "");
        printInfo(wifi_scan, GPSLat_WWifi + ",", "");
        printInfo(wifi_scan, GPSLong_WWif + ",", "");

    } else if (GPS_Passive != null) {

        double GPSLat_Passive = GPS_Passive.getLatitude();
        double GPSLong_Passive = GPS_Passive.getLongitude();
        String GPSLat_PPassive = Double.toString(GPSLat_Passive);
        String GPSLong_PPassive = Double.toString(GPSLong_Passive);
        printInfo(wifi_scan, sdf.format(currentDate) + ",", "");
        printInfo(wifi_scan, GPSLat_PPassive + ",", "");
        printInfo(wifi_scan, GPSLong_PPassive + ",", "");
    }
    else {
        printInfo(wifi_scan, sdf.format(currentDate) + ",", "");
        printInfo(wifi_scan, "NO GPS" + ",", "");
        printInfo(wifi_scan, "NO GPS" + ",", "");
    }
}
    public void getWifiNetworks(int trial) {

        try {

            WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();

            int allbuffersize = 16*1024;



            ////////// Specify the file, download and upload links ////////////////

            String sourceFileUri = "test_upload_100K.txt";
            String urldownload = "http://adhocdata.tk/wp-content/uploads/2018/08/test_upload_100K.txt?dl=1";
            String SERVER_URL = "http://adhocdata.tk/wp-admin/upload.php";

            //////// Get location from GPS,Network, or Passive ////////////

            LocationManager lm = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
            Location GPS_GPS = lm.getLastKnownLocation(lm.GPS_PROVIDER);
            Location GPS_Network = lm.getLastKnownLocation(lm.NETWORK_PROVIDER);
            Location GPS_Passive = lm.getLastKnownLocation(lm.PASSIVE_PROVIDER);

            /////////// Current Time //////////////////////////////

            long currentDateTime = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
            Date currentDate = new Date(currentDateTime);

            ////////// Get the location with GPS having the highest priority //////////////

            if ( GPS_GPS!= null)

            {
                double GPSLat_GPS = GPS_GPS.getLatitude();
                double GPSLong_GPS = GPS_GPS.getLongitude();
                String GPSLat_GGPS = Double.toString(GPSLat_GPS);
                String GPSLong_GGPS = Double.toString(GPSLong_GPS);

                printInfo(level_GPS, sdf.format(currentDate) + ",", "");
                printInfo(level_GPS, GPSLat_GGPS + ",", "");
                printInfo(level_GPS, GPSLong_GGPS + ",", "");

            ////////// If we can not get the location from GPS get it from Wi-Fi //////////////

            } else if (GPS_Network != null) {

                double GPSLat_Wifi = GPS_Network.getLatitude();
                double GPSLong_Wifi = GPS_Network.getLongitude();
                String GPSLat_WWifi = Double.toString(GPSLat_Wifi);
                String GPSLong_WWif = Double.toString(GPSLong_Wifi);

                printInfo(level_GPS, sdf.format(currentDate) + ",", "");
                printInfo(level_GPS, GPSLat_WWifi + ",", "");
                printInfo(level_GPS, GPSLong_WWif + ",", "");

            ///////// If we can not get the location from GPS get it from Passive application //////////////

            } else if (GPS_Passive != null) {

                double GPSLat_Passive = GPS_Passive.getLatitude();
                double GPSLong_Passive = GPS_Passive.getLongitude();
                String GPSLat_PPassive = Double.toString(GPSLat_Passive);
                String GPSLong_PPassive = Double.toString(GPSLong_Passive);

                printInfo(level_GPS, sdf.format(currentDate) + ",", "");
                printInfo(level_GPS, GPSLat_PPassive + ",", "");
                printInfo(level_GPS, GPSLong_PPassive + ",", "");
            } else {

                ///////// No GPS //////////////

                printInfo(level_GPS, sdf.format(currentDate) + ",", "");
                printInfo(level_GPS, "NO GPS" + ",", "");
                printInfo(level_GPS, "NO GPS" + ",", "");

            }


           //// If we are not connected to Wi-Fi ///////////////

            if (connectionInfo.getNetworkId() == -1) {

                List<ScanResult> availableNetworks = new ArrayList<ScanResult>();

                availableNetworks = activity.wm.getScanResults();

           /////// If no Available Wi-Fi Access Points are scanned //////////////////

                if (availableNetworks.isEmpty()) {

                    getGPScoordinates();
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,","");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,","");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan,", "");
                    printInfo(wifi_scan,  "nan"+ "\n","");

                    printInfo(Wi_Fi, sdf.format(currentDate) + ", No Available WiFi Networks \n", "");

                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + "\n", Integer.toString(0));

                    ////////////////  If there are available Wi-Fi Networks ////////////////

                } else {

                    ArrayList<Integer> arrayList = new ArrayList<Integer>();
                    int open_networks = 0;
                    int count = 0;
                    long average_level = 0;
                    if (activity.lightMode) {

                    } else {

                        printInfo(Wi_Fi, sdf.format(currentDate) + ", ", "");
                        for (ScanResult network : availableNetworks) {

                            String words = network.toString();


                            String bssid = network.BSSID;
                            bssid = bssid.replace(":", "");
                            String upToNCharacters = bssid.substring(0, Math.min(bssid.length(), 10));

                            getGPScoordinates();
                            printInfo(wifi_scan, bssid  + ",", "");
                            printInfo(wifi_scan, network.level+ ",", "");
                            printInfo(wifi_scan, upToNCharacters + ",", "");
                            printInfo(wifi_scan, network.SSID + ",", "");
                            printInfo(wifi_scan, network.capabilities+ ",", "");
                            printInfo(wifi_scan, network.frequency+ ",", "");
                            printInfo(wifi_scan, network.timestamp+ ",", "");
                            printInfo(wifi_scan, network.isPasspointNetwork()+ ",", "");
                            printInfo(wifi_scan, network.channelWidth+ ",", "");
                            printInfo(wifi_scan, network.centerFreq0+ ",", "");
                            printInfo(wifi_scan, network.centerFreq1+ ",", "");
                            printInfo(wifi_scan, network.is80211mcResponder()+ "\n", "");


                            String Capabilities = network.capabilities;

                            ////////// Check Access Point Security //////////

                            if (!Capabilities.contains("WPA")) {
                                open_networks++;

                            }
                            arrayList.add(network.level);


                            average_level = average_level + network.level;
                            printInfo(Wi_Fi, network.toString() + "\n", words);
                            count++;
                            }
                    }

                    ////// Find the Maximum,Minimum,Average RSSI //////////////

                    Integer max_level = Collections.max(arrayList);
                    Integer min_level = Collections.min(arrayList);
                    average_level = average_level / count;

                    printInfo(level_GPS, Integer.toString(count) + ", ", Integer.toString(count));
                    printInfo(level_GPS, Integer.toString(open_networks) + ", ", Integer.toString(open_networks));
                    printInfo(level_GPS, Long.toString(max_level) + ", ", Long.toString(max_level) + "\n");
                    printInfo(level_GPS, Long.toString(average_level) + ", ", Long.toString(average_level) + "\n");
                    printInfo(level_GPS, Long.toString(min_level) + ", ", Long.toString(min_level) + "\n");
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Long.toString(0) + ", ", Long.toString(0) + "\n");
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + ", ", Integer.toString(0));
                    printInfo(level_GPS, Integer.toString(0) + "\n", Integer.toString(0));
                }


            }

            ///////////// If I am connected to a Wi-Fi Network ///////////////////////////////

            else {

                List<ScanResult> availableNetworks = new ArrayList<ScanResult>();
                availableNetworks = activity.wm.getScanResults();
                String SSID = connectionInfo.getSSID();
                String BSSID = connectionInfo.getBSSID();
                int connected_level = connectionInfo.getRssi();
                int frequency = connectionInfo.getFrequency();

                ArrayList<Integer> arrayList = new ArrayList<Integer>();

                int open_networks = 0;
                int count = 0;
                long average_level = 0;
                if (activity.lightMode) {

                } else {
                    printInfo(Wi_Fi, sdf.format(currentDate) + ", ", "");

                    for (ScanResult network : availableNetworks)
                    {



                        String words = network.toString();
                        String bssid = network.BSSID;
                        bssid = bssid.replace(":", "");
                        String upToNCharacters = bssid.substring(0, Math.min(bssid.length(), 10));

                        getGPScoordinates ();
                        printInfo(wifi_scan, bssid + ",", "");
                        printInfo(wifi_scan, network.level + ",", "");
                        printInfo(wifi_scan, upToNCharacters + ",", "");
                        printInfo(wifi_scan, network.SSID + ",", "");
                        printInfo(wifi_scan, network.capabilities + ",", "");
                        printInfo(wifi_scan, network.frequency + ",", "");
                        printInfo(wifi_scan, network.timestamp+ ",", "");
                        printInfo(wifi_scan, network.isPasspointNetwork()+ ",", "");
                        printInfo(wifi_scan, network.channelWidth + ",", "");
                        printInfo(wifi_scan, network.centerFreq0+ ",", "");
                        printInfo(wifi_scan, network.centerFreq1+ ",", "");
                        printInfo(wifi_scan, network.is80211mcResponder()+ "\n", "");



                        String Capabilities = network.capabilities;
                        if (network.BSSID.equals(connectionInfo.getBSSID())) {
                            bandwidth = network.channelWidth;

                        }

                        if (!Capabilities.contains("WPA")) {
                            open_networks++;

                        }

                        arrayList.add(network.level);
                        average_level = average_level + network.level;
                        printInfo(Wi_Fi, network.toString() + "\n", words);
                        count++;
                        }
                }

                Integer max_level = Collections.max(arrayList);
                Integer min_level = Collections.min(arrayList);
                average_level = average_level / count;

                printInfo(level_GPS, Integer.toString(count) + ", ","");
                printInfo(level_GPS, Integer.toString(open_networks) + ", ","");
                printInfo(level_GPS, Long.toString(max_level) + ", ","");
                printInfo(level_GPS, Long.toString(average_level) + ", ","");
                printInfo(level_GPS, Long.toString(min_level) + ", ","");
                printInfo(level_GPS, Integer.toString(connected_level) + ", ","");
                printInfo(level_GPS, Integer.toString(frequency) + ", ","");
                printInfo(level_GPS, Integer.toString(bandwidth) + ", ","");
                printInfo(level_GPS, Mode+",","");
                printInfo(level_GPS, utilization+",","");
                printInfo(level_GPS, BSSID.toString() + ", ", BSSID);
                printInfo(level_GPS, SSID.toString() + ",", SSID);


                DataOutputStream dataOutputStream;
                URLConnection conndownload ;

                URL url1download = new URL(urldownload);
                conndownload = url1download.openConnection();

                double file_size = 64000;   /// File Size in bits
                InputStream download = conndownload.getInputStream();
                long start_download = System.currentTimeMillis();
                BufferedInputStream in2 = new BufferedInputStream(download);
                long size2 = 0;
                int red2 = 0;
                byte[] buf2 = new byte[allbuffersize];
                while ((red2 = in2.read(buf2)) != -1) {
                    size2 += red2;
                }

                long end_download = System.currentTimeMillis();
                long duration = end_download - start_download;
                conndownload.getInputStream().close();

                double download_speed = file_size / duration;

                int serverResponseCode = 0;

                File sourceFile = new File(folder,sourceFileUri);
                FileInputStream fileInputStream = new FileInputStream(sourceFile);

                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");

                long start_upload = System.currentTimeMillis();

                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                BufferedInputStream inputStream = new BufferedInputStream(fileInputStream);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(connection.getOutputStream());

                byte[] bytes = new byte[allbuffersize];
                int count2;
                while ((count2 = inputStream.read(bytes)) > 0) {
                    bufferedOutputStream.write(bytes, 0, count2);
                    }

                serverResponseCode = connection.getResponseCode();

                Double upload_speed = 0.0;

                if (serverResponseCode == 200) {
                    long end_upload = System.currentTimeMillis();
                    long upload_duration = end_upload - start_upload;
                    fileInputStream.close();
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    upload_speed = file_size / upload_duration;
                    }
                printInfo(level_GPS, Double.toString(download_speed) + ", ", Double.toString(download_speed) + "\n");
                printInfo(level_GPS, Double.toString(upload_speed) + "\n", Double.toString(upload_speed) + "\n");

                Thread.sleep((long) 1*20000);
                availableNetworks.clear();
                availableNetworks = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stopSelf();
        isRunning = true;

        Log.e(TAG, "mobihocservice: " + "stopSelf");
    }

    public void setMainActivity(MainActivity activity) {
        this.activity = activity;
    }

    public class LocalBinder extends Binder {
        mobiHocService getService() {
            return mobiHocService.this;
        }
    }
}