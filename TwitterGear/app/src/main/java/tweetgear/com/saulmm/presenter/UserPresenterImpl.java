package tweetgear.com.saulmm.presenter;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Collection;

import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.executor.PostExecutionThread;
import tweetgear.com.saulmm.executor.ThreadExecutor;
import tweetgear.com.saulmm.executor.UIThread;
import tweetgear.com.saulmm.helpers.TwitterHelper;
import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCase;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCaseImpl;
import tweetgear.com.saulmm.utils.Constants;
import tweetgear.com.saulmm.views.view.UserView;
import twitter4j.Twitter;

public class UserPresenterImpl implements UserPresenter {

    private UserView userView;

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;


    public UserPresenterImpl(UserView userView) {

        if (userView == null)
            throw new IllegalArgumentException("UserView cannot be null");

        this.userView = userView;
        this.threadExecutor = JobExecutor.getInstance();
        this.postExecutionThread = UIThread.getInstance();

        loadUserData();
    }

    @Override
    public void onResume() {}

    @Override
    public void onPause() {}

    @Override
    public void loadUserData() {

        SharedPreferences preferences = userView.getContext().getSharedPreferences(
            Constants.PREFS,
            Context.MODE_PRIVATE);

        String name             = preferences.getString("NAME", "");
        String userName         = preferences.getString("USER_NAME", "");
        String backgroundURL    = preferences.getString("BACKGROUND_IMG", "");
        String profileImageURL  = preferences.getString("IMAGE_URL", "");

        userName = "@"+userName;

        if (!name.equals("") && !userName.equals(""))
            userView.setNameAndUserName(name, userName);

        if (!backgroundURL.equals(""))
            userView.loadBackgroundImage(backgroundURL);

        if (!profileImageURL.equals(""))
            userView.loadUserImage(profileImageURL);
    }

    @Override
    public void sendTweets() {

        Twitter twitterClient = TwitterHelper.getInstance(userView.getContext())
            .getTwClient();

        GetTweetsUseCase getTweetsUseCase = new GetTweetsUseCaseImpl(
            receiveTweetsCallback, twitterClient);

        threadExecutor.execute(getTweetsUseCase);
    }


    private GetTweetsUseCase.Callback receiveTweetsCallback = new GetTweetsUseCase.Callback() {

        @Override
        public void onTweetsListLoaded(Collection<Tweet> tweetsCollection) {

            for (Tweet tweet : tweetsCollection) {
                Log.d("[DEBUG]", "UserPresenterImpl onTweetsListLoaded - Tweet: " + tweet.toString());
            }
        }

        @Override
        public void onError(String error) {

        }
    };


    @Override
    public void sendTweetsButtonClicked() {

        sendTweets();

    }
}
