# BidHub Android
Android client for an open-source silent auction app forked from HubSpot's BidHub app. For an overview of their auction app project, [check out their blog post about it](http://dev.hubspot.com/blog/building-an-auction-app-in-a-weekend)!

![](http://i.imgur.com/qIud2uSl.png)

## Getting started
The original app used Parse as the backend, but that service was shut down on January 30, 2017 so this project was rewritten to use [Kinvey](https://www.kinvey.com/). If you haven't yet, you're going to want to set up Kinvey by following the instructions in the [BidHub Cloud Code repository](https://github.com/ncauldwell/BidHub-CloudCode/tree/kinvey-backend). Make a note of your app key and app secret (Kinvey > Your App > The App's Environment > click the 3-dots next to your App name at the top of the left-nav).

All set?
`git clone` this repository and import it into Android Studio.

Create *assets/kinvey.properties* and add it to your project with the following contents:
```
# Required settings
app.key=
app.secret=

# Optional settings,
#### required for push
#gcm.enabled=true
#gcm.senderID=
```
Add the application key and secret from your Kinvey app environment. Run the app and you should be all set... almost!
Next steps:
* sign up (in the app)
* assign bidder number (using the [Web Panel](https://github.com/HubSpot/BidHub-WebAdmin/tree/kinvey-backend) or the Kinvey console)
* bid

Try bidding on something. To keep an eye on the action, check out the [Web Panel](https://github.com/HubSpot/BidHub-WebAdmin/tree/kinvey-backend) where you can see all your items and bids.

Push isn't going to work yet, but you should be able to see Test Object 7 and bid on it.

## Customization
Here's a list of the HubSpot-specific assets in the app, which you can change to whatever you want:
* `drawable/notificationicon.png` status bar icon for push notifications
* `drawable/appicon.png` app icon for the app drawer and for push notifications
* `drawable/bg.png` background for the login screen and for the hamburger menu

## Push
Kinvey has documented getting [setting up Push](https://devcenter.kinvey.com/android-v2/guides/push) services for Android, so start there. Follow the first few steps down to 'Receiving push messages', at which point the rest is about adding push to the app, which is already done.
**Important:** If you change the package name from the default `com.hsdemo.auction`, make sure to change the following manifest tags as well, or push notifications won't work and you will be very frustrated:
* `<category android:name="com.hsdemo.auction" />`
* `<permission android:protectionLevel="signature" android:name="com.hsdemo.auction.permission.C2D_MESSAGE" />`
* `<uses-permission android:name="com.hsdemo.auction.permission.C2D_MESSAGE" />`
The [Manifest changes](https://devcenter.kinvey.com/android-v2/guides/push#Manifestchanges) section of the Kinvey Android Push guide might also be useful.
