package com.aliasapps.sports.main.profile;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by evanchen on 16-04-12.
 */
public class ProfileUpcomingGameAdapter extends RecyclerView.Adapter<ProfileUpcomingGameAdapter.MyViewHolder> {

    public Activity context;
    public List<Game> gameItemList;
    private User user = MainActivity.user;
    OnItemClickListener mItemClickListener;

    private static final String TAG = ProfileUpcomingGameAdapter.class.getName();

    public ProfileUpcomingGameAdapter(Activity context, List<Game> gameItemList){
        this.context = context;
        this.gameItemList = gameItemList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView sportName;
        public TextView gameLocation;
        private TextView gameDate;
        private CircleImageView sportImage;


        public MyViewHolder(View itemView) {
            super(itemView);
            sportName = (TextView) itemView.findViewById(R.id.gameNameID);
            gameLocation = (TextView) itemView.findViewById(R.id.gameLocationID);
            gameDate = (TextView) itemView.findViewById(R.id.gameTimeID);
            sportImage = (CircleImageView) itemView.findViewById(R.id.sportImageID);
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
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_upcoming_past_items, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
       final Game game = gameItemList.get(position);
        holder.sportName.setText(game.getName() + " @ ");
        holder.gameLocation.setText(game.getlocationName());
        holder.gameDate.setText(game.getGameTime());
        try {
            Picasso.with(context).load(game.getSport().getIcon()).noFade().into(holder.sportImage);
        }catch (Exception e){
            holder.sportImage.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return gameItemList.size();
    }

}
