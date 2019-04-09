package com.example.lpiem.bluetoothkeychain.activites;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.lpiem.bluetoothkeychain.R;
import com.example.lpiem.bluetoothkeychain.adapter.DeviceListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BLEConnectActivity extends AppCompatActivity {

    private TextView deviceFound;
    private Button testSendData;
    private Button pickerBlue;
    private Button pickerGreen;
    private BleDevice foundDevice;
    private BluetoothGatt mBluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleconnect);

        deviceFound = (TextView) findViewById(R.id.deviceFound);
        testSendData = (Button) findViewById(R.id.testSendData);
        testSendData.setVisibility(View.GONE);
        pickerBlue = (Button) findViewById(R.id.pickerBlue);
        pickerGreen = (Button) findViewById(R.id.pickerGreen);

        BleManager.getInstance().init(getApplication());

        boolean doesSupportBLE = BleManager.getInstance().isSupportBle();

        if (doesSupportBLE) {
            BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);

            BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setDeviceMac("EF:5B:13:B6:6D:59")
                .setAutoConnect(false)
                .setScanTimeOut(10000)
                .build();
            BleManager.getInstance().initScanRule(scanRuleConfig);


            BleManager.getInstance().scan(new BleScanCallback() {
                @Override
                public void onScanStarted(boolean success) {
                    Log.d("BLEConnectActivity", "Scan started ? " + success);
                }

                @Override
                public void onScanning(BleDevice bleDevice) {
                    Log.d("BLEConnectActivity", "Currently scanning, found " + bleDevice.getName());
                }

                @Override
                public void onScanFinished(List<BleDevice> scanResultList) {
                    Log.d("BLEConnectActivity", "Scan finished !");
                    for(BleDevice device : scanResultList) {
                        Log.d("ScanFinished", device.getName());

                        if (device.getName().equals("Adafruit Bluefruit LE")) {
                            deviceFound.setText("Trouvé ! " + device.getName());
                            foundDevice = device;
                            connectDevice(device);
                        }
                    }
                }
            });

            testSendData.setOnClickListener(new View.OnClickListener() {
                String str = "TEST";
                byte[] data = str.getBytes();

                @Override
                public void onClick(View v) {
                    writeData(data);
                }
            });
            pickerBlue.setOnClickListener(new View.OnClickListener() {
                String str = "BLUE";
                byte[] data = str.getBytes();

                @Override
                public void onClick(View v) {
                    writeData(data);
                }
            });
            pickerGreen.setOnClickListener(new View.OnClickListener() {
                String str = "GREEN";
                byte[] data = str.getBytes();

                @Override
                public void onClick(View v) {
                    writeData(data);
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Incompatible device", Toast.LENGTH_SHORT).show();
        }
    }

    void connectDevice(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Log.d("BLEConnectActivity", "Connection started");
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Log.e("BLEConnectActivity", "Connection failed ! " + exception.getDescription());
                Log.d("BLEConnectActivity", "Reconnecting...");
                connectDevice(bleDevice);
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.d("BLEConnectActivity", "Connection Succeeded ! Status: " + status);
                deviceFound.setText("Connecté au device !!!");
                testSendData.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Log.d("BLEConnectActivity", "Device disconnected... Status: "+status);
            }
        });
    }

    void writeData(byte[] data) {
        BleManager.getInstance().write(
            foundDevice,
            "6E400001-B5A3-F393-E0A9-E50E24DCCA9E",
            "6E400002-B5A3-F393-E0A9-E50E24DCCA9E",
            data,
            new BleWriteCallback() {
                @Override
                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                    Log.d("BLEConnectActivity", "Write success -> wrote current: " + current + " which is "+ justWrite +" on a total of "+ total);
                }

                @Override
                public void onWriteFailure(BleException exception) {
                    Log.e("BLEConnectActivity", "Write failed -> " + exception.getDescription());
                }
            }
        );
    }

    void enableBluetooth() {

    }
    void disableBluetooth() {

    }

    public UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID(MSB | (value << 32), LSB);
    }
}
