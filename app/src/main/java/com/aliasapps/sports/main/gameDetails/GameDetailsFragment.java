package com.aliasapps.sports.main.gameDetails;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.aliasapps.sports.main.notifications.NotificationHolderFragment;
import com.aliasapps.sports.main.profile.ProfileHolderFragment;
import com.aliasapps.sports.utils.FindGameTabFragmentListener;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.aliasapps.sports.utils.ProfileTabFragmentListener;
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

public class GameDetailsFragment extends Fragment {
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
    private RecyclerView mRecyclerViewWaiting; //total waiting players recyclerView
    private RecyclerView mRecyclerViewConfirmed; //total confirmed players recyclerView
    private static List<Attendee> userWaitingList;
    private static List<Attendee> userConfirmedList;
    private static List<Attendee> userWaitingAndConfirmedList;
    private static MenuItem mMenu;
    private GameDetailsAttendeeWaitingAdapter gameDetailsAttendeeWaitingAdapter;
    private GameDetailsAttendeeConfirmedAdapter gameDetailsAttendeeConfirmedAdapter;
    private static final String TAG = GameDetailsFragment.class.getName();
    private static Attendee attendeeWaiting;
    private static Attendee attendeeConfirmed;
    public static ProfileTabFragmentListener profileTabFragmentListener;
    public static FindGameTabFragmentListener findGameTabFragmentListener;
    public static NotificationsTabFragmentListener notificationsTabFragmentListener;
    public static boolean backPressedOnFindGameTab = false;
    public static boolean backPressedOnNotificationTab = false;
    public static boolean userImageClicked_gameDetails_profile;
    public static boolean userImageClicked_gameDetails_alert;


    public GameDetailsFragment() {
        // Required empty public constructor
    }

    public static GameDetailsFragment createInstanceOnFindGameTab(FindGameTabFragmentListener listener) {
        GameDetailsFragment gameDetailsFragment= new GameDetailsFragment();
        findGameTabFragmentListener = listener;
        return gameDetailsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileTabFragmentListener = ProfileHolderFragment.profileTabFragmentListener;
        notificationsTabFragmentListener = NotificationHolderFragment.notificationsTabFragmentListener;

        Log.wtf("Mike", "On create game details");

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            game = (Game) bundle.getSerializable("game");
        }

        userWaitingList = new ArrayList<>();
        userConfirmedList = new ArrayList<>();
        userWaitingAndConfirmedList = new ArrayList<>();

        String endpoint = Helpers.BASE_URL + "/game/" + game.getId() +"/attendees";

        attendees(endpoint);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
      //  View v = inflater.inflate(R.layout.fragment_game_details, container, false);
       // this.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_game_details, container, false);
        userImageClicked_gameDetails_profile = false;
        userImageClicked_gameDetails_alert = false;
        setHasOptionsMenu (true);
       // user = MainActivity.user;
        Log.wtf("Mike", "On create view game details");

        spinner = (ProgressBar) v.findViewById(R.id.progressBarConfirmedID);
        spinner.setVisibility(View.VISIBLE);
        spinner2 = (ProgressBar) v.findViewById(R.id.progressBarWaitingID);
        spinner2.setVisibility(View.VISIBLE);

        mRecyclerViewWaiting = (RecyclerView) v.findViewById(R.id.gameDetailsWaitingListMemberImageID);
        mRecyclerViewWaiting.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerViewWaiting.setLayoutManager(lm);
        gameDetailsAttendeeWaitingAdapter = new GameDetailsAttendeeWaitingAdapter(userWaitingList, getActivity());
        mRecyclerViewWaiting.setAdapter(gameDetailsAttendeeWaitingAdapter);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerViewConfirmed = (RecyclerView) v.findViewById(R.id.gameDetailsConfirmedImageID);
        mRecyclerViewConfirmed.setHasFixedSize(true);
        LinearLayoutManager lm2 = new LinearLayoutManager(getContext());
        mRecyclerViewConfirmed.setLayoutManager(lm2);

        gameDetailsAttendeeConfirmedAdapter = new GameDetailsAttendeeConfirmedAdapter(userConfirmedList,getActivity());
        mRecyclerViewConfirmed.setAdapter(gameDetailsAttendeeConfirmedAdapter);
        lm2.setOrientation(LinearLayoutManager.HORIZONTAL);

        //show map
        mMapView = (MapView) v.findViewById(R.id.gameDetailsMapID);
        // mMapView.onCreate(savedInstanceState);
        // save the MapView's saved state on a separate Bundle and then adding it to the outgoing saved state bundle. stop crashes
        Bundle mapViewSavedInstanceState = savedInstanceState != null ? savedInstanceState.getBundle("mapViewSaveState") : null;
        mMapView.onCreate(mapViewSavedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameNameLocation = (TextView) v.findViewById(R.id.gameDetailsNameLocationID);
        gameDesc = (TextView) v.findViewById(R.id.gameDetailsDescID);
        gameTime = (TextView) v.findViewById(R.id.gameDetailsGameTimeID);
        gameCost = (TextView) v.findViewById(R.id.gameDetailsGameCostID);
        gameAtten = (TextView) v.findViewById(R.id.gameDetailsGamePlayersID);
        gameGender = (TextView) v.findViewById(R.id.gameDetailsGameGenderID);
        gameAddr = (TextView) v.findViewById(R.id.gameDetailsAddressID);
        totalWaitingPlayers = (TextView) v.findViewById(R.id.gameDetailsTotalWaitingPlayersID);
        totalConfmedPlrs = (TextView) v.findViewById(R.id.gameDetailsTotalConfirmedID);

        Log.v("mike", "call game details");

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




        return v;
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
                    Attendee attendee = new Attendee(attendeeJsonObject.getInt("id"), MainActivity.user, game.getId(), 0);
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
                Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", MainActivity.user.getToken());
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
                Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", MainActivity.user.getToken());
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
                Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", MainActivity.user.getToken());
                Log.v(TAG, MainActivity.user + "GameDetailActivityUser");
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
                Toast.makeText(getActivity(), "Oops! Something went wrong", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", MainActivity.user.getToken());
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

                    if(user.getId() == MainActivity.user.getId()){
                        attendeeWaiting = attendee;
                    }
                }

                if(status == 1 && (gameId == game.getId())) {
                    attendee = new Attendee(id, user, gameId, status);
                    userConfirmedList.add(attendee);

                    if(user.getId() == MainActivity.user.getId()){
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

//        Set<Attendee> hs = new HashSet<>();
//        hs.addAll(userWaitingList);
//        userWaitingList.clear();
//        userWaitingList.addAll(hs);

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

        for(int i = 0; i < userWaitingAndConfirmedList.size(); i++){
            if(userWaitingAndConfirmedList.get(i).getUser().getId() == MainActivity.user.getId()){
                mMenu.setTitle("Leave Game");
                break;
            }else{
                mMenu.setTitle("Join Game");
            }
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.join_leave_game, menu);
        mMenu = menu.findItem(R.id.joinLeaveGameID);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
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

    public void onBackPressed() {
        userWaitingList.clear();
        userConfirmedList.clear();
        userWaitingAndConfirmedList.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
        onBackPressed();

        Log.wtf("mike", "on resume game details");

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

//    private static boolean status = false;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


}