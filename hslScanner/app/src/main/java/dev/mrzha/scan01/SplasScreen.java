package dev.mrzha.scan01;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplasScreen extends AppCompatActivity {
    private ImageView logo;
    private int splashtimeout=1600;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i1 =new  Intent(SplasScreen.this,main_manu.class);
                startActivity(i1);
                finish();
            }
        },splashtimeout);
       // Animation myanim= AnimationUtils.loadAnimation(this,R.anim.mysplash);
      //  logo.startAnimation(myanim);
    }
}
