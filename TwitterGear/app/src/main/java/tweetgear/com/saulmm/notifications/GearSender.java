package tweetgear.com.saulmm.notifications;


import android.content.Context;

import com.samsung.android.sdk.richnotification.SrnRichNotificationManager;

import tweetgear.com.saulmm.model.Tweet;

public class GearSender implements NotificationSender {

    private final Context context;
    private final SrnRichNotificationManager mRichNotificationManager;


    // Context must be an ApplicationContext
    public GearSender(Context context) {

        if (context == null)
            throw new IllegalArgumentException("Context cannot be null & must be an application context");

        this.context = context;
        mRichNotificationManager = new SrnRichNotificationManager(context);
    }

    @Override
    public void sendTweetNotification(Tweet tweet) {

        if (tweet == null)
            throw new IllegalArgumentException("Tweet cannot be null");

        SmallHeader header = new SmallHeader(context);
        mRichNotificationManager.notify(header.createRichNotification());

    }

    @Override
    public void onResume() {

        mRichNotificationManager.start();
    }

    @Override
    public void onPause() {

        mRichNotificationManager.stop();
    }
}
