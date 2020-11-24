package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.myapplication.fragment.WriteLableDialog;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.Serializable;

import static android.content.Context.MODE_PRIVATE;

public class SerialStart implements Serializable {
    private final Handler mHandler = new Handler();
    private Runnable threadVisualGraph;
    private int i = 0;
    private double point;
    private boolean recFlag;
    private int pointCount;
    private boolean bind;
    private int minBind;
    private int maxBind;
    private boolean connectFlag;
    private ThreadConnectedGetData threadConnected;
    private GraphView graph;
    private Context context;
    public RecodingFileClass lineRecoding;
    public RecodingFileClass pointRecoding;
    private FragmentManager childManager;


    final LineGraphSeries<DataPoint> seriesNormal = new LineGraphSeries<>(new DataPoint[]{});
    final PointsGraphSeries<DataPoint> seriesSplash = new PointsGraphSeries<>(new DataPoint[]{});
    final PointsGraphSeries<DataPoint> seriesPoint = new PointsGraphSeries<>(new DataPoint[]{});


    private SharedPreferences sPref;
    TextView textLongAVG;
    TextView textNowAVG;
    double longAvg;

    double nowAvg;

    public void setLongAvg(double longAvg) {
        this.longAvg = longAvg;
    }

    public void setNowAvg(double nowAvg) {
        this.nowAvg = nowAvg;
    }



    public SerialStart(final GraphView graph, ConnectBluetooth connectDevice, final Context context, final FragmentManager childManager, TextView longAVG, TextView nowAVG) {
        threadConnected = new ThreadConnectedGetData(connectDevice.getBluetoothSocket(),this,context);
        threadConnected.start();
        this.textLongAVG = longAVG;
        this.textNowAVG = nowAVG;
        this.graph = graph;
        this.context = context;
        pointCount = 0;
        bind = false;
        this.childManager = childManager;
        graph.addSeries(seriesNormal);
        graph.addSeries(seriesSplash);
        graph.addSeries(seriesPoint);
        //seriesSplash.setThickness(8);
        seriesSplash.setSize(8);
        seriesSplash.setColor(Color.RED);
        seriesNormal.setColor(Color.BLACK);
        //seriesNormal.setThickness(8);
        seriesNormal.setThickness(12);
        seriesPoint.setColor(Color.GRAY);
    }

    public void setTextLongAVG() {
        textLongAVG.setText("Среднее значение: "+String.valueOf((int)longAvg));
    }

    public void setTextNowAVG() {
        if(longAvg!=0) {
            if (nowAvg - longAvg > 800) {
                textNowAVG.setTextColor(Color.RED);
            } else {
                textNowAVG.setTextColor(Color.GREEN);
            }
        }
        textNowAVG.setText("Текущее значение: "+String.valueOf((int)nowAvg));
    }

    public void setData(double data){
        this.point=data;
    }

    public ThreadConnectedGetData getThreadConnected() {
        return threadConnected;
    }

    public void threadGraph() {
        threadVisualGraph = new Runnable() {
            @Override
            public void run() {

                if (!connectFlag) {
                    Toast.makeText(context, "Потеряно соединение с устройством!", Toast.LENGTH_LONG).show();
                    return;
                }
                i++;
                if(point>0.25){
                    seriesSplash.appendData(new DataPoint(i, point), true, 10000);
                }

                seriesNormal.appendData(new DataPoint(i, point), true, 10000);


                if (bind) {
                    graph.getViewport().setMinY(point - minBind);
                    graph.getViewport().setMaxY(point + maxBind);
                }

                setTextLongAVG();
                setTextNowAVG();

                seriesNormal.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        pointCount = loadCountLable();
                        Log.i("Count", String.valueOf(pointCount));
                        DialogFragment writeLableDialog = WriteLableDialog.newInstance(pointCount, context);
                        writeLableDialog.show(childManager, "writeble");
                        seriesPoint.appendData(new DataPoint(dataPoint.getX(), dataPoint.getY()), true, 10000);
                        if (recFlag) {
                            pointRecoding.writeFile(String.format("%.4f", point) + " " + String.valueOf((int) i) + " " + String.valueOf(pointCount) + "\n");
                        }
                        saveCountLable(pointCount + 1);
                    }
                });

                mHandler.postDelayed(this, 10);
                if (recFlag) {
                    lineRecoding.writeFile(String.format("%.4f", point) + " " + String.valueOf(i) + "\n");
                    pointCount = 0;
                }

            }
        };
        mHandler.postDelayed(threadVisualGraph, 1000);
    }

    public void setConnectFlag(boolean connectFlag) {
        this.connectFlag = connectFlag;
    }

    public void recodingFlagTrue() {
        recFlag = true;
    }

    public void recodingFlagFalse() {
        recFlag = false;
    }

    public void setLineRecoding(RecodingFileClass lineRecoding) {
        this.lineRecoding = lineRecoding;
    }

    public void setPointRecoding(RecodingFileClass pointRecoding) {
        this.pointRecoding = pointRecoding;
    }

    private void saveCountLable(int count) {
        sPref = context.getSharedPreferences("LABLE_COUNT", MODE_PRIVATE);
        Editor ed = sPref.edit();
        ed.putString("LABLE_COUNT", String.valueOf(count));
        ed.commit();
        Toast.makeText(context, "SaveLable", Toast.LENGTH_SHORT).show();
    }

    private int loadCountLable() {
        int count;
        sPref = context.getSharedPreferences("LABLE_COUNT", MODE_PRIVATE);
        String savedText = sPref.getString("LABLE_COUNT", "0");
        count = Integer.parseInt(savedText);
        Toast.makeText(context, "LoadLable", Toast.LENGTH_SHORT).show();
        return count;
    }

    public void bindingData(boolean bind, int maxBind, int minBind) {
        this.bind = bind;
        this.minBind = minBind;
        this.maxBind = maxBind;
    }

}


