package com.alive_homes.aliveapril;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Status extends AppCompatActivity {
    public static boolean isStatusForeground=false;
    FragmentTransaction transaction;
    FragmentManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Intent i= getIntent();
        String stat=i.getStringExtra("stat");
        manager=getSupportFragmentManager();
        transaction= manager.beginTransaction();
        transaction.add(R.id.replace,StatusFragment.newInstance(stat));
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.status, menu);
        MenuItem item=menu.findItem(R.id.connectionstatus);
        MyApp app= (MyApp) getApplication();
        if(app.isAliveSystemWifi()){
            item.setIcon(R.drawable.home);
        }
       else if(app.isNetworkAvailable()){
            item.setIcon(R.drawable.online);
        }
        else if(!app.isNetworkAvailable()){
            item.setIcon(R.drawable.notconnected);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStatusForeground=true;
        Intent intent = new Intent(getBaseContext(), MyService.class);
        startService(intent);
        invalidateOptionsMenu();
        MyApp app= (MyApp) getApplication();
        if(app.mConnection.isConnected())
            Toast.makeText(Status.this, "Connected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.connectionstatus){
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }
        else if(item.getItemId()==R.id.settings){
            startActivity(new Intent(this,Setting.class));
        }
        else if(item.getItemId()==R.id.logout){
            MyApp app= (MyApp) getApplication();
            if(app.mConnection.isConnected()){
                Log.d(MyApp.TAG,"Logging out while Connected");
                app.mConnection.sendTextMessage(Frames.getLogoutFrame());
                SharedPreferences pref=getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor=pref.edit();
                editor.putString(Frames.SESSION_KEY, "");
                editor.putString(Frames.USER_KEY, "");
                editor.apply();
                if(MyApp.mConnection.isConnected()){
                    MyApp.mConnection.disconnect();
                }
                startActivity(new Intent(this, MainActivity.class));
            }
            else{
                Toast.makeText(Status.this, "NO Connection!", Toast.LENGTH_SHORT).show();
            }

        }
        else if(item.getItemId()==R.id.home)
        {
            if(MyService.here!=null) {
                SharedPreferences spfd=getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor= spfd.edit();
                editor.putString("HomeLat", Double.toString(MyService.here.getLatitude()));
                editor.putString("HomeLog", Double.toString(MyService.here.getLongitude()));
                editor.apply();
                Toast.makeText(Status.this, "Home!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(Status.this, "Position is Not available", Toast.LENGTH_LONG).show();
            }
        }
        else if(item.getItemId()==R.id.stopService)
        {
            stopService(new Intent(this,MyService.class));
        }
        else if(item.getItemId()==R.id.retry)
        {
                String URL = "http://192.168.4.1:80/index?TYPE=RETRY";
                //Log.d(MyApp.TAG,URL);
                String[] str={URL};
                Frames.MyTask task= new Frames.MyTask();
                task.execute(str[0]);
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isStatusForeground=false;
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}


//    String URL = "http://192.168.4.1:80/index?TYPE=CONTROL&MESSAGE="
//            +"P-C-" + (pos + 1)
//            + "-" + state;
//    //Log.d(MyApp.TAG,URL);
//    String[] str={URL};
//    Frames.MyTask task= new Frames.MyTask();
//task.execute(str[0]);