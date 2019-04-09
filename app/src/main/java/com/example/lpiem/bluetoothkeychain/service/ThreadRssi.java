package com.example.lpiem.bluetoothkeychain.service;

import com.clj.fastble.data.BleDevice;

public class ThreadRssi extends Thread {
    boolean on;
    BleDevice bleDevice;

    public ThreadRssi(boolean on, BleDevice bleDevice){
        this.on = on;
        this.bleDevice = bleDevice;
    }

    @Override
    public void run() {
        super.run();
        while(on){
            bleDevice.getRssi();
            // run on ui thread
            // sleep(500);
        }
    }
}
