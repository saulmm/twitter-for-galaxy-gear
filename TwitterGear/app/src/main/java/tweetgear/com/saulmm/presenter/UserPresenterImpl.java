package tweetgear.com.saulmm.presenter;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import java.util.Collection;

import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.executor.PostExecutionThread;
import tweetgear.com.saulmm.executor.ThreadExecutor;
import tweetgear.com.saulmm.executor.UIThread;
import tweetgear.com.saulmm.helpers.TwitterHelper;
import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.use_cases.GetTweetsUsecase;
import tweetgear.com.saulmm.use_cases.GetTweetsUsecaseImpl;
import tweetgear.com.saulmm.wearables.CommService;
import tweetgear.com.saulmm.wearables.GearSender;
import tweetgear.com.saulmm.wearables.NotificationSender;
import tweetgear.com.saulmm.utils.Constants;
import tweetgear.com.saulmm.views.view.UserView;
import twitter4j.Twitter;

public class UserPresenterImpl implements UserPresenter {

    private UserView userView;
    private CommService gearService;

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private final NotificationSender wearableSender;

    public UserPresenterImpl(UserView userView) {

        if (userView == null)
            throw new IllegalArgumentException("UserView cannot be null");

        this.userView = userView;
        this.threadExecutor = JobExecutor.getInstance();
        this.postExecutionThread = UIThread.getInstance();

        // Abstraction that can be replaced by a AndroidWearSender in example
        this.wearableSender = new GearSender(userView.getContext().getApplicationContext());

        bindGearService ();
        loadUserData();

    }

    private void bindGearService() {

        userView.getContext().bindService(new Intent(userView.getContext(), CommService.class),
                mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {

        wearableSender.onResume();
    }

    @Override
    public void onPause() {

        wearableSender.onPause();
    }

    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            gearService = ((CommService.LocalBinder) service).getService();

            if (gearService == null)
                throw new IllegalStateException("Gear service is not initialized");

            gearService.setTwitterClient(TwitterHelper.getInstance(userView.getContext())
                .getTwClient());
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {

            gearService = null;
            Log.i ("[INFO] UserFragment - onServiceDisconnected", "Service disconnected");
        }
    };


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
    public void requestTweets() {

        Twitter twitterClient = TwitterHelper.getInstance(userView.getContext())
            .getTwClient();

        GetTweetsUsecase getTweetsUsecase = new GetTweetsUsecaseImpl(
           twitterClient, receiveTweetsCallback);

        threadExecutor.execute(getTweetsUsecase);
    }

    @Override
    public void gearServiceConnected(CommService gearService) {

        gearService.setTwitterClient(TwitterHelper.getInstance(userView.getContext())
            .getTwClient());
    }


    private GetTweetsUsecase.Callback receiveTweetsCallback = new GetTweetsUsecase.Callback() {

        @Override
        public void onTweetsListLoaded(Collection<Tweet> tweetsCollection) {

            for (Tweet tweet : tweetsCollection) {

                Log.d("[DEBUG]", "UserPresenterImpl onTweetsListLoaded - Tweet: " + tweet.toString());
                wearableSender.sendTweetNotification(tweet);
            }
        }

        @Override
        public void onError(String error) {

            Log.e("[ERROR]", "UserPresenterImpl onError - Error: "+error);
        }
    };


    @Override
    public void sendTweetsButtonClicked() {

        requestTweets();
    }
}
