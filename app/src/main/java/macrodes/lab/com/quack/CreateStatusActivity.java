package macrodes.lab.com.quack;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Date;

import macrodes.lab.com.quack.firebasedata.StatusMessage;
import macrodes.lab.com.quack.utils.CreateAppDirectory;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;

public class CreateStatusActivity extends SimpleActivity {
    private static final int GALLERY_INTENT = 255;
    private View view;
    private String myId;
    private static final int REQUEST_CAMERA = 122;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_status);
            getSupportActionBar().hide();

        myId = SimplePreferences.with(CreateStatusActivity.this).getSharedString("account","userId");

    }


    public void CameraStatusClicked(View view) {
        this.view = view;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode == REQUEST_CAMERA && resultCode == RESULT_OK){

            try {

                Bundle extras = intent.getExtras();
                Bitmap bm = (Bitmap) extras.get("data");

                Intent intentSender = new Intent(this,CreateStatusActivity.class);
                intentSender.putExtra("Bitmap",bm);
                intentSender.putExtra("dataUrl","");

                startActivity(CreateStatusActivity.class,"Bitmap",bm);


            }catch (Exception e){
                String pushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                FirebaseDatabase.getInstance().getReference().child("ApplicationError").child(pushKey).setValue("cant send intent to create status activity intent for User{ " + myId + " } errorlog: { " + e + " }");
            }

        }

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            try {
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
                //String Gottenurl =  uploadImage(bitmapImage,filename);
                Intent intentSender = new Intent(this,CreateStatusActivity.class);
                intentSender.putExtra("Bitmap",bitmapImage);
                intentSender.putExtra("dataUrl",uri);

                startActivity(CreateStatusActivity.class,"Bitmap",bitmapImage,"url",uri.toString(),intentSender);

            }catch (Exception e){
                String pushKey = FirebaseDatabase.getInstance().getReference().push().getKey();
                FirebaseDatabase.getInstance().getReference().child("ApplicationError").child(pushKey).setValue("cant send intent to create status activity for User{ " + myId + " } errorlog: { " + e + " }");

            }
        }
    }
//TODO END HERE


    private String getRealPathFromURI(Uri contentUri) {

        String[] proj = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
