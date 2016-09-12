package com.aliasapps.sports.main.messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Conversation;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

/**
 * Created by evanchen on 16-05-13.
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder>{

    private List<Conversation> itemsData;

    public ConversationAdapter(List<Conversation> itemsData) {
        this.itemsData = itemsData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item_view, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Conversation conversation = itemsData.get(position);
        final User creator = conversation.getCreator();
        final User to = conversation.getTo();
        if(creator != MainActivity.user) {
          try {
              Picasso.with(holder.itemView.getContext()).load(creator.getProfileImage()).into(holder.conversationImage);
          } catch (Exception e) {
              holder.conversationImage.setImageResource(0);
          }
          holder.conversationUserName.setText(creator.getFirstName() + " " + creator.getLastName());
      }else{
          try {
              Picasso.with(holder.itemView.getContext()).load(to.getProfileImage()).into(holder.conversationImage);
          } catch (Exception e) {
              holder.conversationImage.setImageResource(0);
          }
          holder.conversationUserName.setText(to.getFirstName() + " " + to.getLastName());
      }
          holder.conversationLastMsg.setText(conversation.getLastMessage().getMessage());

    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView conversationImage;
        private TextView conversationUserName;
        private TextView conversationLastMsg;

        public MyViewHolder(View itemView) {
            super(itemView);
            conversationImage = (ImageView) itemView.findViewById(R.id.conversation_image);
            conversationUserName = (TextView) itemView.findViewById(R.id.conversation_username);
            conversationLastMsg = (TextView) itemView.findViewById(R.id.conversation_last_msg);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Conversation mData = itemsData.get(getAdapterPosition());
            int chatId = mData.getId();
            String creatorName = mData.getCreator().getFirstName() + " " + mData.getCreator().getLastName();
            String toName = mData.getTo().getFirstName() + " " + mData.getTo().getLastName();
            String message = mData.getLastMessage().getMessage();
            Intent i = new Intent(v.getContext(), ChatActivity.class);
            Bundle mBundle = new Bundle();
         //   mBundle.putString("chatId", chatId + "");
            mBundle.putSerializable("Conversation", mData);
            i.putExtras(mBundle);
            v.getContext().startActivity(i);
        }
    }
}
