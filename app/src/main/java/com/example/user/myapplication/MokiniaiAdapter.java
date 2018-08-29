package com.example.user.myapplication;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MokiniaiAdapter extends RecyclerView.Adapter<MokiniaiAdapter.MyViewHolder> {

    private List<Mokinys> mokiniaiList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.vardas);
        }
    }

    public MokiniaiAdapter(List<Mokinys> mokiniaiList) {
        this.mokiniaiList = mokiniaiList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mokiniai_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Mokinys mokinys = mokiniaiList.get(position);
        holder.title.setText(mokinys.getVardas());

        /*holder.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mokiniaiList.size();
    }
}

