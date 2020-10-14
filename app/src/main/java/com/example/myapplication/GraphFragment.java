package com.example.myapplication;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;

public class GraphFragment extends Fragment {

    Context context;
    Button startRec;
    Button stopRec;
    BluetoothSocket bluetoothSocket;
    Button buttonRed;
    Button buttonBlue;
    Button buttonGreen;

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

        startRec=view.findViewById(R.id.start_rec);
        stopRec=view.findViewById(R.id.stop_rec);

        buttonRed=view.findViewById(R.id.button_red);
        buttonBlue=view.findViewById(R.id.button_blue);
        buttonGreen=view.findViewById(R.id.button_green);



        final GraphView graph = (GraphView) view.findViewById(R.id.graph);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(12000);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(false);
        graph.getViewport().setScrollableY(true);
        graph.setBackgroundColor(Color.WHITE);

        final SerialStart serialStart=new SerialStart();
        serialStart.beginGraph(graph,bluetoothSocket,context);

        startRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialStart.recFlagStart();
            }
        });
        stopRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialStart.recFlagStop();
            }
        });

        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialStart.setColor(Color.RED,1);
            }
        });
        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialStart.setColor(Color.BLUE,2);
            }
        });
        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialStart.setColor(Color.GREEN,3);
            }
        });
        return view;
    }
}
