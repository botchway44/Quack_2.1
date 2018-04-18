package macrodes.lab.com.quack.old_group_chat;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import macrodes.lab.com.quack.Chat;
import macrodes.lab.com.quack.LoginActivity;
import macrodes.lab.com.quack.R;
import macrodes.lab.com.quack.ShowImage;
import macrodes.lab.com.quack.public_profile.AccountActivity;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimpleCamera;
import stanford.androidlib.SimpleMedia;
import stanford.androidlib.SimpleNotification;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class MainActivity extends SimpleActivity {

    private static final int FM_NOTIFICATION_ID = 112;
    private   ListView listView;
    private ChatAdapter chatAdapter;
    private ArrayList<Chat> chats;
    private String Username;
    private static final int REQ_CODE_TAKE_PICTURE = 0;
    String local_Image_Storage_Path = Environment.getExternalStorageDirectory().toString()+"/Quack";
    private File quackFolder;


    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;
    private FirebaseStorage storage;
    private String tempUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final String userId = SimplePreferences.with(this).getSharedString("account","userId");
        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }


        //hide the support actionbar
        getSupportActionBar().hide();

        //setUsername
        Username= SimplePreferences.with(MainActivity.this).getSharedString("account","username");


        //set up firebase
       performFIrebaseConnection();


        //create directory to store quack images
        creatQuackDirectory();

         chats = new ArrayList<>();

         listView = (ListView) findViewById(R.id.list);

         chatAdapter = new ChatAdapter(chats);
        listView.setDividerHeight(50);

        ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.colorAccent));
        listView.setDivider(sage);

        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        listView.setStackFromBottom(true);
         listView.setAdapter(chatAdapter);


    }

    public void performFIrebaseConnection(){
        storage = FirebaseStorage.getInstance();
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("messages");

        DatabaseReference messagesNode = fdb.getReference("messages");
        messagesNode.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Chat currentChat = dataSnapshot.getValue(Chat.class);
                chats.add(currentChat);

                //show notification for new message received
                ShowNotification();
                //addNotification();
                chatAdapter.notifyDataSetChanged();
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

    public void ShowNotification(){
        SimpleNotification.with(this)
                .setContentTitle("Quack")
                .setContentText("You have new messages")
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setIntent(MainActivity.class, "")
                .send();

        SimpleMedia.with(this).play(R.raw.notification);
    }
    private String uploadImage(Bitmap bitmap,String filename){


        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        StorageReference imagesRef = storageRef.child(filename);

        // Child references can also take paths
        // spaceRef now points to "images/space.jpg
        // imagesRef still points to "images"
        StorageReference spaceRef = storageRef.child("quack/"+filename);

// While the file names are the same, the references point to different files
        imagesRef.getName().equals(spaceRef.getName());    // true
        imagesRef.getPath().equals(spaceRef.getPath());    // false


        Bitmap currentbitmap = bitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                toast("try again");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                setUri((String) downloadUrl.toString()+"");

                doThisIfUploadIsSuccessful(downloadUrl.toString()+"");
            }
        });

        //return the url of the savaed image
       return tempUri;
    }

    public void setUri(String uri){
        tempUri = uri;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent =new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();

    }

    @Override
    public void onInputDialogClose(AlertDialog dialog, String input) {
        super.onInputDialogClose(dialog, input);

        SimplePreferences.with(this).set("name",input);
        Username = input;
    }

    public void sendMessage(View view) {
          // chats.add(new Chat(Username,findEditText(R.id.message).getText().toString(),false,"001"));
          //chatAdapter.notifyDataSetChanged();

        //if the message length is greater then 1 the =n process
        if(findEditText(R.id.message).getText().toString().length() > 1){
            //add keys to firebase
            String key = dbref.push().getKey();

            String userId = SimplePreferences.with(MainActivity.this).getSharedString("account","userId");
            Username= SimplePreferences.with(MainActivity.this).getSharedString("account","username");


            if(userId == ""){
                startActivity(LoginActivity.class);
            }

            //TODO add a user id
            Chat newChat = new Chat(userId,Username,"", findEditText(R.id.message).getText().toString(),false);
            dbref.child(key).setValue(newChat);

            //set the list onliine
//        DatabaseReference newRef = dbref.child(key);
//        newRef.child("author").setValue(newChat.getAuthor());
//        newRef.child("Url").setValue(newChat.getUrl());
//        newRef.child("msg").setValue(newChat.getUrl());
//        newRef.child("time").setValue(newChat.getTime());
//        newRef.child("isMedia").setValue(newChat.isMedia());
//        newRef.child("userId").setValue(newChat.getUserId());

            findEditText(R.id.message).setText("");
        }

    }

    public void loadImage(View view) {
        requestPermission("android.permission.CAMERA");

                if(hasPermission("android.permission.CAMERA")) {
                    SimpleCamera.with(this).takePhoto();
                }
    }

    public void saveImageToFolder(Bitmap bitmap,String filename){
        String path = local_Image_Storage_Path;

        File file = new File(path, filename);

        try {
            OutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85,fout);
            fout.flush();
            fout.close();
            toast("write successful");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(),file.getName(),file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPhotoReady(Bitmap bitmap) {
        super.onPhotoReady(bitmap);

        String filename = new Date().getTime() +".png";

        String url =  uploadImage(bitmap,filename);

        //Chat(String author,boolean isMedia,String imgUrl,String chatId)
        //chats.add(new Chat(Username, true,url,key));

        String msg = findEditText(R.id.message).getText().toString();

//        DatabaseReference newRef = dbref.child(key);
//        newRef.child("author").setValue(newChat.getAuthor());
//        newRef.child("Url").setValue(newChat.getUrl());
//        newRef.child("msg").setValue(newChat.getUrl());
//        newRef.child("time").setValue(newChat.getTime());
//        newRef.child("isMedia").setValue(newChat.isMedia());
//        newRef.child("chatId").setValue(newChat.getChatId());

        saveImageToFolder(bitmap,filename);

    }

    public void doThisIfUploadIsSuccessful(String url){

        String userId = SimplePreferences.with(MainActivity.this).getSharedString("account","userId");
        Username= SimplePreferences.with(MainActivity.this).getSharedString("account","username");

        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }

        String key = dbref.push().getKey();

        //TODO add a user id
        Chat newChat = new Chat(userId,Username, url, url,true);
        dbref.child(key).setValue(newChat);
        chatAdapter.notifyDataSetChanged();
    }


    public void creatQuackDirectory(){
        quackFolder = new File(local_Image_Storage_Path);
        quackFolder.mkdir();
    }

    public void settingsClicked(View view) {
        startActivity(AccountActivity.class);
    }

    public void logOutClicked(View view) {
        SimplePreferences.with(MainActivity.this).setShared("account","userId","");
        SimplePreferences.with(MainActivity.this).setShared("account","profileImage","");
        SimplePreferences.with(MainActivity.this).setShared("account","aboutMe","");
        //SimplePreferences.with(MainActivity.this).setShared("account","email","");
        SimplePreferences.with(MainActivity.this).setShared("account","username","");
        SimplePreferences.with(MainActivity.this).setShared("account","phoneNumber","");
        startActivity(LoginActivity.class);
    }

    public class ChatAdapter extends BaseAdapter{

    private ArrayList<Chat> chats = new ArrayList<>();

        public ChatAdapter(ArrayList<Chat> chats) {
           this.chats = chats;
        }

        @Override
        public int getCount() {
            return chats.size();
        }

        @Override
        public Object getItem(int position) {
            return chats.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            convertView = inflater.inflate(R.layout.chatview,null);

            final Chat chat = chats.get(position);

            final TextView author = (TextView) convertView.findViewById(R.id.txtauthor);
            ImageView img = (ImageView) convertView.findViewById(R.id.imgmessage);
            TextView message = (TextView) convertView.findViewById(R.id.txtmessage);

            //set the name of the author
            author.setText(chat.getAuthor());

            Log.i("chat",chat.toString());


            String userId = SimplePreferences.with(MainActivity.this).getSharedString("account","userId");

           //if no user id is found take the user to the login page


            //if the user Id is equal to mine
            //set the gravity to the right
            //toast("user id is "+userId+"\n and chat id is "+chat.getUserId()+"\n and chat position is "+position);

            if(chat.getUserId().toString().equals(userId)){
                LinearLayout parent_layout = (LinearLayout) convertView.findViewById(R.id.parentChatView);
               parent_layout.setGravity(Gravity.RIGHT);

            }else if(chat.getUserId() == ""){
                LinearLayout parent_layout = (LinearLayout) convertView.findViewById(R.id.parentChatView);
                parent_layout.setGravity(Gravity.LEFT);

            }else {
                LinearLayout parent_layout = (LinearLayout) convertView.findViewById(R.id.parentChatView);
                parent_layout.setGravity(Gravity.LEFT);
            }


            if(chat.isMedia()){
                //load the image from the url with picaso
                message.setTextSize(1);

                Glide.with(MainActivity.this).load(chat.getUrl()).into(img);

            }else {
                //load the message to the list
                String msg = chat.getMessage();

                ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
                layoutParams.height = 1;
                layoutParams.width = 1;
                //img.setLayoutParams(layoutParams);
                message.setText(msg);
            }

            String time = ""+chat.getTime().getHours()+" : "+chat.getTime().getMinutes();
            TextView timeView = convertView.findViewById(R.id.time);
            timeView.setText(time);

            //image listeners
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(ShowImage.class,"url",chat.getUrl(),"username",chat.getAuthor());
                }
            });

            return convertView;
        }
    }

    /*TODO
    * REWRITE THE NOTIFICATION BUILDER CODE
    * */

    private void addNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher_foreground)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(FM_NOTIFICATION_ID, builder.build());
    }

    // Remove notification
    private void removeNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(FM_NOTIFICATION_ID);
    }
}
