package macrodes.lab.com.quack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import macrodes.lab.com.quack.utils.CreateAppDirectory;
import stanford.androidlib.AutoSaveFields;
import stanford.androidlib.SimpleActivity;
import stanford.androidlib.SimplePreferences;
@AutoSaveFields
public class ShowImage extends SimpleActivity {
    private String url;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        getSupportActionBar().hide();


        url = getStringExtra("url");
        name = getStringExtra("username");

        ImageView img = findImageView(R.id.showimagebox);
        if (url.length() > 1) {
            Glide.with(this).load(url).into(img);
        } else {
            toast("cannot load image");
        }

        findTextView(R.id.user_name).setText(name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void goBackClicked(View view) {
        finish();
    }

    //pop up a screen asking to save to gallery
    public void PopUpSaveImage(View view) {
        LinearLayout linearLayout = findViewById(R.id.saveLayout);
        linearLayout.setVisibility(View.VISIBLE);
    }

    public void hideLayout(View view) {
        LinearLayout linearLayout = findViewById(R.id.saveLayout);
        linearLayout.setVisibility(View.GONE);
    }

    public void SaveImageToFolder(View view) {
        Picasso.with(this)
                .load(url)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        //take image path
                        String path = saveImage(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });



    }

    private String saveImage(Bitmap bitmap) {




        String savedImagePath = null;
        String fileName =FirebaseDatabase.getInstance().getReference().push().getKey();


        String imageFileName = "JPEG_"+fileName + ".JPEG";

        String path = CreateAppDirectory.QUACK_IMAGES_PATH;
        OutputStream fOut = null;

        File file = new File(path, imageFileName); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


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
           galleryAddPic(path + "/" + imageFileName);
        toast("Saved to gallery");
        return path + "/" + imageFileName;


    }


    private void galleryAddPic(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        sendBroadcast(mediaScanIntent);
    }

}
