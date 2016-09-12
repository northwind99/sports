package com.aliasapps.sports.main.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.SimpleNotification;
import com.aliasapps.sports.entity.Sport;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class NotificationsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationAdapter notificationAdapter;
    public static List<SimpleNotification> itemsData = new ArrayList<>();
    private User user;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = false;
    boolean check = false;
    int pageNext = 0;
    private static MenuItem mMenuSimple;
    private static MenuItem mMenuRequest;
    private static View view;
    public static NotificationsTabFragmentListener notificationsTabFragmentListener;
    private static final String TAG = NotificationsFragment.class.getName();

    public NotificationsFragment() {
        // Required empty public constructor
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.wtf("Mike", "on create Notification");
        user = MainActivity.user;
        if(itemsData.isEmpty()){
            getSimpleNotifications(pageNext);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        setHasOptionsMenu(true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.notificationView);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.scrollToPosition(0);
        notificationAdapter = new NotificationAdapter(itemsData);
        mRecyclerView.setAdapter(notificationAdapter);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = lm.getChildCount();
                totalItemCount = lm.getItemCount();
                pastVisiblesItems = lm.findFirstVisibleItemPosition();
                if (!loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        if(itemsData.size() % 25 == 0 && !check) {
                            pageNext = pageNext + 1;
                            getSimpleNotifications(pageNext);
                            check = true;
                        }
                    }
                }
            }
        });

        return view;
    }



    private void getSimpleNotifications(int pageNext){
        String endpoint = Helpers.BASE_SECOND_URL + "/notifications/simple/" + pageNext;

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getSimpleNotificationData(new JSONArray(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
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

    private void getSimpleNotificationData(JSONArray jsonArray) {
        SimpleNotification simpleNotification;
        User user;
        Game game;
        Sport sport;

        try{
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject snJSONObject = jsonArray.getJSONObject(i);
                int id = snJSONObject.getInt("id");
                JSONObject userJsonObject = snJSONObject.getJSONObject("user");
                user = new User(userJsonObject.getInt("id"),userJsonObject.getString("first_name"), userJsonObject.getString("last_name"), userJsonObject.getString("email")
                        , userJsonObject.getString("nickname"),userJsonObject.getString("latitude"),userJsonObject.getString("longitude"), userJsonObject.getString("bio")
                        , userJsonObject.getString("token"), userJsonObject.getString("image"));
                String text  = snJSONObject.getString("text");
                String icon = snJSONObject.getString("icon");
                int seen = snJSONObject.getInt("seen");
                long created = snJSONObject.getInt("created");
                JSONObject gameJSONObject = snJSONObject.getJSONObject("game");
                JSONObject sportJSONObject = gameJSONObject.getJSONObject("sport");
                sport = new Sport(sportJSONObject.getInt("id"), sportJSONObject.getString("name"), sportJSONObject.getString("icon"), sportJSONObject.getString("map_icon"));
                game = new Game(gameJSONObject.getInt("id"), gameJSONObject.getString("name"), gameJSONObject.getString("description"),gameJSONObject.getString("address")
                        , gameJSONObject.getString("location_name"), gameJSONObject.getDouble("latitude"), gameJSONObject.getDouble("longitude")
                        , gameJSONObject.getInt("max_players"), gameJSONObject.getInt("min_players"), gameJSONObject.getDouble("cost"), gameJSONObject.getInt("intensity")
                ,  gameJSONObject.getInt("gender"), gameJSONObject.getInt("user_id"), gameJSONObject.getString("game_time"), sport);
                simpleNotification = new SimpleNotification(id, text, icon, seen, user, game, created);
                itemsData.add(simpleNotification);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        notificationAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.notifiation_actionbar_custom, menu);
        mMenuSimple = menu.findItem(R.id.simple_notification);
        mMenuRequest = menu.findItem(R.id.request_notification);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.simple_notification:
                return true;
            case R.id.request_notification:
                Fragment newFragment = new RequestFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
                transaction.replace(R.id.notification_fragment_container, newFragment);
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();
                 return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void backPressed() {
        MainActivity.viewPager.setCurrentItem(0);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.wtf("mike", "on resume notification");
    }
}
