package com.example.madiba.chatfixreq;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import tgio.parselivequery.BaseQuery;
import tgio.parselivequery.LiveQueryClient;
import tgio.parselivequery.LiveQueryEvent;
import tgio.parselivequery.Subscription;
import tgio.parselivequery.interfaces.OnListener;

public class Messenger extends AppCompatActivity {

    private RecyclerView mRecyclerview;
    private MessageAdapter mAdapter;
    private EditText mMesssage;
    private ImageButton mSend;
    private ParseObject toObject;
    Boolean isSubscribed = false;
    Subscription sub;
    private List<String>   mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mMesssage = (EditText) findViewById(R.id.messageInput);
        mSend = (ImageButton) findViewById(R.id.messageSendButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toObject = ParseObject.createWithoutData("Gossip",getIntent().getStringExtra("id"));
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMessage(mMesssage.getText().toString());
            }
        });

        mAdapter = new MessageAdapter(R.layout.item_comment, mDatas);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setAdapter(mAdapter);
        LiveQueryClient.connect();

        doSearch(toObject);

    }

    private void addMessage(String message){
        ParseObject comment = new ParseObject("Message");
        comment.put("from", ParseUser.getCurrentUser());
        comment.put("fromName", ParseUser.getCurrentUser().getUsername());
        comment.put("message", message);
        comment.put("to", toObject);
        comment.put("toid", toObject.getObjectId());
        comment.saveInBackground();
        mMesssage.setText("");
        mSend.setEnabled(true);
    }

    private Callback<List<String>> loadCallBack = new Callback<List<String>>() {
        @Override
        public void onFinish(final List<String> value, Callable callable, Throwable e) {
            if (e == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setNewData(value);
                    }
                },500);
            }
            setupSubscription();

        }
    };

    private void doSearch(ParseObject id){
        TaskMessengerLoad taskLoad = new TaskMessengerLoad(id);
        Tasks.execute(taskLoad,loadCallBack);
    }


    private void setupSubscription(){
        isSubscribed = true;
        sub = new BaseQuery.Builder("Message")
                .where("toid",toObject.getObjectId() )
                .addField("fromName")
                .addField("message")
                .build()
                .subscribe();

        sub.on(LiveQueryEvent.CREATE, new OnListener() {
            @Override
            public void on(JSONObject object) {
                try {
                    Log.e("CHAT", "onMessage: "+object.toString());
                    String message = (String) ((JSONObject) object.get("object")).get("message");
                    String sender = (String) ((JSONObject) object.get("object")).get("fromName");
                    final String newMsg = sender + " "+'\n' + message;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add(0,newMsg);
                        }
                    });
                }catch (Exception e){
                    Log.e("CHAT", "on: Message error"+e.getMessage() );
                }
            }
        });

    }
//
//    private void useExiting(){
//        if(sub != null){
//            if(sub.isSubscribed()){
//                sub.unsubscribe();
//                isSubscribed = false;
//
//                setupSubscription();
//            }
//        }else
//            setupSubscription();
//    }


    public class MessageAdapter extends BaseQuickAdapter<String> {
        public MessageAdapter(int layoutResId, List<String> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, String event) {
        holder.setText(R.id.msg, event);
        }
    }


    @Override
    protected void onStop() {
        try {
            sub.unsubscribe();
            LiveQueryClient.removeSubscription(sub);
            LiveQueryClient.disconnect();
        }catch (Exception e){
            Log.e("CHAT", "onStop: error ::"+e.getMessage() );
        }
        super.onStop();
    }

}
