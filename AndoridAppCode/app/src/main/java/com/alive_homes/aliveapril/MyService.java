package com.alive_homes.aliveapril;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by alok on 23/3/16.
 */


public class MyService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mApiClient;
    LocationRequest mLocationRequest;
    static Location here;
    final int FOREGROUND_SERVICE_ID = 256451;
    private static final long INTERVAL = 10*1000;
    private static final long FASTEST_INTERVAL = 1000;
    public static Double dist;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private double calculateDistance(double fromLong, double fromLat,
                                     double toLong, double toLat) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createLocationRequest();
        mApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logo);
        builder.setContentTitle("Alive Homes");
        Intent i = new Intent(this, Status.class);
        SharedPreferences pref = getSharedPreferences(Frames.FILE_NAME, MODE_PRIVATE);
        String stat = pref.getString(Frames.CURRENTSTATE, "");
        i.putExtra("stat", stat);
        builder.setContentIntent(PendingIntent.getActivity(this, 1, i, 0));
        final Notification notification = builder.build();
       // startForeground(FOREGROUND_SERVICE_ID, notification);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mApiClient, this);
        mApiClient.disconnect();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(MyApp.TAG,"No permission");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        here=location;
        SharedPreferences spdf=getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
        Double HomeLat=Double.parseDouble(spdf.getString("HomeLat","0"));  //TODO:
        Double HomeLog=Double.parseDouble(spdf.getString("HomeLog","0"));
        if(HomeLat!=0&&HomeLog!=0){
            dist= calculateDistance(here.getLongitude(),here.getLatitude(),HomeLog,HomeLat);
            Toast.makeText(MyService.this, "Distance: "+dist, Toast.LENGTH_SHORT).show();
            if(dist>=Frames.DIST){
                if(MyApp.mConnection.isConnected()){
                    SharedPreferences pref= getSharedPreferences(Frames.FILE_NAME,Context.MODE_PRIVATE);
                    String frame="A-M-"+pref.getString(Frames.USER_KEY,"")+"-"+pref.getString(Frames.SESSION_KEY,"");
                    MyApp.mConnection.sendTextMessage(frame);
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
