package dev.mrzha.scan01;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

public class main_manu extends AppCompatActivity {
    Dialog myDialog;
    LottieAnimationView btMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manu);
        myDialog = new Dialog(this);
        btMenu = findViewById(R.id.menu);


    }

    public void swPopup(View v){
        try {
            TextView txtClose;
            Button btndev, btnscan;
          //  ImageView p_ultra;
            myDialog.setContentView(R.layout.custompopup);
            txtClose = (TextView) myDialog.findViewById(R.id.t_close);
            btndev = (Button) myDialog.findViewById(R.id.b_dev);
            btnscan = (Button) myDialog.findViewById(R.id.b_scan);

            btnscan.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i = new Intent(main_manu.this,f_scan.class); // fscan
                   myDialog.dismiss();
                   startActivity(i);
               }
           });


            btndev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(main_manu.this,devscan.class);
                    myDialog.dismiss();
                    startActivity(i);
                }
            });
            txtClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myDialog.dismiss();
                }
            });
            myDialog.show();

        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
