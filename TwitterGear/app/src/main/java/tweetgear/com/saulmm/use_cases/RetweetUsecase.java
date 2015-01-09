package tweetgear.com.saulmm.use_cases;

import java.util.Collection;

import tweetgear.com.saulmm.executor.Interactor;
import tweetgear.com.saulmm.model.Tweet;

/**
 * Created by saulmm on 20/12/14.
 */
public interface RetweetUsecase extends Interactor {

    interface Callback {
        void onRetweetSuccess();
        void onError(String error);
    }

    void execute(Callback callback);
}
