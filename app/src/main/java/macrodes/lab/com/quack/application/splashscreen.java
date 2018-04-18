package macrodes.lab.com.quack.application;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import macrodes.lab.com.quack.DashBoard;
import macrodes.lab.com.quack.LoginActivity;
import macrodes.lab.com.quack.R;
import macrodes.lab.com.quack.utils.CreateAppDirectory;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimpleDialog;
import stanford.androidlib.SimplePreferences;

public class splashscreen extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        getSupportActionBar().hide();


        CreateAppDirectory createAppDirectory = new CreateAppDirectory(this);
        createAppDirectory.initFolders();


        final String userId = SimplePreferences.with(this).getSharedString("account","userId");

        //toast(userId);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(userId.length() < 1){
                    startActivity(LoginActivity.class);
                }else{
                    startActivity(DashBoard.class);
                }

                finish();
            }
        },2000);



    }
}
