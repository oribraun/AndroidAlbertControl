package services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ori on 9/24/2017.
 */

public class Permissions {

    private final static int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    private static Activity _activity;
    private static Map<String,Map<String,Object>> _Permissions = new HashMap<String,Map<String,Object>>();
    //    private Map<String, String> _permissions = new HashMap<>("");
//    private static final Map<Integer, String> PERMISSIONS = Map.of(
//            "RECORD_AUDIO", Manifest.permission.RECORD_AUDIO
//    );

    public Permissions(Activity activity) {
        _activity = activity;

        try {
            PackageInfo info = _activity.getPackageManager().getPackageInfo(_activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                String subStr = "permission.";
                int num = 2;
                for (String p : info.requestedPermissions) {
                    Map<String,Object> obj = new HashMap<String,Object>();
                    obj.put("Manifest_permission",p);
                    obj.put("approved",false);
                    obj.put("uniqueNum",num++);

                    String key = p.substring(p.indexOf(subStr) + subStr.length() , p.length());
                    _Permissions.put(key,obj);
                }
            }
            checkPermissions();
        } catch (Exception e) {
            e.getMessage();
        }
    }
    private static void checkPermissions() {
        for (Map.Entry<String,Map<String,Object>> entry : _Permissions.entrySet())
        {
            String key = entry.getKey();
            Map<String,Object> obj = entry.getValue();
            String Manifest_permission = obj.get("Manifest_permission").toString();
            Boolean approved = (Boolean) obj.get("approved");
            int uniqueNum = (int) obj.get("uniqueNum");
            int permission = ContextCompat.checkSelfPermission(_activity,
                    Manifest_permission);
            if(permission == PackageManager.PERMISSION_GRANTED) {
                obj.put("approved",true);
                _Permissions.put(key,obj);
            }

        }
    }

    public static boolean requestPermission(String permissionKey) {
        Map <String,Object> currPermission = _Permissions.get(permissionKey);
        String Manifest_permission = currPermission.get("Manifest_permission").toString();
        int Manifest_uniqueNum = (int) currPermission.get("uniqueNum");
        int audioPermission = ContextCompat.checkSelfPermission(_activity,
                Manifest_permission);
        if(audioPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(_activity,
                    Manifest_permission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(_activity,
                        new String[]{Manifest_permission},
                        Manifest_uniqueNum);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(_activity,
                        new String[]{Manifest_permission},
                        Manifest_uniqueNum);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
//            callback
            return true;
        }

        return false;
    }

    public static boolean requestPermissions(String [] permissionsKey) {
        List<String> manifestList = new ArrayList<String>();
//        stockList.add("stock1");
//        stockList.add("stock2");

//        String[] stockArr = new String[stockList.size()];
//        stockArr = stockList.toArray(stockArr);
        for(int i = 0; i < permissionsKey.length; i++) {
            String permissionKey = permissionsKey[i];
            Map <String,Object> currPermission = _Permissions.get(permissionKey);
            String Manifest_permission = currPermission.get("Manifest_permission").toString();
            int Manifest_uniqueNum = (int) currPermission.get("uniqueNum");
            int audioPermission = ContextCompat.checkSelfPermission(_activity,
                    Manifest_permission);
            if(audioPermission != PackageManager.PERMISSION_GRANTED) {
                manifestList.add(Manifest_permission);
            }
        }

        if(manifestList.size() > 0) {
            String[] manifestArr = new String[manifestList.size()];
            manifestArr = manifestList.toArray(manifestArr);
            ActivityCompat.requestPermissions(_activity,
                    manifestArr,
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    public static Map<String,Boolean> onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        String subStr = "permission.";
        for(int i = 0; i< permissions.length; i++) {
            String key = permissions[i].substring(permissions[i].indexOf(subStr) + subStr.length(), permissions[i].length());
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // contacts-related task you need to do.
//                    init();
                Map<String, Object> curr_permission = _Permissions.get(key);
                curr_permission.put("approved", true);
                _Permissions.put(key, curr_permission);
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
//            }

            // other 'case' lines to check for other
            // permissions this app might request
//        }
        }
        Map<String,Boolean> permissionsList = new HashMap<String,Boolean>();
        for (Map.Entry<String,Map<String,Object>> entry : _Permissions.entrySet())
        {
            String key = entry.getKey();
            Map<String,Object> obj = entry.getValue();
            Boolean approved = (Boolean) obj.get("approved");
            permissionsList.put(key,approved);

        }
        return permissionsList;
    }

    public static boolean getPermission(String permissionKey) {
        boolean approved = (boolean) _Permissions.get(permissionKey).get("approved");
        return approved;
    }
}
