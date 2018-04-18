package macrodes.lab.com.quack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import stanford.androidlib.SimpleActivity;

/**
 * Created by Botchway on 3/16/2018.
 */

public class ContactsFragment extends Fragment {

    private ArrayList<Contacts> contacts;
    private ArrayList<String> userIds;
    private ListView listView;
    private MyContactsListAdapter myContactsListAdapter;
    private String id;
    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;
    private View view;

    //progressBar
    private ProgressBar mprogressBar;
    public ContactsFragment() {
    //init firebase connections
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");

        //init contacts list
        contacts = new ArrayList<>();
        userIds = new ArrayList<>();
    }


    @SuppressLint("ValidFragment")
    public ContactsFragment(String id){
        this.id = id;

        //init firebase connections
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");

        //init contacts list
        contacts = new ArrayList<>();
        userIds = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.contacts_fragment, container, false);
        this.view = rootView;

        mprogressBar = rootView.findViewById(R.id.progressBarContacts);

        //TODO FIX USER ID FOR FRIEND REQUEST
        //toast(id);
        listView = rootView.findViewById(R.id.friendsList);
        myContactsListAdapter = new MyContactsListAdapter(contacts);
        listView.setAdapter(myContactsListAdapter);

        //Contacts contact = new Contacts("botch","noel");
        //contacts.add(contact);

        getAllUsersIds();
        myContactsListAdapter.notifyDataSetChanged();


        return rootView;
    }

    public void toast(String text){
        Toast.makeText(view.getContext(),text, Toast.LENGTH_LONG).show();
    }

    public void getAllUsersIds() {
        //get all list of id's
        DatabaseReference newRef = fdb.getReference("users/"+id+"/friendsList");
        //toast(newRef.getRef().toString());

        newRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String id = dataSnapshot.getKey();
                userIds.add(id);
                myContactsListAdapter.notifyDataSetChanged();

                listToValue(id);
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

    public void listToValue(final String userId) {
        //reference the database to the user/id/account table and get all ids
        DatabaseReference root = fdb.getReference("users");
        DatabaseReference useridref = root.child(userId);
        DatabaseReference newRef1 = useridref.child("account");

        //pick all info's to the contact and show on the list
        newRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Contacts contact = dataSnapshot.getValue(Contacts.class);
                //toast(userId+" is tostring "+contact.toString());
                contacts.add(contact);
                myContactsListAdapter.notifyDataSetChanged();

                if(contacts.size() > 0){
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


    public class MyContactsListAdapter extends BaseAdapter {

        private ArrayList<Contacts> contacts = new ArrayList<>();

        public MyContactsListAdapter(ArrayList<Contacts> contacts) {
            this.contacts = contacts;
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

            convertView = inflater.inflate(R.layout.contacts_view_list, null);

            final Contacts contact = getItem(position);

            TextView name = convertView.findViewById(R.id.username);
            TextView about = convertView.findViewById(R.id.aboutUser);
            ImageView img = convertView.findViewById(R.id.imageView);

            RequestOptions options = new RequestOptions()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            if (contact.getProfileImage() != null && (contact.getProfileImage().length() > 1)) {
                Glide.with(convertView.getContext()).load(contact.getProfileImage()).apply(options).into(img);
            } else {
                Glide.with(convertView.getContext()).load(R.raw.quack_user).apply(options).into(img);

            }

            //set onclick for Image
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ShowImage.class);
                        //"url",url,"username",myUsername
                        intent.putExtra("url",contact.getProfileImage());
                        intent.putExtra("username",contact.getUsername());
                        startActivity(intent);
                    }
                });

            name.setText(contact.getUsername());
            about.setText(contact.getEmail());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), PrivateChatFriend.class);
                    //"url",url,"username",myUsername
                    intent.putExtra("personId",contact.getUserId());
                    intent.putExtra("personName",contact.getUsername());
                    intent.putExtra("email",contact.getEmail());
                    intent.putExtra("phoneNumber",contact.getPhoneNumber());
                    intent.putExtra("profileImage",contact.getProfileImage()+"");
                    intent.putExtra("aboutMe",contact.getAboutMe());
                    startActivity(intent);
                }
            });

            return convertView;
        }

    }
}