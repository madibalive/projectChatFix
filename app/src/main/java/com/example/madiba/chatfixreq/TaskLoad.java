package com.example.madiba.chatfixreq;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/7/2016.
 */

public class TaskLoad implements Callable<List<ParseObject>> {
    public TaskLoad() {
    }
    @Override
    public List<ParseObject> call() throws Exception {

        ParseQuery<ParseObject> query2= ParseQuery.getQuery("Gossip");
        query2.orderByDescending("createdAt");
        return query2.find();
    }
}
