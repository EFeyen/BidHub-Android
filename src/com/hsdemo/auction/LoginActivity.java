package com.hsdemo.auction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kinvey.android.AsyncUserDiscovery;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserListCallback;
import com.kinvey.java.User;
import com.kinvey.android.Client;
import com.kinvey.java.model.UserLookup;

public class LoginActivity extends ActionBarActivity {

	// This app allows users to login with either their email address or bidder number (once it is assigned).
	// However, Kinvey only allows logging in with a username, so this feature needed to hacked in
	// We first try to login with whatever the user gave as the username.
	//  - if that fails, then we login with a predefined user
	//      so we can do user discovery using the username given and searching the email field in Kinvey
	//  - if we discover a user with that email address, we grab their username (which should be their bidder number)
	//      and log them in with that and the password they provided
	//
	// This is all very convoluted and I should probably do away with it and just have people log in with their email address,
	//  or let them choose a username.
	private String discoveryUser = "";
	private String discoveryPw = "";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);
        findViewById(R.id.base_tint_darken).setVisibility(View.GONE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.gobutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doLogin(((TextView) findViewById(R.id.email)).getText().toString(),
                        ((TextView) findViewById(R.id.password)).getText().toString());
	            ((TextView) findViewById(R.id.password)).setText("");
            }
        });

		findViewById(R.id.signupbutton).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				Intent signupIntent = new Intent(getApplicationContext(), SignupActivity.class);
				startActivity(signupIntent);
			}
		});

		if(getClient().user().isUserLoggedIn() && getClient().user().getUsername() != null)
		{
			CharSequence text = "Logged in " + getClient().user().getUsername() + ".";
			Log.d(Client.TAG, text.toString());

			IdentityManager.setEmail(getClient().user().get("email").toString(), this);
			IdentityManager.setFName(getClient().user().get("first_name").toString(), this);
			IdentityManager.setLName(getClient().user().get("last_name").toString(), this);
			IdentityManager.setBidder(getClient().user().get("bidderNumber").toString(), this);

			Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
			startActivity(itemListIntent);
		}
		else if(getClient().user().isUserLoggedIn())
		{
			if(getClient().push().isPushEnabled())
			{
				getClient().push().disablePush();
			}
			getClient().user().logout().execute();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if(getClient().user().isUserLoggedIn() && getClient().user().getUsername() != null)
		{
			CharSequence text = "Logged in " + getClient().user().getUsername() + ".";
			Log.d(Client.TAG, text.toString());

			IdentityManager.setEmail(getClient().user().get("email").toString(), this);
			IdentityManager.setFName(getClient().user().get("first_name").toString(), this);
			IdentityManager.setLName(getClient().user().get("last_name").toString(), this);
			IdentityManager.setBidder(getClient().user().get("bidderNumber").toString(), this);

			getClient().push().initialize(getApplication());

			Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
			startActivity(itemListIntent);
		}
		else if(getClient().user().isUserLoggedIn())
		{
			if(getClient().push().isPushEnabled())
			{
				getClient().push().disablePush();
			}
			getClient().user().logout().execute();
		}
	}

	void doLogin(final String email, final String password) {

        if (email.length() < 3 || password.length() < 3) {
            Toast.makeText(getApplicationContext(), "Please enter your bidder number and password.", Toast.LENGTH_LONG).show();
            return;
        }

	    IdentityManager.setEmail(email, this);

	    if(getClient().user().isUserLoggedIn() && getClient().user().getUsername() != null)
	    {
		    CharSequence text = "Logged in " + getClient().user().getUsername() + ".";
		    Log.d(Client.TAG, text.toString());

		    Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
		    startActivity(itemListIntent);
	    }
	    else {
		    if(getClient().user().isUserLoggedIn())
		    {
			    if(getClient().push().isPushEnabled())
			    {
				    getClient().push().disablePush();
			    }
			    getClient().user().logout().execute();
		    }
		    getClient().user().login(email, password, new KinveyUserCallback() {
			    @Override
			    public void onSuccess(User result) {
				    CharSequence text = "Logged in " + result.getUsername() + ".";
				    Log.d(Client.TAG, text.toString());

				    getClient().push().initialize(getApplication());
				    ((TextView) findViewById(R.id.email)).setText("");

				    Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
				    startActivity(itemListIntent);
			    }

			    @Override
			    public void onFailure(Throwable t) {

				    getClient().user().login(discoveryUser, discoveryPw, new KinveyUserCallback() {
					    @Override
					    public void onSuccess(User result) {
						    AsyncUserDiscovery users = getClient().userDiscovery();
						    UserLookup criteria = users.userLookup();
						    criteria.setEmail(email);
						    users.lookup(criteria, new KinveyUserListCallback() {
							    @Override
							    public void onSuccess(User[] result) {
								    getClient().user().logout().execute();
								    if(result.length > 0) {
									    getClient().user().login(result[0].getUsername(), password, new KinveyUserCallback() {
										    @Override
										    public void onSuccess(User result) {
											    CharSequence text = "Logged in " + result.getUsername() + ".";
											    Log.d(Client.TAG, text.toString());

											    getClient().push().initialize(getApplication());
											    ((TextView) findViewById(R.id.email)).setText("");

											    Intent itemListIntent = new Intent(getApplicationContext(), ItemListActivity.class);
											    startActivity(itemListIntent);
										    }

										    @Override
										    public void onFailure(Throwable throwable) {
											    CharSequence text = "Wrong username or password";
											    Log.d(Client.TAG, text.toString());
											    Log.d(Client.TAG, throwable.getLocalizedMessage());
											    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
											    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
											    toast.show();
										    }
									    });
								    }
								    else
								    {
									    CharSequence text = "Wrong username or password";
									    Log.d(Client.TAG, text.toString());
									    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
									    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
									    toast.show();
								    }
							    }
							    @Override
							    public void onFailure(Throwable error) {
								    getClient().user().logout().execute();
								    CharSequence text = "Wrong username or password";
								    Log.d(Client.TAG, text.toString());
								    Log.d(Client.TAG, error.getLocalizedMessage());
								    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
								    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
								    toast.show();
							    }
						    });
					    }

					    @Override
					    public void onFailure(Throwable t) {
						    CharSequence text = "Wrong username or password";
						    Log.d(Client.TAG, text.toString());
						    Log.d(Client.TAG, t.getLocalizedMessage());
						    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
						    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						    toast.show();
					    }
				    });
			    }
		    });
	    }
    }
    /**
     *
     * @return an instance of a Kinvey Client
     */
    public Client getClient(){
        return ((AuctionApplication) getApplicationContext()).getClient();
    }
}