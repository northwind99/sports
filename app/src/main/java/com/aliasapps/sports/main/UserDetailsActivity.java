package com.aliasapps.sports.main;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.Helpers;

public class UserDetailsActivity extends AppCompatActivity {

    public static User user;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        context = this;
    }

}
