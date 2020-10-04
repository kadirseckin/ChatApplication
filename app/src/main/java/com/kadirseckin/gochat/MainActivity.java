package com.kadirseckin.gochat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Bu ekranda kullanıcı girişi ve kayıt olma işlemlerini yapacağım.

public class MainActivity extends AppCompatActivity {


    EditText emailText,parolaText;
    private FirebaseAuth firebaseAuth; //oturum işlemlerini yapmak için firebaseAuth
    private FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailText=findViewById(R.id.emailText);
        parolaText=findViewById(R.id.parolaText);

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseUser=firebaseAuth.getCurrentUser();//o anki güncel kullanıcıyı alıyoruz
        if(firebaseUser!=null){
            // daha önceden giriş yapan kullanıcı varsa uygulamayı tekrar açtığımızda tekrar giriş yapmamak için
            Intent intent=new Intent(MainActivity.this,ChatActivity.class);
            startActivity(intent);
        }

    }


    //Kullanıcı girisini burada yapılacak.
    public void girisYap(View view) {
        String email = emailText.getText().toString();
        String parola = parolaText.getText().toString();


        //Eğer mail ve parola boş bırakılırsa program çökmesin hata ekrana yazdırılsın.
        if (email.matches("") || parola.matches("")) {
            Toast.makeText(MainActivity.this, "Email veya parola boş bırakılamaz", Toast.LENGTH_LONG).show();
        } else {
            //email ve şifre ile giriş yapma işlemi
            firebaseAuth.signInWithEmailAndPassword(email, parola).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) { //Giris yapma işlemi başarılı ise activity değiştir
                    Toast.makeText(MainActivity.this, "Giris basarili", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, ChatActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);





                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { //giris yapma işlemi başarısız ise hatayı ekrana yazdır
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }
            });

        }
    }


    //Kayıt olma islemi burada yapılacak.
    public void kayitOl(View view){


        String email=emailText.getText().toString();
        String parola=parolaText.getText().toString();


        //Eğer mail ve parola boş bırakılırsa program çökmesin hata ekrana yazdırılsın.
        if(email.matches("")||parola.matches(""))
        {
            Toast.makeText(MainActivity.this,"Email veya parola boş bırakılamaz",Toast.LENGTH_LONG).show();
        }
        else{
            //email ve şifre ile kayıt olma işlemi
            firebaseAuth.createUserWithEmailAndPassword(email,parola).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) { //kayıt olma işlermi başarılı ise activity değiştir
                    Toast.makeText(MainActivity.this,"Kayit basarili",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(MainActivity.this,KullaniciProfilActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(intent);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) { //kayıt olma işlemi başarısız ise hatayı ekrana yazdır
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
        }


    }
}
