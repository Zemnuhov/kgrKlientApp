package com.example.myapplication.fragment;

import android.app.Fragment;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
    BluetoothSocket bluetoothSocket;
    FloatingActionButton switchColorButton;
    boolean recFlag;
    ArrayList<Integer> color=new ArrayList<Integer>(Arrays.asList(Color.RED,Color.BLUE,Color.GREEN));
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

        switchColorButton=view.findViewById(R.id.switch_color);

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

        final SerialStart serialStart=new SerialStart();
        serialStart.beginGraph(graph,bluetoothSocket,context);

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

        switchColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorCount==3){
                    colorCount=0;
                }
                serialStart.setColor(color.get(colorCount),colorCount+1);
                switchColorButton.setBackgroundTintList(ColorStateList.valueOf(color.get(colorCount)));
                colorCount++;
            }
        });
        return view;
    }
}
