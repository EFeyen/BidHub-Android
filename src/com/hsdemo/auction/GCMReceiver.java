package com.hsdemo.auction;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by ncauldwell on 3/12/16.
 */
public class GCMReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(), com.hsdemo.auction.GCMService.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}
