package macrodes.lab.com.quack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

import macrodes.lab.com.quack.firebasedata.Contacts;
import macrodes.lab.com.quack.firebasedata.FirebaseConstants;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class ViewContactProfileActivity extends SimpleActivity {

    private Contacts contacts;
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;
    private String userId;
    private String username;
    private String profileImage;
    private FloatingActionButton btn1;
    private FloatingActionButton btn2;
    private FloatingActionButton btn3;
    private FloatingActionButton btn4;
    private HashMap<String,String> Following = new HashMap<>();
    private HashMap<String,String> FriendsList = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contact_profile);

        //action bar
        getSupportActionBar().hide();

        //intifirebase
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");

        //btn hide for friend request


        Intent intent = getIntent();
        String isRequest = intent.getStringExtra("isRequest");
        if(isRequest.length() > 1 && isRequest.equals("true")){
            btn1 = findViewById(R.id.AddFriendBtn);
            btn1.setVisibility(View.GONE);
        }else if(isRequest.length() > 1 && isRequest.equals("false")){
            btn2 = findViewById(R.id.acceptFriendRequest);
            btn2.setVisibility(View.GONE);
        }else{
            btn1 = findViewById(R.id.acceptFriendRequest);
            btn1.setVisibility(View.GONE);
            btn2 = findViewById(R.id.AddFriendBtn);
            btn2.setVisibility(View.GONE);
            btn3 = findViewById(R.id.showActionBtns);
            btn3.setVisibility(View.GONE);

        }

        //get id from intent
        userId = intent.getStringExtra("userId")+"";
        //toast(userId);
        getInfo();

//        String email = intent.getStringExtra("email")+"";
//        username = intent.getStringExtra("username")+"";
//        userId = intent.getStringExtra("userId")+"";
//        String phoneNumber = intent.getStringExtra("phoneNumber")+"";
//        String aboutMe = intent.getStringExtra("aboutMe")+"";
//        profileImage = intent.getStringExtra("profileImage")+"";
//


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void getInfo(){
        DatabaseReference newRef = fdb.getReference("users/"+userId+"/account");
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                contacts = dataSnapshot.getValue(Contacts.class);
                setInfo();
                //toast(contacts.toString());
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


        String myId = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account", "userId");

        fdb.getReference().child("users").child(myId).child("following").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Following.put(dataSnapshot.getKey(),dataSnapshot.getKey());
                //toast(dataSnapshot.getKey());
                //if if following user and set color
                CheckIfFollowingUser();
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

    public void  setInfo(){
        String email = contacts.getEmail()+"";
        username = contacts.getUsername()+"";
        //userId = contacts.getUserId()+"";
        String phoneNumber = contacts.getPhoneNumber()+"";
        String aboutMe = contacts.getAboutMe()+"";
        profileImage = contacts.getProfileImage()+"";

        ///call get Info function


        //Validate email
        if(email.length() > 1) {
            findTextView(R.id.userEmail).setText(email);
        }else {
            findTextView(R.id.userEmail).setText("Email");
        }


        //Validate username
        if(username.length() > 1) {
            findTextView(R.id.username).setText(username);
        }else {
            findTextView(R.id.username).setText("Username");
        }

        //Validate username
        if(aboutMe.length() > 1) {
            findTextView(R.id.aboutmeBtn).setText(aboutMe);
        }else {
            findTextView(R.id.aboutmeBtn).setText("About ");
        }


        //Validate username
        if(phoneNumber.length() > 1) {
            findTextView(R.id.phoneNumberBtn).setText(phoneNumber);
        }else {
            findTextView(R.id.phoneNumberBtn).setText("Phone Number");
        }

        //add the rounded image view
        RequestOptions options = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        ImageView userImage = findImageView(R.id.userImage);
        if(profileImage.length() > 1){
            Glide.with(this).load(profileImage).apply(options).into(userImage);
        }else {
            Glide.with(this).load(R.raw.quack_user).apply(options).into(userImage);
        }

    }
    public void backButtonClicked(View view) {
        finish();
    }

    public void Add_Friend_Btn(final View view) {

        String myId = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account","userId");
        //toast(myId);
        String myUsername = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account","username");

        DatabaseReference newRef = fdb.getReference("users/"+userId);
        String pushKey = newRef.push().getKey();

        newRef.child("friendRequest").child(myId).setValue(myId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar.make(view, "Friend request sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

    }

    public void viewProfile(View view) {
        startActivity(ShowImage.class,"url",profileImage,"name",username);
    }

    public void AcceptButtonClicked(final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Friend Request");
        builder.setMessage("Add "+username+" as friend");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        String myId = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account","userId");
                       // toast(myId);
                        String myUsername = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account","username");

                        DatabaseReference newRef = fdb.getReference("users/"+userId);
                        DatabaseReference newRef2 = fdb.getReference("users/"+myId);
                        DatabaseReference newRef3 = fdb.getReference("users/"+myId);

                        String pushKey = newRef.push().getKey();

                        newRef.child("friendsList").child(myId).setValue(myId).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                            }
                        });

                        newRef2.child("friendsList").child(userId).setValue(userId).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar.make(view, "Added to friends", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });

                        newRef3.child("friendRequest").child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

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


    public void FollowUserClicked(View view) {
        String myId = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account", "userId");
        // toast(myId);
        String myUsername = SimplePreferences.with(ViewContactProfileActivity.this).getSharedString("account", "username");

        if (Following.containsKey(userId)) {
            fdb.getReference().child("users").child(myId).child("following").child(userId).removeValue();
            Following.remove(userId);
            UnsetColor(R.id.followUser);
            } else {
            fdb.getReference().child("users").child(myId).child("following").child(userId).setValue(userId);
            setColor(R.id.followUser);
        }
    }

    public void setColor(int btn){
        FloatingActionButton button = findViewById(btn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red_24dp, this.getTheme()));
        } else {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_red_24dp));
        }

    }

    public void setAddFriendsColor(int btn){
        FloatingActionButton button = findViewById(btn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_red_24dp, this.getTheme()));
        } else {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_red_24dp));
        }

    }

    public void UnsetAddFriendsColor(int btn){
        FloatingActionButton button = findViewById(btn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp, this.getTheme()));
        } else {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
        }
    }

    public void UnsetColor(int btn){
        FloatingActionButton button = findViewById(btn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp, this.getTheme()));
        } else {
            button.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        }

    }

    private void CheckIfFollowingUser(){
        //toast("user id is "+userId);
        if(Following.containsKey(userId)){
            setColor(R.id.followUser);
        }
    }
}
