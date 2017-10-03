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
import java.util.Set;
import java.util.UUID;

import dialogs.BluetoothDialog;
import fragments.BluetoothDialogFragment;
import interfaces.BluetoothCallbacks;

/**
 * Created by private on 03/10/2017.
 */

public class Bluetooth implements BluetoothCallbacks {
    private static android.bluetooth.BluetoothSocket _bluetoothSocket;
    private static android.bluetooth.BluetoothAdapter _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static Handler _connectHandler = new Handler(Looper.getMainLooper());

    public Bluetooth() {

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
//                _bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
//            }
            _bluetoothSocket = (android.bluetooth.BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(device, 1);
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
                            onSuccess();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onError();
                    }
                }
            };
            onStart();
            t.start();
        }
    }

    public static boolean isConnected() {
        return _bluetoothSocket.isConnected();
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
            byte[] msgBuffer = str.getBytes();
            outStream.write(msgBuffer);
            outStream.flush();
        }
    }
    @Override
    public void onStart() {
        _connectHandler.post(new Runnable() {
            @Override
            public void run() {
                // Your UI updates here
                BluetoothDialog.Builder builder = BluetoothDialogFragment.getDialogBuilder();
                if(builder != null) {
                    builder.onStart();
                }
            }
        });
    }

    @Override
    public void onSuccess() {
        _connectHandler.post(new Runnable() {
            @Override
            public void run() {
            // Your UI updates here
                BluetoothDialog.Builder builder = BluetoothDialogFragment.getDialogBuilder();
                if(builder != null) {
                    builder.onSuccess();
                }
            }
        });
    }

    @Override
    public void onError() {
        _connectHandler.post(new Runnable() {
            @Override
            public void run() {
                // Your UI updates here
                BluetoothDialog.Builder builder = BluetoothDialogFragment.getDialogBuilder();
                if(builder != null) {
                    builder.onError();
                }
            }
        });
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
