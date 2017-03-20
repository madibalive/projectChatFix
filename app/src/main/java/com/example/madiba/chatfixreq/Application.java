package com.example.madiba.chatfixreq;

import com.parse.Parse;

import tgio.parselivequery.LiveQueryClient;

/**
 * Created by Madiba on 3/17/2017.
 */

public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("oARVgHNnYTJZH2xKhyoQBOzsyuQjHl0YwGt8V4J7")
                .clientKey("BR9uoqKFnvckxKjzeCR9Q9BoFaslIwa1xYsssufe")
                .server("https://parseapi.back4app.com/").build()
        );

        LiveQueryClient.init("wss://alphatest.back4app.io", "oARVgHNnYTJZH2xKhyoQBOzsyuQjHl0YwGt8V4J7",true);

    }
}
