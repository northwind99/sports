package com.aliasapps.sports.main.findGames;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Sport;
import com.squareup.picasso.Picasso;
import java.util.List;

public class CreateGameSelectAdapter extends RecyclerView.Adapter<CreateGameSelectAdapter.MyViewHolder> {

    public static List<Sport> sportItems;
    public Activity context;
    private Integer pos = null;
    private View preSelectedView;
    private View preSelectedView2;
    public static Sport selectedSport;

    private static final String TAG = CreateGameSelectAdapter.class.getName();

    public static boolean clicked = false; // this is a member variable

    public CreateGameSelectAdapter(Activity context, List<Sport> sportItem){
        this.sportItems = sportItem;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView sportId;
        public TextView sportName;
        public ImageView sportIcon;
        public TextView sportMapIcon;
        public RelativeLayout mBackground;

        public MyViewHolder(View itemView) {
            super(itemView);

            sportId = (TextView) itemView.findViewById(R.id.sportIdID);
            sportName = (TextView) itemView.findViewById(R.id.sportNameID);
            sportIcon= (ImageView) itemView.findViewById(R.id.sportIconID);
            sportMapIcon = (TextView) itemView.findViewById(R.id.sportMapIconID);
            mBackground = (RelativeLayout) itemView.findViewById(R.id.itemBackgroundID);
            itemView.setOnClickListener(this);
        }

        //only one item can be selected
        @Override
        public void onClick(View v) {
            pos = getAdapterPosition();
            try {
                //reset unselected item color
                preSelectedView.findViewById(R.id.itemBackgroundID).setSelected(false);

                //after scroll RecyclerView, reset unselected item color
                preSelectedView2.findViewById(R.id.itemBackgroundID).setSelected(false);
            }catch (Exception e){
                e.printStackTrace();
            }
            mBackground.setSelected(true);

            //store user selected sport object
            selectedSport = sportItems.get(getAdapterPosition());

            //store the previous selected view on the same screen
            preSelectedView = itemView;
            clicked = true;
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_game_sport_item, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Sport sport = sportItems.get(position);
        holder.sportId.setText(Integer.toString(sport.getId()));
        holder.sportName.setText(sport.getName());
        try {
            Picasso.with(context).load(sport.getIcon()).into(holder.sportIcon);
            Log.v(TAG, "create game");
        }catch(Exception e){
            holder.sportIcon.setImageResource(0);
        }
        holder.sportMapIcon.setText(sport.getMap_icon());

        if (pos != null) {
            if (position == pos) {
                holder.mBackground.setSelected(true);

                //store the previous selected view in the whole RecyclerView
                preSelectedView2 = holder.itemView;
            }else {
                holder.mBackground.setSelected(false);
            }
        }else{
            holder.mBackground.setSelected(false);
        }

    }

    @Override
    public int getItemCount() {
        return sportItems.size();
    }

}
