package com.aliasapps.sports.main.findGames;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.Sport;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.gameDetails.GameDetailsFragment;
import com.aliasapps.sports.main.profile.ProfileUpcomingFragment;
import com.aliasapps.sports.utils.FindGameTabFragmentListener;
import com.aliasapps.sports.utils.MapWrapperLayout;
import com.aliasapps.sports.utils.OnInfoWindowElemTouchListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FindGameFragment extends Fragment {

    private static GoogleMap map;
    public static List<Game> gameList = new ArrayList<>();
    protected static double latitude,longitude;
    public static double myLat;
    public static double myLong;
    private static boolean gps_enabled=false;
    private static boolean network_enabled=false;
    static Location location;
    private static Context context;
    public static Game nearbyGame;
    private FragmentActivity myContext;
    public static MenuItem mMenu;

    private ViewGroup infoWindow;
    private Button infoButton;
    private OnInfoWindowElemTouchListener infoButtonListener;
    protected Game game;
    private static FindGameTabFragmentListener findGameTabFragmentListener;
//    private static View v;

    //HashMap use to store multiple marker snippets
    private static HashMap<String, Game> markerHolderMap = new HashMap<String, Game>();
    public static boolean viewGameClicked = false;

    public FindGameFragment(){}

    public static FindGameFragment createInstance(FindGameTabFragmentListener listener) {
        FindGameFragment findGameFragment = new FindGameFragment();
        findGameTabFragmentListener = listener;
        return findGameFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // try {

        View v = inflater.inflate(R.layout.fragment_find_game, container, false);

//        }catch (InflateException e){
//           return v;
//        }

        context = getContext();

        setHasOptionsMenu(true);

        String endpoint = Helpers.BASE_URL + "/games/nearby";

        final MapFragment mapFragment = (MapFragment) myContext.getFragmentManager().findFragmentById(R.id.mapViewID);
        mapFragment.onCreate(savedInstanceState);
        final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout)v.findViewById(R.id.map_relative_layoutID);
        map = mapFragment.getMap();

        findNearbyGames(endpoint);
     //   getMyCurrentLocation(); //get my current latitude and longitude

        // MapWrapperLayout initialization
        // 39 - default marker height
        // 20 - offset between the default InfoWindow bottom edge and it's content bottom edge
        mapWrapperLayout.init(map, getPixelsFromDp(getActivity(), 39 + 20));

        // We want to reuse the info window for all the markers,
        // so let's create only one class member instance
        this.infoWindow = (ViewGroup)getActivity().getLayoutInflater().inflate(R.layout.info_window, null);
        this.infoButton = (Button)infoWindow.findViewById(R.id.button);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(infoButton,
                getResources().getDrawable(R.drawable.btn_default_normal_holo_light),
                getResources().getDrawable(R.drawable.btn_default_pressed_holo_light)){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                enterGameDetailsFragmentOnFindGameTab();
                viewGameClicked = true;
            }
        };
        this.infoButton.setOnTouchListener(infoButtonListener);

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                int yMatrix = 200, xMatrix =40;

                DisplayMetrics metrics1 = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics1);
                switch(metrics1.densityDpi)
                {
                    case DisplayMetrics.DENSITY_LOW:
                        yMatrix = 80;
                        xMatrix = 0;
                        break;
                    case DisplayMetrics.DENSITY_MEDIUM:
                        yMatrix = 100;
                        xMatrix = 0;
                        break;
                    case DisplayMetrics.DENSITY_HIGH:
                        yMatrix = 600;
                        xMatrix = 0;
                        break;
                    case DisplayMetrics.DENSITY_XHIGH:
                        yMatrix = 600;
                        xMatrix = 0;
                        break;
                    case DisplayMetrics.DENSITY_XXHIGH:
                        yMatrix = 600;
                        xMatrix = 0;
                        break;
                }

                Projection projection = map.getProjection();
                LatLng latLng = marker.getPosition();
                Point point = projection.toScreenLocation(latLng);
                Point point2 = new Point(point.x+xMatrix,point.y-yMatrix);

                LatLng point3 = projection.fromScreenLocation(point2);
                CameraUpdate zoom1 = CameraUpdateFactory.newLatLng(point3);
                map.animateCamera(zoom1,400, null);
                marker.showInfoWindow();
                return true;
            }
        });

        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Setting up the infoWindow with current's marker info
                TextView infoTitle = (TextView)infoWindow.findViewById(R.id.infoWindowGameNameLocationID);
                TextView infoSnippetGameAddr = (TextView)infoWindow.findViewById(R.id.infoWindowGameAddrID);
                TextView infoSnippetGameTime = (TextView)infoWindow.findViewById(R.id.infoWindowGameTimeID);
                TextView infoSnippetGameCost = (TextView)infoWindow.findViewById(R.id.infoWindowGameCostID);
                TextView infoSnippetGender = (TextView)infoWindow.findViewById(R.id.infoWindowGameGenderID);

                //hold multiple snippets in marker, ex: time, cost, number of players, gender
                nearbyGame = markerHolderMap.get(marker.getId());

                infoTitle.setText(marker.getTitle());
                infoSnippetGameTime.setText(nearbyGame.getGameTime());
                infoSnippetGameAddr.setText(nearbyGame.getAddress());
                infoSnippetGameCost.setText(nearbyGame.getCost() + "");

                //convert gender from number to string
                String gender = null;
                if(nearbyGame.getGender() == 0){
                    gender = "Co-ed";
                }else if(nearbyGame.getGender() == 1){
                    gender = "Male";
                }else if(nearbyGame.getGender() == 2){
                    gender = "Female";
                }
                infoSnippetGender.setText(gender);

                infoButtonListener.setMarker(marker);

                // We must call this to set the current marker and infoWindow references
                // to the MapWrapperLayout
                mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
                return infoWindow;
            }
        });

        return v;
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public static void findNearbyGames(String endpoint){

        getMyCurrentLocation(); //get my current latitude and longitude

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getGamesData(new JSONArray(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(context, "Oops! Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("latitude", myLat + "");
                params.put("longitude", myLong + "");
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);

    }

    private static void getGamesData(JSONArray response) {

        Game game;
        int id;
        String name;
        String desc;
        String address;
        String locationName;
        double latitude;
        double longitude;
        int max_players;
        int min_players;
        int intensity;
        double cost;
        int gender;
        String game_time;
        int user_id;
        Sport sport;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject gameJsonObject = response.getJSONObject(i);
                JSONObject sportJsonObject = gameJsonObject.getJSONObject("sport");

                id = gameJsonObject.getInt("id");
                name = gameJsonObject.getString("name");
                desc = gameJsonObject.getString("description");
                address = gameJsonObject.getString("address");
                locationName = gameJsonObject.getString("location_name");
                latitude = gameJsonObject.getDouble("latitude");
                longitude = gameJsonObject.getDouble("longitude");
                max_players = gameJsonObject.getInt("max_players");
                min_players = gameJsonObject.getInt("min_players");
                cost = gameJsonObject.getDouble("cost");
                intensity = gameJsonObject.getInt("intensity");
                gender = gameJsonObject.getInt("gender");
                game_time = gameJsonObject.getString("game_time");
                user_id = gameJsonObject.getInt("user_id");
                sport = new Sport(sportJsonObject.getInt("id"), sportJsonObject.getString("name"), sportJsonObject.getString("icon"), sportJsonObject.getString("map_icon"));
                game = new Game(id, name, desc, address, locationName, latitude, longitude, max_players, min_players, cost, intensity, gender, user_id, game_time, sport);

                gameList.add(game);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        showGamesOnMap();

    }

    private static void showGamesOnMap(){

        latitude = myLat;
        longitude = myLong;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14.0f));

        try {
            map.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < gameList.size(); i++) {

            // latitude and longitude
           Game game = gameList.get(i);
            double latitudeGames = game.getLatitude();
            double longitudeGames = game.getLongitude();

            String title = game.getName() + " @ " + game.getlocationName();



            MarkerOptions options = new MarkerOptions()
                                    .position(new LatLng(latitudeGames, longitudeGames))
                                    .title(title);
            Marker marker = map.addMarker(options);
          //  onMarkerClick(marker);

            markerHolderMap.put(marker.getId(), game);
        }

    }

    @Override
    public void onResume () {
        super.onResume();
    }

    @Override
    public void onPause () {
        super.onPause();
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        turnGPSOff();
    }

    @Override
    public void onLowMemory () {
        super.onLowMemory();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.create_game, menu);
        mMenu = menu.findItem(R.id.createGameID);
       super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent i = new Intent(getContext(), CreateGameActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("user", MainActivity.user);
        i.putExtras(mBundle);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    public void turnGPSOn(){
        try
        {

            String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);


            if(!provider.contains("gps")){ //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                getContext().sendBroadcast(poke);
            }
        }
        catch (Exception e) {

        }
    }

    // Method to turn off the GPS
    public void turnGPSOff(){
        String provider = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            getContext().sendBroadcast(poke);
        }
    }

    /**
     * Check the type of GPS Provider available at that instance and
     * collect the location information
     *
     * @Output Latitude and Longitude
     */
    public static void getMyCurrentLocation() {

        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locListener = new MyLocationListener();
        Log.wtf("Location", "Start");

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        try {
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (gps_enabled) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locListener);
        }

        if (gps_enabled) {
            location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (network_enabled && location == null) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
        }

        if (network_enabled && location == null) {
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {

            myLat = location.getLatitude();
            myLong = location.getLongitude();
        } else {
            Location loc = getLastKnownLocation(context);
            if (loc != null) {
                myLat = loc.getLatitude();
                myLong = loc.getLongitude();
            }
        }
        locManager.removeUpdates(locListener);
    }

    // Location listener class. to get location.
    public static class MyLocationListener implements LocationListener {
        public void onLocationChanged(Location location) {
            if (location != null) {
            }
        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    public static Location getLastKnownLocation(Context context){
        Location location = null;
        LocationManager locationmanager = (LocationManager)context.getSystemService(context.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }

        List list = locationmanager.getAllProviders();
        boolean i = false;
        Iterator iterator = list.iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();

            if(i != false && !locationmanager.isProviderEnabled(s))
                continue;

            Location location1 = locationmanager.getLastKnownLocation(s);

            if(location1 == null)
                continue;
            if(location != null)
            {
                float f = location.getAccuracy();
                float f1 = location1.getAccuracy();
                if(f >= f1)
                {
                    long l = location1.getTime();
                    long l1 = location.getTime();
                    if(l - l1 <= 600000L)
                        continue;
                }
            }
            location = location1;
            i = locationmanager.isProviderEnabled(s);
        } while(true);
        return location;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MapFragment f = (MapFragment) myContext.getFragmentManager()
                .findFragmentById(R.id.mapViewID);
        if (f != null)
            myContext.getFragmentManager().beginTransaction().remove(f).commit();
    }

    private void enterGameDetailsFragmentOnFindGameTab() {
        findGameTabFragmentListener.onSwitchToNextFragment();
    }
}


