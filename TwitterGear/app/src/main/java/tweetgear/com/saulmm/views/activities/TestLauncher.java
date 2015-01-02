package tweetgear.com.saulmm.views.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import tweetgear.com.saulmm.wearables.CommService;

/**
 * Created by saulmm on 24/12/14.
 */
public class TestLauncher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button b = new Button(this);
        b.setText("Testing");
        b.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(TestLauncher.this, CommService.class));
            }
        }) ;

        setContentView(b);
    }
}
