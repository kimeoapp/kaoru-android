package in.co.block.kaoru.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class Constants {
    public static boolean debug = true;
    public static final int SPLASH_TIME = 3000;
    public static final int REQUEST_TIMEOUT_TIME = 90000;
    public static final class PrefKey{
        public static final String IS_LOGGED_IN = "is_logged_in";
        public static final String USER_NAME  = "user_name";
        public static final String USER_ID = "user_id";
        public static final String TRIP_IN_PROGRESS = "trip_in_progress";
        public static final String TRIP_HISTORY = "trip_history";
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
