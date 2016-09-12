package com.aliasapps.sports.main.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.notifications.NotificationHolderFragment;
import com.aliasapps.sports.main.notifications.NotificationsFragment;
import com.aliasapps.sports.utils.FindGameTabFragmentListener;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.aliasapps.sports.utils.ProfileTabFragmentListener;

import java.util.ArrayList;
import java.util.List;


public class ProfileUpcomingFragment extends Fragment {

    private static List<Game> upcomingGameList;

    private RecyclerView mRecyclerView;
    public static ProfileUpcomingGameAdapter profileUpcomingGameAdapter;
    private ProfileTabFragmentListener profileTabFragmentListener;
    private FindGameTabFragmentListener findGameTabFragmentListener;
    private NotificationsTabFragmentListener notificationsTabFragmentListener;
    private static final String TAG = ProfileFragment.class.getName();
    public static Game gameOnFindGameTab;
    public static Game gameOnProfileTab;
    public static Game gameOnNotificationTab;
    public static boolean upcomingGameClickedFlagOnFindGameTab = false;

    public ProfileUpcomingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileTabFragmentListener = ProfileHolderFragment.profileTabFragmentListener;
        findGameTabFragmentListener = ProfileFragment.findGameTabFragmentListener;
        notificationsTabFragmentListener = NotificationHolderFragment.notificationsTabFragmentListener;

        upcomingGameList = new ArrayList<>();
        ProfileServerCalls.parseUserProfileDataUpcoming(ProfileFragment.profileResponse ,upcomingGameList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_upcoming, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.profile_upcoming_recyclerViewID);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
        profileUpcomingGameAdapter = new ProfileUpcomingGameAdapter(getActivity(), upcomingGameList);
        mRecyclerView.setAdapter(profileUpcomingGameAdapter);

        profileUpcomingGameAdapter.SetOnItemClickListener(new ProfileUpcomingGameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int currentTabPosition = MainActivity.viewPager.getCurrentItem();

                if(currentTabPosition == 0) {
                    gameOnFindGameTab = profileUpcomingGameAdapter.gameItemList.get(position);
                    upcomingGameClickedFlagOnFindGameTab = true;
                    enterGameDetailsFragmentOnFindGameTab();
                    ProfileFragment.upcomingGameClickedOnFindGameTab = true;
                }else if(currentTabPosition == 2){
                    gameOnNotificationTab = profileUpcomingGameAdapter.gameItemList.get(position);
                    enterGameDetailsFragmentOnNotificationTab();
                }else if(currentTabPosition == 3){
                    //add user to the list
                //   MainActivity.profileTab_gameList.add(profileUpcomingGameAdapter.gameItemList.get(position));
                    gameOnProfileTab = profileUpcomingGameAdapter.gameItemList.get(position);
                    enterGameDetailsFragmentOnProfileTab();
                }

            }
        });

        return view;
    }

    private void enterGameDetailsFragmentOnProfileTab() {
        profileTabFragmentListener.onSwitchToNextFragment();
    }

    private void enterGameDetailsFragmentOnFindGameTab() {
        findGameTabFragmentListener.onSwitchToNextFragment();
    }

    private void enterGameDetailsFragmentOnNotificationTab() {
        notificationsTabFragmentListener.onSwitchToNextFragment();
    }

}
