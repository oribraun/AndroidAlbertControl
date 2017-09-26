package dialogs;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Color;
import android.os.ParcelUuid;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by ori on 9/25/2017.
 */

public class BluetoothDialog extends AlertDialog {

    private static BluetoothSocket _bluetoothSocket;
    private static ArrayAdapter<String> _pairedListAdapter;
    private static ArrayAdapter<String> _availableListAdapter;
    private static ArrayList<String> _pairedMacListAdapter = new ArrayList<String>();
    private static ArrayList<String> _availableMacListAdapter = new ArrayList<String>();
    private static ProgressBar _pairedSpinner;
    private static ProgressBar _availableSpinner;
    private static boolean _searching_devices = false;
    protected BluetoothDialog(Context context) {
        super(context);
    }

    protected BluetoothDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected BluetoothDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder extends  AlertDialog.Builder {

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

            TextView title = new TextView(context);
            title.setText("Bluetooth");
            title.setPadding(40, 40, 40, 40);
            title.setBackgroundColor(Color.parseColor("#adadad"));
            title.setTextColor(Color.parseColor("#ffffff"));
            title.setGravity(Gravity.LEFT);
            title.setTextSize(20);

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
                    if(_bluetoothSocket == null && !_searching_devices) {
                        String mac = _availableMacListAdapter.get(position).toString();
                        String name = _availableListAdapter.getItem(position).toString();
                        try {
                            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
                            ParcelUuid [] uuids = device.getUuids();
                            _bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuids[0].getUuid());
                            _bluetoothSocket.connect();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
//                        _bluetoothSocket = device.createRfcommSocketToServiceRecord(device.getUuids()[0].getUuid())
                    }
//                    addToAvailableList("d");
                }
            });
            availableList.setAdapter(_availableListAdapter);

            _availableSpinner = new ProgressBar(context);
            _availableSpinner.setIndeterminate(true);

//            EditText et = new EditText(context);
//            etStr = et.getText().toString();
//            TextView tv1 = new TextView(context);
//            tv1.setText("Input Student ID");

            LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams progressBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            listParams.weight = 2;
//            titleParams.bottomMargin = 5;
            layout.addView(title,titleParams);
            layout.addView(pairedTitle,titleParams);
            layout.addView(pairedList,listParams);
            layout.addView(_pairedSpinner,progressBarParams);
            layout.addView(availableTitle,titleParams);
            layout.addView(availableList,listParams);
            layout.addView(_availableSpinner,progressBarParams);
//            layout.addView(tv1,tv1Params);
//            layout.addView(et, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            this.setView(layout);
        }

        public void addToPairedList(String name, String mac) {
            _pairedListAdapter.add(name);
            _pairedMacListAdapter.add(mac);
//            _pairedListAdapter.notifyDataSetChanged();
        }

        public void addToAvailableList(String name, String mac) {
            _availableListAdapter.add(name);
            _availableMacListAdapter.add(mac);
//            _availableListAdapter.notifyDataSetChanged();
        }

        public void pairedFinishLoading() {
            _pairedSpinner.setVisibility(View.GONE);
        }

        public void availableFinishLoading() {
            _availableSpinner.setVisibility(View.GONE);
        }

        public void toggleSearching() {
            _searching_devices = !_searching_devices;
        }
    }
}
