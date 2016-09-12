package com.aliasapps.sports.main.profile;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.TrophyUserRelation;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by evanchen on 16-04-28.
 */
public class ProfileTrophiesAdapter extends RecyclerView.Adapter<ProfileTrophiesAdapter.MyViewHolder> {

    public Activity context;
    private List<TrophyUserRelation> trophyUserRelationItemList;

    public ProfileTrophiesAdapter(Activity context, List<TrophyUserRelation> trophyUserRelationItemList) {
        this.context = context;
        this.trophyUserRelationItemList = trophyUserRelationItemList;
    }


    @Override
    public ProfileTrophiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_trophy_item, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProfileTrophiesAdapter.MyViewHolder holder, int position) {
        final TrophyUserRelation  trophyUserRelation = trophyUserRelationItemList.get(position);
        holder.trophyName.setText(trophyUserRelation.getTrophy().getName());
        holder.trophyDesc.setText(trophyUserRelation.getTrophy().getDesc());
        try {
            Picasso.with(context).load(trophyUserRelation.getTrophy().getIcon()).noFade().into(holder.trophyIcon);
        }catch (Exception e){
            holder.trophyIcon.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return trophyUserRelationItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView trophyIcon;
        private TextView trophyName;
        private TextView trophyDesc;

        public MyViewHolder(View itemView) {
            super(itemView);

            trophyIcon = (CircleImageView)itemView.findViewById(R.id.trophyIconID);
            trophyName = (TextView) itemView.findViewById(R.id.trophyName);
            trophyDesc = (TextView) itemView.findViewById(R.id.trophyDescID);
        }
    }
}
