package fragments;

import android.app.AlertDialog;
import dialogs.BluetoothDialog;
import services.Permissions;

import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dev.ori.albertcontrol.R;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ori on 9/25/2017.
 */

public class BluetoothDialogFragment extends DialogFragment {

    private AlertDialog _dialog;
    private BluetoothDialog.Builder _builder;
//    private CharSequence[] _Items = new CharSequence[]{"a","b","c"};
    ArrayAdapter<String> _Adapter;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        _builder = new BluetoothDialog.Builder(getActivity());
//        _Adapter = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), android.R.layout.select_dialog_item);
//        _Adapter.add("a");
//        _Adapter.add("b");
//        _Adapter.add("c");
//        _builder.setTitle(R.string.bluetooth_devices)
//                .setAdapter(_Adapter, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.dismiss();
//                        addItems("d");
//                        // The 'which' argument contains the index position
//                        // of the selected item
//                    }
//                });
        _dialog = _builder.create();
//        _dialog.setCancelable(false);
        _dialog.setCanceledOnTouchOutside(false);

        if(Permissions.getPermission("BLUETOOTH_ADMIN")) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
//                BluetoothDialogFragment newFragment = new BluetoothDialogFragment();
//                newFragment.show(getFragmentManager(),"bluetoothDevices");
                _builder.pairedFinishLoading();
                _builder.availableFinishLoading();
            } else {
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
//                BluetoothDialogFragment bluetoothFragment = new BluetoothDialogFragment();
//                bluetoothFragment.show(getFragmentManager(),"bluetoothDevices");

                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    for (BluetoothDevice device : pairedDevices) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        _builder.addToPairedList(deviceName, deviceHardwareAddress);
                    }
                }
                _builder.pairedFinishLoading();

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
                filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
                filter.addAction(BluetoothDevice.ACTION_UUID);
                getActivity().registerReceiver(mReceiver, filter);
                boolean discoverySuccess = mBluetoothAdapter.startDiscovery();
                if(discoverySuccess) {
//                    _builder.toggleSearching();
                }
                int finish = 1;
//                BluetoothDialogFragment newFragment = new BluetoothDialogFragment();
//                newFragment.show(getFragmentManager(),"bluetoothDevices");
//                BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
//                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
//                        if (profile == BluetoothProfile.HEADSET) {
////                        mBluetoothHeadset = (BluetoothHeadset) proxy;
//                        }
//                    }
//
//                    public void onServiceDisconnected(int profile) {
//                        if (profile == BluetoothProfile.HEADSET) {
////                        mBluetoothHeadset = null;
//                        }
//                    }
//                };

// Establish connection to the proxy.
//                mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);
            }

// ... call functions on mBluetoothHeadset

// Close proxy connection after use.
//            mBluetoothAdapter.closeProfileProxy(mBluetoothHeadset);
        } else {

        }
//        ListView DialogItems = _dialog.getListView();
//        DialogItems.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//// do your staff here
//                        addItems("d");
//                    }
//                });
        return _dialog;
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                _builder.addToAvailableList((deviceName != null ?  deviceName :  deviceHardwareAddress), deviceHardwareAddress);
            }
            else if(BluetoothDevice.ACTION_UUID .equals(action)) {
//                Boolean hasSPP = false;
//
//                // Get the device
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                // Get the UUID
//                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
//                final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//                if(uuidExtra != null) {
//                    for (int i = 0; i < uuidExtra.length; i++) {
//                        ParcelUuid parcelUuid = (ParcelUuid) uuidExtra[i];
//
//                        if (parcelUuid.getUuid().equals(SPP_UUID)) {
//                            hasSPP = true;
//                        }
//                    }
//                }
////                _builder.availableFinishLoading();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                _builder.availableStartLoading();
                _builder.startSearching();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                _builder.availableFinishLoading();
                _builder.finishSearching();
            }
            else if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                boolean connected = device.getBondState() == BluetoothDevice.BOND_BONDED;
//                if(!connected) {
//                    try {
//                        _builder.setBluetoothConnected(false);
//                        _builder.killSocket();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    _builder.setBluetoothConnected(true);
//                    _dialog.dismiss();
//                }
//                Log.v("connected = " , String.valueOf(connected));
            }
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                boolean connected = device.getBondState() == BluetoothDevice.BOND_BONDED;
                if(!connected) {
                    if(device.getBondState() == BluetoothDevice.BOND_BONDING) {
                        _builder.dialogErrorMessage("please approve connection on the device");
                    }
                    try {
                        _builder.setBluetoothConnected(false);
                        _builder.killSocket();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    _builder.setBluetoothConnected(true);
                    _dialog.dismiss();
                    _builder.dialogErrorMessage("connected");
                }
                Log.v("connected = " , String.valueOf(connected));
            }
            else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                try {
                    int pin=intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 1234);
                    //the pin in case you need to accept for an specific pin
//                    Log.d(TAG, "Start Auto Pairing. PIN = " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",1234));
                    byte[] pinBytes;
                    pinBytes = (""+pin).getBytes("UTF-8");
//                    device.setPin(pinBytes);
                    //setPairing confirmation if neeeded
//                    device.setPairingConfirmation(true);
                } catch (Exception e) {
//                    Log.e(TAG, "Error occurs when trying to auto pair");
                    e.printStackTrace();
                }
            }
        }
    };

    public void addItems(String str) {
        _Adapter.add(str);
        _Adapter.notifyDataSetChanged();
//        String actionResultValue = "action";
//        ListView dropdown= _dialog.getListView();
//        ListAdapter dropdownAdapter= dropdown.getAdapter();
//        //
//        StringBuilder listItem0Text=
//                (StringBuilder) dropdownAdapter.getItem(0);
//        listItem0Text.delete(0, listItem0Text.length());
//        listItem0Text.append("Red " + actionResultValue);
//        //
//        dropdown.invalidateViews();
    }
}