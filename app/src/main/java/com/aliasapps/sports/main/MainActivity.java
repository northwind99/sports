package com.aliasapps.sports.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.contact.ContactFragment;
import com.aliasapps.sports.main.findGames.FindGameFragment;
import com.aliasapps.sports.main.gameDetails.GameDetailsAttendeeWaitingAdapter;
import com.aliasapps.sports.main.gameDetails.GameDetailsFragment;
import com.aliasapps.sports.main.messages.ChatActivity;
import com.aliasapps.sports.main.messages.ConversationFragment;
import com.aliasapps.sports.main.notifications.NotificationAdapter;
import com.aliasapps.sports.main.notifications.NotificationHolderFragment;
import com.aliasapps.sports.main.notifications.NotificationsFragment;
import com.aliasapps.sports.main.profile.ProfileFragment;
import com.aliasapps.sports.main.profile.ProfileServerCalls;
import com.aliasapps.sports.main.profile.ProfileUpcomingFragment;
import com.aliasapps.sports.main.profile.ProfileHolderFragment;
import com.aliasapps.sports.utils.FindGameTabFragmentListener;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.aliasapps.sports.utils.ProfileTabFragmentListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static User user;
    public static User alerts_other;
    public static ArrayList<User> profileTab_userList = new ArrayList<>();
    public static ArrayList<Game> profileTab_gameList = new ArrayList<>();
    public static ArrayList<User> alert_userList = new ArrayList<>();
    public static ArrayList<Game> alert_gameList = new ArrayList<>();
    public static Context context;
    private static final String TAG = MainActivity.class.getName();
    public static ViewPagerAdapter adapter;
    public static int currentTabPosition;
    public static boolean backPress_ProfileTab;
    public static boolean backpress_mainActivity = false;
    final int[] ICONS = new int[]{
            R.drawable.ic_location_on_black_24dp,
            R.drawable.ic_chat_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_person_outline_black_24dp,
            R.drawable.ic_contact_mail_black_24dp};

    public static int stackCouter_onProfile;
    public static int stackCouter_onNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(ICONS[0]);
        tabLayout.getTabAt(1).setIcon(ICONS[1]);
        tabLayout.getTabAt(2).setIcon(ICONS[2]);
        tabLayout.getTabAt(3).setIcon(ICONS[3]);
        tabLayout.getTabAt(4).setIcon(ICONS[4]);
        backPress_ProfileTab = false;
        tabLayout.setBackground(new ColorDrawable(Color.GRAY));
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
              //  currentTabPosition = tab.getPosition();
                currentTabPosition  = tab.getPosition();
                Log.v(TAG, currentTabPosition + "positionCheck main");
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), viewPager, 5);
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private TextView actionbarTitle;
        public final FragmentManager mFragmentManagerProfileTab;
        public final FragmentManager mFragmentManagerFindGameTab;
        public final FragmentManager mFragmentManagerNotificationTab;
        public Fragment mFragmentAtPos3;
        public Fragment mFragmentAtPos0;
        public Fragment mFragmentAtPos2;
        ProfilePageListenerOnProfileTab profilePageListenerOnProfileTab = new ProfilePageListenerOnProfileTab();
        FindGamePageListener findGamePageListener = new FindGamePageListener();
        NotificationPageListener notificationPageListener = new NotificationPageListener();
        int num;

        ActionBar.LayoutParams params = new
                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        public ViewPagerAdapter(FragmentManager manager, ViewPager pager, int x) {
            super(manager);
            mActionBar = MainActivity.this.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setOnPageChangeListener(this);
            mFragmentManagerProfileTab   = manager;
            mFragmentManagerFindGameTab = manager;
            mFragmentManagerNotificationTab = manager;
            View v = inflater.inflate(R.layout.actionbar_title_layout, null);
            actionbarTitle = (TextView) v.findViewById(R.id.actionbarTitleID);
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setCustomView(v, params);
            this.num = x;

        }


        // save a fragment's state
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {}

        private final class ProfilePageListenerOnProfileTab implements ProfileTabFragmentListener {
            public void onSwitchToNextFragment() {
                Log.wtf("Mike", " On switch to next fragment");
                mFragmentAtPos3 = ProfileHolderFragment.createInstance(profilePageListenerOnProfileTab);
                if (ProfileHolderFragment.layout == 1){
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout", 2);
                    bundle.putSerializable("game", ProfileUpcomingFragment.gameOnProfileTab);
                    mFragmentAtPos3.setArguments(bundle);

                    stackCouter_onProfile+=1;
                    Log.v(TAG, stackCouter_onProfile + "evannnn ProfileTab add stack gamedetails");
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout", 1);
                    bundle.putSerializable("user",  GameDetailsAttendeeWaitingAdapter.user_gameDetailsWaiting_onProfile);
                    mFragmentAtPos3.setArguments(bundle);
                    stackCouter_onProfile+=1;
                    Log.v(TAG, stackCouter_onProfile + "evannnn ProfileTab add stack profile");
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.profile_holder_container, mFragmentAtPos3, null)
                        .addToBackStack(null)
                        .commit();

            }
        }

        private final class FindGamePageListener implements FindGameTabFragmentListener {
            public void onSwitchToNextFragment() {
           //         mFragmentManagerFindGameTab.beginTransaction().remove(mFragmentAtPos0).commit();
                if (mFragmentAtPos0 instanceof FindGameFragment){
                    mFragmentAtPos0 = GameDetailsFragment.createInstanceOnFindGameTab(findGamePageListener);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("game", FindGameFragment.nearbyGame);
                    mFragmentAtPos0.setArguments(bundle);
                }else if (mFragmentAtPos0 instanceof GameDetailsFragment){
                            mFragmentAtPos0 = ProfileFragment.createInstanceOnFindGameTab(findGamePageListener);
                }else if (mFragmentAtPos0 instanceof ProfileFragment) {
                    mFragmentAtPos0 = GameDetailsFragment.createInstanceOnFindGameTab(findGamePageListener);
                }
                mFragmentManagerFindGameTab.beginTransaction()
                        .replace(android.R.id.content, mFragmentAtPos0, null)
                        .addToBackStack(null)
                        .commit();
            }
        }

        private final class NotificationPageListener implements NotificationsTabFragmentListener{
            public void onSwitchToNextFragment() {
                mFragmentAtPos2 = NotificationHolderFragment.createInstance(notificationPageListener);

                if(NotificationHolderFragment.layout == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout", 2);
                    bundle.putSerializable("game", NotificationAdapter.game);
                    mFragmentAtPos2.setArguments(bundle);
                }else if(NotificationHolderFragment.layout == 2){
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout", 3);
                    bundle.putSerializable("user", GameDetailsAttendeeWaitingAdapter.user_gameDetailsWaiting_onNotification);
                    mFragmentAtPos2.setArguments(bundle);
                }else{
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout", 2);
                    bundle.putSerializable("game", ProfileUpcomingFragment.gameOnNotificationTab);
                    mFragmentAtPos2.setArguments(bundle);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.notification_holder_container, mFragmentAtPos2, null)
                        .addToBackStack(null)
                        .commit();
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mFragmentAtPos0 == null) {
                        mFragmentAtPos0 = FindGameFragment.createInstance(findGamePageListener);
                    }
                    return mFragmentAtPos0;
                case 1:
                    Fragment messageFragment = new ConversationFragment();
                    return messageFragment;
                case 2:
                    mFragmentAtPos2 = NotificationHolderFragment.createInstance(notificationPageListener);
                    Bundle bundle_notification = new Bundle();
                    bundle_notification.putInt("layout", 1);
                    mFragmentAtPos2.setArguments(bundle_notification);
                    return mFragmentAtPos2;
                case 3:
                    mFragmentAtPos3 = ProfileHolderFragment.createInstance(profilePageListenerOnProfileTab);
                    Bundle bundle = new Bundle();
                    bundle.putInt("layout", 1);
                    mFragmentAtPos3.setArguments(bundle);
                    return mFragmentAtPos3;

                case 4:
                    Fragment contactFragment = new ContactFragment();
                    return contactFragment;
                default:
                    return null;
            }

        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return num;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {

            if(position == 0){
                actionbarTitle.setText("Find Games");
            }else if(position == 1){
                actionbarTitle.setText("Messages");
            }else if(position == 2){
                actionbarTitle.setText("");
            }else if(position == 3){
                actionbarTitle.setText("Profile");
            }else
                actionbarTitle.setText("Contact");

            Log.v(TAG, position + " position main");
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }

    @Override
    public void onBackPressed() {
        backpress_mainActivity = true;
    if(viewPager.getCurrentItem() == 0) {
        if (adapter.mFragmentManagerFindGameTab.getBackStackEntryCount() > 0 ){
            adapter.mFragmentManagerFindGameTab.popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
        }

        if(viewPager.getCurrentItem() == 1){
            viewPager.setCurrentItem(0);
        }

        if(viewPager.getCurrentItem() == 2){
            if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
                NotificationHolderFragment.MUST_DETACH_FROM_BACKSTACK = true;
                getSupportFragmentManager().popBackStack();
                stackCouter_onNotification-=1;
                Log.v(TAG, stackCouter_onNotification + "evannnn notificationTab pop stack");
            } else {
                super.onBackPressed();
            }
        }

        //PROFILE TAB
        if(viewPager.getCurrentItem() == 3) {
          //  adapter.mFragmentManagerProfileTab.popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() > 1 ){
                ProfileHolderFragment.MUST_DETACH_FROM_BACKSTACK = true;
                getSupportFragmentManager().popBackStack();

                stackCouter_onProfile-=1;
                Log.v(TAG, stackCouter_onProfile + "evannnn ProfileTab pop stack");
            } else {
                super.onBackPressed();
            }
        }

        if(viewPager.getCurrentItem() == 4){
            viewPager.setCurrentItem(0);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(ChatActivity.chatUserPicPressed){
            viewPager.setCurrentItem(3);
        }

        //load Profile Fragment data
        String endpoint = Helpers.BASE_SECOND_URL + "/user/" + user.getId() + "/profile";
        ProfileServerCalls.getUserProfileData(new ProfileServerCalls.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                   ProfileFragment.profileResponse = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, user, endpoint);
    }

}
