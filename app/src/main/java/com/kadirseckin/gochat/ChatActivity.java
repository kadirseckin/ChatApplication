package com.kadirseckin.gochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    RecyclerView recyclerView;
    RecylerAdapter adapter;
    EditText mesajEditText;

    private FirebaseUser user;
    private FirebaseFirestore db;


    private ArrayList<String >mesajlar=new ArrayList<>();
    private ArrayList<String> idler=new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView=findViewById(R.id.chatRecyclerView); //chat activity içinde recycler
        adapter=new RecylerAdapter(mesajlar);

        mesajEditText=findViewById(R.id.chatMesajText);//mesajı yazıp göndereceğimiz yer

        //recycler view ile adapteri bağlıyoruz
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();


        //firebase database işlemleri için
        db = FirebaseFirestore.getInstance();

        verileriAl();

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                
                .init();


        //player id ile mesajları ekranda gösterebiliriz.

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, String registrationId) {
                //aynı idleri tekrar tekrar eklememek için tüm idleri çekip diziye atacağım oradan kontrol edeceğim

                CollectionReference reference=db.collection("KullaniciIDler");
                reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                Map<String, Object> data = snapshot.getData();
                                String id = data.get("id").toString();

                                idler.add(id);

                            }

                            //eğer aynı id varsa tekrar kaydetmiyoruz
                            if(!idler.contains(userId)){
                                HashMap<String,Object> veriler=new HashMap<>();

                                Intent intent=getIntent();
                                String isim=intent.getStringExtra("isim");

                                //id ve mailleri eşleştiriyoruz.Özel mesajlarda buna göre bildirimleri özelleştireceğim
                                veriler.put("id",userId);
                                veriler.put("email",user.getEmail());
                                veriler.put("adsoyad",isim);

                                db.collection("KullaniciIDler").add(veriler);
                            }
                        }
                    }
                });


                



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
            Intent intent=new Intent(ChatActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);


        }
        else if(item.getItemId()==R.id.menu_profil){ //eğer profile tıklandıysa profil activity git
            Intent intent=new Intent(ChatActivity.this,KullaniciProfilActivity.class);

            startActivity(intent);

        }

        else if(item.getItemId()==R.id.menu_kullanicilar){
            Intent intent=new Intent(ChatActivity.this,Kullanicilar.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }




        else if(item.getItemId()==R.id.menu_genelsohbet){
            Intent intent=new Intent(ChatActivity.this,ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    public void mesajGonder(View view){
        final String mesaj=mesajEditText.getText().toString();
        FirebaseUser user=auth.getCurrentUser();
        String user_mail=user.getEmail();



        HashMap<String,Object> mesajlarMap=new HashMap<>();

        mesajlarMap.put("mesaj",mesaj);
        mesajlarMap.put("email",user_mail);
        mesajlarMap.put("zaman", FieldValue.serverTimestamp());


        db.collection("Mesajlar").add(mesajlarMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(ChatActivity.this,"mesaj gönderildi",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

        mesajEditText.setText("");


        //Mesaj yollandığında bildirim gitmesi için burayı yazıyorum.
        CollectionReference reference=db.collection("KullaniciIDler");
        reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();

                        String id=data.get("id").toString();


                        //bildirim yollama işlemi
                        //content-hangi kullancılara-handler or not
                        try {
                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'Genel Sohbet: "+mesaj+"'}, 'include_player_ids': ['" + id + "']}"), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });



    }

    public void verileriAl(){
        CollectionReference reference=db.collection("Mesajlar");
        reference.orderBy("zaman",Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    mesajlar.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String mesaj = data.get("mesaj").toString();
                        String mail=data.get("email").toString();


                        mesajlar.add(mail+" : "+mesaj);

                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

}
