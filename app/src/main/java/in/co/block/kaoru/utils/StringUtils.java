package in.co.block.kaoru.utils;

import android.provider.Settings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final StringUtils ourInstance = new StringUtils();

    public static StringUtils getInstance() {
        return ourInstance;
    }

    private StringUtils() {

    }

    private boolean check(String regex, String s){

        Pattern mPattern = Pattern.compile(regex);
        Matcher matcher = mPattern.matcher(s);
        return matcher.find();
    }

    public boolean validName(String name){
        if(name == null)
            return false;
        String nameRegex = "^[A-Za-z\\s]+$";
        return check(nameRegex,name);


    }
    public String getUUID(){

        String uuid = Settings.Secure.ANDROID_ID;
        return uuid;
    }
}
