package com.alive_homes.aliveapril;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by alok on 4/2/16.
 */
public class WifiNameSetup extends android.support.v4.app.DialogFragment {
    Button ok;
    EditText wifiName,wifiPass,aliveName,alivePass;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.wifinamesetup,null);
        ok= (Button) view.findViewById(R.id.button);
        wifiName = (EditText) view.findViewById(R.id.editText);
        wifiPass = (EditText) view.findViewById(R.id.editText2);
        aliveName = (EditText) view.findViewById(R.id.ssidAlive);
        alivePass = (EditText) view.findViewById(R.id.pass);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread t= new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //TODO: Get request for wifiName and SSID

                            String strwifiName    = (wifiName.getText().toString());
                            String strwifiPass  = (wifiPass.getText().toString());
                            String straliveName   = (aliveName.getText().toString());
                            String stralivePass    = (alivePass.getText().toString());
                            String URL = "http://192.168.4.1:80/index?TYPE=SETUP&WIFINAME="
                                            +strwifiName+"&"+"WIFIPASS="+strwifiPass+"&"
                                            +"ALIVENAME=" +straliveName+"&"+"ALIVEPASS="+stralivePass;


                            // Create Request to server and get response





                    }
                });
                t.start();

            }
        });
        return view;
    }
}


