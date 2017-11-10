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
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.android.callback.KinveyUserListCallback;
import com.kinvey.java.User;
import com.kinvey.java.model.UserLookup;

public class SignupActivity extends ActionBarActivity {

	// See LoginActivity.java for an explanation of usernames, emails, and bidder numbers
	// The discovery user is needed here because Kinvey does not enforce uniqueness in the email field of the user collection
	// So we need to check if a user with the provided email already exists before creating the new user.
	private String discoveryUser = "";
	private String discoveryPw = "";

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.signup);
        findViewById(R.id.base_tint_darken).setVisibility(View.GONE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.gobutton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                doSignup(((TextView) findViewById(R.id.first_name)).getText().toString(),
		                ((TextView) findViewById(R.id.last_name)).getText().toString(),
		                ((TextView) findViewById(R.id.email)).getText().toString(),
                        ((TextView) findViewById(R.id.password)).getText().toString());
            }
        });

		findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

    void doSignup(final String first_name, final String last_name, final String email, final String password)
    {
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
						    CharSequence text = "Account already exists";
						    Log.d(Client.TAG, text.toString());
						    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
						    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
						    toast.show();
					    }
					    else
					    {
						    getClient().user().create(email, password, new KinveyUserCallback() {
							    @Override
							    public void onSuccess(User result) {
								    CharSequence text = "Created user " + result.getUsername() + ".";
								    Log.d(Client.TAG, text.toString());

								    getClient().user().put("email", email);
								    getClient().user().put("first_name", first_name);
								    getClient().user().put("last_name", last_name);
								    getClient().user().put("bidderNumber", "");
								    getClient().user().update(new KinveyUserCallback() {
									    @Override
									    public void onFailure(Throwable e) {
										    getClient().user().logout().execute();
										    Log.d(Client.TAG, e.getLocalizedMessage());
										    finish();
									    }
									    @Override
									    public void onSuccess(User u) {
//										    getClient().user().logout().execute();
										    finish();
									    }
								    });
							    }

							    @Override
							    public void onFailure(Throwable t) {
								    CharSequence text = "Error creating account. Please try again.";
								    Log.d(Client.TAG, text.toString());
								    Log.d(Client.TAG, t.getLocalizedMessage());
								    Log.d(Client.TAG, t.toString());
								    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
								    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
								    toast.show();
							    }
						    });
					    }
				    }
				    @Override
				    public void onFailure(Throwable error) {
					    getClient().user().logout().execute();
					    CharSequence text = "Error creating account. Please try again.";
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
			    CharSequence text = "Error creating account. Please try again.";
			    Log.d(Client.TAG, text.toString());
			    Log.d(Client.TAG, t.getLocalizedMessage());
			    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
			    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			    toast.show();
		    }
	    });
    }
    /**
     *
     * @return an instance of a Kinvey Client
     */
    public Client getClient(){
        return ((AuctionApplication) getApplicationContext()).getClient();
    }
}