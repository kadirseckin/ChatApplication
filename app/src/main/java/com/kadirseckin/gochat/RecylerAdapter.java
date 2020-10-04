package com.kadirseckin.gochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//recyclerview ile recycler view rowu bağlayacağız.

public class RecylerAdapter extends RecyclerView.Adapter<RecylerAdapter.Holder> {

    private ArrayList<String> mesajlar; //chat activityden gelen arraylisti buna atayıp işlem yapacağız.


    public RecylerAdapter(ArrayList<String> mesajlar) {
        this.mesajlar = mesajlar;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row,parent,false );
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String mesaj=mesajlar.get(position); //recycler viewde hangi mesaj hangi sıradaysa onu alıyoruz.
        holder.recyclerText.setText(mesaj); //mesajı yazdırıyoruz.
        //bu recyler text aşağıdaki Holder classı içindeki recyclertext

    }

    @Override
    public int getItemCount() {
        return mesajlar.size();
    }


    public class Holder extends  RecyclerView.ViewHolder{
       //burada bağlama işlermini yapıyoruz.

        public TextView recyclerText;  //recycler view row içindeki texti alacağız.
        public Holder(@NonNull View itemView) {
            super(itemView);
            recyclerText=itemView.findViewById(R.id.recyclerText);
        }
    }


}
