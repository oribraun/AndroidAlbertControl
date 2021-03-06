package dialogs;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.ori.albertcontrol.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import fragments.BluetoothDialogFragment;
import interfaces.BluetoothCallbacks;
import services.Bluetooth;
import services.Config;
import services.Preferences;

import static android.R.attr.data;

/**
 * Created by ori on 9/25/2017.
 */

public class BluetoothDialog extends AlertDialog {

    private static BluetoothSocket _bluetoothSocket1;
    private static ArrayAdapter<String> _pairedListAdapter;
    private static ArrayAdapter<String> _availableListAdapter;
    private static ArrayList<String> _pairedMacListAdapter = new ArrayList<String>();
    private static ArrayList<String> _availableMacListAdapter = new ArrayList<String>();
    private static ProgressBar _pairedSpinner;
    private static ProgressBar _availableSpinner;
    private static RelativeLayout _loadingLayout;
    private static RelativeLayout _wrapper;
    private static boolean _searching_devices = false;
    private static Handler _connectHandler = new Handler(Looper.getMainLooper());
    private static boolean _isBluetoothConnected = false;

    private final static int _TYPE_PAIRED = 1;
    private final static int _TYPE_AVAILABLE = 2;

    protected BluetoothDialog(Context context) {
        super(context);
    }

    protected BluetoothDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected BluetoothDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder extends  AlertDialog.Builder implements BluetoothCallbacks {


        public Builder(Context context) {
            super(context);
            setLayout(context);
        }

        public Builder(Context context, int themeResId) {
            super(context, themeResId);
        }

        public void setLayout(Context context) {
            LinearLayout layout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(params);

            layout.setGravity(Gravity.CLIP_VERTICAL);
//            layout.setPadding(5,5,5,5);

            RelativeLayout RelativeTitle = new RelativeLayout(context);
            RelativeTitle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
//            RelativeTitle.setPadding(40, 40, 40, 40);
            RelativeTitle.setBackgroundColor(Color.parseColor("#adadad"));
            TextView title = new TextView(context);
            title.setText("Bluetooth");
//            title.setPadding(40, 40, 40, 40);
//            title.setBackgroundColor(Color.parseColor("#adadad"));
            title.setTextColor(Color.parseColor("#ffffff"));
            title.setGravity(Gravity.LEFT);
            title.setTextSize(20);
            title.setPadding(40, 40, 40, 40);
            RelativeLayout.LayoutParams textviewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            textviewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

            final RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(300);
            rotate.setRepeatCount(2);
            rotate.setInterpolator(new LinearInterpolator());

            ImageButton refresh = new ImageButton(context);
            refresh.setBackgroundColor(Color.TRANSPARENT);
            refresh.setImageResource(R.drawable.refresh_icon);
            refresh.setPadding(40, 40, 40, 40);
            refresh.setScaleType(ImageButton.ScaleType.CENTER_CROP);
//            refresh.setImageResource(R.drawable.refresh_icon);
//            refresh.setText("R");
//            refresh.setGravity(Gravity.RIGHT);
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(200,200);
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

            RelativeTitle.addView(title,textviewParams);
            RelativeTitle.addView(refresh, buttonParams);

            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(rotate);
                    if(Bluetooth.getAdapter() != null) {
                        Bluetooth.cancelDiscovery();
                        Bluetooth.startDiscovery();
                    }
//                    _availableSpinner.setVisibility(View.VISIBLE);
                }
            });
            TextView pairedTitle = new TextView(context);
            pairedTitle.setText("Paired Devices");
            pairedTitle.setPadding(20, 20, 20, 20);
            pairedTitle.setBackgroundColor(Color.parseColor("#cccccc"));
            pairedTitle.setGravity(Gravity.LEFT);
            pairedTitle.setTextSize(16);

            ListView pairedList = new ListView(context);
//            pairedList.setPadding(20, 20, 20, 20);
            _pairedListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    /// Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text size 25 dip for ListView each item
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                    tv.setPadding(20, 20, 20, 20);

                    // Return the view
                    return view;
                }
            };
//            _pairedListAdapter.add("a");
//            _pairedListAdapter.add("b");
//            _pairedListAdapter.add("c");
            _pairedListAdapter.setNotifyOnChange(true);
            pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    addToPairedList("d");
                    onListItemClick(position, _TYPE_PAIRED);
                }
            });
            pairedList.setVerticalScrollBarEnabled(false);
            pairedList.setAdapter(_pairedListAdapter);

            _pairedSpinner = new ProgressBar(context);
            _pairedSpinner.setIndeterminate(true);
//            _pairedSpinner.setVisibility(View.VISIBLE);

            TextView availableTitle = new TextView(context);
            availableTitle.setText("Avaliable Devices");
            availableTitle.setPadding(20, 20, 20, 20);
            availableTitle.setBackgroundColor(Color.parseColor("#cccccc"));
            availableTitle.setGravity(Gravity.LEFT);
            availableTitle.setTextSize(16);

            ListView availableList = new ListView(context);
            _availableListAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    /// Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text size 25 dip for ListView each item
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
                    tv.setPadding(20, 20, 20, 20);

                    // Return the view
                    return view;
                }
            };
//            _availableListAdapter.add("a");
//            _availableListAdapter.add("b");
//            _availableListAdapter.add("c");
            _availableListAdapter.setNotifyOnChange(true);
            availableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    addToAvailableList("d");
                    onListItemClick(position, _TYPE_AVAILABLE);
                }
            });
            availableList.setAdapter(_availableListAdapter);

            _availableSpinner = new ProgressBar(context);
            _availableSpinner.setIndeterminate(true);

            _loadingLayout = new RelativeLayout(context);
            _loadingLayout.setBackgroundColor(Color.parseColor("#99000000"));
            final ImageView loading = new ImageButton(context);
            loading.setBackgroundColor(Color.TRANSPARENT);
            loading.setImageResource(R.drawable.refresh_icon);
            loading.setPadding(20, 20, 20, 20);
            loading.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            final RotateAnimation rotateInfinity = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateInfinity.setDuration(500);
            rotateInfinity.setRepeatCount(Animation.INFINITE);
            rotateInfinity.setInterpolator(new LinearInterpolator());
            loading.setAnimation(rotateInfinity);
            RelativeLayout.LayoutParams loadingParams = new RelativeLayout.LayoutParams(300,300);
            loadingParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            RelativeLayout.LayoutParams loadingLayoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//            loadingLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            loadingLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            loadingLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            loadingLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            _loadingLayout.setLayoutParams(loadingLayoutParams);
            _loadingLayout.setClickable(true);
            _loadingLayout.addView(loading,loadingParams);
            _loadingLayout.setVisibility(View.GONE);
            _loadingLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    loading.startAnimation(rotateInfinity);
                }
            });
//            EditText et = new EditText(context);
//            etStr = et.getText().toString();
//            TextView tv1 = new TextView(context);
//            tv1.setText("Input Student ID");

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            listParams.weight = 2;
//            titleParams.bottomMargin = 5;
            layout.addView(RelativeTitle);
            layout.addView(pairedTitle,titleParams);
            layout.addView(pairedList,listParams);
            layout.addView(_pairedSpinner,progressBarParams);
            layout.addView(availableTitle,titleParams);
            layout.addView(availableList,listParams);
            layout.addView(_availableSpinner,progressBarParams);
//            layout.addView(loadingLayout);
//            layout.addView(tv1,tv1Params);
//            layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            _wrapper = new RelativeLayout(context);
            _wrapper.addView(layout);
            _wrapper.addView(_loadingLayout);
            this.setView(_wrapper);
        }

        public void onListItemClick(int position, int type) {
            try {
                Bluetooth.killSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(Bluetooth.getSocket() == null) {
                Bluetooth.cancelDiscovery();
                availableFinishLoading();
                String mac = "";
                String name = "";
                if(type == _TYPE_PAIRED) {
                    mac = _pairedMacListAdapter.get(position).toString();
                    name = _pairedListAdapter.getItem(position).toString();
                } else if(type == _TYPE_AVAILABLE) {
                    mac = _availableMacListAdapter.get(position).toString();
                    name = _availableListAdapter.getItem(position).toString();
                }
                try {
//                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
//                        ParcelUuid[] uuids = device.getUuids();
//                        if(uuids != null) {
//                            UUID uuid = uuids[uuids.length - 1].getUuid();
//                            _bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
//                            _bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                        Bluetooth.setSocket(mac);
                        Bluetooth bluetooth = new Bluetooth(this);
                        bluetooth.connect();


//                        }

                } catch (Exception e) {
                    Log.e("ERROR", "Socket's create() method failed", e);
                    onCallbackError();
                    e.printStackTrace();
                }
//                try {
                    // Connect to the remote device through the socket. This call blocks
                    // until it succeeds or throws an exception.
//                    if(Bluetooth.getSocket() != null) {
//                        Thread t = new Thread(){
//                            public void run(){
//                                try {
//                                    if(Bluetooth.getSocket() != null) {
//                                        boolean is_connected = Bluetooth.isConnected();
//                                        _bluetoothSocket.connect();
////                                        OutputStream outStream = _bluetoothSocket.getOutputStream();
////                                        byte[] msgBuffer = String.valueOf(true).getBytes();
////                                        outStream.write(msgBuffer);
////                                        outStream.flush();
////
//                                        _connectHandler.post(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                // Your UI updates here
//                                                setBluetoothConnected(true);
//                                                dialogErrorMessage("connected");
//                                                BluetoothDialogFragment.dialogDismiss();
//                                            }
//                                        });
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                    _connectHandler.post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            // Your UI updates here
//                                            setBluetoothConnected(false);
//                                            dialogErrorMessage("could not connect device");
//                                            hideLoader();
//                                            try {
//                                                killSocket();
//                                            } catch (IOException e1) {
//                                                e1.printStackTrace();
//                                            }
//                                        }
//                                    });
////                                    try {
////                                        killSocket();
//////                                        _connectHandler.post(new Runnable() {
//////                                            @Override
//////                                            public void run() {
//////                                                // Your UI updates here
//////                                                if(!_isBluetoothConnected) {
//////                                                    dialogErrorMessage("no bluetooth device");
//////                                                }
//////                                            }
//////                                        });
//////                                        dialogErrorMessage("error connect to bluetooth device");
////                                    } catch (IOException closeException) {
////                                        Log.e("ERROR", "Could not close the client socket", closeException);
////                                    }
//                                }
//                            }
//                        };
//                        showLoader();
//                        t.start();
//                        _bluetoothSocket.connect();
//                            builder1.setCancelable(true);
//
//                            builder1.setPositiveButton(
//                                    "Yes",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            builder1.setNegativeButton(
//                                    "No",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            dialog.cancel();
//                                        }
//                                    });
//                    } else {
//                        dialogErrorMessage("no bluetooth device 2");
//                    }
//                } catch (Exception connectException) {
//                    // Unable to connect; close the socket and return.
//                    try {
//                        _bluetoothSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
//                        _bluetoothSocket.connect();
//                        dialogErrorMessage("please approve connection on the device");
//                    } catch (Exception e) {
//                        try {
//                            killSocket();
//                        } catch (IOException closeException) {
//                            Log.e("ERROR", "Could not close the client socket", closeException);
//                        }
//                    }
//                }
//                        _bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid())
            } else {
                if(Bluetooth.getSocket() != null) {
                    Log.i("connect", "c - " + String.valueOf(Bluetooth.isConnected()));
                    if(!Bluetooth.isConnected()) {
                        try {
                            Bluetooth.killSocket();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        public void addToPairedList(String name, String mac) {
            if(_pairedListAdapter.getPosition(name) == -1 && _pairedMacListAdapter.indexOf(mac) == -1) {
                _pairedListAdapter.add(name);
                _pairedMacListAdapter.add(mac);
            }
//            _pairedListAdapter.notifyDataSetChanged();
        }

        public void addToAvailableList(String name, String mac) {
            if(_availableListAdapter.getPosition(name) == -1 && _availableMacListAdapter.indexOf(mac) == -1) {
                _availableListAdapter.add(name);
                _availableMacListAdapter.add(mac);
            } else if(_availableMacListAdapter.indexOf(mac) > -1  && !mac.equals(name)) {
                int index = _availableMacListAdapter.indexOf(mac);
                _availableListAdapter.remove(name);
                _availableListAdapter.insert(name, index);
            }
//            _availableListAdapter.notifyDataSetChanged();
        }

        public void pairedFinishLoading() {
            _pairedSpinner.setVisibility(View.GONE);
        }

        public void availableFinishLoading() {
            _availableSpinner.setVisibility(View.GONE);
        }
        public void availableStartLoading() {
            _availableSpinner.setVisibility(View.VISIBLE);
        }

        public void startSearching() {
            _searching_devices = true;
        }
        public void finishSearching() {
            _searching_devices = false;
        }

        public void showLoader() {
            if(_wrapper != null) {
                int height = _wrapper.getHeight();
                ViewGroup.LayoutParams Rl = _loadingLayout.getLayoutParams();
                Rl.height = height;
                _loadingLayout.setLayoutParams(Rl);
                _loadingLayout.setVisibility(View.VISIBLE);
            }
        }
        public void hideLoader() {
            if(_loadingLayout != null) {
                _loadingLayout.setVisibility(View.GONE);
            }
        }

        public void setBluetoothConnected(boolean isConnected) {
            if(Bluetooth.getSocket() != null) {
                _isBluetoothConnected = isConnected;
            }
        }

//        public void killSocket() throws IOException {
//            if(Bluetooth.getSocket() != null) {
//                _bluetoothSocket.close();
//                _bluetoothSocket = null;
//            }
//        }

//        public void socketSendMessage(String str) throws IOException {
//            if(_bluetoothSocket.isConnected()) {
//                OutputStream outStream = _bluetoothSocket.getOutputStream();
//                byte[] msgBuffer = str.getBytes();
//                outStream.write(msgBuffer);
//                outStream.flush();
//            }
//        }

        public void dialogErrorMessage(String msg) {
            AlertDialog.Builder b = new AlertDialog.Builder(getContext());
            b.setMessage(msg);
            b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do things
                    dialog.dismiss();
                }
            });
            AlertDialog message = b.create();
            message.show();
        }

        @Override
        public void onCallbackStart() {
            showLoader();
        }
        @Override
        public void onCallbackSuccess() {
            dialogErrorMessage("connected to - " + Preferences.getString("bluetoothName"));
            Log.i(Config.TAG + "connect to", Preferences.getString("bluetoothName"));
            BluetoothDialogFragment.dialogDismiss();
            hideLoader();
        }

        @Override
        public void onCallbackError() {
            dialogErrorMessage("could not connect device, please make sure pc software is running");
            Log.e(Config.TAG + "failed connect", Preferences.getString("bluetoothName"));
            try {
                Bluetooth.killSocket();
                hideLoader();
            } catch (IOException e1) {
                Log.e("error killSocket - ", e1.getMessage());
                e1.printStackTrace();
            }
        }

        public void cleanAvailableList() {
            _availableListAdapter.clear();
            _availableMacListAdapter.clear();
            _availableListAdapter.notifyDataSetChanged();
        }

        public void cleanPairedList() {
            _pairedListAdapter.clear();
            _pairedMacListAdapter.clear();
            _pairedListAdapter.notifyDataSetChanged();
        }
    }
}
