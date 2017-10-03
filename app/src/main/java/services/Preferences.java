package services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by private on 03/10/2017.
 */

public class Preferences {
    public static final String PREFS_NAME = "MyPrefsFile";
    private static SharedPreferences _settings;
    private static SharedPreferences.Editor _editor;

    public Preferences(Activity activity) {
        _settings = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        _editor = _settings.edit();
    }

    public static SharedPreferences getPreferences() {
        return _settings;
    }

    public static SharedPreferences.Editor getEditor() {
        return _editor;
    }

    public static void save(String name, String val) {
        _editor.putString(name, val).commit();
    }
    public static void save(String name, Boolean val) {
        _editor.putBoolean(name, val).commit();
    }
    public static void save(String name, Float val) {
        _editor.putFloat(name, val).commit();
    }
    public static void save(String name, int val) {
        _editor.putInt(name, val).commit();
    }
    public static void save(String name, Long val) {
        _editor.putLong(name, val).commit();
    }
    public static void save(String name, Set<String> val) {
        _editor.putStringSet(name, val).commit();
    }

    public static String getString(String name) {
        return _settings.getString(name,"");
    }
    public static Boolean getBoolean(String name) {
        return _settings.getBoolean(name,false);
    }
    public static Float getFloat(String name) {
        return _settings.getFloat(name,-1);
    }
    public static int getInt(String name) {
        return _settings.getInt(name,-1);
    }
    public static Long getLong(String name) {
        return _settings.getLong(name,-1);
    }
    public static Set<String> getStringSet(String name) {
        return _settings.getStringSet(name,new HashSet<String>());
    }
}
