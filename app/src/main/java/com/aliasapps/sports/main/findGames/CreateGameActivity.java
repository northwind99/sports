package com.aliasapps.sports.main.findGames;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Sport;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.utils.CustomMapView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.DateFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateGameActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CreateGameSelectAdapter createGameSelectAdapter;
    public static List<Sport> sportList = new ArrayList<>();
    public User user;
    CustomMapView mMapView;
    private GoogleMap googleMap;
    protected static double latitude, longitude;
    private EditText eventName;
    private EditText eventCost;
    static TextView gameTime;
    private RadioGroup radioGroup;
    private EditText max_players;
    private EditText min_players;
    private EditText gameDesc;
    private EditText gameLoc;
    private int gender;
    private TextView gameMapAddr;
    private String gameLat;
    private String gameLng;
    private static String gameYear, gameMonth, gameDate, gameHour, gameMins, gameSec;
    private static final String TAG = CreateGameActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        mRecyclerView = (RecyclerView) findViewById(R.id.createGameSelectSportID);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager lm = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(lm);

        createGameSelectAdapter = new CreateGameSelectAdapter(this, sportList);
        mRecyclerView.setAdapter(createGameSelectAdapter);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        //create custom view to display center bar title
        final ActionBar abar = getSupportActionBar();
        View v = getLayoutInflater().inflate(R.layout.activity_create_game_title, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        abar.setCustomView(v, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(true);
        abar.setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_create_game_clear);
      //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        String endpoint = Helpers.BASE_URL + "/user/interests/all";
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if (sportList.isEmpty()) {
            getAllSportItems(endpoint);
        }

        gameTime = (TextView) findViewById(R.id.setGameTimeID);
        gameTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
                showDatePickerDialog(v);
            }
        });

        mMapView = (CustomMapView) findViewById(R.id.createGameMapViewID);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        // Getting a reference to the map
        googleMap = mMapView.getMap();

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        latitude = FindGameFragment.myLat;
        longitude = FindGameFragment.myLong;

        try {
            googleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

        //Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                //  markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                gameLat = latLng.latitude + "";
                gameLng = latLng.longitude + "";

                // Clears the previously touched position
                googleMap.clear();

                // Animating to the touched position
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                googleMap.addMarker(markerOptions);

                //convert latitude/longitude to detail address
                Geocoder myLocation = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> myList = myLocation.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    String result;

                    if (myList != null && myList.size() > 0) {
                        Address address = myList.get(0);
                        String streetName = address.getAddressLine(0);
                        String townName = address.getSubLocality();
                        if (townName == null) {
                            townName = address.getLocality();
                        }
                        result = streetName + ", " + townName;
                        //show game detail address above map when user click on the map
                        gameMapAddr.setText(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        eventName = (EditText) findViewById(R.id.createGameEventID);
        eventCost = (EditText) findViewById(R.id.createGameCostID);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroupID);
        gameMapAddr = (TextView) findViewById(R.id.gameMapAddressID);


        try {
            String genderString = ((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
            //covert user's gender into number to send to server
            if (genderString.equals("Co-ed")) {
                gender = 0;
            } else if (genderString.equals("MAle")) {
                gender = 1;
            } else {
                gender = 2;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        max_players = (EditText) findViewById(R.id.maxPlayerID);
        min_players = (EditText) findViewById(R.id.minPlayerID);
        gameDesc = (EditText) findViewById(R.id.createGameDescID);
        gameLoc = (EditText) findViewById(R.id.createGameAddressID);

    }

    //check if the form is valid
    private boolean validForm(){
        eventName.setError(null);
        eventCost.setError(null);
        gameMapAddr.setError(null);
        max_players.setError(null);
        min_players.setError(null);
        gameDesc.setError(null);
        gameLoc.setError(null);

        String name = eventName.getText().toString();
        String cost = eventCost.getText().toString();
        String address = gameMapAddr.getText().toString();
        String maxPlyrs = max_players.getText().toString();
        String minplyrs = min_players.getText().toString();
        String desc = gameDesc.getText().toString();
        String location = gameLoc.getText().toString();
        String time = gameTime.getText().toString();

        if(TextUtils.isEmpty(name)){
            eventName.setError(getString(R.string.error_field_required));
            return false;
        }else if(TextUtils.isEmpty(cost)){
            eventCost.setError(getString(R.string.error_field_required));
            return false;
        }else if(!CreateGameSelectAdapter.clicked){
            Toast.makeText(this, R.string.error_select_sport, Toast.LENGTH_SHORT).show();
            return false;
        }else if(time.equals("Set date, time")){
            Toast.makeText(this, R.string.error_game_time, Toast.LENGTH_SHORT).show();
            return false;
        }else if(radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, R.string.error_gender_select, Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(maxPlyrs)){
            max_players.setError(getString(R.string.error_field_required));
            return false;
        }else if(TextUtils.isEmpty(minplyrs)){
            min_players.setError(getString(R.string.error_field_required));
            return false;
        }else if(TextUtils.isEmpty(desc)){
            gameDesc.setError(getString(R.string.error_field_required));
            return false;
        }else if(TextUtils.isEmpty(location)){
            gameLoc.setError(getString(R.string.error_field_required));
            return false;
        }else if(TextUtils.isEmpty(address)){
            gameMapAddr.setError(getString(R.string.error_field_required));
            return false;
        }
        return true;
    }

    public void attemptCreateGame(View view){
          if(validForm()){
              createGame();
          }
    }

    private void createGame() {
        String endpoint = Helpers.BASE_URL + "/game";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //refresh map, show new game on map
               // FindGameFragment.findNearbyGames(Helpers.BASE_URL + "/games/nearby");

                Intent i = new Intent(getApplicationContext(), GameDetailsActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable("user", MainActivity.user);
                i.putExtras(mBundle);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(getBaseContext(), "Oops! Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", user.getToken());
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", eventName.getText().toString());
                params.put("cost", eventCost.getText().toString());
                params.put("sport_id", CreateGameSelectAdapter.selectedSport.getId() + ""); //pass sport object from adapter
                params.put("game_time", gameYear + "-" + gameMonth + "-" + gameDate + " " + gameHour + ":" + gameMins + ":" + gameSec); //format gameTime
                params.put("gender", gender + "");
                params.put("max_players", max_players.getText().toString());
                params.put("min_players", min_players.getText().toString());
                params.put("description", gameDesc.getText().toString());
                params.put("location_name", gameLoc.getText().toString());
                params.put("address", gameMapAddr.getText().toString());
                params.put("latitude", gameLat);
                params.put("longitude", gameLng);
                params.put("intensity", "3");
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    //popup time piker
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //popup date piker
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    //TimePicker class
    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String AM_PM;
            if (hourOfDay < 12) {
                AM_PM = "AM";
            } else {
                AM_PM = "PM";
                hourOfDay = hourOfDay - 12;
            }

            String minuteS;
            if (minute < 10)
                minuteS = "0" + minute;
            else
                minuteS = minute + "";

            gameTime.setText(gameTime.getText() + " | " + hourOfDay + ":" + minuteS + AM_PM);

            gameHour = hourOfDay + "";
            gameMins = minuteS;
            gameSec = "00";

        }
    }

    //DatePicker class
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date date = new Date(year, month, day - 1);
            String dayOfWeek = simpledateformat.format(date);
            gameTime.setText(dayOfWeek + ", " + (getMonth(month + 1) + " " + day + ", " + year));

            gameYear = year + "";
            if (month + 1 < 10) {
                gameMonth = "0" + (month + 1 + "");
            } else {
                gameMonth = month + 1 + "";
            }
            gameDate = day + "";

        }
    }

    //convert month number to month name
    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    private void getAllSportItems(String endpoint) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("res", response);
                            interestsItems(new JSONArray(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(getBaseContext(), "Something went wrong from server", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", user.getToken());
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }


    private void interestsItems(JSONArray jsonObject) {
        getAllSportItems(jsonObject);
    }

    //show all the sport items for user to select
    private void getAllSportItems(JSONArray response) {
        Sport sport;
        int sportId;
        String sportName;
        String sportIcon;
        String map_icon;

        for (int i = 0; i < response.length(); i++) {
            try {
                sportId = response.getJSONObject(i).getInt("id");
                sportName = response.getJSONObject(i).getString("name");
                sportIcon = response.getJSONObject(i).getString("icon");
                map_icon = response.getJSONObject(i).getString("map_icon");
                sport = new Sport(sportId, sportName, sportIcon, map_icon);
                sportList.add(sport);
                Log.v("sport name", sport.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            createGameSelectAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}


