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

    private Dialog mAuthWebviewDialog;
    private Dialog mWaitDialog;

    private TextView mErrorMessageTextView;
    private Button mLoginButton;

    private boolean mAuthorizationOk;

    private TwitterHelper mTwitterHelper;


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mLoginButton = (Button) rootView.findViewById(R.id.tw_login_fragment_button);
        mErrorMessageTextView = (TextView) rootView.findViewById(R.id.tw_login_error_msg);

        mLoginButton.setOnClickListener(onClickTwitterListener);

        mTwitterHelper = TwitterHelper.getInstance(getActivity().getApplicationContext());
        mTwitterHelper.setLoginListener(this);
        mTwitterHelper.initTwitter();

        return rootView;
    }

    private final OnClickListener onClickTwitterListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

        mErrorMessageTextView.setText("");

        mWaitDialog = new Dialog(getActivity());
        mWaitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mWaitDialog.setContentView(R.layout.dialog_wait);
        mWaitDialog.setCancelable(false);
        mWaitDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mWaitDialog.dismiss();
            }
        });
        mWaitDialog.show();

        ImageView loadingSegment = (ImageView) mWaitDialog.findViewById(R.id.d_loading_img);
        loadingSegment.startAnimation(AnimationUtils.loadAnimation(
            getActivity(), R.anim.loading_animation));

        mTwitterHelper.setLoginListener(LoginFragment.this);
        mTwitterHelper.requestAuthorizationUrl();
        }
    };


    private final WebViewClient oauthWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            super.onPageStarted(view, url, favicon);

            if (url.startsWith(TW_CALLBACK)) {

                mAuthWebviewDialog.dismiss();
                mAuthorizationOk = true;
                mWaitDialog.show();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            super.onPageFinished(view, url);
            Log.d("[DEBUG]", "LoginFragment onPageFinished - Url: " + url);

            if (url.contains("oauth_verifier")) {

                Uri uri = Uri.parse(url);
                String oauthVerifier = uri.getQueryParameter("oauth_verifier");

                mTwitterHelper.setOauthVerifier(oauthVerifier);
                mTwitterHelper.requestAccessToken();

            } else if (url.contains("denied")) {

                onTwitterError();

                Log.e("[ERROR]", "LoginFragment, onPageFinished (126)- " +
                    "Error denied");
            }
        }
    };


    private final OnDismissListener onDismissDialogListener = new OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {

            if (!mAuthorizationOk) {

                onTwitterError();
            }
        }
    };

    private void onTwitterError() {

        mTwitterHelper.initTwitter();
        mWaitDialog.dismiss();
        String errorMsg = getActivity().getString(
            R.string.fragment_login_error_authorization);

        showError(errorMsg);
    }


    @Override
    public void onAuthorizationURLReceived(String url) {

        if (url != null) {

            mAuthWebviewDialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
            mAuthWebviewDialog.setOnDismissListener(onDismissDialogListener);
            mAuthWebviewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mAuthWebviewDialog.setContentView(R.layout.dialog_twitter_authorization);

            WebView webview = (WebView) mAuthWebviewDialog.findViewById(R.id.webv);
            android.webkit.CookieManager.getInstance().removeAllCookie();

            webview.getSettings().setJavaScriptEnabled(true);
            webview.loadUrl("file://android_asset/index.html");
            webview.loadUrl(url);
            webview.setWebViewClient(oauthWebClient);

            mAuthWebviewDialog.show();
            mAuthWebviewDialog.setCancelable(true);

        } else {

            Log.e("[ERROR]", "LoginFragment, onAuthorizationURLReceived (176)- " +
                "Network error or invalid credentials");
        }
    }

    @Override
    public void onAuthorizationURLFailed(String cause) {

        if (cause.equals("401")) {

            String errorMsg = "Invalid twitter api keys";
            showError(errorMsg);
        }
    }

    private void showError(String errorMsg) {

        mErrorMessageTextView.setText(errorMsg);
        mLoginButton.setOnClickListener(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mLoginButton.setOnClickListener(onClickTwitterListener);

            }
        }, 2000);
    }

    @Override
    public void onAccessTokenReceived() {

        mAuthorizationOk = true;
        mLoginButton.setEnabled(false);
        mWaitDialog.dismiss();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, new UserFragment());
        ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        ft.remove(LoginFragment.this);
        ft.commit();
    }

    @Override
    public void onRequestTokenReceived(RequestToken rToken) {

        mTwitterHelper.setRequestToken(rToken);
    }
}