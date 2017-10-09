package services;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import interfaces.BluetoothCallbacks;

/**
 * Created by private on 03/10/2017.
 */

public class Bluetooth implements BluetoothCallbacks {
    private static android.bluetooth.BluetoothSocket _bluetoothSocket;
    private static android.bluetooth.BluetoothAdapter _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static Handler _connectHandler = new Handler(Looper.getMainLooper());

    private static BluetoothCallbacks _callback;

    public Bluetooth() {

    }
    public Bluetooth(BluetoothCallbacks callback) {
        _callback = callback;
    }

    public static boolean startDiscovery() {
        return _bluetoothAdapter.startDiscovery();
    }

    public static boolean cancelDiscovery() {
        return _bluetoothAdapter.cancelDiscovery();
    }

    public static BluetoothAdapter getAdapter() {
        return _bluetoothAdapter;
    }

    public static BluetoothSocket getSocket() {
        return _bluetoothSocket;
    }

    public static BluetoothDevice getRemoteDevice(String mac) {
        return _bluetoothAdapter.getRemoteDevice(mac);
    }

    public static void setSocket(String mac) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        BluetoothDevice device = _bluetoothAdapter.getRemoteDevice(mac);
        if(device.fetchUuidsWithSdp()) {
//            ParcelUuid[] uuids = device.getUuids();
//            if(uuids != null) {
//                UUID uuid = uuids[uuids.length - 1].getUuid();
//                _bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
                _bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
//            }
//            _bluetoothSocket = (android.bluetooth.BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
        }
    }

    public void connect() throws IOException {
        if(_bluetoothSocket != null) {
            Thread t = new Thread(){
                public void run(){
                    try {
                        if(_bluetoothSocket != null) {
                            boolean is_connected = _bluetoothSocket.isConnected();
                            _bluetoothSocket.connect();
                            onCallbackSuccess();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        boolean isConnected = isConnected();
                        if(isConnected) {
                            onCallbackSuccess();
                        } else {
                            onCallbackError();
                        }
                    }
                }
            };
            onCallbackStart();
            t.start();
        }
    }

    public static boolean isConnected() {
        if(_bluetoothSocket != null) {
            return _bluetoothSocket.isConnected();
        } else {
            return false;
        }
    }
    public static boolean isDiscovering() {
        return _bluetoothAdapter.isDiscovering();
    }

    public static boolean isEnabled() {
        return _bluetoothAdapter.isEnabled();
    }

    public static Set<BluetoothDevice> getBondedDevices() {
        return _bluetoothAdapter.getBondedDevices();
    }

    public static void killSocket() throws IOException {
        if(_bluetoothSocket != null) {
            _bluetoothSocket.close();
            _bluetoothSocket = null;
        }
    }

    public static void socketSendMessage(String str) throws IOException {
        if(_bluetoothSocket.isConnected()) {
            OutputStream outStream = _bluetoothSocket.getOutputStream();
            byte[] msgBuffer = str.trim().getBytes();
            outStream.write(msgBuffer);
            outStream.flush();
        }
    }

    public static void socketSendMessage(ArrayList str) throws IOException {
        if(_bluetoothSocket.isConnected()) {
            OutputStream outStream = _bluetoothSocket.getOutputStream();
            byte[] msgBuffer = str.toString().replace('[', ' ').replace(']', ' ').trim().getBytes();
            outStream.write(msgBuffer);
            outStream.flush();
        }
    }

    public static void closeConnection() throws IOException {
        if(_bluetoothSocket != null) {
            _bluetoothSocket.close();
        }
    }
    @Override
    public void onCallbackStart() {
        if(_callback != null) {
            _connectHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Your UI updates here
                    _callback.onCallbackStart();
                }
            });
        }
    }

    @Override
    public void onCallbackSuccess() {
        if(_callback != null) {
            _connectHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Your UI updates here
                    _callback.onCallbackSuccess();
                }
            });
        }
    }

    @Override
    public void onCallbackError() {
        if(_callback != null) {
            _connectHandler.post(new Runnable() {
                @Override
                public void run() {
                    // Your UI updates here
                    _callback.onCallbackError();
                }
            });
        }
    }

//    public void dialogErrorMessage(String msg) {
//        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
//        b.setMessage(msg);
//        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //do things
//                dialog.dismiss();
//            }
//        });
//        AlertDialog message = b.create();
//        message.show();
//    }
}
