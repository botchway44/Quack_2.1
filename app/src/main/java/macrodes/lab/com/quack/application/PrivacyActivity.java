package macrodes.lab.com.quack.application;

import android.os.Bundle;
import android.view.View;

import macrodes.lab.com.quack.LoginActivity;
import macrodes.lab.com.quack.R;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class PrivacyActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        getSupportActionBar().hide();


        final String userId = SimplePreferences.with(this).getSharedString("account","userId");
        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }

    }

    public void backButtonClicked(View view) {
        finish();
    }
}
