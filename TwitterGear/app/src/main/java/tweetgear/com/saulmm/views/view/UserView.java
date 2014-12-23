package tweetgear.com.saulmm.views.view;

/**
 * Created by saulmm on 21/12/14.
 */
public interface UserView extends View {

    void loadUserImage (String url);

    void loadBackgroundImage (String url);

    void setNameAndUserName (String name, String username);
}
