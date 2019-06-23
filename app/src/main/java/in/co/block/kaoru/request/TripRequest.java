package in.co.block.kaoru.request;


import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import in.co.block.kaoru.KaoruApplication;
import in.co.block.kaoru.utils.Constants;

public class TripRequest extends BaseRequest<TripRequest.OnTripResult> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private JSONObject object;

    public TripRequest(Context context) {
        super(context);
    }

    /**
     *
     * @param funder
     * @param reciever
     * @param uid
     * @param start
     * @param end
     * @param memo
     */
    public void setData(String funder,String reciever,String uid,String start,String end,String memo) {
        object = new JSONObject();
        try {
            object.put("funder", Integer.parseInt(funder));
            object.put("reciever",Integer.parseInt(reciever));
            object.put("uid",Integer.parseInt(uid));
            if(end == null)
                object.put("start",Integer.parseInt(start));
            else
                object.put("end",Integer.parseInt(end));
            object.put("memo",memo);

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if (object == null) {
            throw new NullPointerException("Set contactId to start request");
        }

        String url = KaoruAPI.TRIP_REQUEST;
        request = new JsonObjectRequest(Request.Method.POST, url, object, this, this) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return kaoruHeaders();
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(Constants.REQUEST_TIMEOUT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        KaoruApplication.getInstance().addToRequestQueue(request);
    }

    @Override
    public void stop() {
        if (request != null) {
            request.cancel();
        }
    }
    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        onVolleyErrorResponse(error);
        if (callback != null) {
            callback.onTripError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onTripResponse(response);
        }
    }

    public interface OnTripResult {
        void onTripResponse(JSONObject response);

        void onTripError(VolleyError error);
    }


}
