package com.aliasapps.sports.main.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationFragment extends Fragment {


    private RecyclerView mRecyclerView;
    public static ConversationAdapter conversationAdapter;
    public static List<Conversation> itemsData = new ArrayList<>();
    private static User user;
    SwipeRefreshLayout swipeLayout;
    TextView noConversation;
    int pageNext = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = false;
    boolean check = false;
    private static int countNewConversationItem = 0;

    public ConversationFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        noConversation = (TextView) view.findViewById(R.id.profile_noConversationMsg);
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                 itemsData.clear();
                 conversationAdapter.notifyDataSetChanged();
                 getConversations(pageNext);
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.conversationsView);
        user = MainActivity.user;
        if(itemsData.isEmpty()){
            getConversations(pageNext);
        }
        if(itemsData.size() == 0)
            noConversation.setVisibility(View.VISIBLE);
        else
            noConversation.setVisibility(View.INVISIBLE);
        populateData();
        return view;
    }

    private void populateData(){
        mRecyclerView.setHasFixedSize(true);
        final LinearLayoutManager lm = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(lm);
        mRecyclerView.scrollToPosition(0);
        conversationAdapter = new ConversationAdapter(itemsData);
        mRecyclerView.setAdapter(conversationAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean isRefreshing = swipeLayout.isRefreshing();
                if(isRefreshing){
                    swipeLayout.setRefreshing(false);
                }
                visibleItemCount = lm.getChildCount();
                totalItemCount = lm.getItemCount();
                pastVisiblesItems = lm.findFirstVisibleItemPosition();
                if (!loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = true;
                        if((itemsData.size()) % 25 == 0 && !check) {
                            pageNext = pageNext + 1;
                            getConversations(pageNext);
                            check = true;
                        }
                    }
                }
            }
        });

    }

    public static void getConversations(int page){

        String endpoint = Helpers.BASE_SECOND_URL + "/chat/conversations/" + page;

        StringRequest jsonObjRequest = new StringRequest(Request.Method.GET,
                endpoint, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    getConversationsData(new JSONArray(response));
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
                params.put("Authorization", user.getToken());
                return params;
            }

        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjRequest);
    }

    private static void getConversationsData(JSONArray jsonObject) {
        Conversation conversation;
        Message message;
        User creator;
        User to;
        try{
            for(int i = 0; i < jsonObject.length(); i++) {
                JSONObject cvJSONObject = jsonObject.getJSONObject(i);
                int id = cvJSONObject.getInt("id");
                JSONObject creatorJSONObject = cvJSONObject.getJSONObject("creator");
                creator = new User(creatorJSONObject.getInt("id"), creatorJSONObject.getString("first_name"), creatorJSONObject.getString("last_name"),creatorJSONObject.getString("email")
                        , creatorJSONObject.getString("nickname"), creatorJSONObject.getString("latitude"), creatorJSONObject.getString("longitude")
                        , creatorJSONObject.getString("bio"), creatorJSONObject.getString("token"), creatorJSONObject.getString("image"));
                JSONObject toJSONObject = cvJSONObject.getJSONObject("to");
                to = new User(toJSONObject.getInt("id"), toJSONObject.getString("first_name"), toJSONObject.getString("last_name"),toJSONObject.getString("email")
                        , toJSONObject.getString("nickname"), toJSONObject.getString("latitude"), toJSONObject.getString("longitude")
                        , toJSONObject.getString("bio"), toJSONObject.getString("token"), toJSONObject.getString("image"));

                JSONObject messageJSONObject = cvJSONObject.getJSONObject("last_message");
                message = new Message(messageJSONObject.getInt("id"), messageJSONObject.getInt("conversation_id"), messageJSONObject.getInt("creator_id")
                            , messageJSONObject.getString("message") , messageJSONObject.getInt("seen"), messageJSONObject.getLong("created"));

                conversation = new Conversation(id, creator, to, message);
                itemsData.add(conversation);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        conversationAdapter.notifyDataSetChanged();
    }


}
