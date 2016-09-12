package com.aliasapps.sports.main.messages;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliasapps.sports.R;
import com.aliasapps.sports.entity.Conversation;
import com.aliasapps.sports.entity.Message;
import com.aliasapps.sports.entity.User;
import com.aliasapps.sports.http.ApplicationController;
import com.aliasapps.sports.http.Helpers;
import com.aliasapps.sports.main.MainActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ChatAdapter chatAdapter;
    public static List<Message> itemsData = new ArrayList<>();
    private int conversationId;
    public static Conversation conversation;
    private EditText chatMsg;
    User to;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = false;
    boolean check = false;
    int pageNext = 0;
    private Message message;
    private RoundedImageView chatBarUserPic;
    private TextView chatBarUserName;
    public static boolean chatUserPicPressed = false;
    public static User chatBarUser;
    private static int countNewMessage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        populateData();
        customActionBar();

        chatMsg.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    mRecyclerView.smoothScrollToPosition(0);
                }
            }
        });

        chatBarUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               chatUserPicPressed = true;
               finish();
            }
        });

    }

    private void customActionBar(){
        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
        ActionBar.LayoutParams params = new
                ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER| Gravity.CENTER_VERTICAL);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.chat_actionbar_custom, null);
        chatBarUserPic = (RoundedImageView) v.findViewById(R.id.chatActionBarUserPic);
        chatBarUserName = (TextView) v.findViewById(R.id.chatActionBarUserName);
        Picasso.with(this).load(conversation.getCreator().getProfileImage()).into(chatBarUserPic);
        if(conversation.getCreator() == MainActivity.user){
            chatBarUser = conversation.getTo();
        }else{
            chatBarUser = conversation.getCreator();
        }
        chatBarUserName.setText(chatBarUser.getFirstName() + " " + chatBarUser.getLastName());
        chatBarUserPic.setScaleType(RoundedImageView.ScaleType.CENTER_CROP);
        actionBar.setCustomView(v, params);
    }

    private void populateData(){
        chatMsg = (EditText) findViewById(R.id.chat_message);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_messages);
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        lm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.scrollToPosition(0);
        chatAdapter = new ChatAdapter(itemsData);
        mRecyclerView.setAdapter(chatAdapter);

        Intent i = getIntent();
        Bundle mBundle = i.getExtras();
        conversation = (Conversation) mBundle.getSerializable("Conversation");
        conversationId = conversation.getId();

        if(conversation.getCreator() == MainActivity.user){
            to = conversation.getTo();
        }else {
            to = conversation.getCreator();
        }

        if(itemsData.isEmpty()){
            getMessages(pageNext);
        }

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = lm.getChildCount();
                totalItemCount = lm.getItemCount();
                pastVisiblesItems = lm.findFirstVisibleItemPosition();
                if (!loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        if((itemsData.size() - countNewMessage) % 25 == 0 && !check) {
                            pageNext = pageNext + 1;
                            getMessages(pageNext);
                            check = true;
                        }
                    }
                }
            }
        });
    }


    private void getMessages(int page){

        String endpoint = Helpers.BASE_SECOND_URL + "/chat/" + "" + conversationId +  "/conversations/" + page;

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getsMessageData(new JSONArray(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", MainActivity.user.getToken());
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private void getsMessageData(JSONArray jsonArray) {
        Message message;
        try{
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject msgJSONObject = jsonArray.getJSONObject(i);
                message = new Message(msgJSONObject.getInt("id"), msgJSONObject.getInt("conversation_id"), msgJSONObject.getInt("creator_id")
                        , msgJSONObject.getString("message") , msgJSONObject.getInt("seen"),  msgJSONObject.getLong("created"));
                itemsData.add(message);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        chatAdapter.notifyDataSetChanged();
    }

    public void sendMessage(View view){

        String endpoint = Helpers.BASE_SECOND_URL + "/chat/message";

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getNewMessage(new JSONObject(response));
                    ConversationFragment.conversationAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", MainActivity.user.getToken());
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("to_id", to.getId() + "");
                params.put("message", chatMsg.getText().toString());
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);

    }

    private void getNewMessage(JSONObject jsonObject) {
        Message messageNew;
        try {
            messageNew = new Message(jsonObject.getInt("id"), jsonObject.getInt("conversation_id"), jsonObject.getInt("creator_id")
                    , jsonObject.getString("message") , jsonObject.getInt("seen"),  jsonObject.getLong("created"));
            itemsData.add(0, messageNew);
            countNewMessage = countNewMessage + 1;
            chatAdapter.notifyDataSetChanged();
            ConversationFragment.itemsData.get(0).setLastMessage(messageNew);
            ConversationFragment.conversationAdapter.notifyDataSetChanged();
            message = messageNew;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        chatMsg.setText("");
        scrollToBottom();
    }

    private void scrollToBottom(){
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainActivity.viewPager.setCurrentItem(1);
        finish();
    }
}
