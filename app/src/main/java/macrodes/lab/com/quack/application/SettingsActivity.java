package macrodes.lab.com.quack.application;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import macrodes.lab.com.quack.R;
import macrodes.lab.com.quack.ShowImage;
import macrodes.lab.com.quack.firebasedata.Contacts;
import macrodes.lab.com.quack.public_profile.AccountActivity;
import macrodes.lab.com.quack.public_profile.AccountSettingsActivity;
import macrodes.lab.com.quack.utils.UtilityClass;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class SettingsActivity extends SimpleActivity {

    private DatabaseReference dbref;
    private FirebaseDatabase fdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");


    }

    @Override
    protected void onResume() {
        super.onResume();

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        String profileImage = SimplePreferences.with(SettingsActivity.this).getSharedString("account","profileImage");
        String myUsername = SimplePreferences.with(SettingsActivity.this).getSharedString("account","username");
        String aboutMe = SimplePreferences.with(SettingsActivity.this).getSharedString("account","aboutMe");

        findTextView(R.id.usernameInSettings).setText(myUsername);
        findTextView(R.id.aboutMeInSettings).setText(aboutMe);

        pullDataFromFirebase();
        
        //check if there is network connectivity
        UtilityClass utilityClass = new UtilityClass(this);
        if(utilityClass.isNetworkAvailable(this)) {
            ImageView userImage = findImageView(R.id.userImageInSettings);

            if (profileImage.length() > 1) {
                Glide.with(this).load(profileImage).apply(options).into(userImage);
            } else {
                Glide.with(this).load(R.raw.quack_user).apply(options).into(userImage);
            }

        }else {
            ImageView userImage = findImageView(R.id.userImageInSettings);

            if (profileImage.length() > 1) {
                Glide.with(this).load(profileImage).apply(options).into(userImage);
            } else {
                Glide.with(this).load(R.raw.quack_user).apply(options).into(userImage);
            }

        }
    }

    public void accountClicked(View view) {
        startActivity(AccountSettingsActivity.class);

    }

    public void profileClicked(View view) {
        startActivity(AccountActivity.class);
    }

    public void profileImageClicked(View view) {
        String url = SimplePreferences.with(SettingsActivity.this).getSharedString("account","profileImage");
        String myUsername = SimplePreferences.with(SettingsActivity.this).getSharedString("account","username");

        startActivity(ShowImage.class,"url",url,"username",myUsername);
    }

    public void backButtonClicked(View view) {
        finish();
    }

    public void HelpClicked(View view) {
        startActivity(HelpPageActivity.class);
    }

    public void NotificationClicked(View view) {
        startActivity(NotificationActivity.class);
    }

    public void pullDataFromFirebase(){
        String id = SimplePreferences.with(SettingsActivity.this).getSharedString("account","userId");

        dbref.child(id).child("account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contacts userAccount = dataSnapshot.getValue(Contacts.class);
                //save fields as user preference files and edit them anytime needed
                SimplePreferences.with(SettingsActivity.this).setShared("account", "email", userAccount.getEmail());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "username", userAccount.getUsername());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "phoneNumber", userAccount.getPhoneNumber());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "aboutMe", userAccount.getAboutMe());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "profileImage", userAccount.getProfileImage());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //pull information and save
    public  void  pullInfo(){
        final String userId = SimplePreferences.with(this).getSharedString("account","userId");

        //get all the credentials from online and save to offline db
        dbref.child(userId).child("account").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contacts userAccount = dataSnapshot.getValue(Contacts.class);
                //toast(userAccount.toString());
                //save fields as user prefernce files and edit them anytime needed
                SimplePreferences.with(SettingsActivity.this).setShared("account", "email", userAccount.getEmail());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "username", userAccount.getUsername());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "phoneNumber", userAccount.getPhoneNumber());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "aboutMe", userAccount.getAboutMe());
                SimplePreferences.with(SettingsActivity.this).setShared("account", "profileImage", userAccount.getProfileImage());

                if (findCheckBox(R.id.checkKeepMeLogedIn).isChecked()) {
                    SimplePreferences.with(SettingsActivity.this).setShared("account", "saveCredentials", "TRUE");
                } else {
                    SimplePreferences.with(SettingsActivity.this).setShared("account", "saveCredentials", "FALSE");

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
