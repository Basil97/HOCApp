package com.mspsfe.hocapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Set;

public class BluetoothConnectActivity extends AppCompatActivity {

    private ArrayAdapter<String> devicesAdapter;
    private BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect);

        devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        listPairedDevices();

        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(devicesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!btAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is turned OFF", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("Device", devicesAdapter.getItem(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void listPairedDevices() {
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();

        for (BluetoothDevice device : devices) {
            devicesAdapter.add(device.getName() + "\n" + device.getAddress());
        }
    }
}
