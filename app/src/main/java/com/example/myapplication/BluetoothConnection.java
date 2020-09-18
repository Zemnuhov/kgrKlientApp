package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class BluetoothConnection {
    private static final int REQUEST_ENABLE_BT = 0;;

    public  BluetoothConnection(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            System.out.println("No bluetooth module!");
        }else{
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                System.out.println("Bluetooth deactive!");
            }
        }
    }
}
