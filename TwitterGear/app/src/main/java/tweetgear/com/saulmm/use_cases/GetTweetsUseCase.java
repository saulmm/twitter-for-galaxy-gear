package tweetgear.com.saulmm.use_cases;

import java.util.Collection;

import tweetgear.com.saulmm.executor.Interactor;
import tweetgear.com.saulmm.model.Tweet;

/**
 * Created by saulmm on 20/12/14.
 */
public interface GetTweetsUseCase extends Interactor {

    interface Callback {
        void onTweetsListLoaded (Collection<Tweet> tweetsCollection);
        void onError (String error);
    }

    void execute (Callback callback);
}
