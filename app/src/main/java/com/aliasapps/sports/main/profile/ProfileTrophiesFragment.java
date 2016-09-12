package com.aliasapps.sports.main.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.TrophyUserRelation;

import java.util.ArrayList;
import java.util.List;

public class ProfileTrophiesFragment extends Fragment {

    private List<TrophyUserRelation> trophyUserRelationList;
    private RecyclerView mRecyclerView;
    public static ProfileTrophiesAdapter profileTrophiesAdapter;

    public ProfileTrophiesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trophyUserRelationList = new ArrayList<>();
      //ofileServerCalls.parseUserProfileDataTrophies(ProfileFragment.profileResponse ,trophyUserRelationList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_trophies, container, false);
        ProfileServerCalls.parseUserProfileDataTrophies(ProfileFragment.profileResponse ,trophyUserRelationList);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.profile_trophies_recyclerViewID);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
        profileTrophiesAdapter = new ProfileTrophiesAdapter(getActivity(), trophyUserRelationList);
        mRecyclerView.setAdapter(profileTrophiesAdapter);
        return view;
    }

}
