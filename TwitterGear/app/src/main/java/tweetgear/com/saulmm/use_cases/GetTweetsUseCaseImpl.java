package tweetgear.com.saulmm.use_cases;

import android.util.Log;

import java.util.ArrayList;

import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.executor.PostExecutionThread;
import tweetgear.com.saulmm.executor.ThreadExecutor;
import tweetgear.com.saulmm.executor.UIThread;
import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.utils.Utils;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class GetTweetsUseCaseImpl implements GetTweetsUseCase {

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private final Callback callback;
    private final Twitter twitterClient;


    public GetTweetsUseCaseImpl(Twitter twitterClient, GetTweetsUseCase.Callback callback) {
        
        if (callback == null)
            throw new IllegalArgumentException("Callback cannot be null");

        else if (twitterClient == null)
            throw new IllegalArgumentException("Twitter client cannot be null");

        threadExecutor = JobExecutor.getInstance();
        postExecutionThread = UIThread.getInstance();

        this.twitterClient = twitterClient;
        this.callback = callback;
    }


    @Override
    public void execute(Callback callback) {

        if (callback == null)
            throw new IllegalArgumentException("Callback cannot be null");

        this.threadExecutor.execute(this);
    }

    @Override
    public void run() {

        try {

            ResponseList<Status> twitterTimeline = twitterClient.getHomeTimeline();
            ArrayList<Tweet> tweets = new ArrayList<Tweet>(twitterTimeline.size());

            for (Status status : twitterTimeline) {

                String time = Utils.getTimeDiference(status.getCreatedAt()).replace("-","");

                Tweet tweet = new Tweet();
                tweet.setTime(time);
                tweet.setName(status.getUser().getName());
                tweet.setUsername(status.getUser().getScreenName());
                tweet.setText(status.getText());
                tweet.setFavorites(status.getFavoriteCount());
                tweet.setRetweets(status.getRetweetCount());
                tweet.setId(String.valueOf(status.getId()));
                tweets.add(tweet);

                Log.d("[DEBUG]", "GetTweetsUseCaseImpl run - Tweet: " + tweet.toString());
            }

            callback.onTweetsListLoaded(tweets);

        } catch (TwitterException e) {

            e.printStackTrace();
            callback.onError(e.getMessage());
        }
    }
}
