package macrodes.lab.com.quack.utils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Botchway on 3/16/2018.
 */

public class UtilityClass {
    private Context context;
    private MediaRecorder mRecorder = null;

    public UtilityClass(Context context) {
        this.context = context;
    }

    private void initRecorder(){

    }

    public void startRecording(String mFileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("RECORDER ERROR", "prepare() failed");
        }

        mRecorder.start();
    }

    public MediaRecorder stopRecording() {
        mRecorder.stop();
        mRecorder.release();

        MediaRecorder newRef = mRecorder;
        mRecorder = null;

        return newRef;
    }



    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
