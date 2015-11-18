package com.example.drdc_admin.moverioapp.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.drdc_admin.moverioapp.Constants;
import com.example.drdc_admin.moverioapp.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CONNECT_DEVICE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String NAME = "WearablePhone";
    private static final UUID MY_UUID = UUID.fromString("d786565c-9d01-4fac-a40f-baa8a5eb53cb");

    BluetoothAdapter mBluetoothAdapter;
    public static String pose = "";
    InputStream inputStream;
    OutputStream outputStream;
    public TextView tv_msg;
    private Button button;


    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private AcceptThread mAcceptThread;

    private final android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
//            Log.i(TAG, "handleMessage");
            switch (msg.arg1) {
                case Constants.MESSAGE_TOAST:
//                    Log.i(TAG, "MESSAGE_TOAST");
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.BLUETOOTH_CONNECTION_RESULT), Toast.LENGTH_LONG).show();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate");
        tv_msg = (TextView) findViewById(R.id.myoMsg);
        tv_msg.setTextColor(Color.GREEN);

//        button = (Button) findViewById(R.id.bt_tmp);
//        button.setPressed(true);

        turnOnBluetooth();
        // Log.i(TAG, "MainActivity Thread Name = " + Thread.currentThread().getName());

        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Launch the DeviceListActivity to see devices and do scan
            case R.id.searchBluetooth: {
                Intent searchIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(searchIntent, REQUEST_CONNECT_DEVICE);
                return true;
            }
        }
        return false;
    }

    /**
     * send the gesture recognized to all other activities
     * @param gesture one of the myo gestures
     */
    private void sendGesture(String gesture) {
        Intent intent = new Intent("myo-event");
        // add data
        intent.putExtra("gesture", gesture);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void turnOnBluetooth() {
        // check if bluetooth is turned on
        // turn it on if off
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Your device supports bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // connect to the device
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BluetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    // mChatService.connect(device, secure);
                    mConnectThread = new ConnectThread(device);
                    mConnectThread.start();
                    Log.d(TAG, "device = " + device);
                }

        }

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mSocket,
            // because mSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
            Log.i(TAG, "mmDevice is " + mmDevice.toString());

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("d786565c-9d01-4fac-a40f-baa8a5eb53cb"));
                Log.i(TAG, "createRfcommSocket");

            } catch (IOException e) {

            }
            mmSocket = tmp;
            Log.i(TAG, "mSocket is " + tmp.toString());
        }

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            if (mBluetoothAdapter.isDiscovering()) {
                Log.i(TAG, "canceling discovery");
                // Cancel discovery because it will slow down the connection
                mBluetoothAdapter.cancelDiscovery();
            }

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Log.i(TAG, "connect() in run()");
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.e(TAG, "unable to connect " + connectException);
                try {
                    mmSocket.close();
                    Log.i(TAG, "mSocket closed");
                } catch (IOException closeException) {
                    Log.e(TAG, "unable to close() " + closeException);
                }
                return;
            }

            if (mmSocket.isConnected()) {
                toastConnectionResult(true);
//                Log.e(TAG, "Socket connected");
//                Log.i(TAG, "Remote Device = " + mmSocket.getRemoteDevice().toString());
                // Do work to manage the connection (in a separate thread)
                // manageConnectedSocket(mSocket);
                try {
                    inputStream = mmSocket.getInputStream();
                    outputStream = mmSocket.getOutputStream();

                } catch (IOException inoutStream) {
                    Log.e(TAG, "I/O Stream " + inoutStream);
                }
                connect();
            } else {
                toastConnectionResult(false);
            }

        }

        private void connect() {
            // Cancel the thread that completed the connection
            // uncommenting will give InputStream error
            if (mConnectThread != null) {
                Log.i("TAG", "cancelling mConnectThread");
                //mConnectThread.cancel();
                mConnectThread = null;
            }

            ConnectedThread connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                Log.i("TAG", "Closing socket");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error in closing the socket");
            }
        }
    }

    /**
     * This thread runs during a bluetooth connection with a remote device
     * It handles all incoming transmissions
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            // Log.i(TAG, "ConnectedThread");
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mSocket.getInputStream();
                tmpOut = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            // Log.i(TAG, mmInStream.toString());
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.i(TAG, "ConnectedThread run()");

            byte[] buffer = new byte[1024];
            int bytes = 0;

            // Keep listening to the InputStream while connected
            while (true) {

                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    Log.i(TAG, "read " + bytes + " bytes");
                    // update textview
                    if (bytes > 0) {
                        pose = new String(buffer, 0, bytes);
                        sendGesture(pose);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_msg.setText(pose);
                            }
                        });

                        Log.i(TAG, "pose = " + pose);
                        // Log.i(TAG, "Thread Name = " + Thread.currentThread().getName());

                    }

                    // TODO insert the right fragment (course list, lesson blah blah)



                } catch (Exception e) {
                    Log.e(TAG, "read from InputStream failed " + e);
                }

            }
        }

        public void cancel() {
            try {
                Log.i(TAG, "closing socket");
                mSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() (socket) failed", e);
            }
        }

    }

    // TODO need AcceptThread to accept connection request from WearablePhone

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            Log.i(TAG, "ACCEPTTHREAD");

            // Create a new listening server socket
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                //tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "mmServerSocket is " + tmp, e);
            }
            mmServerSocket = tmp;
            Log.i(TAG, "mmServerSocket is " + tmp);
        }

        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            BluetoothSocket socket = null;

            // Keep listening until exception occurs or a socket is returned
            while (socket == null) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "IOException: accept() failed " + e);
                    break;
                }

//                Log.i(TAG, "END WHILE ");
            }


            // notify user
            toastConnectionResult(socket != null);

            byte[] buffer = new byte[1024];
            int bytes;

            while (socket != null) {

                // Do work to manage the connection (in a separate thread)
                // TODO: manageConnectedSocket(socket);
                // Log.i(TAG, "Connection accepted");
                try {
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    // mmServerSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "IO STREAM after accpet()" + e);
                    break;
                }

                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);

                    //inputStream.close();

                    Log.i(TAG, "read " + bytes + " bytes");

                    // update textview
                    if (bytes > 0) {
                        pose = new String(buffer, 0, bytes);
                        sendGesture(pose);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_msg.setText(pose);
                            }
                        });

                        Log.i(TAG, "pose = " + pose);
                        // Log.i(TAG, "Thread Name = " + Thread.currentThread().getName());

                        pose = null;
                    }
                    // TODO insert the right fragment (course list, lesson blah blah)
                } catch (Exception e) {
                    Log.e(TAG, "read from InputStream failed " + e);
                }
            }

            Log.i(TAG, "END RUN() ");
        }

        /**
         * Will cancel the listening socket, and cause the thread to finish
         */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {

            }
        }
    }
    private void toastConnectionResult(boolean result) {

//        Log.i(TAG, "toastConnectionResult");
        Message msg = Message.obtain();
        msg.arg1 = Constants.MESSAGE_TOAST;
        Bundle bundle = new Bundle();
        if (result) {
            bundle.putString(Constants.BLUETOOTH_CONNECTION_RESULT, "Bluetooth Connected");
        } else {
            bundle.putString(Constants.BLUETOOTH_CONNECTION_RESULT, "Bluetooth Connection Failed");
        }
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }
    public void navigateToCourseListActivity(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
        startActivity(intent);
    }
}
