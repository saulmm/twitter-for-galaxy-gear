package tweetgear.com.saulmm.examples;

import android.content.Context;
import android.graphics.Color;

import com.samsung.android.sdk.richnotification.SrnRichNotification;
import com.samsung.android.sdk.richnotification.templates.SrnStandardTemplate;

/**
 * Created by saulmm on 21/12/14.
 */
public class SmallHeader {

    private final Context context;

    public SmallHeader(Context context) {

        this.context = context;
    }

    public SrnRichNotification createRichNotification () {

        SrnRichNotification notification = new SrnRichNotification(context);
        notification.setTitle("An amazing title");

        SrnStandardTemplate smallHeaderTemplate = new SrnStandardTemplate(
            SrnStandardTemplate.HeaderSizeType.SMALL);

        smallHeaderTemplate.setSubHeader("This is the SubHeader");
        smallHeaderTemplate.setBody("This an amazing body, isn't it ? Yes it is men");
        smallHeaderTemplate.setBackgroundColor(Color.rgb(0, 0, 255));

        notification.setPrimaryTemplate(smallHeaderTemplate);

        notification.setAlertType(SrnRichNotification.AlertType.SILENCE);
        return notification;
    }
}
