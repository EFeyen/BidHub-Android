package com.hsdemo.auction;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by jtsuji on 11/14/14.
 */
public class IdentityManager {
	public static final String PREFS_FNAME_KEY = "FNAME";
	public static final String PREFS_LNAME_KEY = "LNAME";
	public static final String PREFS_EMAIL_KEY = "EMAIL";
	public static final String PREFS_BIDDER_KEY = "BIDDER";
	public static final String SHAREDPREFS_KEY = "auctionapp";

	public static String getFName(Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		return prefs.getString(PREFS_FNAME_KEY, "");
	}

	public static void setFName(String fname, Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		prefs.edit().putString(IdentityManager.PREFS_FNAME_KEY, fname).apply();
	}

	public static String getLName(Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		return prefs.getString(PREFS_LNAME_KEY, "");
	}

	public static void setLName(String lname, Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		prefs.edit().putString(IdentityManager.PREFS_LNAME_KEY, lname).apply();
	}

	public static String getEmail(Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		return prefs.getString(PREFS_EMAIL_KEY, "");
	}

	public static void setEmail(String email, Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		prefs.edit().putString(IdentityManager.PREFS_EMAIL_KEY, email).apply();
	}

	public static String getBidder(Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		return prefs.getString(PREFS_BIDDER_KEY, "");
	}

	public static void setBidder(String bidder, Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(SHAREDPREFS_KEY, Activity.MODE_PRIVATE);
		prefs.edit().putString(IdentityManager.PREFS_BIDDER_KEY, bidder).apply();
	}
}
