package com.example.myapplication;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;

public class GraphFragment extends Fragment {

    Context context;
    BluetoothDevice device;

    public static GraphFragment newInstance(Context context,BluetoothDevice device) {
        GraphFragment fragment = new GraphFragment();
        Bundle args = new Bundle();
        fragment.device=device;
        fragment.context=context;
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.graph_fragment, container, false);
        final GraphView graph = (GraphView) view.findViewById(R.id.graph);

        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);

        Button start=view.findViewById(R.id.button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SerialStart beg=new SerialStart();
                beg.beginGraph(graph,device,context);
            }
        });



        return view;
    }
}
