package com.aliasapps.sports.sign_in;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aliasapps.sports.R;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        setTitle(R.string.login_label);

        mEmailView = (EditText) findViewById(R.id.signInEmailID);
        mPasswordView = (EditText) findViewById(R.id.signInPasswordID);
    }

    public void createAccount(View view) {
        Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(i);
    }

    private boolean validForm() {
        // Reset errors
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            return false;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            return false;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    public void attemptLogIn(View view){
        //no errors on the form? make request to server
        if(validForm()){
            String endpoint = Helpers.BASE_URL + "/user/login";

            Map<String, String> params = new HashMap<String, String>();
            params.put("email", mEmailView.getText().toString());
            params.put("password", mPasswordView.getText().toString());

            postLogIn(endpoint, params);
        }

//        Intent i = new Intent(getApplicationContext(), MainActivity.class);
//        startActivity(i);
//        finish();
    }

    private void postLogIn(String endpoint, final Map<String, String> params) {
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Login", response);
                        try {
                            redirectLogIn(new JSONObject(response));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(getBaseContext(), "Make sure your email/password are correct", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void redirectLogIn(JSONObject response) {

        int id = 0;
        String firstName = null, lastName = null, email = null, password = null, nickName = null, latitude = null, longitude = null,  bio = null, token = null, profileImage = null;
        try {
            id = response.getInt("id");
            firstName = response.getString("first_name");
            lastName = response.getString("last_name");
            email = response.getString("email");
         //   password = response.getString("password");
            nickName = response.getString("nickname");
            latitude = response.getString("latitude");
            longitude = response.getString("longitude");
            bio = response.getString("bio");
            token = response.getString("token");
            profileImage = response.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user = new User(id, firstName, lastName, email, nickName, latitude, longitude, bio, token, profileImage);

        Helpers.saveUserToDisk(this, user);

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("user", user);
        i.putExtras(mBundle);
        startActivity(i);
        finish();
    }
}
