package macrodes.lab.com.quack.firebasedata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import macrodes.lab.com.quack.R;

/**
 * Created by Botchway on 3/30/2018.
 */

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.Viewholder> {

    private ArrayList<String> list;
    private Context context;

    public StatusAdapter(ArrayList<String> list, Context context){
        this.context = context;
        this.list = list;
    }
    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_list_view,parent,false);

       return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        String item = list.get(position);
        holder.text.setText(item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView text;

        public Viewholder(View itemView) {
            super(itemView);

            text = (TextView) itemView.findViewById(R.id.name_of_user);
        }
    }
}
