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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Activity that handles incoming and outgoing bluetooth connection
 * ConnectThread sends outgoing bluetooth connection
 * AccpetThread listens incoming bluetooth connection request
 * reads gesture string sent by the phone and save it in the class variable "pose"
 * sends gesture string to other activities using LocalBroadcastManager
 * if an activity is the current activity, it takes actions
 * if an activity is not the current activity, it doesn't do anything
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CONNECT_DEVICE = 0;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String NAME = "WearablePhone";
    private static final UUID MY_UUID = UUID.fromString("d786565c-9d01-4fac-a40f-baa8a5eb53cb");

    BluetoothAdapter mBluetoothAdapter;
    public static String pose;
    InputStream inputStream;
    OutputStream outputStream;
    public TextView tv_msg;
    private Button button;


    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private AcceptThread mAcceptThread;

    /*
    A handler communicates with the main ui thread
    sends the result of bluetooth connection from ConnectThread or AcceptThread
    to the main thread to display it as toast message
     */

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
        tv_msg.setTextColor(Color.BLUE);

        turnOnBluetooth();
        // Log.i(TAG, "MainActivity Thread Name = " + Thread.currentThread().getName());

        // listen to incoming connection
        mAcceptThread = new AcceptThread();
        mAcceptThread.start();

        //TODO FRAGMENT
        // https://www.youtube.com/watch?v=aSRJynmOvFo&index=7&list=PLonJJ3BVjZW4lMlpHgL7UNQSGMERcDzHo
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
            // Launch the DeviceListActivity to see other bluetooth devices and do scan
            case R.id.searchBluetooth: {
                Intent searchIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(searchIntent, REQUEST_CONNECT_DEVICE);
                return true;
            }
        }
        return false;
    }

    /**
     * send the gesture string received to all other activities
     *
     * @param gesture one of the myo gestures
     */
    private void sendGesture(String gesture) {
        Intent intent = new Intent("myo-event");
        // add data
        intent.putExtra("gesture", gesture);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void turnOnBluetooth() {
        // check if this device supports bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Your device supports bluetooth", Toast.LENGTH_SHORT).show();
        }

        // turn bluetooth on if off
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, RESULT_OK);
        }
    }

    /**
     * check the result returned by startActivityForResult
     *
     * @param requestCode
     * @param resultCode
     * @param data        contains the mac address for the device to connect to
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
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
                    mConnectThread = new ConnectThread(device);
                    mConnectThread.start();
                    Log.d(TAG, "device = " + device);
                }
        }
    }

    /**
     * send a bluetooth connection request to another bluetooth device
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket connectBTSocket;
        private final BluetoothDevice mmDevice;

        /**
         * Constructor
         *
         * @param device BluetoothDevice (object) to connect to
         */
        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to connectBTSocket,
            // because connectBTSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;
//            Log.i(TAG, "mmDevice is " + mmDevice.toString());

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("d786565c-9d01-4fac-a40f-baa8a5eb53cb"));
//                Log.i(TAG, "createRfcommSocket");

            } catch (IOException e) {

            }
            connectBTSocket = tmp;
            Log.i(TAG, "Bluetooth Socket in ConnectThread is " + tmp.toString());
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
//                Log.i(TAG, "connect() in run()");
                connectBTSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                Log.e(TAG, "unable to connect " + connectException);
                try {
                    connectBTSocket.close();
                    Log.i(TAG, "connectBTSocket closed");
                } catch (IOException closeException) {
                    Log.e(TAG, "unable to close() " + closeException);
                }
                return;
            }

            if (connectBTSocket.isConnected()) {
                // notify the user whether the connection has succeeded or not
                toastConnectionResult(true);
//                Log.e(TAG, "Socket connected");
//                Log.i(TAG, "Remote Device = " + connectBTSocket.getRemoteDevice().toString());
                connect();
            } else {
                // notify user that connection failed
                toastConnectionResult(false);
            }

        }

        /**
         * Start ConnectedThread to manage incoming myo gestures
         */
        private void connect() {


            ConnectedThread connectedThread = new ConnectedThread(connectBTSocket);
            connectedThread.start();

            // Cancel the thread that completed the connection
            if (mConnectThread != null) {
//                Log.i("TAG", "cancelling mConnectThread");
                // uncommenting the next line will give InputStream error
                // you need the socket to be conneceted for input/outputstream to work
                // mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                Log.i("TAG", "Closing socket");
                connectBTSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error in closing the socket");
            }
        }
    }

    /**
     * This thread runs during a bluetooth connection with a remote device
     * It handles all incoming transmissions from other devices
     */
    private class ConnectedThread extends Thread {

        private final BluetoothSocket connectedBTSocket;
//        private final InputStream mmInStream = null;
//        private final OutputStream mmOutStream = null;

        /**
         * Constructor
         *
         * @param socket socket to connect to
         */
        public ConnectedThread(BluetoothSocket socket) {
            // Log.i(TAG, "ConnectedThread");
            connectedBTSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = connectedBTSocket.getInputStream();
                tmpOut = connectedBTSocket.getOutputStream();

                inputStream = tmpIn;
                outputStream = tmpOut;
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created");
            }


            // Log.i(TAG, mmInStream.toString());
        }

        @Override
        public void run() {
//            Log.i(TAG, "ConnectedThread run()");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            byte[] buffer = new byte[1024];
            int bytes = 0;

            // Keep listening to the InputStream while connected
            while (connectedBTSocket.isConnected()) {
//            while (true) {

                handleBTMsg();

            }
        }

        public void cancel() {
            try {
                Log.i(TAG, "closing socket");
                connectedBTSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() (socket) failed", e);
            }
        }

    }


    /**
     * AcceptThread listens and accepts connection request from WearablePhone
     */
    private class AcceptThread extends Thread {

        // socket listening to incoming connection
        private final BluetoothServerSocket mmServerSocket;

        // constructor
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
//            Log.i(TAG, "ACCEPTTHREAD");

            // Create a new listening server socket
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                //tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
//                Log.e(TAG, "mmServerSocket is " + tmp, e);
            }
            mmServerSocket = tmp;
//            Log.i(TAG, "mmServerSocket is " + tmp);
        }

        // executed when the thread.start() is executed
        public void run() {
//            Log.i(TAG, "AcceptThread run()");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            // socket to the incoming connection
            BluetoothSocket socket = null;

            // Accept an incoming socket connection
            // Keep listening until a socket is returned or exception occurs
            while (socket == null) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "IOException: accept() failed " + e);
                    break;
                }
            }

            // notify user
            toastConnectionResult(socket != null);

            if (socket.isConnected()) {
                try {
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                    mmServerSocket.close();

                } catch (IOException e) {
                    Log.e(TAG, "IO STREAM after accpet()" + e);
                    // break;
                }
            }


            // Receive and handle message/course object from bluetooth connection
            while (socket != null) {

                // Do work to manage the connection (in a separate thread)
                // TODO: manageConnectedSocket(socket);
                // Log.i(TAG, "Connection accepted");

                handleBTMsg();
            }

//            Log.i(TAG, "END RUN() ");
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

    private void handleBTMsg() {
        // store myo gesture string in buffer
        byte[] buffer = new byte[1024];
        int bytes;

        try {
            // Read from the InputStream
            bytes = inputStream.read(buffer);
            //inputStream.close();

//                    Log.i(TAG, "read " + bytes + " bytes");
            // update textview
            if (bytes > 0) {

                // decode the json bytes into string and construct jsonobject
                String jsonString = new String(buffer, 0, bytes);
                Log.i(TAG, "jsonString = " + jsonString);
                JSONObject json = new JSONObject(jsonString);

                if (json.has(Constants.MYO_GESTURE)) {
                    pose = json.getString(Constants.MYO_GESTURE);
                    sendGesture(pose);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!(pose.equals("rest"))) {
                                tv_msg.setText(pose);
                            }
                        }
                    });

//                Log.i(TAG, "pose = " + pose);
//                    Log.i(TAG, "Thread Name = " + Thread.currentThread().getName());
                } else {
                    // start ContentActivity
                    Log.i(TAG, "start ContentActivity");
                    Intent intent = new Intent(this, ContentActivity.class);
                    intent.putExtra(Constants.JSON_STRING, jsonString);
                    startActivity(intent);

                }

            }
        } catch (IOException e) {
//            Log.e(TAG, "read from InputStream failed in handleBTMsg" + e);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * create toast message the result of bluetooth connection attemp
     *
     * @param result true: success, false: failure
     */
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

    /**
     * start CourseListActivity when "Go to Courses" button is clicked
     * This will be removed once login is implemented
     *
     * @param view
     */
    public void navigateToCourseListActivity(View view) {
        Intent intent = new Intent(this, CourseListActivity.class);
//        startActivity(intent);
    }
}
