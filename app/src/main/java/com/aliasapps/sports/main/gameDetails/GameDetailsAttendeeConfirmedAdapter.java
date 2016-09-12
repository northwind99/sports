package com.aliasapps.sports.main.gameDetails;

/**
 * Created by evanchen on 16-05-03.
 */

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Attendee;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GameDetailsAttendeeConfirmedAdapter extends RecyclerView.Adapter<GameDetailsAttendeeConfirmedAdapter.MyViewHolder> {

    public static List<Attendee> userConfirmedImageItem;
    public Activity context;
    private static final String TAG = GameDetailsAttendeeWaitingAdapter.class.getName();
    OnItemClickListener mItemClickListener;

    public GameDetailsAttendeeConfirmedAdapter(List<Attendee> userConfirmedImageItem, Activity context) {
        this.userConfirmedImageItem = userConfirmedImageItem;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RoundedImageView userImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            userImage = (RoundedImageView) itemView.findViewById(R.id.gameDetailsUserImageID);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_image, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Attendee attendee = userConfirmedImageItem.get(position);
        try {
            Picasso.with(context).load(attendee.getUser().getProfileImage()).into(holder.userImage);
        } catch (Exception e) {
            holder.userImage.setImageResource(R.drawable.david_beckham);
        }

    }

    @Override
    public int getItemCount() {
        return userConfirmedImageItem.size();
    }
}
