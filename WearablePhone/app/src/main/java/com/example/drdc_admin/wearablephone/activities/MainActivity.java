package com.example.drdc_admin.wearablephone.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.drdc_admin.wearablephone.Constants;
import com.example.drdc_admin.wearablephone.R;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // TODO: Login opens up the MoverioApp

    TextView tv_myoMsg;
    VideoView vv_test;
    static BluetoothAdapter mBluetoothAdapter;
    String btMsg;
    static InputStream inputStream;
    public static OutputStream outputStream;
    public static ObjectOutputStream oos;

    private static final String TAG = "MainActivity";
    private static final String NAME = "WearablePhone";
    private static final UUID MY_UUID = UUID.fromString("d786565c-9d01-4fac-a40f-baa8a5eb53cb");

    private static final int REQUEST_CONNECT_DEVICE = 0;
    private static final int REQUEST_ENABLE_BT = 1;

    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    // TODO moodleThread that deals with interaction with moodle
    // QUESTION here or in MoverioApp;
    private final android.os.Handler mHandler = new android.os.Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.BLUETOOTH_CONNECTION_RESULT), Toast.LENGTH_LONG).show();
            }

        }
    };

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {
        // onConnect() is called whenever a Myo has been connected.
        @Override
        public void onConnect(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            tv_myoMsg.setText("Myo Connected");
            tv_myoMsg.setTextColor(Color.CYAN);

        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
            tv_myoMsg.setText("Myo Disconnected");
            tv_myoMsg.setTextColor(Color.RED);
        }

        // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {

            tv_myoMsg.setText("Myo unlocked");
            tv_myoMsg.setTextColor(Color.CYAN);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {

            tv_myoMsg.setText("Myo locked");
            tv_myoMsg.setTextColor(Color.RED);
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.

            switch (pose) {
                case UNKNOWN:
                    tv_myoMsg.setText(getString(R.string.hello_world));
                    break;
                case REST:
                    btMsg = "rest";
                    tv_myoMsg.setText(getString(R.string.pose_rest));
                    break;
                case DOUBLE_TAP:
                    int restTextId = R.string.hello_world;
                    switch (myo.getArm()) {
                        case LEFT:
                            restTextId = R.string.arm_left;
                            break;
                        case RIGHT:
                            restTextId = R.string.arm_right;
                            break;
                    }
                    tv_myoMsg.setText(getString(restTextId));
                    break;
                case FIST:
                    btMsg = "fist";
                    tv_myoMsg.setText(getString(R.string.pose_fist));
                    break;
                case WAVE_IN:
                    btMsg = "wave in";
                    tv_myoMsg.setText(getString(R.string.pose_wavein));
                    break;
                case WAVE_OUT:
                    btMsg = "wave out";
                    tv_myoMsg.setText(getString(R.string.pose_waveout));
                    break;
                case FINGERS_SPREAD:
                    btMsg = "fingers spread";
                    tv_myoMsg.setText(getString(R.string.pose_fingersspread));
                    break;
            }

            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
                // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
                // hold the poses without the Myo becoming locked.
                myo.unlock(Myo.UnlockType.HOLD);

                // Notify the Myo that the pose has resulted in an action, in this case changing
                // the text on the screen. The Myo will vibrate.
                myo.notifyUserAction();
            } else {
                // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
                // stay unlocked while poses are being performed, but lock after inactivity.
                myo.unlock(Myo.UnlockType.TIMED);
            }

            if (btMsg != null) {
//            Log.i(TAG, "ONPOSE btMsg is " + btMsg);
                JSONObject json = new JSONObject();
                try {
                    json.put(Constants.MYO_GESTURE, btMsg);
                    Log.i(TAG, json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendMsgToMoverio(json.toString().getBytes());
            }
        }
    };


    private void sendMsgToMoverio(byte[] buffer) {
//        Log.e(TAG, "sendMsgToMoverio");
        if (outputStream != null) {
            try {
                outputStream.write(buffer);
                outputStream.flush();
                Log.i(TAG, "btMsg is " + btMsg);
                // outputStream.close();
                btMsg = "";

            } catch (IOException e) {
                Log.e(TAG, "write(buffer) failed " + e);
            }
        } else {
//            Log.e(TAG, "outputStream is NULL");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate");

        tv_myoMsg = (TextView) findViewById(R.id.myoMsg);

        createDialog();

        // for MYO
        // First, we initialize the Hub singleton with an application identifier
        // Next, register for DeviceListener callbacks.
        Hub hub = initiateHub();
        hub.addListener(mListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Your device does not support bluetooth", Toast.LENGTH_SHORT).show();
        }

        // server
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
    protected void onDestroy() {
        super.onDestroy();
        // TODO stop acceptThread
        // make acceptThread a global variable
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
            case R.id.searchMyo: {
                Intent scanIntent = new Intent(this, ScanActivity.class);
                startActivity(scanIntent);
                return true;
            }
        }
        return false;
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
//                    Log.d(TAG, "device = " + device);
                }

        }

    }

    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
            Log.i(TAG, "end ConnectThread()");
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread run() ");
            // set name of the thread
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                if (mmSocket.isConnected()) {
                    Log.i(TAG, "Socket Connected");
                    toastConnectionResult(true);
//                    outputStream = mmSocket.getOutputStream();
//                    inputStream = mmSocket.getInputStream();
                } else {
                    toastConnectionResult(false);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Connection failed " + e);
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " +
                            " socket during connection failure", e2);
                }

                return;
            }

            mConnectedThread = new ConnectedThread(mmSocket);

/*
            // Reset the ConnectThread because we're done
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }
*/
            // Start the connected thread
            //connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + " socket failed", e);
            }
        }
    }


    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        // private String mSocketType;

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
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "IOException: accept() failed " + e);
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
//                    Log.i(TAG, socket.toString());
//                    Log.i(TAG, "Remote device = " + socket.getRemoteDevice().toString());

                    toastConnectionResult(true);
                    // Do work to manage the connection (in a separate thread)
                    // TODO: manageConnectedSocket(socket);
//                    Log.i(TAG, "Connection accepted");
                    try {
                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();
                        oos = new ObjectOutputStream(outputStream);
                        // mmServerSocket.close();

                    } catch (IOException e) {
                        Log.e(TAG, "IOException after accpet()" + e);
                        break;
                    }
                } else {
                    toastConnectionResult(false);
                }
            }
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

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread: ");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                inputStream = tmpIn;
                outputStream = tmpOut;
                oos = new ObjectOutputStream(outputStream);
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }


        }

        public void run() {

        }


        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer)
//                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * for myo connection
     *
     * @return
     */
    private Hub initiateHub() {
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Could not initialize the Hub.");
            finish();
            return hub;
        }
        return hub;
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please connect to the smart glass and arm band using two icons at the top corner");
        builder.setTitle("Attention");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * pass the result of bluetooth connection to the mainthread through Handler
     *
     * @param result true if connection succeeded, false if failed
     */
    private void toastConnectionResult(boolean result) {
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
        startActivity(intent);
    }

}
