package com.kadirseckin.gochat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KullaniciProfilActivity extends AppCompatActivity {

    Uri fotoUri;
    ImageView profilImageView;
    Bitmap fotoBitmap;
    EditText adSoyadText;
    private Button button;

    private FirebaseFirestore db;
    private StorageReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_profil);

        button=findViewById(R.id.profilKaydetBtn);

        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();


        reference= FirebaseStorage.getInstance().getReference();

        profilImageView=findViewById(R.id.profilimageview);
        adSoyadText=findViewById(R.id.adSoyadtxt);

        profilVerileriCek();
    }

    public void profilVerileriCek(){
        CollectionReference collectionReference=db.collection("Profiller");
        collectionReference.orderBy("adSoyad").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(value!=null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String,Object> data=snapshot.getData();

                        String email=data.get("email").toString();
                        if(email.equals(auth.getCurrentUser().getEmail().toString())){
                            String adSoyad=data.get("adSoyad").toString();
                            String urlFoto=data.get("resimURL").toString();

                            if(!urlFoto.equals("")&&!adSoyad.equals("")){
                                Picasso.get().load(urlFoto).into(profilImageView);
                                adSoyadText.setText(adSoyad);
                                button.setVisibility(View.INVISIBLE);
                                profilImageView.setEnabled(false);
                                adSoyadText.setEnabled(false);

                            }
                            //resim urlsini fotoya çevirip göstermek için picasso library kullanıyorum

                        }

                    }
                }

            }
        });

    }

    public void profilFotoSec(View view){
        //eğer izin yoksa izin  iste
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        //eğer izin varsa galeri git
        else{
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }

    }


    //eğer izin varsa galeriye git 2.kez kullanıcının tıklamaması için
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1 ){
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==2 && resultCode==RESULT_OK &&data!=null){
            fotoUri=data.getData();
            //Imageview içinde gösterebilmek için bitmap'e çevirmeliyiz.

            try {
                if(Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source=ImageDecoder.createSource(this.getContentResolver(),fotoUri);
                    fotoBitmap=ImageDecoder.decodeBitmap(source);
                    profilImageView.setImageBitmap(fotoBitmap);
                }

                else{
                    fotoBitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),fotoUri);
                    profilImageView.setImageBitmap(fotoBitmap);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void profilKaydet(View view){

        //profil bilgileri boş bırakılamaz
        if(fotoUri!=null&&  !(adSoyadText.getText().toString().matches(""))){
            UUID uuid=UUID.randomUUID();
            final String resimAd="images/"+uuid+".jpg"; //aynı isim olursa hep üstüne yazar bu yüzden random id üretiyoruz
            StorageReference storageReference=reference.child(resimAd);
            storageReference.putFile(fotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference ref=FirebaseStorage.getInstance().getReference(resimAd);
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseUser user=auth.getCurrentUser();
                            //resim kaydedildiği url bunu alıp veri tabanına kaydetmeliyiz.;

                            String resimURL=uri.toString();
                            HashMap<String,String>profilBilgiler=new HashMap<>();
                            profilBilgiler.put("email",user.getEmail());
                            profilBilgiler.put("resimURL",resimURL);
                            profilBilgiler.put("adSoyad",adSoyadText.getText().toString());



                            db.collection("Profiller").add(profilBilgiler).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(KullaniciProfilActivity.this,"Güncellendi",Toast.LENGTH_LONG).show();
                                    Intent intent=new Intent(KullaniciProfilActivity.this,ChatActivity.class);
                                    intent.putExtra("isim",adSoyadText.getText().toString()); //idlerin olduğu yere kaydetmek için
                                    //belki ekranda isimleride gösterebilirim mail yerine.
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(KullaniciProfilActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                                }
                            });



                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(KullaniciProfilActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });


        }

        else{
            Toast.makeText(KullaniciProfilActivity.this,"Profil bilgileri boş bırakılamaz",Toast.LENGTH_LONG).show();
        }


    }
}
