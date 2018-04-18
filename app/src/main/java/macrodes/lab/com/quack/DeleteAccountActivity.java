package macrodes.lab.com.quack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class DeleteAccountActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        getSupportActionBar().hide();


    }

    public void backButtonClicked(View view) {finish();
    }
}
