package com.dev.ori.albertcontrol;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fragments.BluetoothDialogFragment;
import listeners.SpeechRecognizerListener;
import services.Bluetooth;
import services.Permissions;
import services.Preferences;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private Button _recButton;
    private ImageView _bluetoothIcon;
    private SpeechRecognizer _sr;
    private TextView _speechText;
    private BluetoothDialogFragment _bluetoothFragment;
    private Preferences _preferences;
    private RelativeLayout _mainLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _preferences = new Preferences(this);

        _mainLoader = (RelativeLayout)findViewById(R.id.mainLoaderLayout);
        ImageView loader = (ImageView) findViewById(R.id.mainLoader);
//        loader.setDrawingCacheEnabled(true);
        final RotateAnimation rotateInfinity = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateInfinity.setDuration(500);
        rotateInfinity.setRepeatCount(Animation.INFINITE);
        rotateInfinity.setInterpolator(new LinearInterpolator());
        loader.setAnimation(rotateInfinity);

        _bluetoothIcon = (ImageView) findViewById(R.id.bluetoothIcon);
        final Animation flashing = new AlphaAnimation(0,1);
        flashing.setDuration(1000);
        flashing.setRepeatCount(Animation.INFINITE);
        flashing.setRepeatMode(Animation.REVERSE);
        _bluetoothIcon.setAnimation(flashing);

        _recButton = (Button) findViewById(R.id.button);
        _speechText = (TextView) findViewById(R.id.speechText);
        Permissions _permissions = new Permissions(this);
        if(Permissions.getPermission("BLUETOOTH_ADMIN")
            && Permissions.getPermission("BLUETOOTH")
            && Permissions.getPermission("ACCESS_COARSE_LOCATION")) {
            findDevices();
        }
        if(Permissions.getPermission("RECORD_AUDIO")
                && Permissions.getPermission("MODIFY_AUDIO_SETTINGS")
                && Permissions.getPermission("VIBRATE")
                && Permissions.getPermission("BLUETOOTH_ADMIN")
                && Permissions.getPermission("BLUETOOTH")
                && Permissions.getPermission("ACCESS_COARSE_LOCATION")) {
            setRecButton();
        } else {
            setRecButtonPermmisions();
        }

        setBluetoothButton();

        Permissions.requestPermissions(new String []{"RECORD_AUDIO","MODIFY_AUDIO_SETTINGS","VIBRATE","BLUETOOTH_ADMIN","BLUETOOTH","ACCESS_COARSE_LOCATION"});
//        checkPermissions();
    }

    public void buttonOnClick(View v) {
        Toast.makeText(getApplicationContext(),"button clicked",Toast.LENGTH_LONG).show();
        Button _RecButton = (Button) v;
    }

//    private void checkPermissions() {
//        int audioPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO);
//        if(audioPermission != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.RECORD_AUDIO)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            setRecButton();
//        }
//    }
    private void setRecButton() {
        final AnimationSet animations = new AnimationSet(false);
        animations.setInterpolator(new LinearInterpolator());
        animations.setRepeatCount(Animation.INFINITE);
        final Animation rotateInfinty = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateInfinty.setRepeatCount(Animation.INFINITE);
        rotateInfinty.setDuration(1200);
        rotateInfinty.setInterpolator(new LinearInterpolator());

        final Animation scaleInfinity = new ScaleAnimation(1f,1.2f,1f,1.2f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleInfinity.setRepeatCount(Animation.INFINITE);
        scaleInfinity.setRepeatMode(Animation.REVERSE);
        scaleInfinity.setDuration(750);
        animations.addAnimation(rotateInfinty);
        animations.addAnimation(scaleInfinity);
        _recButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long lastDown = 0;
                long lastDuration = 0;
                int current_volume = 0;
                final AudioManager mAlramMAnager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    current_volume = mAlramMAnager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    scaleInfinity.setRepeatCount(Animation.INFINITE);
                    scaleInfinity.setRepeatMode(Animation.REVERSE);
                    _recButton.startAnimation(animations);
//                    mAlramMAnager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    if(!Bluetooth.isConnected()) {
                        showDialog();
                    }
                    lastDown = System.currentTimeMillis();
//                    Toast.makeText(getApplicationContext(),"button down",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,10);
                    _sr.startListening(intent);
                    Log.i("started","started");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    lastDuration = System.currentTimeMillis() - lastDown;
//                    Toast.makeText(getApplicationContext(),"button up",Toast.LENGTH_SHORT).show();
                    _sr.stopListening();
                    _speechText.setText("");
                    rotateInfinty.cancel();
                    scaleInfinity.setRepeatCount(0);
                    ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(100);
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(600);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE,0);
                        }
                    });
                    t.start();
//                    mAlramMAnager.setRingerMode(ringer_mode);
                    Log.i("stopped","stopped");
                }
                return false;
            }
        });

        _sr = SpeechRecognizer.createSpeechRecognizer(this);
        _sr.setRecognitionListener(new SpeechRecognizerListener(_speechText, _bluetoothFragment));
    }

    private void setRecButtonPermmisions() {
        _recButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    List<String> requestPermission = new ArrayList<String>();
                    if(!Permissions.getPermission("RECORD_AUDIO")) {
                        requestPermission.add("RECORD_AUDIO");
                    }
                    if(!Permissions.getPermission("MODIFY_AUDIO_SETTINGS")) {
                        requestPermission.add("MODIFY_AUDIO_SETTINGS");
                    }
                    if(!Permissions.getPermission("VIBRATE")) {
                        requestPermission.add("VIBRATE");
                    }
                    if(!Permissions.getPermission("BLUETOOTH_ADMIN")
                            || !Permissions.getPermission("BLUETOOTH")
                            || !Permissions.getPermission("ACCESS_COARSE_LOCATION")) {
                        requestPermission.add("BLUETOOTH_ADMIN");
                        requestPermission.add("BLUETOOTH");
                        requestPermission.add("ACCESS_COARSE_LOCATION");
                    }
                    if(requestPermission.size() > 0) {
                        Permissions.requestPermissions(requestPermission.toArray(new String[requestPermission.size()]));
                    }
                }
                return false;
            }
        });
    }

    private void setBluetoothButton() {
        _bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> requestPermission = new ArrayList<String>();
                if(!Permissions.getPermission("RECORD_AUDIO")) {
                    requestPermission.add("RECORD_AUDIO");
                }
                if(!Permissions.getPermission("MODIFY_AUDIO_SETTINGS")) {
                    requestPermission.add("MODIFY_AUDIO_SETTINGS");
                }
                if(!Permissions.getPermission("VIBRATE")) {
                    requestPermission.add("VIBRATE");
                }
                if(!Permissions.getPermission("BLUETOOTH_ADMIN")
                        || !Permissions.getPermission("BLUETOOTH")
                        || !Permissions.getPermission("ACCESS_COARSE_LOCATION")) {
                    requestPermission.add("BLUETOOTH_ADMIN");
                    requestPermission.add("BLUETOOTH");
                    requestPermission.add("ACCESS_COARSE_LOCATION");
                }
                if(requestPermission.size() > 0) {
                    Permissions.requestPermissions(requestPermission.toArray(new String[requestPermission.size()]));
                } else {
                    showDialog();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Map<String,Boolean> curr_permissions = Permissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean RECORD_AUDIO = curr_permissions.get("RECORD_AUDIO");
        boolean MODIFY_AUDIO_SETTINGS = curr_permissions.get("MODIFY_AUDIO_SETTINGS");
        boolean VIBRATE = curr_permissions.get("VIBRATE");
        boolean BLUETOOTH_ADMIN = curr_permissions.get("BLUETOOTH_ADMIN");
        boolean BLUETOOTH = curr_permissions.get("BLUETOOTH");
        boolean ACCESS_COARSE_LOCATION = curr_permissions.get("ACCESS_COARSE_LOCATION");
        if(BLUETOOTH && BLUETOOTH_ADMIN && ACCESS_COARSE_LOCATION) {
            findDevices();
        }
        if(VIBRATE && MODIFY_AUDIO_SETTINGS && RECORD_AUDIO && BLUETOOTH && BLUETOOTH_ADMIN && ACCESS_COARSE_LOCATION) {
            setRecButton();
        }
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    init();
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
    }

    public void findDevices() {
//        String bluetoothMac = Preferences.getString("bluetoothMac");
//        String bluetoothName = Preferences.getString("bluetoothName");
//        if(bluetoothMac != "") {
//
//        } else {
            if (_bluetoothFragment == null) {
                _bluetoothFragment = new BluetoothDialogFragment();
            }
            showDialog();
//        }

//        if(Permissions.getPermission("BLUETOOTH_ADMIN")) {
//            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if (mBluetoothAdapter == null) {
//                // Device does not support Bluetooth
////                BluetoothDialogFragment newFragment = new BluetoothDialogFragment();
////                newFragment.show(getFragmentManager(),"bluetoothDevices");
//            } else {
//                if (!mBluetoothAdapter.isEnabled()) {
//                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                }
//                BluetoothDialogFragment bluetoothFragment = new BluetoothDialogFragment();
//                bluetoothFragment.show(getFragmentManager(),"bluetoothDevices");
//
//                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
//
//                if (pairedDevices.size() > 0) {
//                    // There are paired devices. Get the name and address of each paired device.
//                    for (BluetoothDevice device : pairedDevices) {
//                        String deviceName = device.getName();
//                        String deviceHardwareAddress = device.getAddress(); // MAC address
//                        bluetoothFragment.addToPairedList(deviceName);
//                    }
//                }
//
//                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//                registerReceiver(mReceiver, filter);
//                boolean discoverySuccess = mBluetoothAdapter.startDiscovery();
//                int finish = 1;
////                BluetoothDialogFragment newFragment = new BluetoothDialogFragment();
////                newFragment.show(getFragmentManager(),"bluetoothDevices");
////                BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {
////                    public void onServiceConnected(int profile, BluetoothProfile proxy) {
////                        if (profile == BluetoothProfile.HEADSET) {
//////                        mBluetoothHeadset = (BluetoothHeadset) proxy;
////                        }
////                    }
////
////                    public void onServiceDisconnected(int profile) {
////                        if (profile == BluetoothProfile.HEADSET) {
//////                        mBluetoothHeadset = null;
////                        }
////                    }
////                };
//
//// Establish connection to the proxy.
////                mBluetoothAdapter.getProfileProxy(this, mProfileListener, BluetoothProfile.HEADSET);
//            }
//
//// ... call functions on mBluetoothHeadset
//
//// Close proxy connection after use.
////            mBluetoothAdapter.closeProfileProxy(mBluetoothHeadset);
//        } else {
//
//        }
    }

    public void showDialog() {
        _bluetoothFragment.show(getFragmentManager(), "bluetoothDevices");
    }

    public void showMainLoader() {
        _mainLoader.setVisibility(View.VISIBLE);
    }
    public void hideMainLoader() {
        _mainLoader.setVisibility(View.GONE);
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Discovery has found a device. Get the BluetoothDevice
//                // object and its info from the Intent.
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address
//            }
//        }
//    };

    @Override
    public void onBackPressed(){
        if(_mainLoader.getVisibility() == View.VISIBLE) {
            hideMainLoader();
            try {
                Bluetooth.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            super.onBackPressed();
        }
    }
}
