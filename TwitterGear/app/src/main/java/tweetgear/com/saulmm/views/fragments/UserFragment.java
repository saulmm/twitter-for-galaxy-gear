package tweetgear.com.saulmm.views.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import tweetgear.com.saulmm.helpers.TwitterHelper;
import tweetgear.com.saulmm.presenter.UserPresenter;
import tweetgear.com.saulmm.presenter.UserPresenterImpl;
import tweetgear.com.saulmm.twittergear.R;
import tweetgear.com.saulmm.views.view.UserView;
import tweetgear.com.saulmm.wearables.CommService;
import twitter4j.Twitter;


public class UserFragment extends Fragment implements UserView {

    private ImageView profileImg;
    private ImageView userBackground;
    private TextView nameTv;
    private TextView usernameTv;

    private UserPresenter userPresenter;
    private Button sendTweetsButton;
    private boolean isBound;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = initUI(inflater);
        userPresenter = new UserPresenterImpl(this);

        doBindService();

        return rootView;
    }


    private void doBindService () {


        getActivity().bindService(new Intent(getActivity(), CommService.class),
                mConnection, Context.BIND_AUTO_CREATE);

        isBound = true;
    }

    private void doUnbindService () {

        if (isBound) {
            getActivity().unbindService(mConnection);

            isBound = false;
        }
    }


    private CommService gearService;
    // Binder to maintain a conversation with the wear & twitter service
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            gearService = ((CommService.LocalBinder) service).getService();
            Log.d("[DEBUG]", "UserFragment onServiceConnected - Service connected ");

            gearService.setTwitterClient(TwitterHelper.getInstance(getActivity())
                .getTwClient());



        }


        @Override
        public void onServiceDisconnected(ComponentName name) {

            gearService = null;
            Log.i ("[INFO] UserFragment - onServiceDisconnected", "Service disconnected");
        }
    };

    @Override
    public void onResume() {

        super.onResume();
        userPresenter.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
        userPresenter.onPause();
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
