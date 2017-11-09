package com.alive_homes.aliveapril;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;




/**
 * Created by alok on 14/3/16.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
Context context;
    public static MyAdapter nyaAdapter;

    ArrayList<Controller> data=new ArrayList<>();
    private final LayoutInflater inflater;
    MyApp app;
    public MyAdapter(Context context,ArrayList<Controller> data) {
        inflater= LayoutInflater.from(context);
        this.data=data;
        this.context=context;
        app= (MyApp) context.getApplicationContext();
        nyaAdapter=this;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType==0){
            View v= inflater.inflate(R.layout.on_off_controller,parent,false);
            ViewHolder0 v0= new ViewHolder0(v);
            return v0;
        }
        else {
            View v= inflater.inflate(R.layout.regulator_controller,parent,false);
            ViewHolder1 v1= new ViewHolder1(v);
            return v1;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Controller current= data.get(position);
            if(holder.getItemViewType()==0){

                final ViewHolder0 v0= (ViewHolder0) holder;
                v0.img.setImageResource(current.getControllerPic());
                v0.name.setText(current.getControllerName());
                v0.sw.setChecked(current.getvalue()==1);
                v0.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int state=(isChecked==true)?1:0;
                        //int state=(isChecked)?0:1;
                        if(!isChecked){
                            v0.img.setImageResource(R.drawable.bulb);
                        }
                        else {
                            v0.img.setImageResource(R.drawable.bulbon);
                        }
                        int pos=position;
                        if(app.mConnection.isConnected()) {
                            app.mConnection.sendTextMessage(Frames.getControlFrame(pos + 1, state)); //Here Position(final) is not creating problem I dont know why will look into this some time after
                        }
                        else if(app.isAliveSystemWifi()){
                            String URL = "http://192.168.4.1:80/index?TYPE=CONTROL&MESSAGE="
                                    +"P-C-" + (pos + 1)
                                    + "-" + state;
                            //Log.d(MyApp.TAG,URL);
                            String[] str={URL};
                            Frames.MyTask task= new Frames.MyTask();
                            task.execute(str[0]);
                        }
                    }
                });
            }
        else{

                ViewHolder1 v1= (ViewHolder1) holder;
                v1.img.setImageResource(current.getControllerPic());
                v1.name.setText(current.getControllerName());
                v1.seekBar.setProgress(current.getvalue());
                v1.seekBar.setMax(5);
                v1.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progress = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        this.progress = progress;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        int pos = position;
                        if (app.mConnection.isConnected()) {
                            app.mConnection.sendTextMessage(Frames.getControlFrame(pos + 3, progress));
                        } else if (app.isAliveSystemWifi()) {

                            String URL = "http://192.168.4.1:80/index?TYPE=CONTROL&MESSAGE="
                                    + "P-C-" + (pos + 3) + "-" + seekBar.getProgress();
                            String[] str = {URL};
                            Frames.MyTask task = new Frames.MyTask();
                            task.execute(str[0]);
                        }
                    }
                });
        }
    }


    public void updateData(){
        this.data=MyApp.controllerList;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    class ViewHolder0 extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        Switch sw;
        public ViewHolder0(View itemView) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.imageView);
            name= (TextView) itemView.findViewById(R.id.textView);
            sw= (Switch) itemView.findViewById(R.id.switch1);
        }
    }

    class ViewHolder1 extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name;
        SeekBar seekBar;
        public ViewHolder1(View itemView) {
            super(itemView);
            img= (ImageView) itemView.findViewById(R.id.imageView2);
            name= (TextView) itemView.findViewById(R.id.textView2);
            seekBar= (SeekBar) itemView.findViewById(R.id.seekBar);
        }
    }






    @Override
    public int getItemViewType(int position) {

        if(data.get(position).getType().equals(Frames.ON_OFF)){

                   return 0;
               }
                else if (data.get(position).getType().equals(Frames.FAN))
               {

                   return 1;
               }

        return -1;

    }


}
