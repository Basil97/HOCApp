package com.mspsfe.hocapp;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class ConnectionThread {

    private final BluetoothSocket mSocket;
    private final OutputStream mOutStream;

    private static final String TAG = "Connection Thread";

    public ConnectionThread(BluetoothSocket mSocket) {
        this.mSocket = mSocket;

        OutputStream tempOut = null;
        try {
            tempOut = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "ERROR getting OutputStream!");
        }

        mOutStream = tempOut;
    }

    public void write(String data) {
        byte[] bytes = data.getBytes();
        try {
            mOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "ERROR sending data to Bluetooth!");
        }
    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "ERROR closing the socket!");
        }
    }
}
