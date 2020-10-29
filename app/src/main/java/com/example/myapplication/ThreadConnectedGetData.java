package com.example.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class ThreadConnectedGetData extends Thread {    // Поток - приём и отправка данных
    private StringBuilder sb = new StringBuilder();
    private final InputStream connectedInputStream;
    private final OutputStream connectedOutputStream;
    private String sbprint;
    private double data;
    private SerialStart serialStart;
    private ArrayList<Double> dataSample = new ArrayList<>();
    private ArrayList<Double> resultSample = new ArrayList<>();
    private ArrayList<Double> resultSecondSample = new ArrayList<>();

    private ArrayList<Double> phasicArray = new ArrayList<>();
    private ArrayList<Double> avgPhasicArrayFirst = new ArrayList<>();
    private ArrayList<Double> avgPhasicArraySecond = new ArrayList<>();
    double mean;
    private double temp;
    private byte mode;

    int count;
    Context context;
    Date date;
    Date dateAvg;
    long time;
    long timeAvg;
    long timeAvgDelay;



    public ThreadConnectedGetData(BluetoothSocket socket, SerialStart serialStart, Context context) {
        this.serialStart=serialStart;
        date=new Date();
        time=date.getTime();
        timeAvg=date.getTime();
        timeAvgDelay=date.getTime();
        mode=0;
        temp=0;
        count=0;
        this.context=context;
        mean=127;

        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectedInputStream = in;
        connectedOutputStream = out;
    }

    @Override
    public void run() { // Приём данных
        serialStart.setConnectFlag(true);

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

                   switch (mode){
                       case 0:
                           tonicAndPhasicResponse();
                           break;
                       case 1:
                           phasicResponse();
                           break;
                   }
                }
            } catch (IOException e) {
                serialStart.setConnectFlag(false);
                break;
            }
        }
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }

    private void phasicResponse(){
        if (dataSample.size() < 400) {
            temp = Double.parseDouble(sbprint);
            dataSample.add(temp);
        } else {
            for (Double num : dataSample) {
                temp += num;
            }
            temp /= dataSample.size();
            temp = 12000 - temp;
            resultSample.add(temp);
            if(resultSample.size()>1){
                data=(resultSample.get(1)-resultSample.get(0))/2;
                //serialStart.setData(data);
                resultSecondSample.add(data);
                resultSample.remove(0);
            }
            System.out.println(temp);
            dataSample.remove(0);
            temp = 0;
        }
        if(resultSecondSample.size()>=300){
            for (double num:resultSecondSample) {
                temp+=num;
            }
            temp/=resultSecondSample.size();
            serialStart.setData(temp);
            phasicArray.add(temp);
            if(temp>0.25){
                notifi();
            }
            resultSecondSample.remove(0);
        }
    }

    private void avgTonicResponse(){
        dateAvg=new Date();
        if(dateAvg.getTime()-timeAvg>5000 && dateAvg.getTime()-timeAvgDelay>100){
            timeAvgDelay=dateAvg.getTime();
            avgPhasicArrayFirst.add(data);

            if(avgPhasicArrayFirst.size()>=1000) {
                double summ = 0;
                for (double num : avgPhasicArrayFirst) {
                    summ += num;
                }
                summ /= avgPhasicArrayFirst.size();
                avgPhasicArraySecond.add(summ);
                Log.i("first",String.valueOf(summ));
                avgPhasicArrayFirst.remove(0);
                Log.i("vg0", String.valueOf(summ));
                summ = 0;
                Log.i("second",String.valueOf(avgPhasicArraySecond.size()));
                if (avgPhasicArraySecond.size() >= 3800) {
                    for (double num : avgPhasicArraySecond) {
                        summ += num;
                    }
                    summ /= avgPhasicArraySecond.size();

                    avgPhasicArraySecond.remove(0);
                    Log.i("avg", String.valueOf(summ));
                }
            }
        }
    }

    private void tonicAndPhasicResponse(){
        if (dataSample.size() < 500) {
            temp += Double.parseDouble(sbprint);
            dataSample.add(temp);
        } else {
            for (Double num : dataSample) {
                temp += num;
            }
            temp /= dataSample.size();
            temp = 12000 - temp;
            data = ((temp - 0) / (12000 - 0)) * (255 - 0);
            serialStart.setData(data);
            avgTonicResponse();
            System.out.println(temp);
            dataSample.remove(0);
            temp = 0;

        }
    }

    private void notifi(){
        date=new Date();
        if((long)date.getTime()-time>4000) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Stress")
                    .setSmallIcon(R.drawable.adjust)
                    .setContentTitle("textTitle")
                    .setContentText("textContent")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Hello";
                String description = "getString(R.string.channel_description)";
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel("Stress", name, importance);
                channel.setDescription(description);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(1, builder.build());
            time=date.getTime();
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

