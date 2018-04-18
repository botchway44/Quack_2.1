package macrodes.lab.com.quack.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import macrodes.lab.com.quack.old_group_chat.MainActivity;
import macrodes.lab.com.quack.R;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;

@AutoSaveFields
public class HelpPageActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_page);


        getSupportActionBar().hide();
    }

    public void backButtonClicked(View view) {
        finish();
    }

    public void AdminGroupClicked(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Quack Admin Group");
        builder.setMessage("Are you sure you want to continue");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       startActivity(MainActivity.class);
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

    @Override
    public void onAlertDialogClose(AlertDialog dialog) {
        super.onAlertDialogClose(dialog);
        startActivity(MainActivity.class);
    }

    public void contactUsClicked(View view) {
        startActivity(ContactUsActivity.class);
    }

    public void appInfoClicked(View view) {
        startActivity(AppInfoActivity.class);
    }

    public void termsAndPrivacyClicked(View view) {
        startActivity(TermsAndPrivacyActivity.class);
    }
}
