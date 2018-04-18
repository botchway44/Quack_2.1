package macrodes.lab.com.quack;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import macrodes.lab.com.quack.firebasedata.StatusAdapter;

/**
 * Created by Botchway on 3/16/2018.
 */

public class StatusFragment extends Fragment{
private RecyclerView recyclerView;
private RecyclerView.Adapter recyclerViewAdapter;
private ArrayList<String> listItems;
    private Context context;
    public StatusFragment(){

    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.status_fragment,container,false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.status_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setClickable(true);


        listItems = new ArrayList<>();
        listItems.add("ham");
        listItems.add("ham1");
        listItems.add("ham2");

          recyclerViewAdapter = new StatusAdapter(listItems,getActivity());
          recyclerView.setAdapter(recyclerViewAdapter);

        return rootView;
    }

}
