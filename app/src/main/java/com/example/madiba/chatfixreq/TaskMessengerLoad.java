package com.example.madiba.chatfixreq;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Madiba on 12/7/2016.
 */

public class TaskMessengerLoad implements Callable<List<String>> {
    private  ParseObject object;
    public TaskMessengerLoad(ParseObject object) {
        this.object=object;
    }

    @Override
    public List<String> call() throws Exception {
        List<String> cells = new ArrayList<>();

        ParseQuery<ParseObject> query= ParseQuery.getQuery("Message");
        query.whereEqualTo("to",object);
        query.orderByDescending("createdAt");
        query.setLimit(20);

        for (ParseObject object: query.find()
                ) {
            cells.add( object.getString("fromName")+"\n"+object.getString("message"));
        }

        return cells;
    }
}
