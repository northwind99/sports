package com.aliasapps.sports.sign_in;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.http.MultipartRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = CreateAccountActivity.class.getName();

    EditText firstName, lastName, email, password, nickname, bio;

    private CircleImageView profileImage;

    private User user;

    private static final int PICK_IMAGE_ID = 234;

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setTitle(R.string.createAccount_label);

        firstName = (EditText) findViewById(R.id.firstNameInputID);
        lastName = (EditText) findViewById(R.id.lastNameInputID);
        email = (EditText) findViewById(R.id.createAccountEmailInputID);
        password = (EditText) findViewById(R.id.createAccountPasswordInputID);
        nickname = (EditText) findViewById(R.id.createAccountNicknameInputID);
        bio = (EditText) findViewById(R.id.createAccountDescInputID);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        profileImage = (CircleImageView) findViewById(R.id.userImageID);
    }

    public void registerUser(View view) {
        String endpoint = Helpers.BASE_URL + "/user/register";
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.v("success create account", response);
                    saveUserDetails(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
        protected Map<String, String> getParams() throws AuthFailureError {
            Map<String, String> params = new HashMap<String, String>();
            params.put("first_name", firstName.getText().toString());
            params.put("last_name", lastName.getText().toString());
            params.put("email", email.getText().toString());
            params.put("password", password.getText().toString());
            params.put("nickname", nickname.getText().toString());
            params.put("bio", bio.getText().toString());
            return params;
        }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void saveUserDetails(JSONObject response) {
        //Helpers.clearSharedPreferences(this);
        int id = 0;
        String firstName = null, lastName = null, email = null, nickName = null, latitude = null, longitude = null,  bio = null, token = null, profileImage = null;
        try {
            id = response.getInt("id");
            firstName = response.getString("first_name");
            lastName = response.getString("last_name");
            email = response.getString("email");
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

        //start image upload
        startImageUpload();

        Intent i = new Intent(getApplicationContext(), ChooseInterestsActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable("user", user);
        i.putExtras(mBundle);
        startActivity(i);
        finish();
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

    public void registerUserDetails(View view) {
        registerUser(view);
    }

    private void startImageUpload(){

   //     final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);

        String endpoint = Helpers.BASE_URL + "/user/image/upload";

        profileImage.buildDrawingCache();
        //create a file to write bitmap data
        File f = new File(getCacheDir(), "image");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Convert bitmap to byte array
        Bitmap bitmap2 = profileImage.getDrawingCache();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();


        //write the bytes in file
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultipartRequest req = new MultipartRequest(endpoint, f,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getApplicationContext(), "error "+arg0.toString(), Toast.LENGTH_LONG).show();
                    }
                } ,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Success", response.toString());
                    }
                }
        ){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", user.getToken());
                return params;
            }

        };

        ApplicationController.getInstance().addToRequestQueue(req);
    }

    public void uploadImage(View view){
        onPickImage(view);
    }

    public void onPickImage(View view) {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE_ID:
                    bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    profileImage.setImageBitmap(bitmap);
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }

        }
    }

}
