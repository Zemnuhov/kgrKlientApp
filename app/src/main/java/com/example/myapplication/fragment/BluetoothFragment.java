package com.example.myapplication.fragment;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.Constants;
import com.example.myapplication.R;
import com.example.myapplication.ConnectBluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.R.*;


public class BluetoothFragment extends Fragment {
    Context context;
    View view;
    Button refreshButton;
    ListView deviceList;
    BluetoothAdapter bluetoothAdapter;
    ArrayAdapter<String> availlableBluetoothAdapter;
    ArrayList<String> availlableBluetoothList;
    private UUID myUUID;
    private static final int REQUEST_ENABLE_BT = 1;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";
    private ConnectBluetooth connectBluetooth;
    private int connectState;

    private final Handler mHandler = new Handler();
    private Runnable waitConnectThread;


    public static BluetoothFragment newInstance(Context context) {
        BluetoothFragment fragment = new BluetoothFragment();
        Bundle args = new Bundle();
        fragment.context=context;
        fragment.connectState=0;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.bluetooth_list_fragmet, container, false);
        refreshButton=view.findViewById(R.id.refresh_button);
        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        deviceList=view.findViewById(R.id.list_connection);

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

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
            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String  itemValue = (String) deviceList.getItemAtPosition(position);
                    String MAC = itemValue.substring(itemValue.length() - 17); // Вычленяем MAC-адрес
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(MAC);

                    connectBluetooth =new ConnectBluetooth(device);
                    connectBluetooth.start();
                    Log.i("stateBT",String.valueOf(connectBluetooth.getSuccess()));

                    waitConnectThread=new Runnable() {
                        @Override
                        public void run() {
                            while(true){
                                Log.i("stateBT",String.valueOf(connectBluetooth.getSuccess()));
                                if(connectBluetooth.getSuccess()== Constants.CONNECT_BT_ERROR){
                                    Toast.makeText(context,"Соединение не удалось!",Toast.LENGTH_LONG).show();
                                    Log.i("stateBT",String.valueOf(connectBluetooth.getSuccess()));
                                    break;
                                }
                                if(connectBluetooth.getSuccess()==Constants.SUCCESS_CONNECT_BT){
                                    GraphFragment graphFragment=GraphFragment.newInstance(context,connectBluetooth);
                                    FragmentTransaction transaction=getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragmentOne, graphFragment);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                    Log.i("stateBT",String.valueOf(connectBluetooth.getSuccess()));
                                    break;
                                }
                            }
                        }
                    };
                    mHandler.postDelayed(waitConnectThread, 10);
                }
            });

        }
    }
}