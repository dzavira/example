package com.dzavira.example;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {
        String User = "User";
        switch (view.getId()){
            case  R.id.bt_ig:

                Uri uri = Uri.parse("http://instagram.com/_u/"+User);
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                likeIng.setPackage("com.instagram.android");
                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/"+User)));
                }

                break;
            case R.id.bt_mail:
                String penerima = "dzavira.codes@gmail.com,davidmirza85@gmail.com";
                String[] list_penerima = penerima.split(",");

                String subject = "Dzavira Subject";
                String message = "Hello";

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, list_penerima);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
                break;
            case R.id.bt_tokped:

                Uri uriTkpd = Uri.parse("https://tokopedia.com/_u/"+User);
                Intent inTkpd = new Intent(Intent.ACTION_VIEW, uriTkpd);
                inTkpd.setPackage("com.tokopedia.tkpd");
                try{
                    startActivity(inTkpd);
                }
                catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://tokopedia.com/"+User)));
                }
                break;
            case R.id.bt_wa:
                try {
                    Intent intnWA = new Intent(Intent.ACTION_VIEW);
                    String nomor = "628112233445";
                    String Pesan = "Hai, ini kami dari Dzavira";
                    intnWA.setData(Uri.parse("http://api.whatsapp.com/send?phone="+nomor +"&text="+Pesan));
                    startActivity(intnWA);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(this,"WhatsApp not Installed",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}