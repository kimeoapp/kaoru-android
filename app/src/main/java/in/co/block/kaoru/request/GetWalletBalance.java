package in.co.block.kaoru.request;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.Map;

import in.co.block.kaoru.KaoruApplication;
import in.co.block.kaoru.utils.AppPreferenceHelper;
import in.co.block.kaoru.utils.Constants;

public class GetWalletBalance extends BaseRequest<GetWalletBalance.WalletBalanceResult> implements Response.ErrorListener, Response.Listener<JSONObject> {

    private final String TAG = this.getClass().getSimpleName();

    private JsonObjectRequest request;
    private String contactId;
    private JSONObject object;

    public GetWalletBalance(Context context) {
        super(context);

    }

    @Override
    public void start() {

        String url = KaoruAPI.WALLET_BALANCE+"?uid="+AppPreferenceHelper.getInstance(getContext()).getString(Constants.PrefKey.USER_ID,"");
        request = new JsonObjectRequest(Request.Method.GET, url, null, this, this) {
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
            callback.onWalletBalanceError(error);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        onVolleyResponse(response);
        if (callback != null) {
            callback.onWalletBalanceResponse(response);
        }
    }

    public interface WalletBalanceResult {
        void onWalletBalanceResponse(JSONObject response);

        void onWalletBalanceError(VolleyError error);
    }


}
