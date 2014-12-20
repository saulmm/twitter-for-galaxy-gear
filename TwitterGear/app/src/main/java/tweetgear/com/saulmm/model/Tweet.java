package tweetgear.com.saulmm.model;

/**
 * Created by saulmm on 20/12/14.
 */
public class Tweet {

    private String username;
    private String text;
    private String setTweetTime;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSetTweetTime(String setTweetTime) {
        this.setTweetTime = setTweetTime;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "username='" + username + '\'' +
                ", text='" + text + '\'' +
                ", setTweetTime='" + setTweetTime + '\'' +
                '}';
    }
}
