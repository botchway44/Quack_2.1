package macrodes.lab.com.quack.public_profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import macrodes.lab.com.quack.DeleteAccountActivity;
import macrodes.lab.com.quack.LoginActivity;
import macrodes.lab.com.quack.application.PrivacyActivity;
import macrodes.lab.com.quack.R;
import macrodes.lab.com.quack.application.SecurityActivity;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class AccountSettingsActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        getSupportActionBar().hide();

    }

    public void backButtonClicked(View view) {
        finish();
    }


    public void SecurityClicked(View view) {
        startActivity(SecurityActivity.class);
    }

    public void PrivacyClicked(View view) {
        startActivity(PrivacyActivity.class);
    }

    public void DeleteAccountClicked(View view) {
        startActivity(DeleteAccountActivity.class);
    }

    public void logOutClicked(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Log Out");
        builder.setMessage("Do you want to log out your account");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SimplePreferences.with(AccountSettingsActivity.this).setShared("account","userId","");
                        SimplePreferences.with(AccountSettingsActivity.this).setShared("account","profileImage","");
                        SimplePreferences.with(AccountSettingsActivity.this).setShared("account","aboutMe","");
                        //SimplePreferences.with(AccountSettingsActivity.this).setShared("account","email","");
                        SimplePreferences.with(AccountSettingsActivity.this).setShared("account","username","");
                        SimplePreferences.with(AccountSettingsActivity.this).setShared("account","phoneNumber","");
                        startActivity(LoginActivity.class);
                        finish();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // code to run when Cancel is pressed
                    }
                });

        builder.show();


    }
}
