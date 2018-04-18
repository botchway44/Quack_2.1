package macrodes.lab.com.quack;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Date;

import macrodes.lab.com.quack.application.SettingsActivity;
import macrodes.lab.com.quack.utils.CreateAppDirectory;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;

@AutoSaveFields
public class DashBoard extends SimpleActivity {

    private static final int REQUEST_CAMERA = 12;
    private static final int GALLERY_INTENT = 134;
    private String myId;
    private ProgressBar progressBarHorizontal;
    private android.support.v4.app.FragmentManager fragmentManager;

    /*
    start at 2 because the chats activity is always loaded when started*/
   /*
    private boolean chatsActivated = true;
    private boolean statusActivated = false;
    private boolean contactsActivated = false;
    */

   //Toogle ahowActionBtn
    private Boolean showActionBtnCheck;
    private  PrivateChatFragment privateChatFragment;
    private ContactsFragment contactsFragment;
    private StatusFragment statusFragment;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        myId = SimplePreferences.with(DashBoard.this).getSharedString("account","userId");
        privateChatFragment = new PrivateChatFragment(myId);
        contactsFragment =  new ContactsFragment(myId);
        statusFragment = new StatusFragment();

        //hide action bar
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(255,255,64,129)));
        getSupportActionBar().hide();
        LoadDefault();
        listenToFloatingButtons();

    }

    private void listenToFloatingButtons() {
        FloatingActionButton changeToChats = findViewById(R.id.changeToChats);
        FloatingActionButton changeToDashBoard = findViewById(R.id.changeToDashBoard);

        changeToChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.myfragment, new PrivateChatFragment(myId)).commit();

            }
        });

        changeToDashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.myfragment, new NewFeedFragment()).commit();

            }
        });
    }


    public void LoadDefault(){

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.myfragment, privateChatFragment).commit();
        EnableFloatingButton(R.id.changeToChats);
        EnableFloatingButton(R.id.changeToDashBoard);

        DisableFloatingButton(R.id.mediaStatus);
        DisableFloatingButton(R.id.textStatus);

        DisableFloatingButton(R.id.findfriends);
        DisableFloatingButton(R.id.findGroups);

        //set the action buttons listeners
        FloatingActionButton showActionBtn = findViewById(R.id.showActionBtns);
        FloatingActionButton hideActionBtns = findViewById(R.id.showActionBtns);

        showActionBtn.hide();
        showActionBtnCheck = false;
        showActionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showActionBtnCheck) {
                    hideAllActionButton();
                    showActionBtnCheck = false;

                    FloatingActionButton hideActionBtns = findViewById(R.id.showActionBtns);
                    hideActionBtns.hide();

                }else {
                    showAllActionButtons();
                    showActionBtnCheck = true;

                    FloatingActionButton hideActionBtns = findViewById(R.id.showActionBtns);
                    hideActionBtns.show();
                }
            }
        });



        hideActionBtns.hide();
        showActionBtnCheck = false;
        hideActionBtns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showActionBtnCheck) {
                    hideAllActionButton();

                    showActionBtnCheck = false;
                }else {
                    showAllActionButtons();
                    showActionBtnCheck = true;
                }
            }
        });

    }


    public void hideAllActionButton(){
        DisableFloatingButton(R.id.showFriendRequest);
        DisableFloatingButton(R.id.findfriends);
        DisableFloatingButton(R.id.findGroups);
    }



    public void showAllActionButtons(){
        EnableFloatingButton(R.id.showFriendRequest);
        EnableFloatingButton(R.id.findfriends);
        EnableFloatingButton(R.id.findGroups);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String Username= SimplePreferences.with(DashBoard.this).getSharedString("account","username");

        if(Username.length() > 1) {
            findTextView(R.id.quack_user).setText(Username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    public void actionBarBtnCliked(View view) {
        //hide the status
        if(view.getId() == R.id.contactsBtn){

            //set and unset Text and divider color
            setContacts();
            fragmentManager.beginTransaction().replace(R.id.myfragment, new ContactsFragment(myId)).commit();
            DisableFloatingButton(R.id.changeToChats);
            DisableFloatingButton(R.id.changeToDashBoard);

            DisableFloatingButton(R.id.mediaStatus);
            DisableFloatingButton(R.id.textStatus);

            EnableFloatingButton(R.id.showActionBtns);
            hideAllActionButton();

        }else if(view.getId() == R.id.statusBtn){

            //set and unset Text and divider color
            setStatus();
            fragmentManager.beginTransaction().replace(R.id.myfragment, statusFragment).commit();
            DisableFloatingButton(R.id.changeToChats);
            DisableFloatingButton(R.id.changeToDashBoard);

            EnableFloatingButton(R.id.mediaStatus);
            EnableFloatingButton(R.id.textStatus);

            DisableFloatingButton(R.id.showActionBtns);
            hideAllActionButton();

        } else if(view.getId() == R.id.chatsBtn){
            //toast("chats clicked");

            //set and unset Text and divider color
            setChats();
            fragmentManager.beginTransaction().replace(R.id.myfragment, new PrivateChatFragment(myId)).commit();
            EnableFloatingButton(R.id.changeToChats);
            EnableFloatingButton(R.id.changeToDashBoard);

            DisableFloatingButton(R.id.mediaStatus);
            DisableFloatingButton(R.id.textStatus);


            hideAllActionButton();
            DisableFloatingButton(R.id.showActionBtns);
            DisableFloatingButton(R.id.findfriends);
            DisableFloatingButton(R.id.findGroups);
            DisableFloatingButton(R.id.showFriendRequest);


        }else{

        }

    }

    public void setColor(int btn,int divider){
        View view = findViewById(divider);
        view.setBackgroundColor(Color.parseColor("#FFF9F2F4"));

       // findButton(btn).setTextColor(Color.parseColor("#FFF9F2F4"));
    }


    public void UnsetColor(int btn,int divider){
        View view = findViewById(divider);
        view.setBackgroundColor(Color.parseColor("#FF4081"));
        //findImageButton(btn).setTextColor(Color.parseColor("#FFDCDBDB"));
    }

    public void setContacts(){
        //setColor(R.id.contactsBtn,R.id.contactsDivider);

        //UnsetColor(R.id.statusBtn,R.id.statusDivider);
        //UnsetColor(R.id.chatsBtn,R.id.chatsDivider);
    }
    public void setChats(){
        //UnsetColor(R.id.contactsBtn,R.id.contactsDivider);

        //UnsetColor(R.id.statusBtn,R.id.statusDivider);
        //setColor(R.id.chatsBtn,R.id.chatsDivider);
    }

    public void setStatus(){
        //UnsetColor(R.id.contactsBtn,R.id.contactsDivider);

        //setColor(R.id.statusBtn,R.id.statusDivider);
        //UnsetColor(R.id.chatsBtn,R.id.chatsDivider);
    }


    public void moreClicked(View view) {
        startActivity(SettingsActivity.class);
    }

/***
 * SWIPE CODES WRITTEN HERE
 * ******************************************************
    public void swippingCodeUselessnow(){
        public void SwipRightListener(){
            if(contactsActivated){
                fragmentManager.beginTransaction().replace(R.id.myfragment, new StatusFragment()).commit();
                statusActivated = true;
                chatsActivated = false;
                contactsActivated = false;

                setStatus();

            }else  if(statusActivated){
                fragmentManager.beginTransaction().replace(R.id.myfragment, new PrivateChatFragment()).commit();
                statusActivated = false;
                chatsActivated = true;
                contactsActivated = false;

                setChats();
            }
        }


        LinearLayout linearLayout = find(R.id.myfragment);
        linearLayout.setOnTouchListener( new OnSwipeTouchListener(DashBoard.this){
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                toast("right swip "+checkSwip);
                SwipRightListener();

            }
            public void onSwipeLeft() {
                toast("left swip "+checkSwip);
                SwipLeftListener();

            }
            public void onSwipeBottom() {

            }
        });

    }

    public void SwipLeftListener(){
        if(chatsActivated){
            fragmentManager.beginTransaction().replace(R.id.myfragment, new StatusFragment()).commit();
            statusActivated = true;
            chatsActivated = false;
            contactsActivated = false;

            setStatus();

        }else if(statusActivated){
            fragmentManager.beginTransaction().replace(R.id.myfragment, new ContactsFragment()).commit();
            statusActivated = false;
            chatsActivated = false;
            contactsActivated = true;

            setContacts();
        }
    }
*****/
    public void NewFeedsClicked(View view) {
        fragmentManager.beginTransaction().replace(R.id.myfragment, new NewFeedFragment()).commit();
        EnableFloatingButton(R.id.changeToChats);
        EnableFloatingButton(R.id.changeToDashBoard);

    }

    public void ChatsClicked(View view) {
        EnableFloatingButton(R.id.changeToChats);
        EnableFloatingButton(R.id.changeToDashBoard);

        fragmentManager.beginTransaction().replace(R.id.myfragment, new PrivateChatFragment()).commit();
    }

    public void DisableFloatingButton(int id){
        FloatingActionButton findFriendsBtn = findViewById(id);
        findFriendsBtn.hide();

    }

    public void EnableFloatingButton(int id){
        FloatingActionButton findFriendsBtn = findViewById(id);
        findFriendsBtn.show();

    }


    public void FindFriendsClicked(View view) {
        startActivity(FindFriendsActivity.class);
    }

    public void ShowFriendRequest(View view) {
        startActivity(ShowFriendRequest.class);
    }

    public void ShowGroupsAvailable(View view) {
        startActivity(ShowGroupsAvailable.class);
    }


    //TODO START FROM Here
    public void SelctFromGalleryClicked(View view) {
        this.view = view;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }




    public void StatusOptionsSelector(View view) {
        startActivity(CreateStatusActivity.class);
    }

    public void createTextStatus(View view) {
        startActivity(CreateTextStatusActivity.class);
    }
}
