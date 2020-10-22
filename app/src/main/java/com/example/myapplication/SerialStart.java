package com.example.myapplication;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

public class SerialStart implements Serializable{
    private final Handler mHandler = new Handler();

    private Runnable threadVisualGraph;
    private float i = 0;
    private int count=0;

    private ThreadConnected myThreadConnected;
    private StringBuilder sb = new StringBuilder();
    private GraphView graph;
    private String sbprint;
    private double point;
    private Context context;
    private boolean recFlag;
    private int pointFlag;
    private int color;
    private int colorId;
    private boolean flagExceptionWriteFile;
    private boolean bind;
    private int minBind;
    private int maxBind;

    OutputStreamWriter myOutWriter;

    public void bindingData(boolean bind, int maxBind, int minBind){
        this.bind=bind;
        this.minBind=minBind;
        this.maxBind=maxBind;
    }

    public void setColor(int color,int colorId){
        this.color=color;
        this.colorId=colorId;
    }


    public void beginGraph(final GraphView graph, BluetoothSocket bluetoothSocket, final Context context) {
        myThreadConnected=new ThreadConnected(bluetoothSocket);
        myThreadConnected.start();
        this.graph=graph;
        this.context=context;
        color=Color.RED;
        pointFlag=0;
        colorId=1;
        flagExceptionWriteFile=false;
        bind=false;

        final LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{});
        final PointsGraphSeries<DataPoint> seriesPoint = new PointsGraphSeries<>(new DataPoint[]{});
        graph.addSeries(series);
        graph.addSeries(seriesPoint);


        threadVisualGraph = new Runnable() {
            @Override
            public void run() {
                i += 0.0001;
                series.appendData(new DataPoint(i, point), true, 10000);
                if(bind) {
                    graph.getViewport().setMinY(point-minBind);
                    graph.getViewport().setMaxY(point+maxBind);
                }
                series.setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        seriesPoint.setColor(color);
                        seriesPoint.appendData(new DataPoint(i,point),true,10000);
                        pointFlag=colorId;
                    }
                });
                mHandler.postDelayed(this, 5);
                if(recFlag){
                    try {
                        myOutWriter.write(String.valueOf((int)point)+"\t"+String.valueOf(pointFlag)+"\n");
                        pointFlag=0;
                    } catch (IOException e) {
                        if(!flagExceptionWriteFile){
                            Toast.makeText(context,"Ошибка записи в файл!",Toast.LENGTH_LONG).show();
                            flagExceptionWriteFile=true;
                        }
                    }
                }

            }
        };
        mHandler.postDelayed(threadVisualGraph, 1000);
    }
    public void recFlagStart(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File file = new File(context.getExternalFilesDir(null),"Log.txt");

            System.out.println(file.getAbsolutePath());
            File fhandle = new File(file.getAbsolutePath());
            if (!fhandle.getParentFile().exists()) {
                fhandle.getParentFile().mkdirs();
            }
            try {
                fhandle.createNewFile();
                myOutWriter=new OutputStreamWriter(new FileOutputStream(fhandle));
                recFlag=true;
            } catch (IOException e) {
                Toast.makeText(context,"Поток записи не открыт!",Toast.LENGTH_SHORT);
            }
        }

    }

    public void recFlagStop(){
        recFlag=false;
        try {
            myOutWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    private class ThreadConnected extends Thread implements Serializable {    // Поток - приём и отправка данных
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;
        public ThreadConnected(BluetoothSocket socket) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() { // Приём данных
            double temp=0;
            while (true) {
                try {
                    byte[] buffer = new byte[1];
                    int bytes = connectedInputStream.read(buffer);
                    String strIncom = new String(buffer, 0, bytes);
                    sb.append(strIncom); // собираем символы в строку
                    int endOfLineIndex = sb.indexOf("\r\n"); // определяем конец строки
                    if (endOfLineIndex > 0) {
                        sbprint = sb.substring(0, endOfLineIndex);
                        sb.delete(0, sb.length());

                        if(count<10){
                            temp+=Double.parseDouble(sbprint);
                            count++;
                        }
                        else {
                            temp=12000-temp/10;
                            point=((temp-0)/(12000-0))*(255-0);
                            //System.out.println(point);
                            count=0;
                            temp=0;
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }


        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}


