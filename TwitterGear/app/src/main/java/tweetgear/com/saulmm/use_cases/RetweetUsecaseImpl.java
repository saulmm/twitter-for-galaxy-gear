package tweetgear.com.saulmm.use_cases;

import android.util.Log;

import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.executor.PostExecutionThread;
import tweetgear.com.saulmm.executor.ThreadExecutor;
import tweetgear.com.saulmm.executor.UIThread;
import twitter4j.Twitter;
import twitter4j.TwitterException;


public class RetweetUsecaseImpl implements RetweetUsecase {

    private final ThreadExecutor threadExecutor;
    private final PostExecutionThread postExecutionThread;
    private final Callback callback;
    private final Twitter twitterClient;
    private final String tweetID;


    public RetweetUsecaseImpl(Twitter twitterClient, String tweetID, Callback callback) {
        
        if (callback == null)
            throw new IllegalArgumentException("Callback cannot be null");

        else if (twitterClient == null)
            throw new IllegalArgumentException("Twitter client cannot be null");

        else if (tweetID == null)
            throw  new IllegalArgumentException("Tweet ID cannot be null");

        this.tweetID = tweetID;

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

        Log.d("[DEBUG]", "RetweetUsecaseImpl run - ");

        try {

            twitterClient.retweetStatus(Long.parseLong(tweetID));
            callback.onRetweetSuccess();

        } catch (TwitterException e) {

            callback.onError(e.getMessage());
        }
    }
}
