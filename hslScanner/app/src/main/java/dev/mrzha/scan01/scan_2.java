package dev.mrzha.scan01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class scan_2 extends AppCompatActivity {
    private TextView txtView;
    private SurfaceView srfView;
    private QREader qrReader;
    private String Hasil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_2);


beri_akses();

    }
    private void setupCamera() {
        txtView=(TextView)findViewById(R.id.code_info);
        final ToggleButton btn_on_off = (ToggleButton)findViewById(R.id.btn_togle);
        btn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qrReader.isCameraRunning())
                {
                    btn_on_off.setChecked(false);
                    qrReader.stop();
                    txtView.setText("");
                }
                else{
                    btn_on_off.setChecked(true);
                    qrReader.start();
                }
            }
        });
        srfView = (SurfaceView)findViewById(R.id.camera_view);
        setupQReader();

    }
    private void beri_akses() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                      //  Toast.makeText(getApplicationContext(), "Permission Granted ", Toast.LENGTH_LONG).show();
                            setupCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(scan_2.this, "Enable Permission Guys", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
    private void setupQReader() {
        try {
            qrReader = new QREader.Builder(this, srfView, new QRDataListener() {
                @Override
                public void onDetected(final String data) {
                    txtView.post(new Runnable() {
                        @Override
                        public void run() {
                            Hasil=data;
                            txtView.setText(data);
                            if(qrReader.isCameraRunning())
                            {
                                qrReader.stop();
                                showDialog();
                            }
                        }
                    });
                }

            }).facing(QREader.BACK_CAM)
                    .enableAutofocus(true)
                    .height(srfView.getWidth())
                    .width(srfView.getHeight())
                    .build();

            Toast.makeText(scan_2.this, "Ready to Scan", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title dialog
        alertDialogBuilder.setTitle("Data ");
        // set pesan dari dialog
        alertDialogBuilder
                .setMessage(Hasil)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        //  f_scan.this.finish();
                        dialog.cancel();
                        qrReader.start();
                    }
                });
//                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // jika tombol ini diklik, akan menutup dialog
//                        // dan tidak terjadi apa2
//                        dialog.cancel();
//                    }
//                });
        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();
        // menampilkan alert dialog
        alertDialog.show();
    }

    protected void onResume(){
        super.onResume();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(qrReader !=null)
                            qrReader.initAndStart(srfView);
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(scan_2.this, "harus di enable dahulu",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                    }
                }).check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(qrReader !=null)
                            qrReader.releaseAndCleanup();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(scan_2.this, "harus di enable dahulu",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
}
