package macrodes.lab.com.quack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends SimpleActivity {

    private String username;
    private String email;
    private String password;
    private String confirmpassword;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private View view;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Set up the login form.


        auth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //init firebase database and authenticate
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        auth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);

        String username= SimplePreferences.with(SignUpActivity.this).getSharedString("account","username");
        findEditText(R.id.Signupusername).setText(username);

    }

    public void createAccountClicked(View view) {
        this.view = view;
        password = findEditText(R.id.signuppassword).getText().toString();
        email = findEditText(R.id.signupemail).getText().toString();
        confirmpassword = findEditText(R.id.confirmpassword).toString();
        username = findEditText(R.id.Signupusername).getText().toString();

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(username)) {
            progress.setTitle("Signing Up");
            progress.show();

            tryLogin();

        }
    }

    private void tryLogin(){

        try {

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Task task) {
                            if (task.isSuccessful()) {

                                //String email = user.getEmail();
                                // ...
                                //toast("the account already exist");
                                Snackbar.make(view, "Account Already exist", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                progress.hide();
                            }else {
                                createAccount();
                            }
                        }
                    });
        }catch (Exception e){
            log(e);
        }
    }


    private  void createAccount(){
        try {

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(Task task) {
                            if (task.isSuccessful()) {
                                String id = auth.getCurrentUser().getUid();
                                DatabaseReference db = myRef.child(id).child("account").child("userInfo");
                                db.child("username").setValue(username);
                                db.child("email").setValue(email);
                                db.child("aboutMe").setValue("");
                                db.child("phoneNumber").setValue("");
                                db.child("userId").setValue(id);

                                //Add user to joined members Id
                                database.getReference("joinedUsersId").child(id).setValue(id);

                                toast("successfull");
                                SimplePreferences.with(SignUpActivity.this).setShared("account","username",username);
                                SimplePreferences.with(SignUpActivity.this).setShared("account","userId",id);
                                SimplePreferences.with(SignUpActivity.this).setShared("account","email",email);
                                progress.hide();
                                startActivity(DashBoard.class);
                                finish();

                            }else {
                                //toast("unsuccessful");
                                Snackbar.make(view, "Could not create account with user information", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                progress.hide();
                            }
                        }
                    });
        }catch (Exception e){
            log(e);
        }
    }



    public void loginClicked(View view) {
        startActivity(LoginActivity.class);
    }
}

