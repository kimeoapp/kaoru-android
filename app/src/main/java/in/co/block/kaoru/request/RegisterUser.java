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

public class RegisterUser extends BaseRequest<RegisterUser.OnRegisterUserResult> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();
    private JsonObjectRequest request;
    private JSONObject object;

    public RegisterUser(Context context) {
        super(context);
    }


    public void setData(String name,String uuid) {
        object = new JSONObject();
        try {
            object.put("name", name);
            object.put("info",uuid);
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

        String url = KaoruAPI.REGISTER_USER;
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
            callback.onRegisterUserError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onRegisterUserResponse(response);
        }
    }

    public interface OnRegisterUserResult {
        void onRegisterUserResponse(JSONObject response);

        void onRegisterUserError(VolleyError error);
    }


}
