package tweetgear.com.saulmm.views.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import info.hoang8f.widget.FButton;
import tweetgear.com.saulmm.presenter.UserPresenter;
import tweetgear.com.saulmm.presenter.UserPresenterImpl;
import tweetgear.com.saulmm.twittergear.R;
import tweetgear.com.saulmm.utils.Constants;
import tweetgear.com.saulmm.views.activities.MainActivity;
import tweetgear.com.saulmm.views.view.UserView;


public class UserFragment extends Fragment implements UserView, View.OnClickListener {

    private ImageView mProfileImg;
    private ImageView mUserBackground;
    private TextView mNametextView;
    private TextView mUsernameTextView;
    private FButton mLogoutButton;

    private UserPresenter userPresenter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = initUI(inflater);
        userPresenter = new UserPresenterImpl(this);

        return rootView;
    }

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

        mNametextView = (TextView) rootView.findViewById (R.id.fragment_user_user_name);
        mUsernameTextView = (TextView) rootView.findViewById (R.id.fragment_user_twitter_username);
        mProfileImg = (ImageView) rootView.findViewById (R.id.fragment_user_avatar);
        mUserBackground = (ImageView) rootView.findViewById (R.id.fragment_user_background);
        mLogoutButton = (FButton) rootView.findViewById (R.id.fragment_user_logout_button);

        mLogoutButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void setNameAndUserName(String name, String username) {

        mNametextView.setText(name);
        mUsernameTextView.setText(username);
    }

    @Override
    public void loadBackgroundImage(String url) {

        Picasso.with(getActivity())
            .load(url)
            .placeholder(R.drawable.background)
            .into(mUserBackground);
    }

    @Override
    public void loadUserImage(String url) {

        Picasso.with(getActivity())
            .load(url)
            .placeholder(R.drawable.placeholder_user)
            .error(R.drawable.background)
            .into(mProfileImg);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void onClick(View v) {

        new AlertDialog.Builder(getActivity())
            .setTitle("Logout")
            .setMessage(getActivity().getString(R.string.fragment_user_logout_message))
            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    getActivity().finish();

                    SharedPreferences preferences = getActivity().getSharedPreferences(
                        Constants.PREFS, Context.MODE_PRIVATE);

                    preferences.edit().clear().apply();

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            })

            .setNegativeButton("Cancel", null)
            .create()
        .show();


    }
}
