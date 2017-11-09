package com.alive_homes.aliveapril;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class LoginFragment extends Fragment {
    EditText name,pass;
    protected   MyApp app;
    Button login_btn;
    TextView login_txt;
    Activity activity;
    Communicator communicator;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.login_fragment, container, false);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            activity=(Activity) context;
            communicator= (Communicator) activity;
            activity=null;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name= (EditText) view.findViewById(R.id.Mobile);
        pass = (EditText) view.findViewById(R.id.password);
        login_btn= (Button) view.findViewById(R.id.btn_login);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_name=name.getText().toString();
                String str_pass=pass.getText().toString();
                app= (MyApp) getActivity().getApplication();
                app.start(str_name,str_pass,getContext());
            }
        });
        login_txt= (TextView) view.findViewById(R.id.txt_signup);
        login_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.goToFragment(1);
            }
        });
    }
}
