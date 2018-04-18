package macrodes.lab.com.quack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import stanford.androidlib.SimpleActivity;

public class CreateTextStatusActivity extends SimpleActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text_status);

        getSupportActionBar().hide();

    }
}
