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

import java.util.ArrayList;
import java.util.List;

public class ProfilePastFragment extends Fragment {

    private static List<Game> pastGameList;

    private RecyclerView mRecyclerView;
    public static ProfilePastGameAdapter profilePastGameAdapter;

    public ProfilePastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pastGameList = new ArrayList<>();
       // ProfileServerCalls.parseUserProfileDataPastGame(ProfileFragment.profileResponse ,pastGameList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_past, container, false);
        ProfileServerCalls.parseUserProfileDataPastGame(ProfileFragment.profileResponse ,pastGameList);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.profile_past_recyclerViewID);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
        profilePastGameAdapter = new ProfilePastGameAdapter(getActivity(), pastGameList);
        mRecyclerView.setAdapter(profilePastGameAdapter);

        return view;
    }

}
