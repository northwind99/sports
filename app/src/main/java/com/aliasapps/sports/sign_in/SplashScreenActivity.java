package com.aliasapps.sports.sign_in;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.utils.NetworkManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreenActivity extends Activity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        user = Helpers.getUser(this);
        //try to log user in using token
     //   if(NetworkManager.isConnected(this))
       // {
            if (hasToken()) {
                logInWithToken();
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
     //   }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void logInWithToken() {
        String endpoint = Helpers.BASE_URL + "/user/login";

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Login",response);
                        try {
                            redirectLogIn(new JSONObject(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
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

    public boolean hasToken(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Gson gson = new Gson();
        String json = preferences.getString("User", "");
        user = gson.fromJson(json, User.class);

        if(user != null) {
            return true;
        }
        else{
            return false;
        }
    }

    private void saveUserDetails(JSONObject response) {

    }

    private void redirectLogIn(JSONObject response) {
      //  saveUserDetails(response);

        Intent intent;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("user", user);
        intent.putExtras(mBundle);
        startActivity(intent);
        finish();
    }

    //check network connection
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}

