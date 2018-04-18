package macrodes.lab.com.quack.application;

import android.os.Bundle;

import macrodes.lab.com.quack.R;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;

@AutoSaveFields
public class AppInfoActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        getSupportActionBar().hide();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
