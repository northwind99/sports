package com.aliasapps.sports.main.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.MainActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileImageFragment extends Fragment {

    private CircleImageView profileUserImage;
    private TextView profileUserName;
    private TextView profileUserLocation;

    private User user;

    public ProfileImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_profile_image, container, false);
        profileUserImage = (CircleImageView) view.findViewById(R.id.profileUserImageID);
        profileUserName = (TextView) view.findViewById(R.id.profileUserNameID);
        profileUserLocation = (TextView) view.findViewById(R.id.profileUserLocationID);

//        user = ProfileFragment.user;
//        try {
//            Picasso.with(getContext()).load(user.getProfileImage()).noFade().into(profileUserImage);
//        }catch (Exception e){
//            profileUserImage.setImageResource(0);
//        }
//
//        try {
//            profileUserName.setText(user.getFirstName() + " " + user.getLastName());
//            profileUserLocation.setText(user.getLatitude() + ", " + user.getLongitude());
//        }catch (Exception e){
//            e.printStackTrace();
//        }


       return view;
    }

}
