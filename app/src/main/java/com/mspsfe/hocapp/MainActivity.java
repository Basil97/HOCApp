package com.mspsfe.hocapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter btAdapter;
    private ConnectionThread connectionThread;

    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int CONNECTION_STATUS = 2;
    private static final int CODE = 123;
    private Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        RelativeLayout mainLayout = findViewById(R.id.mainLayout);
        mainLayout.setOnDragListener(new MyLayoutDragListener());

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == CONNECTION_STATUS) {
                    if (msg.arg1 == 1) {
                        Toast.makeText(MainActivity.this, "Connected to " + msg.obj, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.connect:
                if (!btAdapter.isEnabled()) {
                    Toast.makeText(this, "Turn Bluetooth ON first", Toast.LENGTH_LONG).show();
                    break;
                }
                Intent intent = new Intent(this, BluetoothConnectActivity.class);
                startActivityForResult(intent, CODE);
                return true;

            case R.id.send:
                //TODO get the data and send through Bluetooth (Khalid)
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE) {
            if (resultCode == RESULT_OK) {
                String device = data.getStringExtra("Device");
                String name = device.substring(0, device.length() - 18);
                String address = device.substring(device.length() - 17);
                connectToBluetooth(name, address);
            }
        }
    }

    private static final String CONNECTION_TAG = "BTSocket Creation";
    private void connectToBluetooth(final String name, final String address) {
        Toast.makeText(this, "Connecting to " + name, Toast.LENGTH_LONG).show();
        new Thread() {
            BluetoothSocket btSocket;
            public void run() {
                boolean failed = false;
                BluetoothDevice device = btAdapter.getRemoteDevice(address);
                try {
                    btSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    Log.e(CONNECTION_TAG, "ERROR creating Bluetooth Socket");
                    failed = true;
                }
                try {
                    btSocket.connect();
                } catch (IOException e) {
                    Log.e(CONNECTION_TAG, "ERROR connecting to socket");
                    failed = true;
                    try {
                        btSocket.close();
                        handler.obtainMessage(CONNECTION_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e1) {
                        Log.e(CONNECTION_TAG, "ERROR closing socket");
                    }
                }
                if (!failed) {
                    connectionThread = new ConnectionThread(btSocket);
                    handler.obtainMessage(CONNECTION_STATUS, 1, -1, name)
                            .sendToTarget();
                }
            }
        }.start();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            Method method = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket)method.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(CONNECTION_TAG, "ERROR creating Bluetooth Socket");
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_move:
            case R.id.nav_handle:
            case R.id.nav_loop:
            case R.id.nav_condition:
                inflateView(id);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inflateView(int id) {
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup mainLayout = findViewById(R.id.mainLayout);
        View view = null;
        switch (id) {
            case R.id.nav_move:
                view = inflater.inflate(R.layout.motion, null);
                break;
            case R.id.nav_handle:
                view = inflater.inflate(R.layout.handle, null);
                break;
            case R.id.nav_loop:
                view = inflater.inflate(R.layout.loop, null);
                break;
            case R.id.nav_condition:
                view = inflater.inflate(R.layout.condition, null);
                break;
        }
        if (view == null) return;
        view.setX(50);
        view.setY(50);
        view.setOnTouchListener(touchListener);
        view.setOnDragListener(new MyItemDragListener());
        view.setTag("View");
        view.requestFocus();
        mainLayout.addView(view);
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    ClipData clipData = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    view.startDrag(clipData, shadowBuilder, view, 0);
                    view.setVisibility(View.INVISIBLE);
                    return true;
            }
            return false;
        }
    };
}
