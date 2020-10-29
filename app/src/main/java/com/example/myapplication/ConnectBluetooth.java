package com.example.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.fragment.GraphFragment;

import java.io.IOException;
import java.util.UUID;


public class ConnectBluetooth extends Thread { // Поток для коннекта с Bluetooth
        private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
        private int success;
        private UUID myUUID;
        private BluetoothSocket bluetoothSocket = null;


        public ConnectBluetooth(BluetoothDevice device) {
            try {
                myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                bluetoothSocket.connect();
                success = Constants.SUCCESS_CONNECT_BT;
            }
            catch (IOException e) {
                success=Constants.CONNECT_BT_ERROR;
                e.printStackTrace();
                try {
                    bluetoothSocket.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        public int getSuccess(){
            return success;
        }

        public BluetoothSocket getBluetoothSocket(){
            return bluetoothSocket;
        }

    }

