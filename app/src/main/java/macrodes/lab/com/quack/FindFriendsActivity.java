package macrodes.lab.com.quack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import macrodes.lab.com.quack.firebasedata.Contacts;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class FindFriendsActivity extends SimpleActivity {
    //Contacts arraylist for storing the contacts
    private ArrayList<Contacts> contacts;
    private ArrayList<String> userIds;
    private ListView listView;
    private MyContactsListAdapter myContactsListAdapter;

    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;

    //Progress
    private ProgressBar mprogressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        getSupportActionBar().hide();


            final String userId = SimplePreferences.with(this).getSharedString("account","userId");
        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }


        //init firebase connections
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("joinedUsersId");

        //init contacts list
        contacts = new ArrayList<>();
        userIds = new ArrayList<>();

        //contacts.add(new Contacts("sd","df"));

        listView = findListView(R.id.find_friends);
        myContactsListAdapter = new MyContactsListAdapter(contacts);
        listView.setAdapter(myContactsListAdapter);

        getAllUsersIds();
        myContactsListAdapter.notifyDataSetChanged();

        mprogressBar = findProgressBar(R.id.findFriendsProgressBar);
    }


    public void getAllUsersIds(){
        //get all list of id's
        final String myId = SimplePreferences.with(this).getSharedString("account","userId");

        dbref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //put all user id's in the usersId list

                String id = dataSnapshot.getKey();
                    if(!(myId.equals(id))) {
                        userIds.add(id);
                        //toast("id is : "+id);
                        //log("id is : " + id);
                        listToValue(id);
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

    public void listToValue(final String userId){
        //reference the database to the user/id/account table and get all ids
         DatabaseReference root = fdb.getReference("users");
         DatabaseReference useridref = root.child(userId);
         DatabaseReference newRef = useridref.child("account");

        //pick all info's to the contact and show on the list
        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contacts contact = dataSnapshot.getValue(Contacts.class);
                //toast(userId+" is tostring "+contact.toString());
                contacts.add(contact);
                myContactsListAdapter.notifyDataSetChanged();

                if (contacts.size() > 0) {
                    mprogressBar.setVisibility(View.GONE);
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

    public void backButtonClicked(View view) {finish();
    }

    public void SearchClicked(View view) {
        //TODO SEARCH FOR FRIENDS
    }


    public  class MyContactsListAdapter extends BaseAdapter {

        private ArrayList<Contacts> contacts = new ArrayList<>();

        public MyContactsListAdapter(ArrayList<Contacts> contacts){
            this.contacts= contacts;
        }


        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Contacts getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final LayoutInflater inflater = getLayoutInflater();

            convertView = inflater.inflate(R.layout.contacts_view_list,null);

            final Contacts contact = getItem(position);

            TextView name = convertView.findViewById(R.id.username);
            TextView about = convertView.findViewById(R.id.aboutUser);
            ImageView img = convertView.findViewById(R.id.imageView);

            RequestOptions options = new RequestOptions()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            if(contact.getProfileImage()!= null && (contact.getProfileImage().length() > 1)){
                Glide.with(FindFriendsActivity.this).load(contact.getProfileImage()).apply(options).into(img);
            }else {
                Glide.with(FindFriendsActivity.this).load(R.raw.quack_user).apply(options).into(img);

            }

            name.setText(contact.getUsername());
            about.setText(contact.getEmail());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(ViewContactProfileActivity.class, "userId",contact.getUserId(),
                                                                            "isRequest","false");
                }
            });

            return convertView;
        }
    }


}
