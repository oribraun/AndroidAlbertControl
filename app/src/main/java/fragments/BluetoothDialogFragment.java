package fragments;

import android.app.AlertDialog;
import dialogs.BluetoothDialog;
import interfaces.BluetoothCallbacks;
import services.Bluetooth;
import services.Permissions;
import services.Preferences;

import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dev.ori.albertcontrol.MainActivity;
import com.dev.ori.albertcontrol.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ori on 9/25/2017.
 */

public class BluetoothDialogFragment extends DialogFragment implements BluetoothCallbacks {

    private static AlertDialog _dialog;
    private static BluetoothDialog.Builder _builder;
//    private CharSequence[] _Items = new CharSequence[]{"a","b","c"};
    BluetoothAdapter _mBluetoothAdapter1;
    private static final int REQUEST_ENABLE_BT = 1;
    private String _bluetoothMac;
    private String _bluetoothName;

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
//        String bluetoothMac = Preferences.getString("bluetoothMac");
//        String bluetoothName = Preferences.getString("bluetoothName");
//        if(bluetoothMac != "") {
//
//        }
        _bluetoothMac = Preferences.getString("bluetoothMac");
        _bluetoothName = Preferences.getString("bluetoothName");
        if(_bluetoothMac != "") {
            if (Permissions.getPermission("BLUETOOTH_ADMIN")) {
                if (Bluetooth.getAdapter() != null) {
                    if (!Bluetooth.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        try {
                            Bluetooth.setSocket(_bluetoothMac);
                            Bluetooth bluetooth = new Bluetooth(this);
                            bluetooth.connect();
                            if(Bluetooth.isConnected()) {
                                Bluetooth.isConnected();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else {
            createDialog();
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

    public void createDialog() {
        if(_dialog == null) {
            _dialog = _builder.create();
//        _dialog.setCancelable(false);
            _dialog.setCanceledOnTouchOutside(false);
        }


        if (Permissions.getPermission("BLUETOOTH_ADMIN")) {
            if (Bluetooth.getAdapter() == null) {
                // Device does not support Bluetooth
//                BluetoothDialogFragment newFragment = new BluetoothDialogFragment();
//                newFragment.show(getFragmentManager(),"bluetoothDevices");
                this.dismiss();
                _builder.pairedFinishLoading();
                _builder.availableFinishLoading();
            } else {
                if (!Bluetooth.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    startSearch();
                }
//                BluetoothDialogFragment bluetoothFragment = new BluetoothDialogFragment();
//                bluetoothFragment.show(getFragmentManager(),"bluetoothDevices");

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
        _dialog.show();
    }

    private void startSearch() {
        _builder.cleanLists();
        Bluetooth.cancelDiscovery();
        Set<BluetoothDevice> pairedDevices = Bluetooth.getBondedDevices();

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
        boolean discoverySuccess = Bluetooth.startDiscovery();
        if(discoverySuccess) {
//                    _builder.toggleSearching();
        }
        int finish = 1;
    }
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
//                device.fetchUuidsWithSdp();
                BluetoothClass cls = device.getBluetoothClass();
                boolean is_pc = false;
                if(cls.getDeviceClass() == BluetoothClass.Device.COMPUTER_DESKTOP
                        || cls.getDeviceClass() == BluetoothClass.Device.COMPUTER_LAPTOP
                        || cls.getDeviceClass() == BluetoothClass.Device.COMPUTER_SERVER) {
                    is_pc = true;
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(is_pc) {
                    _builder.addToAvailableList((deviceName != null ? deviceName : deviceHardwareAddress), deviceHardwareAddress);
                }
            }
            else if(BluetoothDevice.ACTION_UUID .equals(action)) {
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                ParcelUuid[] uuids = device.getUuids();
                String deviceName = device.getName();
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
            else if(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                boolean co = true;
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
                boolean connecting = device.getBondState() == BluetoothDevice.BOND_BONDING;
                boolean disconnect = device.getBondState() == BluetoothDevice.BOND_NONE;
                if(disconnect) {
//                    try {
//                        _builder.setBluetoothConnected(false);
//                        Bluetooth.killSocket();
//                        _builder.hideLoader();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                } else if(connecting) {
//                    _builder.dialogErrorMessage("please approve connection on the device");
                } else if(connected) {
                    Preferences.save("bluetoothMac", device.getAddress());
                    Preferences.save("bluetoothName", device.getName());
                    SharedPreferences p = Preferences.getPreferences();
                    String address = p.getString("bluetoothMac","");
                    if(address != "") {

                    }
//                    _builder.setBluetoothConnected(true);
//                    _dialog.dismiss();
//                    _builder.dialogErrorMessage("connected");
//                    try {
//                        _builder.socketSendMessage();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1){
            // bluetooth enabled
            if(_bluetoothMac == "") {
                startSearch();
            } else {
                try {
                    Bluetooth.setSocket(_bluetoothMac);
                    Bluetooth bluetooth = new Bluetooth(this);
                    bluetooth.connect();
                    if(Bluetooth.isConnected()) {
                        Bluetooth.isConnected();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            // show error
        }
    }

    public static void dialogDismiss() {
        if(_dialog != null) {
            _dialog.dismiss();
        }
    }

    public void sendBluetoothMessage(String msg) {
        try {
            Bluetooth.socketSendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BluetoothDialog.Builder getDialogBuilder() {
        return _builder;
    }

    @Override
    public void onCallbackStart() {
        ((MainActivity) getActivity()).showMainLoader();
        _builder.onCallbackStart();
    }

    @Override
    public void onCallbackSuccess() {
        ((MainActivity) getActivity()).hideMainLoader();
        _builder.onCallbackSuccess();
    }

    @Override
    public void onCallbackError() {
        ((MainActivity) getActivity()).hideMainLoader();
        createDialog();
        _builder.onCallbackError();
    }
}