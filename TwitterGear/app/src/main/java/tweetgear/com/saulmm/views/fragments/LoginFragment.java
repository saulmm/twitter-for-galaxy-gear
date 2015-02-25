package tweetgear.com.saulmm.views.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

    private static final String TW_CALLBACK = "http://saulmm.com/";

    private Dialog authDialog;
    private Dialog waitDialog;

    private TextView errorMessageTv;
    private Button twitterLoginFragmentButton;


    // Other stuff
    private boolean authOk;

    private TwitterHelper twHelper;


    @Override
    public void onSaveInstanceState(Bundle outState) { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        twitterLoginFragmentButton  = (Button) rootView.findViewById(R.id.tw_login_fragment_button);
        errorMessageTv              = (TextView) rootView.findViewById(R.id.tw_login_error_msg);

        twitterLoginFragmentButton.setOnClickListener(onClickTwitterListener);

        twHelper = TwitterHelper.getInstance(getActivity().getApplicationContext());
        twHelper.setLoginListener(this);
        twHelper.initTwitter();

        return rootView;
    }

    private final OnClickListener onClickTwitterListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

        errorMessageTv.setText("");

        waitDialog = new Dialog(getActivity());
        waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        waitDialog.setContentView(R.layout.dialog_wait);
        waitDialog.setCancelable(false);
        waitDialog.show();

        ImageView loadingSegment = (ImageView) waitDialog.findViewById(R.id.d_loading_img);
        loadingSegment.startAnimation(AnimationUtils.loadAnimation(
            getActivity(), R.anim.loading_animation));

        twHelper.setLoginListener(LoginFragment.this);
        twHelper.requestAuthorizationUrl();
        }
    };


    private final WebViewClient oauthWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (url.startsWith(TW_CALLBACK)) {

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

                Log.e("[ERROR]", "LoginFragment, onPageFinished (126)- " +
                    "Error denied");
            }
        }
    };


    private final OnDismissListener onDismissDialogListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {

        if (!authOk) {

            twHelper.initTwitter();
            waitDialog.dismiss();
            String errorMsg = getActivity().getString(
                R.string.fragment_login_error_authorization);

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
            android.webkit.CookieManager.getInstance().removeAllCookie();

            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("file://android_asset/index.html");
            webview.loadUrl(url);
            webview.setWebViewClient(oauthWebClient);

            authDialog.show();
            authDialog.setCancelable(true);

        } else {

            Log.e("[ERROR]", "LoginFragment, onAuthorizationURLReceived (176)- " +
                "Network error or invalid credentials");
        }
    }

    @Override
    public void onAuthorizationURLFailed(String cause) {

        if (cause.equals ("401")) {

            String errorMsg = "Invalid twitter api keys";
            showButtonError (errorMsg);
        }
    }

    private void showButtonError(String errorMsg) {

        errorMessageTv.setText (errorMsg);
        twitterLoginFragmentButton.setOnClickListener(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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

        twHelper.setRequestToken (rToken);
    }
}