package com.alive_homes.aliveapril;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;


public class StatusFragment extends Fragment{
    RecyclerView recyclerView;
    MyAdapter adapter;
    public static StatusFragment newInstance(String stat) {
        StatusFragment f = new StatusFragment();
        Bundle args = new Bundle();
        args.putString("stat", stat);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_status, container, false);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        int[] val={0,0,0};
        if (args  != null && args.containsKey("stat")){
            String str = args.getString("stat");
            String[] strs;
            if(str!=null) {
                 strs= str.split("-");
            }
            else {
                SharedPreferences pref= getActivity().getSharedPreferences(Frames.FILE_NAME, Context.MODE_PRIVATE);
                String stat=pref.getString(Frames.CURRENTSTATE, "");
                strs=stat.split("-");
            }
            val[0]=Integer.parseInt(strs[0]);
            val[1]=Integer.parseInt(strs[1]);
            val[2]=Integer.parseInt(strs[4]);
//            val[0]=(Integer.parseInt(strs[0])==1)?0:1;    // This complex shit is because of inverted logic at hardware removing this will create  very fast toggling
//            val[1]=(Integer.parseInt(strs[1])==1)?0:1;
//            val[2]=(Integer.parseInt(strs[4]));
        }
        recyclerView= (RecyclerView) v.findViewById(R.id.recycler);
        adapter=new MyAdapter(getActivity(),getData(val));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }


    private ArrayList<Controller> getData(int [] values) {
        ArrayList<Controller> controllerList= new ArrayList<>();
        int pic;
        if(values[0]==0){
            pic=R.drawable.bulb;
        }
        else{
            pic=R.drawable.bulbon;
        }
        controllerList.add(0,new Controller("Light 1",pic,values[0] ,Frames.ON_OFF));
        controllerList.add(1,new Controller("Light 2",pic,values[1],Frames.ON_OFF));
        controllerList.add(2,new Controller("Fan",R.drawable.fan, values[2],Frames.FAN));
        return controllerList;
    }



    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(context,R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
