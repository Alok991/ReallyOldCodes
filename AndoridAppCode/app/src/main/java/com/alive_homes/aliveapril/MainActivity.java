package com.alive_homes.aliveapril;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity implements Communicator{
    FragmentManager manager;
    FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentViewOrIntro();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void contentViewOrIntro() {
        Log.d(MyApp.TAG,"setting up contentView");
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean(Frames.firstTime, true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent intro = new Intent(MainActivity.this, Intro.class);
                    startActivity(intro);


                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean(Frames.firstTime, false);

                    //  Apply changes
                    e.apply();
                } else {
                    SharedPreferences pref= getSharedPreferences(Frames.FILE_NAME, Context.MODE_PRIVATE);
                    if(pref.getString(Frames.SESSION_KEY,"").equals("")&&pref.getString(Frames.USER_KEY,"").equals("")) {
                        goToFragment(0);  //0 --> login
                    }
                    else{
                        MyApp app= (MyApp) getApplication();
                        if(!app.mConnection.isConnected())
                            app.restart(MainActivity.this);
                        else {
                            String stat=pref.getString(Frames.CURRENTSTATE, "");
                            Intent i= new Intent(MainActivity.this,Status.class);
                            i.putExtra("stat", stat);
                            startActivity(i);
                        }
                    }
                }
            }
        });
        t.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void goToFragment(int id) {

        if(id==0){
            LoginFragment loginFragment= new LoginFragment();
            manager= getSupportFragmentManager();
            transaction=manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container,loginFragment);
            transaction.commit();
        }

        else {
            SignupFragment signupFragment= new SignupFragment();
            manager= getSupportFragmentManager();
            transaction=manager.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.fragment_container,signupFragment);
            transaction.commit();
        }

    }
}

