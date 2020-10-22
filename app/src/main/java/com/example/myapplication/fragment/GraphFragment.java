package com.example.myapplication.fragment;



import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.example.myapplication.R;
import com.example.myapplication.SerialStart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;

import java.util.ArrayList;
import java.util.Arrays;

public class GraphFragment extends Fragment {

    Context context;
    FloatingActionButton startRec;
    FloatingActionButton switchColorButton;
    FloatingActionButton settingButton;
    BluetoothSocket bluetoothSocket;
    ArrayList<Integer> color=new ArrayList<Integer>(Arrays.asList(Color.RED,Color.BLUE,Color.GREEN));
    boolean recFlag;
    int colorCount;


    public static GraphFragment newInstance(Context context,BluetoothSocket bluetoothSocket) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.bluetoothSocket=bluetoothSocket;
        fragment.context=context;
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.graph_fragment, container, false);

        startRec=view.findViewById(R.id.start_rec_and_stop);
        settingButton=view.findViewById(R.id.setting_button);


        recFlag=false;
        colorCount=0;



        final GraphView graph = (GraphView) view.findViewById(R.id.graph);
        //graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(255);
        //graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.setBackgroundColor(Color.WHITE);
        graph.getGridLabelRenderer().setGridColor(Color.WHITE);

        DialogFragment writeLableDialog=new WriteLableDialog();
        FragmentManager childManager=getChildFragmentManager();

        final SerialStart serialStart=new SerialStart();
        serialStart.beginGraph(graph,bluetoothSocket,context,writeLableDialog,childManager);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment=SettingDialog.newInstance(graph,serialStart);
                dialogFragment.show(getChildFragmentManager(),"setting");
            }
        });


        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recFlag) {
                    serialStart.recFlagStart();
                    recFlag=true;
                    Toast.makeText(context,"Начало записи",Toast.LENGTH_LONG).show();
                }else {
                    serialStart.recFlagStop();
                    recFlag=false;
                    Toast.makeText(context,"Запись остановлена",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
}
