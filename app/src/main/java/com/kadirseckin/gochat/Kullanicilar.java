package com.kadirseckin.gochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class Kullanicilar extends AppCompatActivity {
    ListView kullanicilar_listview;
    ArrayList<String>kullanici_mailler;
    ArrayList<String>kullanici_isimler;
    ArrayList<String>kullanici_fotoUrller;


    private FirebaseAuth auth;
    private ArrayAdapter adapter;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanicilar);

        db= FirebaseFirestore.getInstance();

        kullanici_fotoUrller=new ArrayList<>();
        kullanici_isimler=new ArrayList<>();
        kullanici_mailler=new ArrayList<>();

        kullanicilar_listview=findViewById(R.id.kullanicilarListView);

        isimleriCek();

       adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,kullanici_isimler);
        kullanicilar_listview.setAdapter(adapter);

        kullanicilar_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String mail=kullanici_mailler.get(position);
                String ad=kullanici_isimler.get(position);
                String url=kullanici_fotoUrller.get(position);

                Intent intent=new Intent(Kullanicilar.this,DigerProfiller.class);
                intent.putExtra("mail",mail);
                intent.putExtra("ad",ad);
                intent.putExtra("url",url);
                startActivity(intent);

            }
        });




    }

    public void isimleriCek(){
        db.collection("Profiller").orderBy("adSoyad").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String isim=data.get("adSoyad").toString();
                        String email=data.get("email").toString();
                        String foto_URL=data.get("resimURL").toString();

                        kullanici_isimler.add(isim);
                        kullanici_fotoUrller.add(foto_URL);
                        kullanici_mailler.add(email);

                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    //sağ üstte oluşturduğumuz menü ile bu aktiviteyi senkronize etmek için
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.ayar_menu,menu);//menüyü başladık

        return super.onCreateOptionsMenu(menu);

    }


    //sağ üstteki menüden seçim yaptığımızda neler olacağını bu metod altında yapacağım.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_cikis){ //eğer çıkışa tıklandıysa ilk activity geri dön ve çıkış yap.

            auth.signOut();
            Intent intent=new Intent(Kullanicilar.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        }
        else if(item.getItemId()==R.id.menu_profil){ //eğer profile tıklandıysa profil activity git
            Intent intent=new Intent(Kullanicilar.this,KullaniciProfilActivity.class);

            startActivity(intent);

        }

        else if(item.getItemId()==R.id.menu_kullanicilar){
            Intent intent=new Intent(Kullanicilar.this,Kullanicilar.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }





        else if(item.getItemId()==R.id.menu_genelsohbet){
            Intent intent=new Intent(Kullanicilar.this,ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
