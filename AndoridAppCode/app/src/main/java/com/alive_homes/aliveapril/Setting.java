package com.alive_homes.aliveapril;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class Setting extends AppCompatActivity {
    ListView mylist;
    String[] settinglist={"Alive","Home WiFi"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mylist= (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, settinglist);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    showEditDialog(settinglist[0],0);
                }
                else if(position==1){
                    showEditDialog(settinglist[1],2);
                }
            }
        });
    }

    private void showEditDialog(String title, int mode) {
        FragmentManager fm = getSupportFragmentManager();
        EditNameDialog editNameDialog = EditNameDialog.newInstance(title,mode);
        editNameDialog.show(fm, "fragment_edit_name");
    }

    public static class EditNameDialog extends android.support.v4.app.DialogFragment {

        private EditText mEditText,password_extra;
        private Button done,cancel;
        public EditNameDialog() {
            // Empty constructor is required for DialogFragment
            // Make sure not to add arguments to the constructor
            // Use `newInstance` instead as shown below
        }

        public static EditNameDialog newInstance(String title,int mode) {
            EditNameDialog frag = new EditNameDialog();
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putInt("mode",mode);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog, container);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Get field from view
            mEditText = (EditText) view.findViewById(R.id.editText);
            password_extra = (EditText) view.findViewById(R.id.password_extra);

            done= (Button) view.findViewById(R.id.done);
            cancel= (Button) view.findViewById(R.id.cancel);
            final int m=getArguments().getInt("mode");
            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(m==0){ //ALIVENAME
                        SharedPreferences pref=getActivity().getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(Frames.ALIVE_WIFI_NAME, mEditText.getText().toString());
                        editor.putString(Frames.ALIVE_WIFI_Pass,password_extra.getText().toString());
                        editor.apply();
                        //Now send this to 192.168.4.1
                        String URL = "http://192.168.4.1:80/index?TYPE=SETUP&ALIVENAME="+mEditText.getText().toString()+"&ALIVEPASS="+password_extra.getText().toString()+"&WIFINAME=&WIFIPASS=";
                        String[] str={URL};
                        Log.d(MyApp.TAG,URL);
                        Frames.MyTask task= new Frames.MyTask();
                        task.execute(str[0]);
                    }
//                    else if(m==1){//ALIVEPASS
//                        SharedPreferences pref=getActivity().getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putString(Frames.ALIVE_WIFI_Pass,mEditText.getText().toString());
//                        editor.apply();
//                        //Now send this to 192.168.4.1
//                        String URL = "http://192.168.4.1:80/index?TYPE=SETUP&ALIVEPASS="+mEditText.getText().toString();
//                        String[] str={URL};
//                        Frames.MyTask task= new Frames.MyTask();
//                        task.execute(str[0]);
//                    }
                    else if(m==2){//WIFINAME
//                        SharedPreferences pref=getActivity().getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.putString(Frames.ALIVE_WIFI_Pass,mEditText.getText().toString());
//                        editor.apply();
                        //Now send this to 192.168.4.1
                        String URL = "http://192.168.4.1:80/index?TYPE=SETUP&WIFINAME="+mEditText.getText().toString()+"&WIFIPASS="+password_extra.getText().toString()+"&ALIVENAME=&ALIVEPASS=";
                        String[] str={URL};
                        Frames.MyTask task= new Frames.MyTask();
                        Log.d(MyApp.TAG,URL);

                        task.execute(str[0]);
                    }
//                    else if(m==3){//WIFIPASS
////                        SharedPreferences pref=getActivity().getSharedPreferences(Frames.FILE_NAME,MODE_PRIVATE);
////                        SharedPreferences.Editor editor = pref.edit();
////                        editor.putString(Frames.ALIVE_WIFI_Pass,mEditText.getText().toString());
////                        editor.apply();
//                        //Now send this to 192.168.4.1
//                        String URL = "http://192.168.4.1:80/index?TYPE=SETUP&WIFIPASS="+mEditText.getText().toString();
//                        String[] str={URL};
//                        Frames.MyTask task= new Frames.MyTask();
//                        task.execute(str[0]);
//                    }
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });





            // Fetch arguments from bundle and set title
            String title = getArguments().getString("title", "");
            getDialog().setTitle(title);
            mEditText.requestFocus();
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }
}


//ALIVENAME
//ALIVEPASS
//WIFINAME
//WIFIPASS