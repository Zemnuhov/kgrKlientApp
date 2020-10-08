package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

public class SerialStart {
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private final Handler mHandler = new Handler();

    private Runnable mTimer2;
    private float i = 0;
    private int count=0;
    private BluetoothDevice device;
    private UUID myUUID;
    private ThreadConnected myThreadConnected;
    private StringBuilder sb = new StringBuilder();
    private ThreadConnectBTdevice threadConnectBTdevice;
    private GraphView graph;
    private String sbprint;
    private double point;
    private Context context;
    private boolean recFlag;

    OutputStreamWriter myOutWriter;

    void recFlagStart(){
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
    void recFlagStop(){
        recFlag=false;
        try {
            myOutWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beginGraph(GraphView graph, BluetoothDevice device, final Context context) {
        this.device = device;
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
        threadConnectBTdevice=new ThreadConnectBTdevice(device);
        threadConnectBTdevice.start();
        this.graph=graph;
        sbprint="0";
        this.context=context;

        final LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        graph.addSeries(series);
        mTimer2 = new Runnable() {
            @Override
            public void run() {
                    i += 0.001;
                    series.appendData(new DataPoint(i, 12000-point), true, 10000);
                    mHandler.postDelayed(this, 20);
                    if(recFlag){
                        try {
                            myOutWriter.write(String.valueOf((int)point)+"\n");
                        } catch (IOException e) {
                            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        }
                    }

            }
        };
        mHandler.postDelayed(mTimer2, 1000);
    }




    private class ThreadConnectBTdevice extends Thread { // Поток для коннекта с Bluetooth
        private BluetoothSocket bluetoothSocket = null;

        private ThreadConnectBTdevice(BluetoothDevice device) {
            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            }
            catch (IOException e) {
                e.printStackTrace();
                try {
                    bluetoothSocket.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(success) {  // Если законнектились, тогда открываем панель с кнопками и запускаем поток приёма и отправки данных
                myThreadConnected = new ThreadConnected(bluetoothSocket);
                myThreadConnected.start(); // запуск потока приёма и отправки данных
            }

        }
    }
    private class ThreadConnected extends Thread {    // Поток - приём и отправка данных
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
                        System.out.println(sbprint);
                        if(count<50){
                            temp+=Double.parseDouble(sbprint);
                            count++;
                        }
                        else {
                            point=temp/50;
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


