package macrodes.lab.com.quack.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import macrodes.lab.com.quack.R;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields

public class NotificationActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().hide();

    }

    public void backButtonClicked(View view) {
        finish();
    }
}
