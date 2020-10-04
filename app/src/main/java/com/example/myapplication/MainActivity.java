package com.example.myapplication;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothFragment bluetoothFragment=BluetoothFragment.newInstance(getApplicationContext());
        android.app.FragmentTransaction transaction=getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentOne, bluetoothFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

