package com.kadirseckin.gochat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecylerOzelMesaj extends RecyclerView.Adapter<RecylerOzelMesaj.Holder2> {

    private ArrayList<String> mesajlar; //chat activityden gelen arraylisti buna atayıp işlem yapacağız.


    public RecylerOzelMesaj(ArrayList<String> mesajlar) {
        this.mesajlar = mesajlar;
    }

    @NonNull
    @Override
    public Holder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recylerview_ozelmesaj,parent,false );
        return new Holder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder2 holder, int position) {
        String mesaj=mesajlar.get(position); //recycler viewde hangi mesaj hangi sıradaysa onu alıyoruz.
        holder.ozelRecylerText.setText(mesaj); //mesajı yazdırıyoruz.
        //bu recyler text aşağıdaki Holder classı içindeki recyclertext
    }

    @Override
    public int getItemCount() {
        return mesajlar.size();
    }


    public class Holder2 extends  RecyclerView.ViewHolder{
        //burada bağlama işlermini yapıyoruz.

        public TextView ozelRecylerText;  //recycler view row içindeki texti alacağız.
        public Holder2(@NonNull View itemView) {
            super(itemView);
            ozelRecylerText=itemView.findViewById(R.id.recyclerTextOzelMesaj);
        }
    }
}
