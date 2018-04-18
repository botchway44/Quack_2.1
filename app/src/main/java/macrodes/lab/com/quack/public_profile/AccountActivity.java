package macrodes.lab.com.quack.public_profile;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Date;

import macrodes.lab.com.quack.LoginActivity;
import macrodes.lab.com.quack.R;
import macrodes.lab.com.quack.ShowImage;
import macrodes.lab.com.quack.firebasedata.ChatMessages;
import macrodes.lab.com.quack.utils.CreateAppDirectory;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class AccountActivity extends SimpleActivity {

    private static final int REQUEST_CAMERA = 12;
    private static final int GALLERY_INTENT = 134;
    String local_Image_Storage_Path = Environment.getExternalStorageDirectory()+"/Quack/Profile";

    //firebase Init
    private DatabaseReference dbref;
    private FirebaseDatabase fdb;
    private FirebaseStorage storage;
    private String tempUri;
    private View view;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        fdb = FirebaseDatabase.getInstance();
        dbref = fdb.getReference("users");

        userId = SimplePreferences.with(AccountActivity.this).getSharedString("account", "userId");


    }

    @Override
    protected void onResume() {
        super.onResume();
        String Username = SimplePreferences.with(AccountActivity.this).getSharedString("account", "username", "username");
        String phonenumber = SimplePreferences.with(AccountActivity.this).getSharedString("account", "phoneNumber", "Phone Number");
        String aboutme = SimplePreferences.with(AccountActivity.this).getSharedString("account", "aboutMe", "About me");
        String userEmail = SimplePreferences.with(AccountActivity.this).getSharedString("account", "email", "Email");

        findTextView(R.id.userEmail).setText(userEmail);
        findTextView(R.id.username).setText(Username);
        findTextView(R.id.aboutmeBtn).setText(aboutme);
        findTextView(R.id.phoneNumberBtn).setText(phonenumber);

        String url = SimplePreferences.with(AccountActivity.this).getSharedString("account", "profileImage", "");
        //"https://firebasestorage.googleapis.com/v0/b/quack-42785.appspot.com/o/wallpaper-for-facebook-profile-photo-738967.jpg?alt=media&token=695030f9-985e-49c4-b9be-5285d758fe0b");

        final ImageView userImage = findImageView(R.id.userImage);

        if (url.length() > 1) {
            Glide.with(this).load(url).into(userImage);
            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else {

            String userId = SimplePreferences.with(AccountActivity.this).getSharedString("account","userId");

            dbref.child(userId).child("account").child("userInfo").child("profileImage").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                   double count = dataSnapshot.getChildrenCount();
                   if(count == 1){
                       Iterable<DataSnapshot> urls = dataSnapshot.getChildren();
                       ArrayList<String> url = new ArrayList<>();

                       for(DataSnapshot data : urls){
                           url.add(data.toString());
                       }

                       Glide.with(AccountActivity.this).load(url.get(0)).into(userImage);
                       userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                   }else {
                       Glide.with(AccountActivity.this).load(R.raw.quack_user).into(userImage);
                       userImage.setScaleType(ImageView.ScaleType.CENTER);
                       userImage.setBackgroundColor(Color.parseColor("#FFFFFF"));
                   }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    double count = dataSnapshot.getChildrenCount();
                    if(count == 1){
                        Iterable<DataSnapshot> urls = dataSnapshot.getChildren();
                        ArrayList<String> url = new ArrayList<>();

                        for(DataSnapshot data : urls){
                            url.add(data.toString());
                        }

                        Glide.with(AccountActivity.this).load(url.get(0)).into(userImage);
                        userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    }else {
                        Glide.with(AccountActivity.this).load(R.raw.quack_user).into(userImage);
                        userImage.setScaleType(ImageView.ScaleType.CENTER);
                        userImage.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
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


    public void changenameClicked(View view) {
        startActivity(ChangeNameActivity.class);
    }

    public void phoneNumberClicked(View view) {
        startActivity(ChangePhoneNumber.class);
    }

    public void aboutClicked(View view) {
        startActivity(ChangeAboutMe.class);
    }

    public void backButtonClicked(View view) {
        finish();
    }

//
//    public void changePictureClicked(View view) {
//        this.view = view;
//        requestPermission("android.permission.CAMERA");
//
//        if(hasPermission("android.permission.CAMERA")) {
//            SimpleCamera.with(this).takePhoto();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){
            Bundle extras = intent.getExtras();
            Bitmap bm = (Bitmap) extras.get("data");
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) 8000) / width;
            float scaleHeight = ((float) 8000) / height;

            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);

            String filename = new Date().getTime()+userId+".jpg";
            String url =  uploadImage(resizedBitmap,filename);

        }

            if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            final String filename = FirebaseDatabase.getInstance().getReference().push().getKey() + new Date().getTime() + ".JPEG";
            final String newfile = CreateAppDirectory.QUACK_IMAGES_PATH + "/" + filename;
            Uri uri = null;
            String url = "";
            try {
                uri = intent.getData();
                //toast(uri);
                url = getRealPathFromURI(uri);
            } catch (Exception e) {
                log(e);
            }

            Bitmap bitmapImage = null;
            try {
                bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //toast(bitmapImage.toString());
            String Gottenurl =  uploadImage(bitmapImage,filename);

        }
    }

    public void changePictureClicked(View view) {
        this.view = view;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
    }



    //Sets up the image to be taken
    @Override
    public void onPhotoReady(Bitmap bm) {
        super.onPhotoReady(bm);

        String filename = new Date().getTime() +".png";
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) 8000) / width;
        float scaleHeight = ((float) 8000) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);

        String url =  uploadImage(resizedBitmap,filename);

    }

    private String uploadImage(final Bitmap bitmap, final String filename){


        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        StorageReference imagesRef = storageRef.child(filename);

        // Child references can also take paths
        // spaceRef now points to "images/space.jpg
        // imagesRef still points to "images"
        StorageReference spaceRef = storageRef.child("quack");

// While the file names are the same, the references point to different files
        imagesRef.getName().equals(spaceRef.getName());    // true
        imagesRef.getPath().equals(spaceRef.getPath());    // false

            try {
                Bitmap currentbitmap = bitmap;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // Handle unsuccessful uploads
                        //toast("try again");
                        Snackbar.make(view, "Try again", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        setUri(downloadUrl.toString() + "");
                        saveImageToFolder(bitmap, filename);
                        doThisIfUploadIsSuccessful(downloadUrl.toString() + "");

                    }
                });

            }catch (Exception e){
                String pushKey = fdb.getReference().push().getKey();
                fdb.getReference().child("ApplicationError").child(pushKey).setValue("Setting profile photo for  User{ " + userId + " } errorlog: { " + e + " }");

                Snackbar.make(view, "Try again", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        //return the url of the savaed image
        return tempUri;
    }


    public void setUri(String uri){
        tempUri = uri;
    }


    public void doThisIfUploadIsSuccessful(String url){

        String userId = SimplePreferences.with(AccountActivity.this).getSharedString("account","userId");

        if(userId.length() < 1){
            startActivity(LoginActivity.class);
        }

        //TODO add a user id
        dbref.child(userId).child("account").child("userInfo").child("profileImage").setValue(url);


        ImageView userImage = findImageView(R.id.userImage);

        SimplePreferences.with(AccountActivity.this).setShared("account","profileImage",url);
        String geturl = SimplePreferences.with(AccountActivity.this).getSharedString("account","profileImage");
        if (geturl.length() > 1) {
            try {
                Glide.with(this).load(geturl).into(userImage);
            }catch (Exception e){
                String pushKey = fdb.getReference().push().getKey();
                fdb.getReference().child("ApplicationError").child(pushKey).setValue("Loading profile for user for  User{ " + userId + " } errorlog: { " + e + " }");
            }

        }
        //toast("this is the url : "+tempUri);
//        //toast("Profile Image Saved");
//        Snackbar.make(view, "Profile Image Saved", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
    }



    public void saveImageToFolder(Bitmap bitmap, String filename){
        String path = CreateAppDirectory.QUACK_IMAGES_PATH+"/profile";

        File file = new File(path, filename);

        try {
            OutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85,fout);
            fout.flush();
            fout.close();

            Snackbar.make(view, "Image Saved To Disk", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
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

    public void profileImageClicked(View view) {
        String url = SimplePreferences.with(AccountActivity.this).getSharedString("account","profileImage");
        String myUsername = SimplePreferences.with(AccountActivity.this).getSharedString("account","username");

        startActivity(ShowImage.class,"url",url,"username",myUsername);
    }

    public void hideLayoutSelector(View view) {
        this.view = view;
            LinearLayout linearLayout = findViewById(R.id.ImageSelectorLayout);
            if (linearLayout.getVisibility() == View.VISIBLE){
                linearLayout.setVisibility(View.GONE);
            }else {
                linearLayout.setVisibility(View.VISIBLE);
            }

    }

    public void SelctFromGalleryClicked(View view) {
        this.view = view;
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


    public void RemovePhoto(View view) {
        //TODO add a user id
        dbref.child(userId).child("account").child("userInfo").child("profileImage").setValue("https://firebasestorage.googleapis.com/v0/b/quack-42785.appspot.com/o/quack%2Fquack_user.png?alt=media&token=f100f157-3505-40e6-a4e0-da0e78bc79d2");
        Snackbar.make(view, "Profile image removed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
