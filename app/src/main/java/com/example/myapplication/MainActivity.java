package com.example.myapplication;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.fragment.BluetoothFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothFragment bluetoothFragment=BluetoothFragment.newInstance(getApplicationContext());
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentOne, bluetoothFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

