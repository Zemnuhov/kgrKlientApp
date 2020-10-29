package com.example.myapplication.fragment;



import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.myapplication.ConnectBluetooth;
import com.example.myapplication.R;
import com.example.myapplication.RecodingFileClass;
import com.example.myapplication.SerialStart;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jjoe64.graphview.GraphView;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class GraphFragment extends Fragment {

    Context context;
    FloatingActionButton startRec;
    FloatingActionButton settingButton;
    ConnectBluetooth connectDevice;
    boolean recFlag;
    int colorCount;
    OutputStreamWriter writerLine;
    OutputStreamWriter writerPoint;
    SerialStart serialStart;
    RecodingFileClass lineRecoding;
    RecodingFileClass pointRecoding;
    GraphView graph;


    public static GraphFragment newInstance(Context context, ConnectBluetooth connectDevice) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.connectDevice=connectDevice;
        fragment.context=context;
        return fragment;
    }

    private void setBeginSettingGraph(GraphView graph){
        //graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(255);
        //graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.setBackgroundResource(R.drawable.shape);
        graph.getGridLabelRenderer().setGridColor(Color.WHITE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.graph_fragment, container, false);

        startRec=view.findViewById(R.id.start_rec_and_stop);
        settingButton=view.findViewById(R.id.setting_button);

        recFlag=false;
        colorCount=0;

        graph = (GraphView) view.findViewById(R.id.graph);
        setBeginSettingGraph(graph);

        FragmentManager childManager=getChildFragmentManager();

        serialStart=new SerialStart(graph,connectDevice,context,childManager);
        serialStart.threadGraph();

        lineRecoding=new RecodingFileClass(context,serialStart,writerLine);
        pointRecoding=new RecodingFileClass(context,serialStart,writerPoint);
        serialStart.setLineRecoding(lineRecoding);
        serialStart.setPointRecoding(pointRecoding);

        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment=SettingDialog.newInstance(graph,serialStart,serialStart.getThreadConnected());
                dialogFragment.show(getChildFragmentManager(),"setting");
            }
        });


        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!recFlag) {
                    lineRecoding.StartRecoding("line.txt");
                    pointRecoding.StartRecoding("point.txt");
                    recFlag=true;
                    Toast.makeText(context,"Начало записи",Toast.LENGTH_LONG).show();
                }else {
                    lineRecoding.StopRecoding();
                    pointRecoding.StopRecoding();
                    recFlag=false;
                    Toast.makeText(context,"Запись остановлена",Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }
}
