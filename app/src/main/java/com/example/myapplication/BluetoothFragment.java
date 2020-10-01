package com.example.myapplication;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import static android.R.*;


public class BluetoothFragment extends Fragment {
    Context context;
    View view;
    Button refreshButton;
    ListView deviceList;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> availlableBluetoothAdapter;
    ArrayList<String> availlableBluetoothList;
    private static final int REQUEST_ENABLE_BT = 1;

    public static BluetoothFragment newInstance(Context context) {
        BluetoothFragment fragment = new BluetoothFragment();
        Bundle args = new Bundle();
        fragment.context=context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.bluetooth_list_fragmet, container, false);
        refreshButton=view.findViewById(R.id.refresh_button);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        deviceList=view.findViewById(R.id.list_connection);
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Обновляем...",Toast.LENGTH_SHORT).show();
                availableBluetooth();

            }
        });
        return view;
    }
    private void availableBluetooth(){
        Set<BluetoothDevice> availableDevice=bluetoothAdapter.getBondedDevices();
        if(availableDevice.size()>0){
            availlableBluetoothList=new ArrayList<>();
            for(BluetoothDevice device:availableDevice){
                availlableBluetoothList.add(device.getName()+"\n"+device.getAddress());
            }
            availlableBluetoothAdapter=new ArrayAdapter(context, layout.simple_list_item_1,
                    availlableBluetoothList);
            deviceList.setAdapter(availlableBluetoothAdapter);
        }
    }
}