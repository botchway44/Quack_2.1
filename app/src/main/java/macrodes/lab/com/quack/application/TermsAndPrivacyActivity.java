package macrodes.lab.com.quack.application;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import macrodes.lab.com.quack.R;
import stanford.androidlib.AutoSaveFields;

@AutoSaveFields
public class TermsAndPrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_privacy);

        getSupportActionBar().hide();
    }

    public void backButtonClicked(View view) {
        finish();
    }
}
