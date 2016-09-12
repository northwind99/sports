package com.aliasapps.sports.main.notifications;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Game;
import com.aliasapps.sports.entity.SimpleNotification;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.main.MainActivity;
import com.aliasapps.sports.main.gameDetails.GameDetailsFragment;
import com.aliasapps.sports.utils.NotificationsTabFragmentListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by evanchen on 16-05-20.
 */
public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<SimpleNotification> itemsData;
    public static Game game;
    public static boolean alert_gameItemclicked = false;
    private NotificationsTabFragmentListener notificationsTabFragmentListener =  NotificationHolderFragment.notificationsTabFragmentListener;
    public NotificationAdapter(List<SimpleNotification> itemsData){
        this.itemsData = itemsData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_item, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final SimpleNotification simpleNotification = itemsData.get(position);
        final User user = simpleNotification.getUser();
        final Game game = simpleNotification.getGame();

        String str =  simpleNotification.getText();
        String strGameName = game.getName();
        String strFirstName = user.getFirstName();
        String strLastName = user.getLastName();
        Picasso.with(holder.itemView.getContext()).load(simpleNotification.getIcon()).into(holder.notificationImage);
        holder.notificationText.setText(str);
        holder.notificationTimestamp.setText(simpleNotification.getCreated() + "");

        changeTextinView(holder.notificationText, strGameName, strFirstName, strLastName, holder.itemView, game);

    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView notificationImage;
        private TextView notificationText;
        private TextView notificationTimestamp;

        public MyViewHolder(View itemView) {
            super(itemView);
            notificationImage = (RoundedImageView) itemView.findViewById(R.id.notification_itemImage);
            notificationText = (TextView) itemView.findViewById(R.id.notification_itemText);
            notificationTimestamp = (TextView) itemView.findViewById(R.id.notification_timestamp);
        }
    }

    // highlight game name
    private void changeTextinView(TextView tv, String gameName, String firstName, String lastName, final View view, final Game game2) {
        String vString = tv.getText().toString().replace("{GAME_NAME}", gameName);
        String replaceFN = vString.replace("{FIRST_NAME}",firstName);
        String replaceLN = replaceFN.replace("{LAST_NAME}",lastName);
        int startSpan, endSpan = 0;
        SpannableString ss = new SpannableString(replaceLN);
        while (true) {
            startSpan = replaceLN.indexOf(gameName, endSpan);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
//                    Toast.makeText(view.getContext(), game.getName() +" is clicked", Toast.LENGTH_SHORT).show();
//                    Bundle mBundle = new Bundle();
//                    mBundle.putSerializable("game object from Notifications", game);
//                    GameDetailsFragment gameDetailsFragment = new GameDetailsFragment();
//                    gameDetailsFragment.setArguments(mBundle);
//                    alert_gameItemclicked = true;
//                    MainActivity.alert_gameList.add(game);
                    game = game2;
                    alert_gameItemclicked = true;
                    enterGameDetailsFragmentOnNotificationsTab();
                    alert_gameItemclicked= false;
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.RED);
                }
            };
            // Need a NEW span object every loop, else it just moves the span
            if (startSpan < 0)
                break;
            endSpan = startSpan + gameName.length();
            ss.setSpan(clickableSpan, startSpan, endSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setHighlightColor(Color.TRANSPARENT);
    }

    private void enterGameDetailsFragmentOnNotificationsTab() {
        notificationsTabFragmentListener.onSwitchToNextFragment();
    }
}
