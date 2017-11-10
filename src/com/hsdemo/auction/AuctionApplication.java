package com.hsdemo.auction;

import android.app.Application;
import android.util.Log;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyPingCallback;

public class AuctionApplication extends Application {

    private Client mKinveyClient = null;

    @Override
    public void onCreate() {
        super.onCreate();

        getClient().ping(new KinveyPingCallback() {
            public void onFailure(Throwable t) {
                Log.e("TAG", "Kinvey Ping Failed", t);
            }
            public void onSuccess(Boolean b) {
                Log.d("TAG", "Kinvey Ping Success");
            }
        });
    }

    public Client getClient() {
        if(mKinveyClient == null)
        {
            mKinveyClient = new Client.Builder(getApplicationContext()).build();
        }
        return mKinveyClient;
    }
}
