package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.SerialStart;
import com.jjoe64.graphview.GraphView;

public class SettingDialog extends DialogFragment {


    TextView begin;
    TextView end;
    SeekBar seekBarBegin;
    SeekBar seekBarEnd;
    Switch dataSwitch;
    Switch autoSwitch;
    GraphView graph;
    SerialStart serialStart;
    Button setParameter;


    public static SettingDialog newInstance(GraphView graph, SerialStart serialStart) {
        Bundle args = new Bundle();
        SettingDialog fragment = new SettingDialog();
        fragment.graph = graph;
        fragment.serialStart=serialStart;
        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.setting_dialog,null);

        seekBarBegin=view.findViewById(R.id.seek_bar_begin);
        seekBarEnd=view.findViewById(R.id.seek_bar_end);
        begin=view.findViewById(R.id.text_view_begin);
        end=view.findViewById(R.id.text_view_end);
        dataSwitch=view.findViewById(R.id.data_switch);
        autoSwitch=view.findViewById(R.id.auto_switch);
        setParameter=view.findViewById(R.id.ok_button);

        begin.setText("0");
        end.setText("0");

        dataSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(autoSwitch.isChecked()){
                    autoSwitch.setChecked(false);
                }
            }
        });
        autoSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dataSwitch.isChecked()){
                    dataSwitch.setChecked(false);
                }
            }
        });

        seekBarBegin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                begin.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarEnd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                end.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        setParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dataSwitch.isChecked() && !autoSwitch.isChecked()){
                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setMinY(seekBarBegin.getProgress());
                    graph.getViewport().setMaxY(seekBarEnd.getProgress());
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getViewport().setScalableY(false);
                    graph.getViewport().setScrollableY(false);
                    serialStart.bindingData(false,0,0);
                }else if (dataSwitch.isChecked() && !autoSwitch.isChecked()){
                    graph.getViewport().setYAxisBoundsManual(true);
                    graph.getViewport().setMinY(0);
                    graph.getViewport().setMaxY(255);
                    graph.getViewport().setXAxisBoundsManual(true);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getViewport().setScalableY(false);
                    graph.getViewport().setScrollableY(false);
                    serialStart.bindingData(true,seekBarBegin.getProgress(),seekBarEnd.getProgress());
                }else if (!dataSwitch.isChecked() && autoSwitch.isChecked()){
                    graph.getViewport().setYAxisBoundsManual(false);
                    graph.getViewport().setXAxisBoundsManual(false);
                    graph.getViewport().setMinY(0);
                    graph.getViewport().setMaxY(255);
                    graph.getViewport().setScalable(true);
                    graph.getViewport().setScrollable(true);
                    graph.getViewport().setScalableY(true);
                    graph.getViewport().setScrollableY(true);

                    serialStart.bindingData(false,0,0);
                }
            }
        });


        builder.setView(view);
        return builder.create();
    }


}
