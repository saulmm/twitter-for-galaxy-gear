package tweetgear.com.saulmm.views.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import tweetgear.com.saulmm.helpers.TwitterHelper;
import tweetgear.com.saulmm.helpers.TwitterLoginListener;
import tweetgear.com.saulmm.twittergear.R;
import twitter4j.auth.RequestToken;

import static android.content.DialogInterface.OnDismissListener;
import static android.view.View.OnClickListener;

public class LoginFragment extends Fragment implements TwitterLoginListener {

    // UI Stuff
    private Dialog authDialog;
    private Dialog waitDialog;

    private TextView errorMessageTv;
    private Button twitterLoginFragmentButton;

    private Context ctx;

    // Other stuff
    private boolean authOk;

    private TwitterHelper twHelper;


    @Override
    public void onSaveInstanceState(Bundle outState) { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = initUI(inflater);

        twHelper = TwitterHelper.getInstance(getActivity().getApplicationContext());
        twHelper.setLoginListener(this);
        twHelper.initTwitter();

        return rootView;
    }

    private View initUI(LayoutInflater inflater) {

        waitDialog = new Dialog(getActivity());
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(R.layout.dialog_wait);
        waitDialog.setCancelable(false);

        ImageView loadingSegment = (ImageView) waitDialog.findViewById(R.id.d_loading_img);
        loadingSegment.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.loading_animation));

        View rootView = inflater.inflate(R.layout.fragment_login, null); // TODO Check this

        twitterLoginFragmentButton  = (Button) rootView.findViewById(R.id.tw_login_fragment_button);
        errorMessageTv              = (TextView) rootView.findViewById(R.id.tw_login_error_msg);

        twitterLoginFragmentButton.setOnClickListener(onClickTwitterListener);

        return rootView;
    }


    private final OnClickListener onClickTwitterListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

        errorMessageTv.setText("");
        waitDialog.show();

        twHelper.setLoginListener(LoginFragment.this);
        twHelper.requestAuthorizationUrl();
        }
    };


    private final WebViewClient oauthWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            Log.d("[DEBUG] GetTokenTask - onPageStarted", "Loading URL: " + url);

            if (url.startsWith("http://saulmm.com/")) {
                authDialog.dismiss();
                authOk = true;
                waitDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);

            if (url.contains("oauth_verifier")) {

                Uri uri = Uri.parse(url);
                String oauthVerifier = uri.getQueryParameter("oauth_verifier");

                twHelper.setOauthVerifier(oauthVerifier);
                twHelper.requestAccessToken();


            } else if (url.contains("denied")) {
                Log.e("[ERROR] GetTokenTask - onPageFinished", "[ERROR] Denied");
            }
        }
    };


    private final OnDismissListener onDismissDialogListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {

            // The user dismissed the dialog
            if (!authOk) {

                // Reinitialize the twitter client
                twHelper.initTwitter();
                waitDialog.dismiss();
                String errorMsg = "You must be logged in to use twitter in android wear"; // TODO Hardcoded string
                showButtonError(errorMsg);
            }
        }
    };


    @Override
    public void onAuthorizationURLReceived(String url) {

        if (url != null) {
            authDialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
            authDialog.setOnDismissListener(onDismissDialogListener);
            authDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            authDialog.setContentView(R.layout.dialog_twitter_authorization);

            WebView webview = (WebView) authDialog.findViewById(R.id.webv);

            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("file://android_asset/index.html");
            webview.loadUrl(url);
            webview.setWebViewClient(oauthWebClient);

            authDialog.show();
            authDialog.setCancelable(true);

        } else {
            Log.e("[ERROR] LoginFragment - onAuthorizationURLReceived", "Network Error or Invalid Credentials");
//            twitterLoginFragmentButton.setProgress(-1);

        }
    }

    @Override
    public void onAuthorizationURLFailed(String cause) {
        if (cause.equals ("401")) {
            String errorMsg = "Invalid twitter api keys"; // TODO harcoded string
            showButtonError (errorMsg);
        }
    }

    private void showButtonError(String errorMsg) {

        errorMessageTv.setText (errorMsg);
//        twitterLoginFragmentButton.setProgress(-1);
        twitterLoginFragmentButton.setOnClickListener(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//            twitterLoginFragmentButton.setProgress(0);
            twitterLoginFragmentButton.setOnClickListener(onClickTwitterListener);

            }
        }, 2000);
    }

    @Override
    public void onAccessTokenReceived() {

        authOk = true;
        twitterLoginFragmentButton.setEnabled(false);
        waitDialog.dismiss();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new UserFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        ft.remove(LoginFragment.this);
        ft.commit();
    }

    @Override
    public void onRequestTokenReceived(RequestToken rToken) {

        // I know that this is not the best way to save the request token
        twHelper.setRequestToken (rToken);
    }
}