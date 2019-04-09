package com.example.lpiem.bluetoothkeychain.activites;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleRssiCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.lpiem.bluetoothkeychain.R;

import java.util.List;
import java.util.UUID;

public class BLEConnectActivity extends AppCompatActivity {

    private TextView deviceFound;
    private Button testSendData;
    private Button pickerBlue;
    private Button pickerGreen;
    private Button findKey;
    private ConstraintLayout layoutSearch;
    private ConstraintLayout layoutScan;
    private TextView txtMetres;

    private boolean inFindMode;
    private boolean isBuzzing;

    private BleDevice foundDevice;
    private BluetoothGatt mBluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleconnect);

        setTitle("Key finder");
        inFindMode = false;

        deviceFound = findViewById(R.id.deviceFound);
        testSendData = findViewById(R.id.testSendData);
        testSendData.setVisibility(View.GONE);
        pickerBlue = findViewById(R.id.pickerBlue);
        pickerGreen = findViewById(R.id.pickerGreen);
        findKey = findViewById(R.id.findKey);
        layoutSearch = findViewById(R.id.layoutSearch);
        layoutScan = findViewById(R.id.layoutFind);
        txtMetres = findViewById(R.id.metres);

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

            // UI //
            findKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (inFindMode){

                        inFindMode = !inFindMode;
                        findKey.setText("Trouver");
                        layoutSearch.setVisibility(View.VISIBLE);
                        layoutScan.setVisibility(View.INVISIBLE);

                    } else {

                        inFindMode = !inFindMode;
                        layoutSearch.setVisibility(View.INVISIBLE);
                        findKey.setText("Trouvé !");
                        layoutScan.setVisibility(View.VISIBLE);

                        // Faire buzzer
                        final Button btnBuzz = findViewById(R.id.btnBuzz);
                        btnBuzz.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(isBuzzing){
                                    isBuzzing = !isBuzzing;
                                    btnBuzz.setText("Faire sonner");
                                    String str = "BUZZSTOP";
                                    byte[] data = str.getBytes();
                                    writeData(data);

                                } else {
                                    isBuzzing = !isBuzzing;
                                    btnBuzz.setText("Arrêter la sonnerie");
                                    String str = "BUZZ";
                                    byte[] data = str.getBytes();
                                    writeData(data);
                                }
                            }
                        });

                        // Changement du nombre de mètres
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(inFindMode){
                                    txtMetres.setText(foundDevice.getRssi() + " m");
                                }
                                handler.postDelayed(this, 1000);

                            }
                        }, 1000);

//                        Thread thread = new Thread(){
//                            @Override
//                            public void run() {
//                                try {
//
//                                        while(inFindMode) {
//                                            wait(1000);
//
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    txtMetres.setText(foundDevice.getRssi() + " m");
//                                                }
//                                            });
//                                        }
//
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                } };
//                        };
//                        thread.start();

//                            BleManager.getInstance().readRssi(foundDevice, new BleRssiCallback() {
//                                @Override
//                                public void onRssiFailure(BleException e) {
//                                    Log.d("mlk", "rssiFailure");
//                                }
//
//                                @Override
//                                public void onRssiSuccess(int i) {
//                                    Log.d("mlk", "onRssiSuccess");
//
//                                    txtMetres.setText(foundDevice.getRssi()+" m");
//                                }
//                            });


                        // Changement de la couleur du thermomètre
                        ImageView btnThermometre = findViewById(R.id.thermometre);
                        ImageView halo = findViewById(R.id.thermometreHalo);

                        // animation du bouton thermomètre
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(halo, "alpha", .5f, .1f);
                        fadeOut.setDuration(1200);
                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(halo, "alpha", .1f, .5f);
                        fadeIn.setDuration(1200);

                        final AnimatorSet mAnimationSet = new AnimatorSet();
                        mAnimationSet.play(fadeIn).after(fadeOut);
                        mAnimationSet.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mAnimationSet.start();
                            }
                        });
                        mAnimationSet.start();



                    }

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
