package macrodes.lab.com.quack.public_profile;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import macrodes.lab.com.quack.LoginActivity;
import macrodes.lab.com.quack.R;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class ChangeAboutMe extends SimpleActivity {

    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_about_me);

        getSupportActionBar().hide();
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");


        String aboutme= SimplePreferences.with(ChangeAboutMe.this).getSharedString("account","aboutme");
        findEditText(R.id.aboutme).setText(aboutme);


        final String userId = SimplePreferences.with(this).getSharedString("account","userId");
        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }

    }

    public void SaveClicked(final View view) {
        String userId = SimplePreferences.with(ChangeAboutMe.this).getSharedString("account","userId");

        String aboutme = findEditText(R.id.aboutme).getText().toString();
        SimplePreferences.with(ChangeAboutMe.this).setShared("account","aboutme",aboutme);


        dbref.child(userId).child("account").child("userInfo").child("aboutMe").setValue(aboutme).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar.make(view, "Saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        finish();
    }

    public void cancelClicked(View view) {
        finish();
    }

    public void backButtonClicked(View view) {finish();
    }
}
