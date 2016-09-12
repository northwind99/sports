package com.aliasapps.sports.main.findGames;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Attendee;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.gameDetails.GameDetailsAttendeeConfirmedAdapter;
import com.aliasapps.sports.main.gameDetails.GameDetailsAttendeeWaitingAdapter;
import com.aliasapps.sports.main.profile.ProfileFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameDetailsActivity extends AppCompatActivity {

    public User user;
    private TextView gameNameLocation;
    private TextView gameDesc;
    private TextView gameTime;
    private TextView gameCost;
    private TextView gameAtten;
    private TextView gameGender;
    private TextView gameAddr;
    private TextView totalWaitingPlayers;
    private TextView totalConfmedPlrs;
    private ProgressBar spinner;
    private ProgressBar spinner2; //confirmed players progress bar
    private Game game;
    MapView mMapView;
    private GoogleMap googleMap;
    private double latitude;
    private double longitude;
    private RecyclerView mRecyclerView; //total waiting players recyclerView
    private RecyclerView mRecyclerView2; //total confirmed players recyclerView
    public static List<Attendee> userWaitingList = new ArrayList<>();
    public static List<Attendee> userConfirmedList = new ArrayList<>();
    public static List<Attendee> userWaitingAndConfirmedList = new ArrayList<>();
    private static MenuItem mMenu;
    private GameDetailsAttendeeWaitingAdapter gameDetailsAttendeeWaitingAdapter;
    private GameDetailsAttendeeConfirmedAdapter gameDetailsAttendeeConfirmedAdapter;
    private static final String TAG = GameDetailsActivity.class.getName();
    private static Attendee attendeeWaiting;
    private static Attendee attendeeConfirmed;
    private Context context;
    static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.activity_game_details);

        Intent intent = getIntent();
        user = MainActivity.user;
        game = (Game) intent.getSerializableExtra("game");

        spinner = (ProgressBar) findViewById(R.id.progressBarID);
        spinner.setVisibility(View.VISIBLE);
        spinner2 = (ProgressBar) findViewById(R.id.progressBarConfirmedPlayersID);
        spinner2.setVisibility(View.VISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.gameDetailsWaitingListMemberImageID);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lm);

        gameDetailsAttendeeWaitingAdapter = new GameDetailsAttendeeWaitingAdapter(userWaitingList,this);
        mRecyclerView.setAdapter(gameDetailsAttendeeWaitingAdapter);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

//        gameDetailsAttendeeWaitingAdapter.SetOnItemClickListener(new GameDetailsAttendeeWaitingAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//               ProfileFragment profileFragment = new ProfileFragment();
//                try {
//                    profileFragment.setObject(GameDetailsAttendeeWaitingAdapter.userWaitingImageItem.get(position).getUser());
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//                Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                i.putExtra("viewpager_position", 3);
//                startActivity(i);
//        }
//        });

        //total confirmed players recyclerView
        mRecyclerView2 = (RecyclerView) findViewById(R.id.gameDetailsConfirmedImageID);
        mRecyclerView2.setHasFixedSize(true);
        LinearLayoutManager lm2 = new LinearLayoutManager(this);
        mRecyclerView2.setLayoutManager(lm2);

        gameDetailsAttendeeConfirmedAdapter = new GameDetailsAttendeeConfirmedAdapter(userConfirmedList,this);
        mRecyclerView2.setAdapter(gameDetailsAttendeeConfirmedAdapter);
        lm2.setOrientation(LinearLayoutManager.HORIZONTAL);

        gameDetailsAttendeeConfirmedAdapter.SetOnItemClickListener(new GameDetailsAttendeeConfirmedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                ProfileFragment profileFragment = new ProfileFragment();
                profileFragment.setObject(GameDetailsAttendeeConfirmedAdapter.userConfirmedImageItem.get(position).getUser());

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("viewpager_position", 3);
                startActivity(i);

            }
        });

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
      //  getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        //show map
        mMapView = (MapView) findViewById(R.id.gameDetailsMapID);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameNameLocation = (TextView) findViewById(R.id.gameDetailsNameLocationID);
        gameDesc = (TextView) findViewById(R.id.gameDetailsDescID);
        gameTime = (TextView) findViewById(R.id.gameDetailsGameTimeID);
        gameCost = (TextView) findViewById(R.id.gameDetailsGameCostID);
        gameAtten = (TextView) findViewById(R.id.gameDetailsGamePlayersID);
        gameGender = (TextView) findViewById(R.id.gameDetailsGameGenderID);
        gameAddr = (TextView) findViewById(R.id.gameDetailsAddressID);
        totalWaitingPlayers = (TextView) findViewById(R.id.gameDetailsTotalWaitingPlayersID);
        totalConfmedPlrs = (TextView) findViewById(R.id.gameDetailsTotalConfirmedID);


        gameNameLocation.setText(game.getName() + " @ " + game.getlocationName());
        gameDesc.setText(game.getDesc());
        gameTime.setText(game.getGameTime());
        gameCost.setText(game.getCost() + "");
        gameAddr.setText(game.getAddress());



        //convert gender from number to string
        String gender = null;
        if(game.getGender() == 0){
            gender = "Co-ed";
        }else if(game.getGender() == 1){
            gender = "Male";
        }else if(game.getGender() == 2){
            gender = "Female";
        }
        gameGender.setText(gender);

        googleMap = mMapView.getMap();
        latitude = game.getLatitude();
        longitude = game.getLongitude();

        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        //adding marker
        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));

        // disable moving inside the google map
        googleMap.getUiSettings().setAllGesturesEnabled(false);

        //add the member image list

        Log.v(TAG, "recyclerView");
        String endpoint = Helpers.BASE_URL + "/game/" + game.getId() +"/attendees";

        if(userWaitingList.isEmpty() && userConfirmedList.isEmpty()) {
            attendees(endpoint);
        }

    }

    private void joinGame(){
        String endpoint = Helpers.BASE_URL + "/game/" + game.getId() +"/join";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    gameDetailsAttendeeWaitingAdapter.notifyDataSetChanged();
                    JSONObject attendeeJsonObject = new JSONObject(response);
                    Attendee attendee = new Attendee(attendeeJsonObject.getInt("id"), user, game.getId(), 0);
                    attendeeWaiting = attendee;
                    if(!userWaitingList.contains(attendeeWaiting)) {
                        userWaitingList.add(attendeeWaiting);
                    }
                    gameDetailsAttendeeWaitingAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void leaveGame(){
        String endpoint = Helpers.BASE_URL + "/game/" + game.getId() +"/leave";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //update list
                userWaitingList.remove(attendeeWaiting);
                gameDetailsAttendeeWaitingAdapter.notifyDataSetChanged();

                userConfirmedList.remove(attendeeConfirmed);
                gameDetailsAttendeeConfirmedAdapter.notifyDataSetChanged();
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
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    //get game attendees
    private void attendees(String endpoint){
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getAllAttendees(new JSONArray(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                Log.v(TAG, user + "GameDetailActivityUser");
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void cancelGame(){
        String endpoint = Helpers.BASE_URL + "/game/" + game.getId() +"/cancel";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void getAllAttendees(JSONArray response){
        Attendee attendee;
        User user;
        int id;
        int gameId;
        int status;

        for (int i = 0; i < response.length(); i++) {
            try {

                JSONObject attendeeJsonObject = response.getJSONObject(i);
                JSONObject userJsonObject = attendeeJsonObject.getJSONObject("user");

                id = response.getJSONObject(i).getInt("id");
                gameId = response.getJSONObject(i).getInt("game_id");
                status = response.getJSONObject(i).getInt("status");
                user = new User(userJsonObject.getInt("id"),userJsonObject.getString("first_name"), userJsonObject.getString("last_name"), userJsonObject.getString("email")
                        , userJsonObject.getString("nickname"),userJsonObject.getString("latitude"),userJsonObject.getString("longitude"), userJsonObject.getString("bio")
                        , userJsonObject.getString("token"), userJsonObject.getString("image"));

                if(status == 0 && (gameId == game.getId())) {
                    attendee = new Attendee(id, user, gameId, status);
                    userWaitingList.add(attendee);

                    if(user.getId() == this.user.getId()){
                        attendeeWaiting = attendee;
                    }
                }

                if(status == 1 && (gameId == game.getId())) {
                    attendee = new Attendee(id, user, gameId, status);
                    userConfirmedList.add(attendee);

                    if(user.getId() == this.user.getId()){
                        attendeeConfirmed = attendee;
                    }
                }

                //add the waiting players and confirmed players to list
                if((status == 0 || status == 1) && (gameId == game.getId())){
                    attendee = new Attendee(id, user, gameId, status);
                    userWaitingAndConfirmedList.add(attendee);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            gameDetailsAttendeeWaitingAdapter.notifyDataSetChanged();
            gameDetailsAttendeeConfirmedAdapter.notifyDataSetChanged();

        }

        for(int i = 0; i < userWaitingList.size(); i++){

        }

        if(userWaitingList.size() == 0) {
            totalWaitingPlayers.setText("No"); //set the number of waiting players
        }else{
            totalWaitingPlayers.setText(userWaitingList.size() + "");
        }
        if(userConfirmedList.size() == 0) {
            totalConfmedPlrs.setText("No"); // set the number of confirmed players
        }else {
            totalConfmedPlrs.setText(userConfirmedList.size() + "");
        }
        spinner.setVisibility(View.INVISIBLE);//dismiss progress bar
        spinner2.setVisibility(View.INVISIBLE);//dismiss progress bar

        Log.v("token", this.user.getToken());
        for(int i = 0; i < userWaitingAndConfirmedList.size(); i++){
            if(userWaitingAndConfirmedList.get(i).getUser().getId() == this.user.getId()){
                mMenu.setTitle("Leave Game");
                break;
            }else{
                mMenu.setTitle("Join Game");
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            case R.id.joinLeaveGameID:
                if(mMenu.getTitle().toString().toLowerCase().equals("JOIN GAME".toLowerCase())) {
                    joinGame();
                    mMenu.setTitle("Leave Game");
                }else {
                    leaveGame();
                    mMenu.setTitle("Join Game");
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        userWaitingList.clear();
        userConfirmedList.clear();
        userWaitingAndConfirmedList.clear();

//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        i.putExtra("viewpager_position", 0);
//        startActivity(i);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
     //   user = MainActivity.user;
    }

    @Override
    public void onRestart(){
        super.onRestart();
      //  user = MainActivity.user;
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

//    private static boolean status = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.join_leave_game, menu);
        mMenu = menu.findItem(R.id.joinLeaveGameID);

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

}


