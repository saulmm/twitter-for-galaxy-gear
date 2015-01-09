package tweetgear.com.saulmm.model;

import java.util.Collection;

/**
 * Created by saulmm on 20/12/14.
 */
public class Tweet {

    public static final String SEPARATOR_FIELD = "-.__";
    public static final String SEPARATOR_TWEET = "|_|";

    private String id;
    private String text;
    private String username;
    private String time;

    private int retweets;
    private int favorites;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRetweets(int retweets) {
        this.retweets = retweets;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public String getUsername() {
        return username;
    }

    public int getRetweets() {
        return retweets;
    }

    public String getText() {
        return text;
    }

    public int getFavorites() {
        return favorites;
    }

    public static String getCompressedTweets (Collection<Tweet> homeline) {

        String compressedTweet = "";

        for (Tweet tweet : homeline) {

            compressedTweet += tweet.getUsername();
            compressedTweet += Tweet.SEPARATOR_FIELD;

            compressedTweet += tweet.getTime();
            compressedTweet += Tweet.SEPARATOR_FIELD;
            compressedTweet += "@"+tweet.getUsername();

            compressedTweet += Tweet.SEPARATOR_FIELD;
            compressedTweet += tweet.getText();

            compressedTweet += Tweet.SEPARATOR_FIELD;
            compressedTweet += tweet.getId();

            compressedTweet += Tweet.SEPARATOR_TWEET;
        }

        return compressedTweet;
    }

}
