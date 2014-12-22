package tweetgear.com.saulmm.notifications;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.format.Time;

import com.samsung.android.sdk.richnotification.SrnImageAsset;
import com.samsung.android.sdk.richnotification.SrnRichNotification;
import com.samsung.android.sdk.richnotification.templates.SrnSecondaryTemplate;
import com.samsung.android.sdk.richnotification.templates.SrnStandardSecondaryTemplate;
import com.samsung.android.sdk.richnotification.templates.SrnStandardTemplate;

import tweetgear.com.saulmm.model.Tweet;
import tweetgear.com.saulmm.twittergear.R;

/**
 * Created by saulmm on 21/12/14.
 */
public class GearNotificationsHelper {

    public static SrnRichNotification createTweetNotification (Context context, Tweet tweet) {

        SrnStandardTemplate smallHeaderTemplate = new SrnStandardTemplate(SrnStandardTemplate.HeaderSizeType.SMALL);
        smallHeaderTemplate.setBody("<b>@"+tweet.getUsername()+"<b>");

        SrnStandardSecondaryTemplate smallSecondaryTemplate = new SrnStandardSecondaryTemplate();
        smallSecondaryTemplate.setBody(tweet.getText());

        SrnImageAsset retweetIcon = getSrnImageAsset(context, "favoriteIcon", R.drawable.star);
        SrnImageAsset starIcon = getSrnImageAsset(context, "retweetIcon", R.drawable.ic_retweet);
        SrnImageAsset twitterIcon = getSrnImageAsset(context, "twitter_icon", R.drawable.ic_twitter);

        smallSecondaryTemplate.setSmallIcon1(retweetIcon, "  " + tweet.getFavorites());
        smallSecondaryTemplate.setSmallIcon2(starIcon, "  " + tweet.getRetweets());

        SrnRichNotification notification = new SrnRichNotification(context);
        notification.setAlertType(SrnRichNotification.AlertType.SILENCE);
        notification.setPrimaryTemplate(smallHeaderTemplate);
        notification.setSecondaryTemplate(smallSecondaryTemplate);
        notification.setIcon(twitterIcon);
        notification.setTitle("Twitter");
        return notification;
    }


    private static SrnImageAsset getSrnImageAsset (Context context, String name, int drawableRes) {

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        return new SrnImageAsset(context, name, bm);
    }
}
