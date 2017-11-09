package com.alive_homes.aliveapril;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MyApp extends Application{
    public static WebSocketConnection mConnection;
    public static String TAG="ALIVE";
    public static ArrayList<Controller> controllerList;
    @Override
    public void onCreate() {
        super.onCreate();
        mConnection = new WebSocketConnection();
        Frames.context=getApplicationContext();
    }


    public String getWifiName(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled())    {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    String ssid=wifiInfo.getSSID();
                    if (ssid.startsWith("\"") && ssid.endsWith("\"")){
                        ssid = ssid.substring(1, ssid.length()-1);
                    }
                    return ssid;
                }
            }
        }
        return "";
    }




    public void start(String name, String pass,final Context context) {
        final String nam=name;
        final String pas=pass;
        final String wsuri = Frames.URL;

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage(Frames.getLoginFrame(nam, pas));
                }

                @Override
                public void onTextMessage(String payload) {
                    //Log.d(TAG, "Got msg: " + payload);
                    int result=Frames.processPayload(payload);
                    handleResult(result,context);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost. "+reason);
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }


    public void start(final String name, final String mobile , final String pass, final String pid,final Context context) {

        final String wsuri = Frames.URL;

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage(Frames.getSignupFrame(name, mobile, pass, pid));
                }

                @Override
                public void onTextMessage(String payload) {
                    //Log.d(TAG, "Got msg: " + payload);
                    int result=Frames.processPayload(payload);
                    handleResult(result,context);
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost. "+code);

                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    public void restart(final Context context) {
        final String wsuri = Frames.URL;

        try {
            mConnection.connect(wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + wsuri);
                    mConnection.sendTextMessage(Frames.getStatusFrame());
                }

                @Override
                public void onTextMessage(String payload) {
                    //Log.d(TAG, "Got msg: " + payload);
                    int result=Frames.processPayload(payload);
                    handleResult(result,context);
                }

                @Override
                public void onClose(int code, String reason) {
                    if(code==2){
                        SharedPreferences pref= getSharedPreferences(Frames.FILE_NAME, MODE_PRIVATE);
                        int result=Frames.processPayload("S-6-"+pref.getString(Frames.CURRENTSTATE,""));
                        handleResult(result,context);
                    }
                }
            });
        } catch (WebSocketException e) {

            Log.d(TAG, e.toString());
        }
    }

    private void handleResult(int result,Context context) {
        if(result==Frames.GOTSESSIONID){
            mConnection.sendTextMessage(Frames.getStatusFrame());
        }
        else if(result==Frames.GOTSTATUS){
            SharedPreferences pref= getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
            String stat=pref.getString(Frames.CURRENTSTATE, "");
            Intent i= new Intent(getApplicationContext(),Status.class);
            i.putExtra("stat", stat);
            context.startActivity(i);
        }
        else if(result==Frames.FAIL){

        }
        else if (result==Frames.AlreadyRegistered){

        }
        else if(result==Frames.INVALID){
            Toast.makeText(MyApp.this, "Got wrong Reply from server", Toast.LENGTH_SHORT).show();
        }
        else if(result==Frames.NEWUSERREGISTERED){
            SharedPreferences pref= getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
            mConnection.sendTextMessage(Frames.getLoginFrame(pref.getString(Frames.USER_NAME,""),pref.getString(Frames.UserPASSWORD,"")));
        }
        else if (result==Frames.GOTUPDATE){
            SharedPreferences pref= context.getSharedPreferences(Frames.FILE_NAME, Context.MODE_PRIVATE);
            String updated=pref.getString(Frames.CURRENTSTATE, "");
            if(Status.isStatusForeground) {
                myNewMethod(updated);
            }
            else   {
                Double localDist=MyService.dist;
                if(localDist>=Frames.DIST){
                    //Make a notification.
                    createNotification(updated,Frames.oldState);
                }
            }
        }
    }

    private void createNotification(String updated, String oldState) {
        String s="";
        String[] states=updated.split("-");
        if(!states[0].equals("1")||!states[4].equals("0")){
            if(!states[0].equals("1")&&states[4].equals("0")){
                s="Light 1 is ON";
            }
            else if(states[0].equals("1")&&!states[4].equals("0")){
                s="Fan is ON";
            }
            else {
                s="Appliances are  ON in Home";
            }
        }

        Intent intent = new Intent(this, Status.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(s)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pIntent);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.noti, "Take Action", pIntent).build();
        builder.addAction(action);
        Notification noti = builder.build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_SOUND;
        if(!s.equals(""))
            notificationManager.notify(5442, noti);
    }

    private void myNewMethod(String updated) {
        String data=updated;
        controllerList= new ArrayList<>();
        String[] strs=data.split("-");
        int[] val={0,0,0};
        val[0]=(Integer.parseInt(strs[0]));
        val[1]=(Integer.parseInt(strs[1]));
        val[2]=(Integer.parseInt(strs[4]));
//        val[0]=(Integer.parseInt(strs[0])==1)?0:1;    // This complex shit is because of inverted logic at hardware removing this will create  very fast toggling
//        val[1]=(Integer.parseInt(strs[1])==1)?0:1;
//        val[2]=(Integer.parseInt(strs[4]));
        int res1=(val[0]==1)?R.drawable.bulbon:R.drawable.bulb;
        int res2=(val[1]==1)?R.drawable.bulbon:R.drawable.bulb;
        controllerList.add(0,new Controller("Light 1",res1,val[0] ,Frames.ON_OFF));
        controllerList.add(1,new Controller("Light 2",res2,val[1],Frames.ON_OFF));
        controllerList.add(2, new Controller("Fan", R.drawable.fan, val[2], Frames.FAN));
        MyAdapter.nyaAdapter.updateData();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public boolean isAliveSystemWifi() {
        String wifiName=getWifiName(Frames.context);
        SharedPreferences pref= getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
        if(wifiName.equals(pref.getString(Frames.ALIVE_WIFI_NAME,"adhfjbewgjkrkj54v5ds434sG5v4s6sversdnsjxkd5")))
        {
            return true;
        }
        return false;
    }



}
