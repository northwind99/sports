package com.aliasapps.sports.main.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.Sport;
import com.aliasapps.sports.entity.Trophy;
import com.aliasapps.sports.entity.TrophyUserRelation;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.gameDetails.GameDetailsFragment;
import com.aliasapps.sports.main.messages.ChatActivity;
import com.aliasapps.sports.sign_in.LoginActivity;
import com.aliasapps.sports.utils.FindGameTabFragmentListener;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.aliasapps.sports.utils.ProfileTabFragmentListener;
import com.aliasapps.sports.utils.SlidingTabLayout;
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

public class ProfileFragment extends Fragment{

    private SlidingTabLayout  mSlidingTabLayout;
    static int dotsCount = 0;

    private LinearLayout dotsLayout;
    private TextView[] dots;
    TextView name;
    private static final String TAG = ProfileFragment.class.getName();
    public static ProfileTabFragmentListener profileTabFragmentListener;
    public static FindGameTabFragmentListener findGameTabFragmentListener;
    public static NotificationsTabFragmentListener notificationsTabFragmentListener;
    public static boolean upcomingGameClickedOnFindGameTab = false;
    private static Adapter adpter = (Adapter) ProfileUpcomingFragment.profileUpcomingGameAdapter;
    private static View view;

    private User user;

    public static JSONObject profileResponse;

    public ProfileFragment() {}

    public static ProfileFragment createInstanceOnFindGameTab(FindGameTabFragmentListener listener) {
        ProfileFragment profileFragment = new ProfileFragment();
        findGameTabFragmentListener = listener;
        return profileFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("Mike", "On create profile");

        user = null;

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            try {
                user = (User) bundle.getSerializable("user");
                Log.wtf("Mike", " got a user passed via bundle...."+user.getFirstName());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(user == null){
            user = MainActivity.user;
            Log.v("mike", user + "user value");
        }

        String endpoint = Helpers.BASE_SECOND_URL + "/user/" + user.getId() + "/profile";

        ProfileServerCalls.getUserProfileData(new ProfileServerCalls.VolleyCallback() {
          @Override
          public void onSuccess(String result) {
              try {
                  profileResponse = new JSONObject(result);
              } catch (JSONException e) {
                  e.printStackTrace();
              }
          }
      }, user, endpoint);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        Log.wtf("Mike", "On create view profile");


        ViewPager profileViewPager = (ViewPager) view.findViewById(R.id.profileViewPager);
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.slidingTabID);
        setupViewPager(profileViewPager);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(profileViewPager);

        name = (TextView) view.findViewById(R.id.usersnameprofile);

        name.setText(user.getFirstName() + " " + user.getLastName());
        Log.wtf("Mike", "The user you are viewing is " + user.getFirstName() + " " + user.getLastName());

        ViewPager profileImageViewPager = (ViewPager) view.findViewById(R.id.profileImageViewPagerID);
        setupImageViewPager(profileImageViewPager);
        dots = new TextView[dotsCount];
        dotsLayout = (LinearLayout) view.findViewById(R.id.viewPagerCountDots);

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
            dotsLayout.addView(dots[i]);
        }

        dots[0].setTextColor(getResources().getColor(R.color.colorAccent));

        profileImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setTextColor(getResources().getColor(android.R.color.darker_gray));
                }
                dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });//finished

        return view;
    }

    private void setupViewPager(final ViewPager viewPager) {
        MyAdapter adapter = new MyAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileUpcomingFragment(), "UPCOMING");
        adapter.addFragment(new ProfilePastFragment(), "PAST");
        adapter.addFragment(new ProfileTrophiesFragment(), "TROPHIES");
        viewPager.setAdapter(adapter);
    }

    private void setupImageViewPager(final ViewPager viewPager){
        ImageAdapter adapter = new ImageAdapter(getChildFragmentManager());
        adapter.addFragment(new ProfileImageFragment());
        adapter.addFragment(new ProfileUserDescFragment());
        viewPager.setAdapter(adapter);
        dotsCount = adapter.getCount();
    }

    public void setObject(User user) {

    }

    public static class MyAdapter extends FragmentPagerAdapter {

        private final List<Fragment> cFragmentList = new ArrayList<>();
        private final List<String> cFragmentTitleList = new ArrayList<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return cFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return cFragmentList.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            cFragmentList.add(fragment);
            cFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return cFragmentTitleList.get(position);
        }
    }

    public static class ImageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }



    private void logout(){
        postLogout();
    }

    private void postLogout(){
        String endpoint = Helpers.BASE_URL + "/user/logout";
       // Log.v(TAG, user.getId()+ "user id");
        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Helpers.clearSharedPreferences(getContext());
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
             //   params.put("Authorization", user.getToken());
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.logout, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        logout();
        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();

        if(ChatActivity.chatUserPicPressed) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            MainActivity.viewPager.setCurrentItem(1);
                        return true;

                    }

                    return false;
                }
            });

            ChatActivity.chatUserPicPressed = false;
        }

        Log.wtf("mike", "on resume profile");

    }


    @Override
    public void onPause() {
        // code run before super
        super.onPause(); // <-- the super
        // code run after super
    }

    public void backPressed() {
        profileTabFragmentListener.onSwitchToNextFragment();
    }

    public void backPressedOnFindGameTab() {
        findGameTabFragmentListener.onSwitchToNextFragment();
    }

    public void backPressedOnNotificationTab() {
        notificationsTabFragmentListener.onSwitchToNextFragment();
    }

}