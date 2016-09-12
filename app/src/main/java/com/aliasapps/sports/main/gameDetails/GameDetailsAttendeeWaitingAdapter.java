package com.aliasapps.sports.main.gameDetails;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Attendee;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.notifications.NotificationHolderFragment;
import com.aliasapps.sports.main.profile.ProfileHolderFragment;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.aliasapps.sports.utils.ProfileTabFragmentListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by evanchen on 16-04-22.
 */
public class GameDetailsAttendeeWaitingAdapter extends RecyclerView.Adapter<GameDetailsAttendeeWaitingAdapter.MyViewHolder>{

    private static List<Attendee> userWaitingImageItem;
    public Activity context;
    private static final String TAG = GameDetailsAttendeeWaitingAdapter.class.getName();
    OnItemClickListener mItemClickListener;
    public static User user_gameDetailsWaiting_onNotification;
    public static User user_gameDetailsWaiting_onProfile;
    private static NotificationsTabFragmentListener notificationsTabFragmentListener = NotificationHolderFragment.notificationsTabFragmentListener;
    private static ProfileTabFragmentListener profileTabFragmentListener = ProfileHolderFragment.profileTabFragmentListener;

    public GameDetailsAttendeeWaitingAdapter(List<Attendee> userWaitingImageItem, Activity context) {
        this.userWaitingImageItem = userWaitingImageItem;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
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

            Attendee attendee = userWaitingImageItem.get(getAdapterPosition());
            User user =  attendee.getUser();

            int currentTabPosition = MainActivity.viewPager.getCurrentItem();

            if(currentTabPosition == 0) {

            }
            else if(currentTabPosition == 2){
                user_gameDetailsWaiting_onNotification = user;
                showProfileOnNotificationTab();
            }
            else if(currentTabPosition == 3){
                user_gameDetailsWaiting_onProfile = user;
                showProfileOnProfileTab();
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
        final Attendee attendee = userWaitingImageItem.get(position);
        try {
            Picasso.with(context).load(attendee.getUser().getProfileImage()).into(holder.userImage);
        } catch (Exception e) {
            holder.userImage.setImageResource(R.drawable.david_beckham);
        }

    }

    @Override
    public int getItemCount() {
        return userWaitingImageItem.size();
    }

    private void showProfileOnNotificationTab() {
        notificationsTabFragmentListener.onSwitchToNextFragment();
    }

    private void showProfileOnProfileTab()    {
        profileTabFragmentListener.onSwitchToNextFragment();
    }

}
