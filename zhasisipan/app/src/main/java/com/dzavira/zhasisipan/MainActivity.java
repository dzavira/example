package com.dzavira.zhasisipan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class MainActivity extends AppCompatActivity {
    private TextView txInfo;
    private EditText TxSerial;
    private ProgressBar pbBar;
    private Button btPros, btadd;
    private String FILE_NAME = "",LOKASI_="";
    private String SERVER_ ="192.168.37.33/share/trial/", USER_="it", PASSWORD_="satu", SERVERNAME_="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txInfo = findViewById(R.id.tinfo);
        pbBar = findViewById(R.id.progressBar);
        btPros = findViewById(R.id.btsave);
        btadd = findViewById(R.id.addText);
        TxSerial = findViewById(R.id.inseral);
        FILE_NAME = "sisipan.txt";
        LOKASI_=this.getApplicationContext().getExternalFilesDir("/mrzha") + "/project/";
     //   LOKASI_ = Environment.getExternalStorageDirectory()+"/mrzha/";
        txInfo.setText("");
        btPros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Simpan cpy = new Simpan();
                    cpy.execute("mrzha");
                }
                catch (Exception e){
                    Log.e("error",e.getMessage());
                }
            }
        });

        pbBar.setVisibility(View.INVISIBLE);

    }
    public void tambahSerial(View v){
        String text = TxSerial.getText().toString();
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME,MODE_PRIVATE);
            fos.write(text.getBytes());
            TxSerial.getText().clear();
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME,
                    Toast.LENGTH_LONG).show();
            Log.e("simpan","Saved to " + getFilesDir() + "/" + FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void tulis(View v){
        String text = TxSerial.getText().toString();
        String path = LOKASI_;
        File file = new File(path);
        file.mkdirs();
        path += "cobamrzha.txt";
        OutputStream myOutput;
        try {
            myOutput = new BufferedOutputStream(new FileOutputStream(path,true));
            myOutput.write( text.getBytes() );
            myOutput.flush();
            myOutput.close();
            Toast.makeText(this, "Saved to " + path  ,
                    Toast.LENGTH_LONG).show();
            Log.e("simpan","Saved to " +path );
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "eror "+e.getMessage()  ,
                    Toast.LENGTH_LONG).show();
            Log.e("error",e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "error IO to "   ,
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    public void load(View v) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }
            TxSerial.setText(sb.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class Simpan extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            pbBar.setVisibility(View.VISIBLE);
            SERVERNAME_ = "smb://" + SERVER_;
            Log.e("samba",SERVERNAME_);
        }

        @Override
        protected void onPostExecute(String s) {
            txInfo.setText(s);
            pbBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String z = "";

            File file = new File(FILE_NAME);
            String filename = getFilesDir()+"/"+file.getName();
            Log.e("file",filename);
            NtlmPasswordAuthentication auth1 = new NtlmPasswordAuthentication(
                    SERVERNAME_, USER_, PASSWORD_);
            try {


                SmbFile sfile = new SmbFile(SERVERNAME_ + "/" + LOKASI_+"cobamrzha.txt", auth1);
                if (!sfile.exists())
                    sfile.createNewFile();
                sfile.connect();

                InputStream in = new FileInputStream(file);

                SmbFileOutputStream sfos = new SmbFileOutputStream(sfile);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    sfos.write(buf, 0, len);
                }
                in.close();
                sfos.close();

                z = "File copied successfully";
//                    String url = "smb://192.168.37.33/share/trial/"
//                            + String.valueOf("strings") + ".txt";
//
//                    NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
//                            null, "it", "satu");
//                    SmbFile sfile = new SmbFile(url, auth);
//
//                    if (!sfile.exists()) {
//                        sfile.createNewFile();
//                        z = "Success ";
//                    } else
//                        z = "Already exists";
            } catch (Exception ex) {
                // TODO: handle exception
                // z = ex.getMessage().toString();
                z = z + " " + ex.getMessage().toString();
            }
            return z;
        }
    }
}