package com.aliasapps.sports.main.profile;


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
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.gameDetails.GameDetailsFragment;
import com.aliasapps.sports.utils.ProfileTabFragmentListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileHolderFragment extends Fragment {
    public static int layout;
    public static ProfileTabFragmentListener profileTabFragmentListener;
    private Game game;
    private User user;
    public static boolean MUST_DETACH_FROM_BACKSTACK = false;

    public ProfileHolderFragment() {
        // Required empty public constructor
    }

    public static ProfileHolderFragment createInstance(ProfileTabFragmentListener listener) {
        ProfileHolderFragment profileHolderFragment = new ProfileHolderFragment();
        profileTabFragmentListener = listener;
        return profileHolderFragment;
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
        View view = inflater.inflate(R.layout.fragment_profle_holder, container, false);


        if (MUST_DETACH_FROM_BACKSTACK) {
            MUST_DETACH_FROM_BACKSTACK = false;
            return null;
        }

        if(layout == 1) {
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("user", user);
            profileFragment.setArguments(bundle2);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.profile_holder_container, profileFragment);
            transaction.commit();
        }else if(layout == 2){
            GameDetailsFragment gameDetailsFragment = new GameDetailsFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putSerializable("game", game);
            gameDetailsFragment.setArguments(bundle2);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.profile_holder_container, gameDetailsFragment);
            transaction.commit();
        }



        return view;
    }

}
