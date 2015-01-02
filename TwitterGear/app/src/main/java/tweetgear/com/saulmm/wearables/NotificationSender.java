package tweetgear.com.saulmm.wearables;

import tweetgear.com.saulmm.model.Tweet;


public interface NotificationSender {

    public void sendTweetNotification (Tweet tweet);

    public void onResume ();

    public void onPause ();

}
