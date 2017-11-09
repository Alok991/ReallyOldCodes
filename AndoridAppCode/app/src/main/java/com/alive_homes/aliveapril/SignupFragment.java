package com.alive_homes.aliveapril;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupFragment extends Fragment {
    protected   MyApp app;
    EditText name,mobile,pass,pid;
    Button signup;
    TextView txt;
    Activity activity;
    Communicator communicator;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.signup_fragment, container, false);
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
        name= (EditText) view.findViewById(R.id.name);
        mobile= (EditText) view.findViewById(R.id.mobile_number);
        signup= (Button) view.findViewById(R.id.btn_signup);
        pass= (EditText) view.findViewById(R.id.input_password);
        pid= (EditText) view.findViewById(R.id.input_pid);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_name=name.getText().toString();
                String str_mobile=mobile.getText().toString();
                String str_pass=pass.getText().toString();
                String str_pid=pid.getText().toString();
                app= (MyApp) getActivity().getApplication();
                app.start(str_name,str_mobile,str_pass,str_pid,getContext());
            }
        });

        txt= (TextView) view.findViewById(R.id.txt_login);
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                communicator.goToFragment(0);
            }
        });

    }
}
