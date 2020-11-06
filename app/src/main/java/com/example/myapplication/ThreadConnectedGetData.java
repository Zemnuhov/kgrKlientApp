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
    private ArrayList<Double> avgNow = new ArrayList<>();
    private ArrayList<Double> tonicSample = new ArrayList<>();
    private double temp;
    private double tempTonic;

    Context context;
    Date date;
    Date dateAvg;
    long time;
    long timeAvg;
    long timeAvgDelay;


    public ThreadConnectedGetData(BluetoothSocket socket, SerialStart serialStart, Context context) {
        this.serialStart = serialStart;
        date = new Date();
        time = date.getTime();
        timeAvg = date.getTime();
        timeAvgDelay = date.getTime();
        temp = 0;
        tempTonic = 0;
        this.context = context;

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
                    tonicAndPhasicResponse();
                    phasicResponse();

                }

            } catch (IOException e) {
                serialStart.setConnectFlag(false);
                break;
            }
        }
    }

    private void phasicResponse() {
        if (dataSample.size() < 400) {
            temp = Double.parseDouble(sbprint);
            dataSample.add(temp);
        } else {
            for (Double num : dataSample) {
                temp += num;
            }
            temp /= dataSample.size();
            resultSample.add(temp);
            if (resultSample.size() > 1) {
                data = (resultSample.get(1) - resultSample.get(0)) / 2;
                //serialStart.setData(data);
                resultSecondSample.add(data);
                resultSample.remove(0);
            }
            dataSample.remove(0);
            temp = 0;
        }
        if (resultSecondSample.size() >= 300) {
            for (double num : resultSecondSample) {
                temp += num;
            }
            temp /= resultSecondSample.size();
            serialStart.setData(temp);
            phasicArray.add(temp);
            if (temp > 0.25) {
                notifi();
            }
            resultSecondSample.remove(0);
        }
    }

    private void avgRightNow(double v) {
        avgNow.add(v);
        double summ = 0;
        if (avgNow.size() >= 1000) {
            for (double num : avgNow) {
                summ += num;
            }
            summ /= avgNow.size();
            System.out.println(summ);
            serialStart.setNowAvg(summ);
            System.out.println(123);
            Log.i("now", String.valueOf(summ));
            avgNow.remove(0);
        }
    }

    private void avgTonicResponse(double v) {
        dateAvg = new Date();
        if (dateAvg.getTime() - timeAvg > 5000 && dateAvg.getTime() - timeAvgDelay > 100) {
            timeAvgDelay = dateAvg.getTime();
            avgPhasicArrayFirst.add(v);
            if (avgPhasicArrayFirst.size() >= 1000) {
                double summ = 0;
                for (double num : avgPhasicArrayFirst) {
                    summ += num;
                }
                summ /= avgPhasicArrayFirst.size();
                avgPhasicArraySecond.add(summ);
                avgPhasicArrayFirst.remove(0);

                if (avgPhasicArraySecond.size() >= 3800) {
                    summ = 0;
                    for (double num : avgPhasicArraySecond) {
                        summ += num;
                    }
                    summ /= avgPhasicArraySecond.size();
                    serialStart.setLongAvg(summ);
                    avgPhasicArraySecond.remove(0);
                } else {
                    serialStart.setLongAvg(summ);
                    Log.i("perLong", String.valueOf(summ));
                }
            }
        }
    }

    private void tonicAndPhasicResponse() {
        if (tonicSample.size() < 500) {
            tempTonic += Double.parseDouble(sbprint);
            tonicSample.add(tempTonic);
        } else {
            for (Double num : tonicSample) {
                tempTonic += num;
            }
            tempTonic /= tonicSample.size();
            avgRightNow(tempTonic);
            avgTonicResponse(tempTonic);

            tonicSample.remove(0);
            tempTonic = 0;

        }
    }

    private void notifi() {
        date = new Date();
        if ((long) date.getTime() - time > 4000) {
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
            time = date.getTime();
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

