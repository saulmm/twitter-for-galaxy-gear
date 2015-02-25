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
import tweetgear.com.saulmm.helpers.TwitterHelper;
import tweetgear.com.saulmm.presenter.UserPresenter;
import tweetgear.com.saulmm.presenter.UserPresenterImpl;
import tweetgear.com.saulmm.twittergear.R;
import tweetgear.com.saulmm.utils.Constants;
import tweetgear.com.saulmm.views.activities.MainActivity;
import tweetgear.com.saulmm.views.view.UserView;


public class UserFragment extends Fragment implements UserView, View.OnClickListener {

    private ImageView profileImg;
    private ImageView userBackground;
    private TextView nameTv;
    private TextView usernameTv;
    private FButton logoutButton;

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

        nameTv              = (TextView) rootView.findViewById (R.id.tw_name);
        usernameTv          = (TextView) rootView.findViewById (R.id.tw_username);
        profileImg          = (ImageView) rootView.findViewById (R.id.tw_profile_img);
        userBackground      = (ImageView) rootView.findViewById (R.id.tw_user_background);
        logoutButton        = (FButton) rootView.findViewById (R.id.fragment_user_logout_button);

        logoutButton.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {

        new AlertDialog.Builder(getActivity())
            .setTitle("Logout")
            .setMessage("Are you sure?\nYour data will be erased and the application will be restarted")
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
