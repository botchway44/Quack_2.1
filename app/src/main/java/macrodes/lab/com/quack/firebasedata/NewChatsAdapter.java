package macrodes.lab.com.quack.firebasedata;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import macrodes.lab.com.quack.R;
import macrodes.lab.com.quack.ShowImage;
import macrodes.lab.com.quack.utils.CreateAppDirectory;
import stanford.androidlib.SimplePreferences;

/**
 * Created by Botchway on 3/20/2018.
 */

public class NewChatsAdapter extends BaseAdapter {

    private ArrayList<ChatMessages> chatMessages;
    private Context context;
    private String personUserName;
    private String myUserName;
    private String myId;

    public NewChatsAdapter(Context context, ArrayList<ChatMessages> chatMessages, String myUserName, String personUserName, String myId){
        this.chatMessages = chatMessages;
        this.context = context;
        this.myUserName = myUserName;
        this.personUserName = personUserName;
        this.myId = myId;
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public ChatMessages getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.chat_messenger_view,null);

        final ChatMessages chatMessages = getItem(position);

            //final TextView authorView = (TextView) convertView.findViewById(R.id.msgAuthor);
            ImageView img = (ImageView) convertView.findViewById(R.id.msgImg);
            TextView message = (TextView) convertView.findViewById(R.id.msgView);
//        VideoView vv = (VideoView) convertView.findViewById(R.id.videoView);
//        ImageButton play = (ImageButton) convertView.findViewById(R.id.playAudio);


        String author = "";
        //set the name of the author
        if(chatMessages.getUserId() == myId){
           author  =  myUserName;
        }else {
            author = personUserName;
        }
        //authorView.setText(author);

        //Toast.makeText(context,"chat "+chatMessages.getMessage()+" "+chatMessages.getIsMediaType(),Toast.LENGTH_LONG).show();

        RequestOptions options = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);


        String userId = SimplePreferences.with(context).getSharedString("account","userId");

        //if no user id is found take the user to the login page


        //if the user Id is equal to mine
        //set the gravity to the right
        //toast("user id is "+userId+"\n and chat id is "+chat.getUserId()+"\n and chat position is "+position);

        if(chatMessages.getUserId().toString().equals(myId)){
            LinearLayout parent_layout = (LinearLayout) convertView.findViewById(R.id.parentChatlayout);
            parent_layout.setGravity(Gravity.RIGHT);

           //authorView.setText(myUserName);
            author = myUserName;

        }else {
            LinearLayout parent_layout = (LinearLayout) convertView.findViewById(R.id.parentChatlayout);
            parent_layout.setGravity(Gravity.LEFT);
            //authorView.setText(personUserName);
            author = personUserName;
        }


        if(chatMessages.getIsMediaType().equals(CreateAppDirectory.MEDIA_TYPE_TExT)){
            //load the image from the url with picaso
            message.setText(chatMessages.getMessage());
            //play.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
            //vv.setVisibility(View.GONE);

        }else if(chatMessages.getIsMediaType().equals(CreateAppDirectory.MEDIA_TYPE_IMAGE)){
            //message.setText(chatMessages.getMessage());
            message.setVisibility(View.GONE);
            //play.setVisibility(View.GONE);
            //vv.setVisibility(View.GONE);
            //Glide.with(context).load(Uri.parse(chatMessages.getUrl())).into(img);
            img.setVisibility(View.VISIBLE);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(context).load(chatMessages.getUrl()).into(img);
            //Glide.with(context).load(chatMessages.getUrl()).apply(options).into(img);


        }else if(chatMessages.getIsMediaType()  == CreateAppDirectory.MEDIA_TYPE_AUDIO){


        }else if(chatMessages.getIsMediaType()  == CreateAppDirectory.MEDIA_TYPE_VIDEO){


        }



        String time = ""+chatMessages.getTime().getHours()+" : "+chatMessages.getTime().getMinutes();
        TextView timeView = convertView.findViewById(R.id.time);
        timeView.setText(time);

        final String newAuthor = author;
        //image listeners
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ShowImage.class);
                //"url",url,"username",myUsername
                intent.putExtra("url",chatMessages.getUrl());
                intent.putExtra("offline",chatMessages.getOfflineUrl());
                intent.putExtra("username",newAuthor);
                context.startActivity(intent);
            }
        });

        return convertView;
    }


}
