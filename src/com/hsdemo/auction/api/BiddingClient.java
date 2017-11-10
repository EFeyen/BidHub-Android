package com.hsdemo.auction.api;

import android.app.Activity;
import android.util.Log;

import com.hsdemo.auction.IdentityManager;
import com.hsdemo.auction.models.AuctionItem;
import com.hsdemo.auction.models.Bid;
import com.kinvey.android.AsyncAppData;
import com.kinvey.java.core.KinveyClientCallback;

public class BiddingClient {

	static DataManager data = DataManager.getInstance();

	public static void placeBid(final AuctionItem item, final int amt, final DataManager.BidCallback after, Activity context) {

		// Add our Bid and confirm that it's the latest and highest Bid afterward
		final Bid bid = new Bid();
		bid.setRelatedItemId(item.getEntityId());
		bid.setAmount(amt);
		bid.setName(IdentityManager.getFName(context) + " " + IdentityManager.getLName(context));
		bid.setEmail(IdentityManager.getEmail(context));
		bid.setBidderNumber(IdentityManager.getBidder(context));

		AsyncAppData<Bid> auctionBids = data.getClient().appData("bids", Bid.class);
		auctionBids.save(bid, new KinveyClientCallback<Bid>() {
			@Override
			public void onFailure(final Throwable e) {
				Log.e("TAG", "failed to save event data", e);
				data.fetchAllItems(new Runnable() {
					@Override
					public void run() {
						if (after != null)
							after.bidResult(e == null);
					}
				});
			}

			@Override
			public void onSuccess(Bid r) {
				Log.d("TAG", "saved data for entity " + r.getName());
				data.fetchAllItems(new Runnable() {
					@Override
					public void run() {
						if (after != null)
							after.bidResult(true);
					}
				});
			}
		});

/*
		bid.saveInBackground(new SaveCallback() {
			@Override
			public void done(final ParseException e) {
				data.fetchAllItems(new Runnable() {
					@Override
					public void run() {
						if (after != null)
							after.bidResult(e == null);
					}
				});
			}
		});
*/
	}
}
