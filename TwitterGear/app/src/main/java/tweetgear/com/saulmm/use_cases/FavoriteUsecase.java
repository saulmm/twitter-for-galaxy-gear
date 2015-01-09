package tweetgear.com.saulmm.use_cases;

import tweetgear.com.saulmm.executor.Interactor;

/**
 * Created by saulmm on 09/01/15.
 */
public interface FavoriteUsecase extends Interactor {

    interface Callback {
        void onFavoriteSuccess ();
        void onFavoriteError ();
    }

    void execute(Callback callback);
}
