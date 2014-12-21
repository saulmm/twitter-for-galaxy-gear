package tweetgear.com.saulmm.com;


import java.util.Collection;

import tweetgear.com.saulmm.model.Tweet;

public interface TwitterSender {

    public void sendTweets (Collection<Tweet> tweets);
    
    public void sendRetweet ();

    public void sendFavorite ();

    public void sendReply ();
}
