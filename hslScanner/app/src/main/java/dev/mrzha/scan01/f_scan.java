package dev.mrzha.scan01;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class f_scan extends AppCompatActivity implements SurfaceHolder.Callback {
    private TextView txtView;
    private SurfaceView srfView;
    private SurfaceHolder surfaceHolder;
    private QREader qrReader;
    private String Hasil;
    private Button lagi;
    private Button bak;
    private Bitmap bmp;
    private ImageView imgV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_f_scan);
        srfView = (SurfaceView)findViewById(R.id.camera_view);

        surfaceHolder = srfView.getHolder();
        srfView.getDrawingCache(true);
        surfaceHolder.addCallback(this);
        bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        beri_akses();
        lagi = (Button)findViewById(R.id.b_lagi);
        lagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartActivity();
            }
        });


    }
    void restartActivity() {
        startActivity(new Intent(f_scan.this, f_scan.class));
        finish();
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

        setupQReader();

    }
    private void beri_akses() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                      //    Toast.makeText(getApplicationContext(), "Permission Granted ", Toast.LENGTH_LONG).show();
                        setupCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(f_scan.this, "Enable Permission Guys", Toast.LENGTH_SHORT).show();
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
                            int d = QREader.BACK_CAM;
                         //   Log.e("ma", String.valueOf(srfView.getdraw));
                    //        Bitmap b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                            Canvas c = new Canvas(bmp);
                            srfView.draw(c);

                          //  Log.e("gm", String.valueOf(bmp));
                            if(qrReader.isCameraRunning())
                            {
                                qrReader.stop();
                         //       showDialog();
                            }
                        }
                    });
                }

            }).facing(QREader.BACK_CAM)
                    .enableAutofocus(true)
                    .height(srfView.getWidth())
                    .width(srfView.getHeight())
                    .build();

          //  Toast.makeText(f_scan.this, "surface Ok", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(f_scan.this, "harus di enable dahulu",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(f_scan.this, "harus di enable dahulu",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
