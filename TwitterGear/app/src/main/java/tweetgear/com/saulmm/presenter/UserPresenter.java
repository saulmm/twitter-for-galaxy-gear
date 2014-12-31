package tweetgear.com.saulmm.presenter;


public interface UserPresenter extends Presenter {

    void loadUserData ();

    void requestTweets();

    void sendTweetsButtonClicked();
}