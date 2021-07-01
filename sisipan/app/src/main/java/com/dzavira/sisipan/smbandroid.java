package com.dzavira.sisipan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class smbandroid extends AppCompatActivity {
Button btSave;
EditText edInputData;
TextView txNotif;
String LOKASI_="" ,
        SERVER_="smb://192.168.37.33/share/",
        USER_="it",
        PASSWORD_="satu";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smbandroid);
        btSave = findViewById(R.id.btnSave);
        edInputData = findViewById(R.id.edData);
        txNotif = findViewById(R.id.txinfo);
        txNotif.setText("");
        LOKASI_ = this.getApplicationContext().getExternalFilesDir("/mrzha")+"/";
    }

    public void CreateFile(View v){
        File file_ = new File(LOKASI_);
        String data_ = edInputData.getText().toString();
        file_.mkdirs();
        LOKASI_ += "Data_Mrzha.txt";
        OutputStream OsFile;
        try{
            OsFile = new BufferedOutputStream(new FileOutputStream(LOKASI_,true));
            OsFile.write( data_.getBytes() );
            OsFile.flush();
            OsFile.close();
        }
        catch (Exception Ex){
            txNotif.setText(Ex.getMessage());
            return;
        }
    //      untuk proses copy file ke server
        CopyKeServer CpyServer = new CopyKeServer();
        CpyServer.execute("");
    }
    private class CopyKeServer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
           txNotif.setText("sedang proses..");
        }
        @Override
        protected void onPostExecute(String st) {
            txNotif.setText(st);
        }
        @Override
        protected String doInBackground(String... strings) {
            String notif = "";
            File file = new File(LOKASI_);
            String filename = file.getName();
            NtlmPasswordAuthentication auth1 = new NtlmPasswordAuthentication(
                    SERVER_, USER_, PASSWORD_);
            try {
                SmbFile sbfile = new SmbFile(SERVER_ + "/" +filename, auth1);
                if (!sbfile.exists())
                    sbfile.createNewFile();
                sbfile.connect();
                InputStream inStream = new FileInputStream(file);
                SmbFileOutputStream smbFOS = new SmbFileOutputStream(sbfile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = inStream.read(buf)) > 0) {
                    smbFOS.write(buf, 0, len);
                }
                inStream.close();
                smbFOS.close();
                notif = "File Berhasil tersimpan ke Server SMB";
            } catch (Exception ex) {
                notif = notif + " " + ex.getMessage();
            }
            return notif;
        }
    }
}