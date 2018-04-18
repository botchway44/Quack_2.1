package macrodes.lab.com.quack.application;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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
public class ContactUsActivity extends SimpleActivity {

    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getSupportActionBar().hide();

        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("feedback");


        final String userId = SimplePreferences.with(this).getSharedString("account","userId");
        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }
    }

    public void backButtonClicked(View view) {
        finish();
    }

    public void sendFeedBack(final View view) {
        String key = dbref.push().getKey();

        String userId = SimplePreferences.with(ContactUsActivity.this).getSharedString("account","userId");

        if(userId == ""){
            startActivity(LoginActivity.class);
        }

        EditText message = findEditText(R.id.enterMessage);

        if(message.length() > 2) {
            dbref.child(key).setValue(message.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(view, "Thanks for the feedback \n if your problem is still not resolved join " +
                            "\nthe admin group and send message there", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            });
        }
    }
}
