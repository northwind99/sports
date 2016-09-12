package com.aliasapps.sports.main.messages;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Conversation;
import com.aliasapps.sports.entity.Message;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by evanchen on 16-05-13.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    public List<Message> itemsData;
    public Boolean me;
    public Activity context;
    private static int creatorId;

    public ChatAdapter(List<Message> itemsData){
        this.itemsData = itemsData;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView message;
        public ImageView imgViewIcon;
        private TextView time;
        Message myData;


        public MyViewHolder(View itemView) {
            super(itemView);

            if(me) {
                message = (TextView) itemView.findViewById(R.id.chat_me_text);
                imgViewIcon = (ImageView) itemView.findViewById(R.id.chat_me_userpic);
                time = (TextView) itemView.findViewById(R.id.chat_me_timestamp);
            }else {
                message = (TextView) itemView.findViewById(R.id.chat_other_text);
                imgViewIcon = (ImageView) itemView.findViewById(R.id.chat_other_userpic);
                time = (TextView) itemView.findViewById(R.id.chat_other_timestamp);
            }
        }

        public void setItem(Message item){
            myData = item;

            final String text = item.getMessage();
            creatorId = item.getCreatorId();
            String imageUrl;

            User creator = ChatActivity.conversation.getCreator();
            User to = ChatActivity.conversation.getTo();

            if(creatorId == MainActivity.user.getId()) {
                imageUrl = to.getProfileImage();
            }else{
                imageUrl = creator.getProfileImage();
            }

           if (imageUrl.compareTo("null") != 0){
                try {
                    Picasso.with(MyViewHolder.this.itemView.getContext()).load(imageUrl).into(imgViewIcon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            long timeStamp = item.getCreated();
            String time1 = new java.util.Date(timeStamp*1000).toString(); //convert time from long to data/time
            try {
                time.setText(time1);
                message.setText(text);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == 0){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_me, parent, false);
            me = true;
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_other, parent, false);
            me = false;
        }

        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        Message obj = itemsData.get(position);

            if(obj.getCreatorId() == MainActivity.user.getId())
                return 0; //ME
            else
                return 1; //OTHER
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.setItem(itemsData.get(position));
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

}
