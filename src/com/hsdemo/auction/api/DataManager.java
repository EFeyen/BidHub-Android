package com.hsdemo.auction.api;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hsdemo.auction.AuctionApplication;
import com.hsdemo.auction.events.BidsRefreshedEvent;
import com.hsdemo.auction.models.AuctionItem;
import com.hsdemo.auction.models.Bid;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.query.AbstractQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;

public class DataManager {
	public Activity activity;

	private List<AuctionItem> allItems = new ArrayList<AuctionItem>();
	static DataManager singletonInstance;
	Handler handler = new Handler();

	public static final int BID_FETCH_INTERVAL = 3000;
	public static final int RETRY_INTERVAL = 60000;

	public static final String QUERY_ALL = "ALL";
	public static final String QUERY_NOBIDS = "NOBIDS";
	public static final String QUERY_MINE = "MINE";

//  AsyncHttpClient client = new AsyncHttpClient();
	Context context;

	public DataManager() {
	}

	public DataManager(Activity act) {
		this.activity = act;
	}

	public static DataManager getInstance() {
		if (singletonInstance == null)
			return null;

		return singletonInstance;
	}

	public static DataManager getInstance(Activity act) {
		if (singletonInstance == null)
			singletonInstance = new DataManager(act);

		return singletonInstance;
	}

	public void fetchAllItems() {
		fetchAllItems(new Runnable() {
			@Override
			public void run() {
				EventBus.getDefault().post(new BidsRefreshedEvent());
			}
		});
	}

	public void fetchAllItems(final Runnable after) {
		if (getClient().user().isUserLoggedIn()) {
			Log.i("TEST", "Fetching all items.");

			Query qItems = getClient().query();
			qItems.addSort("closetime", AbstractQuery.SortOrder.ASC);
			qItems.addSort("name", AbstractQuery.SortOrder.ASC);
			AsyncAppData<AuctionItem> myItems = getClient().appData("items", AuctionItem.class);
			myItems.get(qItems, new KinveyListCallback<AuctionItem>() {
				@Override
				public void onSuccess(AuctionItem[] auctionItems) {
					Log.i(Client.TAG, "Kinvey Items fetched: " + auctionItems.length + " items.");
					allItems.clear();
					allItems.addAll(Arrays.asList(auctionItems));

					for(Iterator<AuctionItem> i = allItems.iterator(); i.hasNext(); ) {
						final AuctionItem item = i.next();
						Query qBids = getClient().query();
						qBids.addSort("amount", AbstractQuery.SortOrder.ASC);
						qBids.equals("itemId", item.getEntityId());
						AsyncAppData<Bid> itemBids = getClient().appData("bids", Bid.class);
						itemBids.get(qBids, new KinveyListCallback<Bid>() {
							@Override
							public void onSuccess(Bid[] auctionBids) {
								Log.i(Client.TAG, "Kinvey Item Bids fetched: " + auctionBids.length + " bids.");
								item.setBids(Arrays.asList(auctionBids));
							}

							@Override
							public void onFailure(Throwable throwable) {
								Log.w(Client.TAG, "Error fetching bids: " + throwable.getMessage());
								Toast.makeText(context, "An error occurred. Try again in a minute or log out and log back in.", Toast.LENGTH_LONG).show();
							}
						});
					}

					after.run();
				}

				@Override
				public void onFailure(Throwable throwable) {
					Log.w(Client.TAG, "Error fetching items: " + throwable.getMessage());
					Toast.makeText(context, "An error occurred. Try again in a minute or log out and log back in.", Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	public List<AuctionItem> getItemsMatchingQuery(String query, Activity context) {
		if (query.equals(QUERY_ALL)) {
			return allItems;
		} else if (query.equals(QUERY_MINE)) {
			ArrayList<AuctionItem> mine = new ArrayList<AuctionItem>();
			for (AuctionItem item : allItems) {
				if (item.hasUserBid(context))
					mine.add(item);
			}

			return mine;
		} else if (query.equals(QUERY_NOBIDS)) {
			ArrayList<AuctionItem> noBids = new ArrayList<AuctionItem>();
			for (AuctionItem item : allItems) {
				if (getBidQuantityForItem(item.getEntityId()) == 0)
					noBids.add(item);
			}

			return noBids;
		} else {
			ArrayList<String> queryWords = new ArrayList<String>();
			queryWords.addAll(Arrays.asList(query.split(" ")));

			ArrayList<AuctionItem> results = new ArrayList<AuctionItem>();
			for (AuctionItem item : allItems) {
				for (String word : queryWords) {
					if (word.length() > 1 &&
							(item.getName().toLowerCase().contains(word.toLowerCase()) || item.getDonorName().toLowerCase().contains(word.toLowerCase()) ||
									item.getDescription().toLowerCase().contains(word.toLowerCase())))
						results.add(item);
				}
			}

			return results;
		}
	}

	public AuctionItem getItemForId(String id) {
		for (AuctionItem item : allItems) {
			if (item.getEntityId().equals(id))
				return item;
		}

		return null;
	}

	public void beginBidCoverage(Context context) {
		Log.i("TEST", "Beginning coverage...");
		this.context = context;

		if (allItems.size() > 0)
			refreshBids.run();
		else
			fetchAllItems(refreshBids);
	}

	public void refreshBidsNow(final Runnable after) {
		Log.i("TEST", "Refreshing bids...");
		EventBus.getDefault().post(new BidsRefreshedEvent());

		if (after != null)
			after.run();
	}

	private Runnable refreshBids = new Runnable() {
		@Override
		public void run() {

			// Post an emergency retry runnable
			handler.removeCallbacks(retryRefreshBids);
			handler.postDelayed(retryRefreshBids, RETRY_INTERVAL);

			// Don't bother if we don't have the items yet
			if (allItems.size() == 0) {
				handler.removeCallbacks(refreshBids);
				handler.postDelayed(refreshBids, BID_FETCH_INTERVAL);
				return;
			}

			fetchAllItems(new Runnable() {
				@Override
				public void run() {
					EventBus.getDefault().post(new BidsRefreshedEvent());
					// handler.postDelayed(refreshBids, BID_FETCH_INTERVAL);
				}
			});
		}
	};

	private Runnable retryRefreshBids = new Runnable() {
		@Override
		public void run() {
			if (getClient().user().isUserLoggedIn()) {
				Log.i(Client.TAG, "Retrying...");

/*
      if (inflightQuery != null) {
        Log.i("TEST", "Cancelling inflight query.");
        inflightQuery.cancel();
        inflightQuery = null;
      }
*/

				handler.removeCallbacks(refreshBids);
				handler.removeCallbacks(retryRefreshBids);
				refreshBids.run();
			}
		}
	};

	public int getBidQuantityForItem(String itemId) {
		return getItemForId(itemId).getNumberOfBids();
	}

	public void endBidCoverage() {
		Log.i("TEST", "Ending coverage...");
		handler.removeCallbacks(refreshBids);
	}

	public static abstract class BidCallback {
		public abstract void bidResult(boolean placed);
	}

	/**
	 * @return an instance of a Kinvey Client
	 */
	public Client getClient() {
		return ((AuctionApplication) activity.getApplicationContext()).getClient();
	}
}
