package com.alive_homes.aliveapril;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by alok on 13/3/16.
 */
public class Frames {
    public static final String USER_NAME ="com.alive_homes.alive20.username.jehgj" ;
    public static final String MOBILE ="com.alive_homes.alive20.MOBILE.ncvwhsj" ;
    public static final String PID = "com.alive_homes.alive20.PID.jchdsgj";
    public static final String CURRENTSTATE = "com.alive_homes.alive20.CURRENTSTATE.jcaswehj";
    public static final String UserPASSWORD ="com.alive_homes.alive20.UserPASSWORD.jekjdsne" ;
    public static final String ALIVE_WIFI_NAME ="com.alive_homes.alive20.ALIVE_WIFI_NAME.jehface45" ;
    public static final String ALIVE_WIFI_Pass ="com.alive_homes.alive20.ALIVE_WIFI_Pass.fexg44xgf" ;
    public static final Double DIST = 10.0;
    public static Context context;
    public static final String URL = "ws://54.169.236.206:80/";
    public static final String USER_KEY ="com.alive_homes.alive20.UserKey.efjedwg" ;
    public static final String SESSION_KEY ="com.alive_homes.alive20.sessionId.kefjakwe";
    public static final String FILE_NAME = "com.alive_homes.alive20.fileName.jkwrSH";
    public static final String FAN="com.alive_homes.alive20.FAN.dehsedf";
    public static final String ON_OFF="com.alive_homes.alive20.ON_OFF.jdhegjwh";
    public static String firstTime = "com.alive_homes.alive.firstTime.jfdashwj";
    private static String username,userMobile,userPID,userPASS;
    public static int GOTSESSIONID=100;
    public static int AlreadyRegistered=200;
    public static int  NEWUSERREGISTERED=300;
    public static int  FAIL=400;
    public static int GOTSTATUS=500;
    public static int INVALID=600;
    public static int GOTUPDATE =800 ;
    public static String  oldState="";
    //A-L-Roll-Password
    public static String getLoginFrame(String str_name, String str_pass){
        String frame;
        frame="A-L-"+str_name+"-"+str_pass;
        return frame;
    }

    //A-R-Roll-Room-PID-Password
    public static String getSignupFrame(String str_name, String str_mobile, String str_pass, String str_pid) {
        String frame;
        frame="A-R-"+str_name+"-"+str_mobile+"-"+str_pid+"-"+str_pass;
        username = str_name;
        userMobile=str_mobile;
        userPID=str_pid;
        userPASS=str_pass;
        return frame;
    }

    //A-S-Key-SessionID
    public static String getStatusFrame(){
        SharedPreferences pref= context.getSharedPreferences(Frames.FILE_NAME,Context.MODE_PRIVATE);
        String frame="A-S-"+pref.getString(USER_KEY,"")+"-"+pref.getString(SESSION_KEY,"");
        return frame;
    }

    //A-C-key-SessionID-Switch-State
    public static String getControlFrame(int i, int state) {
        SharedPreferences pref= context.getSharedPreferences(Frames.FILE_NAME,Context.MODE_PRIVATE);
        String frame="A-C-"+pref.getString(Frames.USER_KEY,"")+"-"+pref.getString(Frames.SESSION_KEY,"")+"-"+i+"-"+state;
        return frame;
    }

    public static int processPayload(String payload)
    {   SharedPreferences pref= context.getSharedPreferences(Frames.FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        Log.d(MyApp.TAG, "inside Process Payload: "+payload);
        String[] str = payload.split("-");
        if(str[0].equals("I")){  // Logging in we are getting a session_Id and User_key
            editor.putString(Frames.USER_KEY, str[1]);
            editor.putString(Frames.SESSION_KEY, str[2]);
            editor.apply();
            return GOTSESSIONID;
        }
        else if(str[0].equals("S")){              //"S-R-0-You are already registered",,,,,"S-R-1-New user added"
            if(str[1].equals("R")){
                if(str[2].equals("0")){  //already exists
                    Toast.makeText(context, str[3], Toast.LENGTH_LONG).show();
                    return AlreadyRegistered;
                }
                else {    //New User registered
                    Toast.makeText(context,"Your Home is now Alive",Toast.LENGTH_LONG).show();
                    editor.putString(Frames.USER_NAME,username );
                    editor.putString(Frames.MOBILE, userMobile);
                    editor.putString(Frames.PID, userPID);
                    editor.putString(Frames.UserPASSWORD,userPASS);
                    editor.apply();
                    return NEWUSERREGISTERED;
                }
            }

            else if(str[1].equals("L")&&(str[2].equals("0"))){    //"S-L-0-User does not exist",,,,,,,,,"S-L-0-Incorrect Password"
                Toast.makeText(context,str[3],Toast.LENGTH_LONG).show();
                MyApp app= (MyApp) context.getApplicationContext();
                app.mConnection.disconnect();
                return FAIL;
            }
            else if(str[1].equals("6")) {
                editor.putString(Frames.CURRENTSTATE, payload.substring(4));
                editor.apply();
                return GOTSTATUS;
            }
        }
        else if(str[0].equals("U")&&str[1].equals("6")){
            //oldState=pref.getString(Frames.CURRENTSTATE,"");
            editor.putString(Frames.CURRENTSTATE, payload.substring(4));
            editor.apply();
            return GOTUPDATE;
        }
        return INVALID;
    }
    //A-Z-Key-SessionID
    public static String getLogoutFrame() {
        SharedPreferences pref=context.getSharedPreferences(Frames.FILE_NAME,Context.MODE_PRIVATE);
        return "A-Z-"+pref.getString(Frames.USER_KEY,"")+"-"+pref.getString(Frames.SESSION_KEY,"");
    }

    public static class MyTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... params) {
            final OkHttpClient client = new OkHttpClient();
            String result="";
            Request request = new Request.Builder()
                    .url(params[0])
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                }
            });
            return result;
        }

    }
    }










