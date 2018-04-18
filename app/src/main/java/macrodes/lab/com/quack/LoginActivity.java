package macrodes.lab.com.quack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import macrodes.lab.com.quack.firebasedata.Contacts;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends SimpleActivity {

    private String email;
    private String password;
    private FirebaseAuth auth;
    private String username;
    private ProgressDialog progress;
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().hide();

        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");

        progress = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();



    }


    @Override
    protected void onResume() {
        super.onResume();
        String keepMeLogedIn= SimplePreferences.with(LoginActivity.this).getSharedString("account","saveCredentials");

        if(keepMeLogedIn.equals("TRUE")) {
            //set the email field if
            String email = SimplePreferences.with(LoginActivity.this).getSharedString("account", "email");
            findCheckBox(R.id.checkKeepMeLogedIn).setActivated(true);
            findEditText(R.id.email).setText(email);
        }
    }

    public void signupClicked(View view){
        startActivity(SignUpActivity.class);
}

    public void authenticateUser(final View view) {

        this.view = view;


    password = findEditText(R.id.password).getText().toString();
    email = findEditText(R.id.email).getText().toString();


        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {

            progress.setTitle("Signing In");
            progress.show();

            //try if theres is internet connection
            if(isNetworkAvailable()) {

                // Check for a valid password, if the user entered one.
                try {

                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(Task task) {
                                    if (task.isSuccessful()) {

                                        String id = auth.getCurrentUser().getUid();
                                        SimplePreferences.with(LoginActivity.this).setShared("account", "userId", id);

                                        //get all the credentials from online and save to offline db
                                        dbref.child(id).child("account").addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Contacts userAccount = dataSnapshot.getValue(Contacts.class);
                                                //toast(userAccount.toString());
                                                //save fields as user prefernce files and edit them anytime needed
                                                SimplePreferences.with(LoginActivity.this).setShared("account", "email", userAccount.getEmail());
                                                SimplePreferences.with(LoginActivity.this).setShared("account", "username", userAccount.getUsername());
                                                SimplePreferences.with(LoginActivity.this).setShared("account", "phoneNumber", userAccount.getPhoneNumber());
                                                SimplePreferences.with(LoginActivity.this).setShared("account", "aboutMe", userAccount.getAboutMe());
                                                SimplePreferences.with(LoginActivity.this).setShared("account", "profileImage", userAccount.getProfileImage());

                                                if (findCheckBox(R.id.checkKeepMeLogedIn).isChecked()) {
                                                    SimplePreferences.with(LoginActivity.this).setShared("account", "saveCredentials", "TRUE");
                                                } else {
                                                    SimplePreferences.with(LoginActivity.this).setShared("account", "saveCredentials", "FALSE");

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

                                                //toast("successfull");
                                                Snackbar.make(view, "Login Successful", Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();

                                        progress.hide();
                                        startActivity(DashBoard.class);
                                        finish();
                                    } else {
                                        //toast("unsuccessful");
                                        Snackbar.make(view, "Login failed, check email and password ", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        progress.hide();
                                    }
                                }
                            });
                } catch (Exception e) {
                    log(e);
                }
            }else {
                Snackbar.make(view, "Check Internet Connection", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        }else {
            Snackbar.make(view, "Enter Login credentials", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

