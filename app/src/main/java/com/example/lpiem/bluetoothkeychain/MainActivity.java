package com.example.lpiem.bluetoothkeychain;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.*;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String status;
    private static final int REQUEST_ENABLE_BT = 0;
    final Button btnConnectBluetooth = (Button) findViewById(R.id.btnConnectBluetooth);
    BluetoothAdapter bluetooth = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bluetooth = BluetoothAdapter.getDefaultAdapter();

        if(bluetooth != null){
            if (bluetooth.isEnabled()) {
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                status = mydevicename + " : " + mydeviceaddress;
            }
            else {
                status = "Bluetooth is not Enabled.";
            }
            Toast.makeText(this, status, Toast.LENGTH_LONG).show();
        }

        btnConnectBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetooth.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });
    }


}
