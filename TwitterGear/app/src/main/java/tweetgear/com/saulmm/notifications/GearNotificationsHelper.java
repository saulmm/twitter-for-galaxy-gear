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
        smallHeaderTemplate.setSubHeader("<b> Tweet </b>");

        SrnStandardSecondaryTemplate smallSecondaryTemplate = new SrnStandardSecondaryTemplate();
        smallSecondaryTemplate.setTitle("<b>"+tweet.getUsername()+"<b>");
        smallSecondaryTemplate.setSubHeader("<b>"+tweet+"<b>");
        smallSecondaryTemplate.setBody(tweet.getText());

        Bitmap commentBM = BitmapFactory.decodeResource(context.getResources(),R.drawable.star);
        SrnImageAsset commentIcon = new SrnImageAsset(context, "comment_icon", commentBM);
        smallSecondaryTemplate.setSmallIcon1(commentIcon, ""+tweet.getFavorites());

        SrnRichNotification notification = new SrnRichNotification(context);
        notification.setAlertType(SrnRichNotification.AlertType.SILENCE);
        notification.setPrimaryTemplate(smallHeaderTemplate);
        notification.setSecondaryTemplate(smallSecondaryTemplate);

        return notification;
    }
}
