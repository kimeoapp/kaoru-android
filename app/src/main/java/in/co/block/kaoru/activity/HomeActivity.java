package in.co.block.kaoru.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.block.kaoru.R;
import in.co.block.kaoru.adapter.TripsAdapter;
import in.co.block.kaoru.models.Trip;
import in.co.block.kaoru.request.GetWalletBalance;
import in.co.block.kaoru.request.TripRequest;
import in.co.block.kaoru.utils.AppPreferenceHelper;
import in.co.block.kaoru.utils.Constants;

public class HomeActivity extends AppCompatActivity {
    private final int REQUEST_PERMISSIONS = 1;
    private final int QR_CODE_RESULT = 2;
    private RecyclerView rvTrips;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<Trip> trips = new ArrayList<Trip>();
    private TripsAdapter.TripsListInterationListner mListener;
    private String startStation;
    private ProgressDialog dialog;
    private TextView tvTripProgress, tvGreen,tvBlack;
    private Button bStartEnd;
    private  TripsAdapter tripsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        checkForOnGoingTrip();
        getHistory();
        getWalletBalance();
    }

    private void checkForOnGoingTrip(){
        String ongoingTrip = AppPreferenceHelper.getInstance(this).getString(Constants.PrefKey.TRIP_IN_PROGRESS,null);
        if(ongoingTrip != null && ongoingTrip.length() >0){
            final String split[] = ongoingTrip.split("_");
            startStation = split[2];
            updateTripInProgress(startStation);
        }
    }

    private void getWalletBalance(){
        GetWalletBalance getWalletBalance = new GetWalletBalance(this);
        getWalletBalance.setParserCallback(new GetWalletBalance.WalletBalanceResult() {
            @Override
            public void onWalletBalanceResponse(JSONObject response) {
                try {
                    JSONObject data = response.getJSONObject("data");
                    int green = data.getInt("green");
                    int black = data.getInt("black");
                    tvGreen.setText("Green Trips : "+green);
                    tvBlack.setText("Black Trips : "+black);
                    tvGreen.setVisibility(View.VISIBLE);
                    tvBlack.setVisibility(View.VISIBLE);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onWalletBalanceError(VolleyError error) {

            }
        });
        getWalletBalance.start();
    }

    private void init(){

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        tvTripProgress = findViewById(R.id.trip_in_progress);
        tvBlack = findViewById(R.id.black_trips);
        tvGreen = findViewById(R.id.green_trips);

        bStartEnd = findViewById(R.id.start_trip);

        TextView tvName = findViewById(R.id.name);
        String userName = AppPreferenceHelper.getInstance(this).getString(Constants.PrefKey.USER_NAME,"");
        tvName.setText(userName);

        ImageView nameInit = findViewById(R.id.name_init);
        TextDrawable drawable1 = TextDrawable.builder()
                .buildRoundRect(userName.substring(0,1), Color.parseColor("#b085f5"), (int) Constants.convertDpToPixel(40,this));

        nameInit.setImageDrawable(drawable1);
        rvTrips = findViewById(R.id.trips);
        mListener = new TripsAdapter.TripsListInterationListner() {
            @Override
            public void onListInteraction(Trip item) {
                //TODO: Do whatever you want with this list
            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvTrips.setLayoutManager(mLayoutManager);
        tripsAdapter = new TripsAdapter(trips, mListener);
        rvTrips.setAdapter(tripsAdapter);
        bStartEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });

    }

    public void scanQRCode(){
        int result = ContextCompat.checkSelfPermission(this, "android.permission.CAMERA");
        if (result != PackageManager.PERMISSION_GRANTED){
            ArrayList<String> permissionList = new ArrayList<>();
            permissionList.add("android.permission.CAMERA");
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]),
                    REQUEST_PERMISSIONS);
        }
        else{
            Intent intent = new Intent(HomeActivity.this,QRCodeScanner.class);
            startActivityForResult(intent,QR_CODE_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = true;
        for (int i = 0; i < grantResults.length; i++) {

            if(grantResults[i]==PackageManager.PERMISSION_DENIED)
                permissionGranted = false;
        }
        if(permissionGranted)
            scanQRCode();
        else{
            Toast.makeText(this,getString(R.string.permission_required),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case QR_CODE_RESULT:
                    String text = data.getStringExtra("qr_text");
                    processQRCodeResult(text);
                    break;
            }
        }

    }

    private void updateTripInProgress(String startStation){
        bStartEnd.setText(R.string.end_trip);
        tvTripProgress.setText(getString(R.string.trip_progress)+" "+startStation);
        tvTripProgress.setVisibility(View.VISIBLE);
    }

    private void onTripEnd(){
        bStartEnd.setText(R.string.start_trip);
        tvTripProgress.setText("");
        tvTripProgress.setVisibility(View.GONE);
        startStation = null;
    }

    private void processQRCodeResult(final String qrText){
        final String split[] = qrText.split("_");
        String uid = AppPreferenceHelper.getInstance(this).getString(Constants.PrefKey.USER_ID,"");
        if(startStation == null){

            showLoader("Starting a new trip..");
            TripRequest request = new TripRequest(this);
            request.setData(uid,split[0],uid,split[1],null,"from "+split[2]);
            request.setParserCallback(new TripRequest.OnTripResult() {
                @Override
                public void onTripResponse(JSONObject response) {
                    hideLoader();
                    startStation = split[2];
                    AppPreferenceHelper.getInstance(HomeActivity.this).setString(Constants.PrefKey.TRIP_IN_PROGRESS,qrText);
                    updateTripInProgress(startStation);
                }
                @Override
                public void onTripError(VolleyError error) {
                    hideLoader();

                }
            });
            request.start();

        }
        else{
            showLoader("Ending your trip");
            TripRequest request = new TripRequest(this);
            request.setData(uid,split[0],uid,null,split[1],"to "+split[2]);
            request.setParserCallback(new TripRequest.OnTripResult() {
                @Override
                public void onTripResponse(JSONObject response) {
                    hideLoader();
                    double charges = 10;
                    try {
                        charges = response.getJSONObject("data").getDouble("amount");
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    updateTripHistory(startStation,split[2],charges);
                    AppPreferenceHelper.getInstance(HomeActivity.this).setString(Constants.PrefKey.TRIP_IN_PROGRESS,"");
                    onTripEnd();
                    getWalletBalance();

                }
                @Override
                public void onTripError(VolleyError error) {
                    hideLoader();

                }
            });
            request.start();
        }
    }

    private void updateTripHistory(String startStation, String toStation, double charges){
        Trip trip0 = new Trip();
        trip0.setCharges(charges);
        trip0.setStationFrom(startStation);
        trip0.setStationTo(toStation);
        trips.add(0,trip0);
        tripsAdapter.notifyDataSetChanged();

    }

    private void getHistory(){
        String sArray = AppPreferenceHelper.getInstance(this).getString(Constants.PrefKey.TRIP_HISTORY,null);
        if(sArray != null){
            trips.clear();
            try{
                JSONArray jsonArray = new JSONArray(sArray);
                for(int i = 0;i<jsonArray.length();i++){
                    JSONObject object = jsonArray.getJSONObject(i);
                    Trip trip = new Trip();
                    trip.setStationFrom(object.getString("stationFrom"));
                    trip.setStationTo(object.getString("stationTo"));
                    trip.setCharges(object.getDouble("charges"));
                    trips.add(trip);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(trips.size() > 0){
            JSONArray jsonArray = new JSONArray();
            for(Trip trip : trips){

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("stationFrom", trip.getStationFrom());
                    jsonObject.put("stationTo", trip.getStationTo());
                    jsonObject.put("charges", trip.getCharges());
                    jsonArray.put(jsonObject);
                }catch(JSONException e){
                    e.printStackTrace();
                }

            }

            AppPreferenceHelper.getInstance(this).setString(Constants.PrefKey.TRIP_HISTORY,jsonArray.toString());
        }
        dialog.dismiss();
    }



    private void addDummyTrips(){

        Trip trip0 = new Trip();
        trip0.setCharges(10);
        trip0.setStationFrom("Grindelwald");
        trip0.setStationTo("Zurich");

        Trip trip1 = new Trip();
        trip1.setCharges(10);
        trip1.setStationFrom("Zurich");
        trip1.setStationTo("Grindelwald");

        trips.add(trip0);
        trips.add(trip1);



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
