package com.kadirseckin.gochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OzelMesajActivity extends AppCompatActivity {

    private ArrayList<String > mesajlar=new ArrayList<>();
    EditText mesajTxt;
    RecyclerView recyclerView;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private RecylerOzelMesaj adapter;

    private String mail;
    private String otherMail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ozel_mesaj);




        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        Intent intent=getIntent();
        mail=intent.getStringExtra("aliciMail");


        mesajTxt=findViewById(R.id.ozelMesajText);

        recyclerView=findViewById(R.id.ozelmesajRecyclerView);
        adapter=new RecylerOzelMesaj(mesajlar);

        RecyclerView.LayoutManager manager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        otherMail=user.getEmail();

        verileriAl();

    }

    public void ozelMesajGonderButon(View view){



        String mesaj=mesajTxt.getText().toString();
        String gondericiMail=user.getEmail();
        String aliciMail=mail;

        //Kendimize mesaj göndermimizi engelliyoruz.
        if(gondericiMail.matches(aliciMail)){
            Toast.makeText(OzelMesajActivity.this, "Kendinize mesaj gönderemezsiniz", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> mesajlarMap=new HashMap<>();

            mesajlarMap.put("mesaj",mesaj);
            mesajlarMap.put("gondericiMail",gondericiMail);
            mesajlarMap.put("aliciMail",aliciMail);
            mesajlarMap.put("zaman", FieldValue.serverTimestamp());


            db.collection("OzelMesajlar").add(mesajlarMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(OzelMesajActivity.this,"mesaj gönderildi",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(OzelMesajActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });

            mesajTxt.setText("");
        }






    }

    public void verileriAl(){


        CollectionReference reference=db.collection("OzelMesajlar");
        reference.orderBy("zaman", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    mesajlar.clear();

                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String mesaj = data.get("mesaj").toString();
                        String gonderici_Mail=data.get("gondericiMail").toString();
                        String alici_Mail=data.get("aliciMail").toString();

                        if((gonderici_Mail.matches(mail)||gonderici_Mail.matches(otherMail))&&(alici_Mail.matches(otherMail)||alici_Mail.matches(mail)))
                        {

                                mesajTxt.setText("");
                                mesajlar.add(gonderici_Mail +" :  "+mesaj);
                                System.out.println("gonderici mail: "+gonderici_Mail);
                                System.out.println("alici mail: "+alici_Mail);
                                System.out.println("mail: "+mail);
                                System.out.println("other mail: "+otherMail);



                        }


                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

}
