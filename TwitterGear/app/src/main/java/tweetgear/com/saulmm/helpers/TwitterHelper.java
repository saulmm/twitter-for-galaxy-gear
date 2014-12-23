package tweetgear.com.saulmm.helpers;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;



import tweetgear.com.saulmm.utils.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;


public class TwitterHelper {

    private static  TwitterHelper INSTANCE;

    public static TwitterHelper getInstance (Context c) {

        if (INSTANCE == null)
            INSTANCE = new TwitterHelper(c);

        return  INSTANCE;
    }



    private boolean userLogged;
    private SharedPreferences preferences;

    // Twitter listeners
    private TwitterOperationListener twitterListener;
    private TwitterLoginListener loginListener;

    // Twitter stuff
    private Twitter twClient;
    private String oauthVerifier;
    private RequestToken requestToken;

    private TwitterHelper(Context context) {

        preferences = context.getSharedPreferences(
                Constants.PREFS, Context.MODE_PRIVATE);

        String accessToken = preferences.getString("ACCESS_TOKEN", "");
        String accessTokenSecret = preferences.getString("ACCESS_TOKEN_SECRET", "");

        userLogged = (!TextUtils.isEmpty(Constants.CONSUMER_KEY) && !TextUtils.isEmpty(Constants.CONSUMER_SECRET) &&
                !TextUtils.isEmpty(accessToken) && !TextUtils.isEmpty(accessTokenSecret));

        twClient = new TwitterFactory().getInstance();
        twClient.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);

        if (userLogged) {

            twClient.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        }
    }

    public Twitter getTwClient() {
        return twClient;
    }

    public void initTwitter() {

        twClient = new TwitterFactory().getInstance();

        twClient.setOAuthConsumer(
                Constants.CONSUMER_KEY,
                Constants.CONSUMER_SECRET);
    }


    public void requestAuthorizationUrl() {

        new GetAuthorizationUrlTask().execute();
    }


    public void requestTwitterTimeLine(TwitterOperationListener twitterListener) {
    }


    public void requestAccessToken() {

        new GetAccessTokenTask(requestToken, oauthVerifier)
                .execute();
    }


    public void retweet(String tweetID) {

        new TwitterOperationTask(tweetID).execute(true);
    }


    public void markTweetAsFavorite(String tweetID) {

        new TwitterOperationTask(tweetID).execute(false);
    }


    public void setOauthVerifier(String oauthVerifier) {

        this.oauthVerifier = oauthVerifier;
    }


    public void setRequestToken(RequestToken requestToken) {

        this.requestToken = requestToken;
    }


    public void setLoginListener(TwitterLoginListener loginListener) {
        this.loginListener = loginListener;
    }


    public void setTwitterListener(TwitterOperationListener twitterListener) {
        this.twitterListener = twitterListener;
    }


    public boolean isUserLogged() {
        return userLogged;
    }




    private class TwitterOperationTask extends AsyncTask<Boolean, Void, Boolean> {
        private final String tweetID;
        private boolean isARetweet;

        public TwitterOperationTask(String tweetID) {
            this.tweetID = tweetID;
        }

        @Override
        protected Boolean doInBackground(Boolean... isRetweet) {

            try {
                long id = Long.parseLong(tweetID);
                isARetweet = isRetweet[0];

                if (isARetweet)
                    twClient.retweetStatus(id);

                else
                    twClient.createFavorite(id);

            } catch (Exception e) {

                twitterListener.onTwitterFail(e.getMessage());
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean operationSuccessfully) {

            super.onPostExecute(operationSuccessfully);
            twitterListener.onTwitterOperationSuccess(operationSuccessfully);
        }
    }


    private class GetAccessTokenTask extends AsyncTask<Void, Void, String> {

        private final RequestToken rToken;
        private final String verifier;
        private String errorMessage;

        public GetAccessTokenTask(RequestToken rToken, String verifier) {

            this.verifier = verifier;
            this.rToken = rToken;
            this.errorMessage = "";
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                AccessToken aToken = twClient.getOAuthAccessToken(rToken, verifier);
                User twUser = twClient.showUser(aToken.getUserId());

                fillPrefWithTwitterData(twUser, aToken.getToken(),
                        aToken.getTokenSecret());

            } catch (TwitterException e) {

                errorMessage = e.getMessage();
            }

            return errorMessage;
        }


        @Override
        protected void onPostExecute(String errorMessage) {

            super.onPostExecute(errorMessage);

            if (errorMessage.equals("")) {
                loginListener.onAccessTokenReceived();

            } else {
                loginListener.onAccessTokenReceived();
            }
        }

    }


    private class GetAuthorizationUrlTask extends AsyncTask<Void, Void, String> {

        private String authorizationURL = "";
        private String errorMessage = "";
        private RequestToken rToken;


        @Override
        protected String doInBackground(Void... params) {

            try {
                rToken = twClient.getOAuthRequestToken();
                authorizationURL = rToken.getAuthorizationURL();

            } catch (Exception e) {

                errorMessage = e.getMessage();
            }

            return authorizationURL;
        }


        @Override
        protected void onPostExecute(String url) {

            super.onPostExecute(url);

            if (!url.equals ("")) {

                loginListener.onRequestTokenReceived (rToken);
                loginListener.onAuthorizationURLReceived (url);

            } else {

                loginListener.onAuthorizationURLFailed(errorMessage);
            }
        }
    }


    private void fillPrefWithTwitterData(User user, String aToken, String aTokenSecret) {

        SharedPreferences.Editor prefEditor = preferences.edit();

        prefEditor.putString("NAME", user.getName());
        prefEditor.putString("USER_NAME", user.getScreenName());
        prefEditor.putString("IMAGE_URL", user.getOriginalProfileImageURL());

        prefEditor.putString("CONSUMER_KEY", Constants.CONSUMER_KEY);
        prefEditor.putString("CONSUMER_SECRET", Constants.CONSUMER_SECRET);
        prefEditor.putString("ACCESS_TOKEN", aToken);
        prefEditor.putString("ACCESS_TOKEN_SECRET", aTokenSecret);

        prefEditor.putString("BACKGROUND_IMG", user.getProfileBackgroundImageURL());

        prefEditor.apply();
    }
}
