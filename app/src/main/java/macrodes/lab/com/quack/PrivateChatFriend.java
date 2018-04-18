package macrodes.lab.com.quack;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import macrodes.lab.com.quack.firebasedata.ChatMessages;
import macrodes.lab.com.quack.firebasedata.NewChatsAdapter;
import macrodes.lab.com.quack.firebasedata.PrivateChat;
import macrodes.lab.com.quack.public_profile.AccountSettingsActivity;
import macrodes.lab.com.quack.utils.CreateAppDirectory;
import macrodes.lab.com.quack.utils.UtilityClass;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimpleCamera;
import stanford.androidlib.SimplePreferences;

@AutoSaveFields
public class PrivateChatFriend extends SimpleActivity {
    private static final int REQUEST_MICROPHONE = 32;
    private static final int GALLERY_INTENT = 12;
    private static final int REQUEST_CAMERA = 23;
    private boolean permissionToRecordAccepted = false;
    private MediaRecorder mRecorder = null;
    private String mFileName;

    private String myId;
    private String myName;
    private String personId;
    private String personName;
    private String aboutMe;
    private String phoneNumber;
    private String email;
    private String profileImage;
    private HashMap<String, String> namesToId;

    private StorageReference firebaseStorage;
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;
    private FirebaseStorage storage;

    private ArrayList<ChatMessages> chats;
    private NewChatsAdapter newChatsAdapter;

    private ProgressBar progressBar;
    @SuppressLint("ClickableViewAccessibility")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat_friend);
        progressBar = findProgressBar(R.id.privateChatFriendProgressBar);

        performThese();
    }

    public  void  performThese(){
        myId = SimplePreferences.with(PrivateChatFriend.this).getSharedString("account", "userId");
        myName = SimplePreferences.with(PrivateChatFriend.this).getSharedString("account", "username");


        //comment
        Intent intent = getIntent();
        personId = intent.getStringExtra("personId") + "";
        email = intent.getStringExtra("email") + "";
        aboutMe = intent.getStringExtra("aboutMe") + "";
        phoneNumber = intent.getStringExtra("phoneNumber") + "";
        personName = intent.getStringExtra("personName") + "";
        profileImage = intent.getStringExtra("profileImage") + "";

        //init firebase
        storage = FirebaseStorage.getInstance();
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference();

        //add the firebase accessibility
        chats = new ArrayList<>();
        myId = SimplePreferences.with(PrivateChatFriend.this).getSharedString("account", "userId");
        myName = SimplePreferences.with(PrivateChatFriend.this).getSharedString("account", "username");

        //perform the basic operaions
        performAllOperations();

        newChatsAdapter = new NewChatsAdapter(this, chats, myName, personName, myId);

        ListView listView = findListView(R.id.messagesList);
        listView.setAdapter(newChatsAdapter);
        listView.setDividerHeight(50);

        ColorDrawable sage = new ColorDrawable(this.getResources().getColor(R.color.colorAccent));
        listView.setDivider(sage);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        ListenToMessageNode();
//        chats.add(new ChatMessages(myId,"","I am the message", CreateAppDirectory.MEDIA_TYPE_TExT, new Date(),""));
//        chats.add(new ChatMessages("","","I am the reply", CreateAppDirectory.MEDIA_TYPE_TExT, new Date(),""));
//        chats.add(new ChatMessages("","https://firebasestorage.googleapis.com/v0/b/quack-42785.appspot.com/o/Wed%20Mar%2021%2020%3A12%3A48%20GMT%2B00%3A00%202018.jpg?alt=media&token=0487ebc2-ac54-47c0-b282-b5fd3a149366","I am the reply", CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(),""));
        listView.setStackFromBottom(true);
        newChatsAdapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();


    }

    public void ListenToMessageNode() {

        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");

        DatabaseReference messagesNode = dbref.child(myId);
        DatabaseReference messagesNode1 = messagesNode.child("chats/" + personId);

        messagesNode1.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessages currentChat = dataSnapshot.getValue(ChatMessages.class);
                chats.add(currentChat);
                //toast("new messages detcted");
                //show notification for new message received
                //ShowNotification();
                //addNotification();
                if(chats.size() > 0) progressBar.setVisibility(View.GONE);
                newChatsAdapter.notifyDataSetChanged();
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


    public void performAllOperations() {
        //get the image view
        ImageView img = findImageView(R.id.profileImg);
        findTextView(R.id.personName).setText(personName);

        if (aboutMe.length() > 1) {
            findTextView(R.id.description).setText(aboutMe);
        } else if (phoneNumber.length() > 1) {
            findTextView(R.id.description).setText(phoneNumber);
        } else if (email.length() > 1) {
            findTextView(R.id.description).setText(email);
        } else {
            findTextView(R.id.description).setText("");
        }

        findTextView(R.id.personName).setText(personName);

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        //set listener
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ShowImage.class, "url", profileImage, "username", personName);
            }
        });

        //toast(profileImage);

        if (profileImage.length() > 6) {
            Glide.with(PrivateChatFriend.this).load(profileImage).apply(options).into(img);
        } else {

        }


        getSupportActionBar().hide();

        //getCacheDirectoryfor Recording for the cache directory
        CreateAppDirectory createAppDirectory = new CreateAppDirectory(this);
        mFileName = createAppDirectory.QUACK_AUDIO_PATH;
        mFileName += "/" + myId + new Date().getTime() + "audio.mp3";

        final UtilityClass utilityClass = new UtilityClass(this);

        checkPermission();

        findImageButton(R.id.recordAudio).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    toast("recording started");
                } else {
                    stopRecording();
                    upLoadAudio();
                    toast("recording stopped");
                }
                return false;
            }
        });


    }

    public void sendMessage(View view) {

        DatabaseReference db = fdb.getReference("users");
        String id = db.push().getKey();
        String message = findEditText(R.id.message).getText().toString();
        findEditText(R.id.message).setText("");
        //chats.add(new ChatMessages(myId,url,"I am the reply", CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(),""));
        ChatMessages mine = new ChatMessages(myId, "", message, CreateAppDirectory.MEDIA_TYPE_TExT, new Date(), "");

        db.child(myId).child("chats").child(personId).child(id).setValue(mine).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        db.child(personId).child("chats").child(myId).child(id).setValue(mine).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });

        db.child(myId).child("chatsList").child(personId).setValue(personId);
        db.child(personId).child("chatsList").child(myId).setValue(myId);
        //newChatsAdapter.notifyDataSetChanged();
    }

    public void loadImage(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            final String filename = FirebaseDatabase.getInstance().getReference().push().getKey() + new Date().getTime() + ".JPEG";

            Uri uri = null;
            String url = "";

            try {
                uri = intent.getData();

                url = getRealPathFromURI(uri);
                //toast("url is "+ url);
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    String pushKey = fdb.getReference().push().getKey();
                    fdb.getReference().child("ApplicationError").child(pushKey).setValue("Camera crash for User{ " + myId + " } errorlog: { " + e + " }");
                }



                //findImageView(R.id.campic).setImageBitmap(bitmapImage);
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

                // Create a child reference
                // imagesRef now points to "images"
                StorageReference imagesRef = storageRef.child("ChatUploads/" + new Date().getYear() + "/" + new Date().getMonth() + "/" + filename);

                Bitmap currentbitmap = bitmapImage;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                currentbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                        //chats.add(new ChatMessages(myId,downloadUrl.toString(),"I am the reply", CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(),filename));
                        //toast("the download url is "+ downloadUrl.toString());
                        sendMessageToDb(downloadUrl.toString(), filename);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("couldnt upload image");
                    }
                });

            } catch (Exception e) {
                log(e);
                String pushKey = fdb.getReference().push().getKey();
                fdb.getReference().child("ApplicationError").child(pushKey).setValue("gallery crash for User{ " + myId + " } errorlog: { " + e + " }");

            }
        }

            //take gallery ouput
            if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
                //toast("about to get bundle extras");

                try {
                    Bundle extras = intent.getExtras();
                    Bitmap bm = (Bitmap) extras.get("data");
                    int width = bm.getWidth();
                    int height = bm.getHeight();
                    float scaleWidth = ((float) 4000) / width;
                    float scaleHeight = ((float) 4000) / height;
                    // CREATE A MATRIX FOR THE MANIPULATION
                    Matrix matrix = new Matrix();
                    // RESIZE THE BIT MAP
                    matrix.postScale(scaleWidth, scaleHeight);

                    // "RECREATE" THE NEW BITMAP
                    Bitmap resizedBitmap = Bitmap.createBitmap(
                            bm, 0, 0, width, height, matrix, false);

                    final String filename = FirebaseDatabase.getInstance().getReference().push().getKey() + new Date().getTime() + ".JPEG";

                    String url = "";
                    try {
                        url = saveBitmapAndReturnUrl(resizedBitmap, filename);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        String pushKey = fdb.getReference().push().getKey();
                        fdb.getReference().child("ApplicationError").child(pushKey).setValue("Camera crash for User{ " + myId + " } errorlog: { " + e + " }");

                    }

                    Uri uri = Uri.parse(url);

//            toast(uri);
//            chats.add(new ChatMessages(myId,url,"I am the reply", CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(),""));
//            newChatsAdapter.notifyDataSetChanged();

                    // Create a storage reference from our app
                    StorageReference storageRef = storage.getReference();

                    // Create a child reference
                    // imagesRef now points to "images"
                    StorageReference imagesRef = storageRef.child("ChatUploads/" + new Date().getYear() + "/" + new Date().getMonth() + "/" + filename);

                    Bitmap currentbitmap = resizedBitmap;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
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
                            //toast("image is working");
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            chats.add(new ChatMessages(myId, downloadUrl.toString(), "I am the reply", CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(), filename));

                            sendMessageToDb(downloadUrl.toString(), filename);
                            //newChatsAdapter.notifyDataSetChanged();

                        }
                    });


                } catch (Exception e){
                    log(e);
                    String pushKey = fdb.getReference().push().getKey();
                    fdb.getReference().child("ApplicationError").child(pushKey).setValue("Stop Recording crash for User{ " + myId + " } errorlog: { " + e + " }");

                }
            }
    }

    private void sendMessageToDb(String url, String filename) {
//            toast("i am about to sent the mesage");
//            toast("url is : " + url);
//            toast("filename is : " + filename);

        String message = findEditText(R.id.message).getText().toString();

            DatabaseReference db = fdb.getReference("users");

            String id = db.push().getKey();
            //chats.add(new ChatMessages(myId,url,"I am the reply", CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(),""));
            ChatMessages mine = new ChatMessages(myId, url, message, CreateAppDirectory.MEDIA_TYPE_IMAGE, new Date(), filename);

            db.child(myId).child("chats").child(personId).child(id).setValue(mine).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    toast("sent image");
                }
            });

            db.child(personId).child("chats").child(myId).child(id).setValue(mine).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

            findEditText(R.id.message).setText("");
            db.child(myId).child("chatsList").child(personId).setValue(personId);
            db.child(personId).child("chatsList").child(myId).setValue(myId);
        }



    private void uploadImage(Bitmap bitmap, String filename) {


        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        StorageReference imagesRef = storageRef.child("ChatUploads/" + new Date().getMonth());

        // Child references can also take paths
        // spaceRef now points to "images/space.jpg
        // imagesRef still points to "images"
        StorageReference spaceRef = storageRef.child("quack/" + filename);

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

            }
        });

        //return the url of the savaed image
    }


    public String saveBitmapAndReturnUrl(Bitmap bitmap, String filename) throws FileNotFoundException {
        String path = CreateAppDirectory.QUACK_IMAGES_PATH;
        OutputStream fOut = null;


        File file = new File(path, filename); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        fOut = new FileOutputStream(file);


        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
        try {
            fOut.flush(); // Not really required
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close(); // do not forget to close the stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        // MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

        return path + "/" + filename;

    }


    public void backButtonClicked(View view) {
        finish();
    }

    public void ShowProfile(View view) {
        startActivity(ViewContactProfileActivity.class, "userId", personId, "isRequest", "view");
    }


    //button to record audio
    public void checkPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);

        } else {

        }
    }

    public void upLoadAudio() {
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        StorageReference str = firebaseStorage.child("audio").child(myId + new Date().getTime() + "audio.3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        //toast("filename is  "+mFileName);
        //toast("url location is  "+uri);
        str.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                toast("sent");
            }
        });

    }

    //grant permission to the recording
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_MICROPHONE:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) finish();
    }


    /*
    * CODE TO START AND STOP RECORDING
    * */
    private void startRecording() {

        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // mRecorder.setOutputFile(myId+"audio"+new Date().getTime()+".3gp");
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mFileName);

            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("I am recording tag", "prepare() failed");
            String pushKey = fdb.getReference().push().getKey();
            fdb.getReference().child("ApplicationError").child(pushKey).setValue("Starting Recording crash for User{ " + myId + " } errorlog: { " + e + " }");
        }
        try {
            mRecorder.start();
        } catch (Exception e) {
            Log.e("I am recording tag", "prepare() failed");
            String pushKey = fdb.getReference().push().getKey();
            fdb.getReference().child("ApplicationError").child(pushKey).setValue("Starting Recording crash for User{ " + myId + " } errorlog: { " + e + " }");

        }
    }

    private void stopRecording() {
        try {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();

        } catch (Exception e) {
            String pushKey = fdb.getReference().push().getKey();
            fdb.getReference().child("ApplicationError").child(pushKey).setValue("Stop Recording crash for User{ " + myId + " } errorlog: { " + e + " }");
        }

    }

    /*
    * COPY FILE FROM ONE LOCATION TO ANOTHER
    * */
    public void copyFiles(String sourcePath, String destinationPath) {
        //String sourcePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/sourceFile.3gp";
        File source = new File(sourcePath);

        //String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TongueTwister/destFile.3gp";
        File destination = new File(destinationPath);
        try {
            FileUtils.copyFile(source, destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void attachMentSelection(View view) {
        LinearLayout linearLayout = findViewById(R.id.selectionAction);
        if (linearLayout.getVisibility() == View.GONE) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    public void closeAttachmentLayout(View view) {
        LinearLayout linearLayout = findViewById(R.id.selectionAction);
        linearLayout.setVisibility(View.GONE);
    }

    public void CameraClicked(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    public void SelectAudioClick(View view) {

    }

    public void HideLayoutInChats(View view) {
    LinearLayout linearLayout = findViewById(R.id.inChatsMenuLayout);
    linearLayout.setVisibility(View.GONE);
    }

    public void showOptionsMenu(View view) {
        LinearLayout linearLayout = findViewById(R.id.inChatsMenuLayout);
        if (linearLayout.getVisibility() == View.VISIBLE){
            linearLayout.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public void ClearAllChats(View view) {
        String pushKey = fdb.getReference().push().getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Clear Chats");
        builder.setMessage("Do you want clear all chats");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("chats").child(personId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("chatsList").child(personId).removeValue();
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

    public void UnfriendUser(View view) {
        String pushKey = fdb.getReference().push().getKey();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("UnFriend");
        builder.setMessage("Do you want to unfriend "+ personName +" \n Unfriending will clear all chats ");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("chats").child(personId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("chatsList").child(personId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("friendsList").child(personId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(myId).child("following").child(personId).removeValue();


                        FirebaseDatabase.getInstance().getReference().child("users").child(personId).child("chats").child(myId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(personId).child("chatsList").child(myId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(personId).child("friendsList").child(myId).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("users").child(personId).child("following").child(myId).removeValue();
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

   /*Private Adapter for the user**/

}
