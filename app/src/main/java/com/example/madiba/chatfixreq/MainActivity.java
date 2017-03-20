package com.example.madiba.chatfixreq;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.rongi.async.Callback;
import com.github.rongi.async.Tasks;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private MainAdapter mAdapter;
    private List<ParseObject> mDatas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshview);
        refreshLayout.setOnRefreshListener(this);

        mAdapter = new MainAdapter(R.layout.item_comment, mDatas);
        mAdapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                startActivity(new
                        Intent(MainActivity.this, Messenger.class)
                        .putExtra("id",mAdapter.getItem(i).getObjectId()));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        FloatingActionButton fab_gossip = (FloatingActionButton) findViewById(R.id.fab);
        fab_gossip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();

            }
        });
    }


    @Override
    public void onRefresh() {
        doSearch();
    }


    private Callback<List<ParseObject>> loadCallBack = new Callback<List<ParseObject>>() {
        @Override
        public void onFinish(final List<ParseObject> value, Callable callable, Throwable e) {
            if (e == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setNewData(value);
                    }
                }, 500);
                refreshLayout.setRefreshing(false);
            }else {
                Log.e("CHAT", "onFinish: error"+e.getMessage() );
            }
        }

    };

    private void doSearch(){
        TaskLoad taskLoad = new TaskLoad();
        Tasks.execute(taskLoad,loadCallBack);
    }

   public class MainAdapter extends BaseQuickAdapter<ParseObject> {
        public MainAdapter(int layoutResId, List<ParseObject> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, ParseObject data) {
            holder.setText(R.id.msg, data.getString("title"));
        }
    }

}
