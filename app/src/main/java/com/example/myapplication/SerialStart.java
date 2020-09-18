package com.example.myapplication;

import android.os.Handler;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class SerialStart {
    private Runnable mTimer2;
    private final Handler mHandler = new Handler();
    float i=0;
    public void beginGraph(GraphView graph){
        final LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        graph.addSeries(series);
        mTimer2 = new Runnable() {
            @Override
            public void run() {
                i += 0.2;
                Random random = new Random();
                series.appendData(new DataPoint(i, random.nextInt(80000 + 1)), true, 400);
                mHandler.postDelayed(this, 50);
            }
        };
        mHandler.postDelayed(mTimer2, 1000);
    }
}
