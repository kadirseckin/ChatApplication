package com.kadirseckin.gochat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DigerProfiller extends AppCompatActivity {


    private ImageView imageview;
    private TextView adText,mailText;
    private String mail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diger_profiller);

        imageview=findViewById(R.id.digerProfilimageView);
        adText=findViewById(R.id.digerProfilAd);
        mailText=findViewById(R.id.digerProfilMail);


        //tıklanan kullanıcının verilerini diğer activityden alıp burada gösteriyoruz.
        Intent intent=getIntent();
         mail= intent.getStringExtra("mail");
        String url=intent.getStringExtra("url");
        String ad=intent.getStringExtra("ad");

        adText.setText(ad);
        mailText.setText(mail);
        Picasso.get().load(url).into(imageview);


    }

    public void digerMesajGonder(View view){  //özel mesaj gönderme sayfasını açacak
        Intent intent=new Intent(this,OzelMesajActivity.class);
        intent.putExtra("aliciMail",mail);
        startActivity(intent);
    }


}
