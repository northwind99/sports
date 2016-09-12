package com.aliasapps.sports.main.notifications;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.gameDetails.GameDetailsFragment;
import com.aliasapps.sports.main.profile.ProfileFragment;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;

public class NotificationHolderFragment extends Fragment {
    public static int layout;
    public static NotificationsTabFragmentListener notificationsTabFragmentListener;
    private Game game;
    private User user;
    public static boolean MUST_DETACH_FROM_BACKSTACK = false;

    public NotificationHolderFragment() {
        // Required empty public constructor
    }

    public static NotificationHolderFragment createInstance(NotificationsTabFragmentListener listener) {
        NotificationHolderFragment notificationHolderFragment = new NotificationHolderFragment();
        notificationsTabFragmentListener = listener;
        return notificationHolderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            layout = bundle.getInt("layout");
            game = (Game) bundle.getSerializable("game");
            user = (User) bundle.getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_holder, container, false);


        if (MUST_DETACH_FROM_BACKSTACK) {
            MUST_DETACH_FROM_BACKSTACK = false;
            return null;
        }

        Log.v("mike", "call game details on notification Holder");

        if(layout == 1) {
            NotificationsFragment notificationsFragment = new NotificationsFragment();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.notification_holder_container, notificationsFragment);
            transaction.commit();
        }else if(layout == 2) {
            GameDetailsFragment gameDetailsFragment = new GameDetailsFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("game", game);
            gameDetailsFragment.setArguments(bundle2);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.notification_holder_container, gameDetailsFragment);
            transaction.commit();
        }else if(layout == 3){
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("user", user);
            profileFragment.setArguments(bundle2);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.notification_holder_container, profileFragment);
            transaction.commit();

        }
        return view;
    }

}
