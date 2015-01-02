package tweetgear.com.saulmm.presenter;


import tweetgear.com.saulmm.wearables.CommService;

public interface UserPresenter extends Presenter {

    void loadUserData ();

    void requestTweets();

    void gearServiceConnected (CommService gearService);

    void sendTweetsButtonClicked();
}
