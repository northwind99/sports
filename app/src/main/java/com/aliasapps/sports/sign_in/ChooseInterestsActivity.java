package com.aliasapps.sports.sign_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Sport;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseInterestsActivity extends AppCompatActivity {

    public static List<Sport> sportList = new ArrayList<>();

    private InterestsAdapter interestsAdapter;
    private RecyclerView mRecyclerView;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_interests);

        setTitle("Choose interests");

        mRecyclerView = (RecyclerView) findViewById(R.id.chooseInterests_recyclerViewID);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager glm = new GridLayoutManager(this, 3); // 3 columns in each row
        glm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(glm);
        interestsAdapter = new InterestsAdapter(this, sportList);
        mRecyclerView.setAdapter(interestsAdapter);

        String endpoint = Helpers.BASE_URL + "/user/interests/all";
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        if(sportList.isEmpty()) {
            postSaveUserDetails(endpoint);
        }

    }

    private void postSaveUserDetails(String endpoint) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("res", response);
                            interestsItems(new JSONArray(response));

                            for(int i = 0; i < sportList.size(); i++){
                                Log.v("sport item 2  ", sportList.get(i).getName());
                            }

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

    private void getAllSportItems(JSONArray response) {
        Sport sport;
        int sportId;
        String sportName;
        String sportIcon;
        String map_icon;

        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject sportJSONObject = response.getJSONObject(i);
                sportId = sportJSONObject.getInt("id");
                sportName = sportJSONObject.getString("name");
                sportIcon = sportJSONObject.getString("icon");
                map_icon = sportJSONObject.getString("map_icon");
                sport = new Sport(sportId, sportName, sportIcon, map_icon);
                sportList.add(sport);
                Log.v("sport name", sport.getName());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            interestsAdapter.notifyDataSetChanged();

        }

    }

    //save user interests
    private void chooseSportItems(View view){

        String endpoint = Helpers.BASE_URL + "/user/interests";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                    toMainActivity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(getBaseContext(), "Oops! Something went wrong",Toast.LENGTH_LONG).show();
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
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < InterestsAdapter.selectSports.size(); i++) {
                     Sport s = InterestsAdapter.selectSports.get(i);
                     sb.append(s.getId()).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                params.put("interests", sb.toString());

                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);

    }

    public void toMainActivity(){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("user", user);
        i.putExtras(mBundle);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void saveInterests(View view) {
        chooseSportItems(view);
    }
}
