package tweetgear.com.saulmm.views.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.android.sdk.richnotification.SrnRichNotificationManager;
import com.squareup.picasso.Picasso;

import java.util.Collection;
import java.util.UUID;

import tweetgear.com.saulmm.examples.SmallHeader;
import tweetgear.com.saulmm.presenter.UserPresenter;
import tweetgear.com.saulmm.presenter.UserPresenterImpl;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCase;
import tweetgear.com.saulmm.use_cases.GetTweetsUseCaseImpl;
import tweetgear.com.saulmm.executor.JobExecutor;
import tweetgear.com.saulmm.executor.PostExecutionThread;
import tweetgear.com.saulmm.executor.ThreadExecutor;
import tweetgear.com.saulmm.executor.UIThread;
import tweetgear.com.saulmm.helpers.TwitterHelper;
import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.twittergear.R;
import tweetgear.com.saulmm.utils.Constants;
import tweetgear.com.saulmm.views.view.UserView;


public class UserFragment extends Fragment implements UserView {

//    private SrnRichNotificationManager mRichNotificationManager;

    private ImageView profileImg;
    private ImageView userBackground;
    private TextView nameTv;
    private TextView usernameTv;

    private UserPresenter userPresenter;
    private Button sendTweetsButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = initUI(inflater);
        userPresenter = new UserPresenterImpl(this);
        return rootView;

        //        mRichNotificationManager = new SrnRichNotificationManager(getActivity().getApplicationContext());
    }

    @Override
    public void onResume() {

        super.onResume();
        userPresenter.onResume();

//        mRichNotificationManager.start();
//        mRichNotificationManager.registerRichNotificationListener(this);
    }

    @Override
    public void onPause() {

        super.onPause();
        userPresenter.onPause();

//        mRichNotificationManager.unregisterRichNotificationListener(this);
//        mRichNotificationManager.stop();
    }

    private View initUI(LayoutInflater inflater) {

        View rootView = inflater.inflate(R.layout.fragment_user, null);

        nameTv              = (TextView) rootView.findViewById (R.id.tw_name);
        usernameTv          = (TextView) rootView.findViewById (R.id.tw_username);
        sendTweetsButton    = (Button) rootView.findViewById (R.id.tw_send_tweets);
        profileImg          = (ImageView) rootView.findViewById (R.id.tw_profile_img);
        userBackground      = (ImageView) rootView.findViewById (R.id.tw_user_background);

        sendTweetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPresenter.sendTweetsButtonClicked();
            }
        });

        return rootView;
    }


    @Override
    public void setNameAndUserName(String name, String username) {

        nameTv.setText (name);
        usernameTv.setText (username);
    }

    @Override
    public void loadBackgroundImage(String url) {

        Picasso.with(getActivity())
            .load(url)
            .placeholder(R.drawable.background)
            .into(userBackground);
    }

    @Override
    public void loadUserImage(String url) {

        Picasso.with(getActivity())
            .load(url)
            .placeholder(R.drawable.placeholder_user)
            .error(R.drawable.background)
            .into(profileImg);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }
}
