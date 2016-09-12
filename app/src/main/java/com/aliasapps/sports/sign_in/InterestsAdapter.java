package com.aliasapps.sports.sign_in;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Sport;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by evanchen on 16-03-28.
 */
public class InterestsAdapter extends RecyclerView.Adapter<InterestsAdapter.MyViewHolder> {

    public static List<Sport> sportItems;
    public static List<Sport> selectSports;
    public Activity context;

    private static final String TAG = InterestsAdapter.class.getName();

    public InterestsAdapter(Activity context, List<Sport> sportItem){
        this.sportItems = sportItem;
        this.selectSports = new ArrayList<>();
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView sportId;
        public TextView sportName;
        public ImageView sportIcon;
        public TextView sportMapIcon;

        public MyViewHolder(View itemView) {
            super(itemView);

            sportId = (TextView) itemView.findViewById(R.id.sportIdID);
            sportName = (TextView) itemView.findViewById(R.id.sportNameID);
            sportIcon= (ImageView) itemView.findViewById(R.id.sportIconID);
            sportMapIcon = (TextView) itemView.findViewById(R.id.sportMapIconID);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.wtf("Adapter", "OnCLICK");
            if(!selectSports.contains(sportItems.get(getAdapterPosition()))){
                selectSports.add(sportItems.get(getAdapterPosition()));
                ((CardView)v).setCardBackgroundColor(v.getContext().getResources().getColor(R.color.orange));
            }else{
                selectSports.remove(sportItems.get(getAdapterPosition()));
                ((CardView)v).setCardBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder");
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_item, parent, false);
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
        }catch(Exception e){
            holder.sportIcon.setImageResource(0);
        }
        holder.sportMapIcon.setText(sport.getMap_icon());
        if(selectSports.contains(sportItems.get(position))) {
            ((CardView)holder.itemView).setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.orange));
        }else {
            ((CardView)holder.itemView).setCardBackgroundColor(Color.TRANSPARENT);
        }

    }

    @Override
    public int getItemCount() {
        return sportItems.size();
    }

}
