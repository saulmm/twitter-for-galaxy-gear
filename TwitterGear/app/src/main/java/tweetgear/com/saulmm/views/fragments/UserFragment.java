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

import com.squareup.picasso.Picasso;

import java.util.Collection;

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


public class UserFragment extends Fragment {

    private SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        preferences = getActivity().getSharedPreferences(
            Constants.PREFS,
            Context.MODE_PRIVATE);

        View rootView = initUI(inflater);
        return rootView;
    }

    private View initUI(LayoutInflater inflater) {
        View rootView = inflater.inflate(R.layout.fragment_user, null);

        TextView nameTv             = (TextView) rootView.findViewById (R.id.tw_name);
        TextView usernameTv         = (TextView) rootView.findViewById (R.id.tw_username);
        Button sendTweetsButton         = (Button) rootView.findViewById (R.id.tw_send_tweets);
        ImageView profileImg        = (ImageView) rootView.findViewById (R.id.tw_profile_img);
        ImageView userBackground    = (ImageView) rootView.findViewById (R.id.tw_user_background);

        Picasso.with(getActivity())
            .load(preferences.getString("IMAGE_URL", ""))
            .placeholder(R.drawable.placeholder_user)
            .into(profileImg);

        String backgroundURL = preferences.getString("BACKGROUND_IMG", "");

        if (!backgroundURL.equals("")) {

            Picasso.with(getActivity())
                .load(backgroundURL)
                .placeholder(R.drawable.background)
                .error(R.drawable.background)
                .into(userBackground);
        }

        nameTv.setText (preferences.getString("NAME", ""));
        usernameTv.setText ("@"+preferences.getString("USER_NAME", ""));
        sendTweetsButton.setOnClickListener(onClickListener);

        return rootView;
    }


    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            ThreadExecutor threadExecutor = JobExecutor.getInstance();
            PostExecutionThread postExecutionThread = UIThread.getInstance();

            GetTweetsUseCase getTweetsUseCase = new GetTweetsUseCaseImpl(callback, TwitterHelper.getInstance(getActivity().getApplicationContext()).getTwClient());
            threadExecutor.execute(getTweetsUseCase);
        }
    };

    GetTweetsUseCase.Callback callback = new GetTweetsUseCase.Callback() {

        @Override
        public void onTweetsListLoaded(Collection<Tweet> tweetsCollection) {

            for (Tweet tweet : tweetsCollection) {
                Log.d("[DEBUG]", "UserFragment onTweetsListLoaded - Tweet: "+tweet.toString());
            }
        }

        @Override
        public void onError(String error) {
            Log.d("[DEBUG]", "UserFragment onError - ERror: "+error);
        }
    };
}
