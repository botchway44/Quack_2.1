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
public class ChangePhoneNumber extends SimpleActivity {

    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone_number);


        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");


        getSupportActionBar().hide();

        String phonenumber= SimplePreferences.with(ChangePhoneNumber.this).getSharedString("account","phonenumber");
        findEditText(R.id.phonenumber).setText(phonenumber);



        final String userId = SimplePreferences.with(this).getSharedString("account","userId");
        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }

    }

    public void SaveClicked(final View view) {
        String userId = SimplePreferences.with(ChangePhoneNumber.this).getSharedString("account","userId");

        String phonenumber = findEditText(R.id.phonenumber).getText().toString();
        SimplePreferences.with(ChangePhoneNumber.this).setShared("account","phonenumber",phonenumber);


        dbref.child(userId).child("account").child("userInfo").child("phoneNumber").setValue(phonenumber).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar.make(view, "Phone number saved", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        toast("Phonenumber saved");
        finish();
    }

    public void cancelClicked(View view) {

    }

    public void backButtonClicked(View view) {finish();
    }
}
