package macrodes.lab.com.quack.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Botchway on 3/20/2018.
 */

public class CreateAppDirectory
{
private Context context;

    public CreateAppDirectory(Context context) {
        this.context = context;
    }


    public static final String  QUACK_PATH = Environment.getExternalStorageDirectory()+"/Quack";
    public static final String  QUACK_IMAGES_PATH = Environment.getExternalStorageDirectory()+"/Quack/Images";
    public static final String  QUACK_AUDIO_PATH = Environment.getExternalStorageDirectory()+"/Quack/Audio";
    public static final String  QUACK_VIDEOS_PATH = Environment.getExternalStorageDirectory()+"/Quack/Videos";
    public static final String  QUACK_DOCUMENTS_PATH = Environment.getExternalStorageDirectory()+"/Quack/Videos";


    public static final String  MEDIA_TYPE_AUDIO = "AUDIO";
    public static final String  MEDIA_TYPE_VIDEO= "VIDEO";
    public static final String  MEDIA_TYPE_IMAGE= "IMAGE";
    public static final String  MEDIA_TYPE_TExT = "TEXT";

    public static final String  STATUS_MEDIA_TYPE_PICYURE = "PICTURE";
    public static final String  STATUS_MEDIA_TYPE_TEXT= "TEXT";
    public static final String  STATUS_MEDIA_TYPE_VIDEO= "VIDEO";


    public void createQuackDirectory(String path) {
        //Create Folder
        File folder = new File(path);
        folder.mkdirs();
    }

    public void initFolders(){
        createQuackDirectory(QUACK_PATH);
        createQuackDirectory(QUACK_IMAGES_PATH);
        createQuackDirectory(QUACK_AUDIO_PATH);
        createQuackDirectory(QUACK_VIDEOS_PATH);
        createQuackDirectory(QUACK_DOCUMENTS_PATH);
    }

}
