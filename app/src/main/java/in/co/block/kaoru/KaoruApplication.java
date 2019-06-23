package in.co.block.kaoru;


import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import in.co.block.kaoru.utils.Constants;


public class KaoruApplication extends Application  {
    private static final String TAG = KaoruApplication.class.getSimpleName();
    private RequestQueue requestQueue;
    private static Context context;
    private static KaoruApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        context = getApplicationContext();
        mInstance = this;
        VolleyLog.DEBUG = Constants.debug;

    }

    public static synchronized KaoruApplication getInstance() {
        return mInstance;
    }


    private RequestQueue getRequestQueue(){
        requestQueue = Volley.newRequestQueue(context);
        return requestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        addToRequestQueue(req,null);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.wtf(TAG, "Low Memory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
