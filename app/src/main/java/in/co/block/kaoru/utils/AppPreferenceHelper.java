package in.co.block.kaoru.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppPreferenceHelper {
    private static AppPreferenceHelper instance;
    private SharedPreferences preferences;

    public static AppPreferenceHelper getInstance(Context context) {
        if(instance == null){
            instance = new AppPreferenceHelper(context);
        }
        return instance;
    }

    private AppPreferenceHelper(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return preferences.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value){
        preferences.edit().putBoolean(key,value).commit();
    }

    public String getString(String key, String defaultValue){
        return preferences.getString(key,defaultValue);
    }

    public void setString(String key,String value){
        preferences.edit().putString(key,value).commit();
    }
}
