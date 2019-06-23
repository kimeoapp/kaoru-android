package in.co.block.kaoru.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import in.co.block.kaoru.R;
import in.co.block.kaoru.request.RegisterUser;
import in.co.block.kaoru.utils.AppPreferenceHelper;
import in.co.block.kaoru.utils.Constants;
import in.co.block.kaoru.utils.StringUtils;

public class UserOnboardingActivity extends AppCompatActivity {
    private EditText etName;
    private TextView tvTitle;
    private Button register;
    private RegisterUser registerUserRequest;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_user_onboarding);
        init();
    }

    private void init(){
        etName = findViewById(R.id.name);
        register = findViewById(R.id.register);
        tvTitle = findViewById(R.id.title);
        tvTitle.setText(R.string.registration);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });
        registerUserRequest = new RegisterUser(this);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
    }
    private void verify(){
        String name = etName.getText().toString();
        if(StringUtils.getInstance().validName(name)){
            AppPreferenceHelper.getInstance(this).setString(Constants.PrefKey.USER_NAME,name);
            String uuid = StringUtils.getInstance().getUUID();
            sendRegisterUserRequest(name,uuid);

        }
    }
    private void sendRegisterUserRequest(String name,String uuid){
        showLoader("Please Wait..");
        registerUserRequest.setData(name,uuid);
        registerUserRequest.setParserCallback(new RegisterUser.OnRegisterUserResult() {
            @Override
            public void onRegisterUserResponse(JSONObject response) {
                hideLoader();
                try{
                    int uid = response.getJSONObject("data").getInt("uid");
                    onSuccessfulRegistration(uid);
                }catch(JSONException e){
                    e.printStackTrace();
                    Toast.makeText(UserOnboardingActivity.this,"JSON error",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onRegisterUserError(VolleyError error) {
                hideLoader();
            }
        });
        registerUserRequest.start();
    }
    private void onSuccessfulRegistration(int uid){

        Toast.makeText(UserOnboardingActivity.this,R.string.reg_success,Toast.LENGTH_SHORT).show();
        AppPreferenceHelper.getInstance(this).setString(Constants.PrefKey.USER_ID,String.valueOf(uid));
        AppPreferenceHelper.getInstance(UserOnboardingActivity.this).setBoolean(Constants.PrefKey.IS_LOGGED_IN,true);
        Intent intent  = new Intent(UserOnboardingActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void showLoader(String message){
        if(!dialog.isShowing()){
            dialog.setMessage(message);
            dialog.show();
        }
    }
    private void hideLoader(){
        if(dialog.isShowing())
            dialog.dismiss();
    }
}
