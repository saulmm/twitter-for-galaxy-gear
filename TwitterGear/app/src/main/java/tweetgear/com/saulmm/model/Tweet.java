package tweetgear.com.saulmm.model;

/**
 * Created by saulmm on 20/12/14.
 */
public class Tweet {

    private String text;
    private String username;
    private String time;

    private int retweets;
    private int favorites;

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

}
