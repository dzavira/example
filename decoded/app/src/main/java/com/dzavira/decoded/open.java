package com.dzavira.decoded;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dzavira.decoded.common.HybridBinarizer;
import com.dzavira.decoded.qrcode.QRCodeReader;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

public class open extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    CameraBridgeViewBase cameraBridgeViewBase;
    //camera listener callback
    BaseLoaderCallback baseLoaderCallback;
    //image holder
    Mat bwIMG, hsvIMG, lrrIMG, urrIMG, dsIMG, usIMG, cIMG, hovIMG;
    MatOfPoint2f approxCurve;
    //  int threshold;
    private Animation anim1,anim2,anim3;
    private String m_QRcodeString2;
    private TextView t_Hasil,t_cek;
    private Mat qr_code2;
    private Mat mRgba, mRGBAT;
    pandora pnd = new pandora(open.this);
    private Button btn_lagi;
    private CameraManager mCameraManager;
    private String mCameraId;
    private ToggleButton toggleButton;
    private JavaCameraView Jcm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);
        pnd.beri_akses();

        anim1 = AnimationUtils.loadAnimation(this,R.anim.bounce);
        anim3 = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.cameraViewer);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        t_Hasil = findViewById(R.id.t_result);
        btn_lagi = findViewById(R.id.b_lagi);

        btn_lagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_lagi.startAnimation(anim1);
                t_Hasil.setText("");
                t_Hasil.startAnimation(anim3);
                t_Hasil.setVisibility(View.GONE);
                cameraBridgeViewBase.enableView();
            }
        });

        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:

                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        try {
            scans();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return mRgba;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    public void scans() throws ChecksumException, FormatException {

        Bitmap bMap2 = Bitmap.createBitmap(mRgba.width(),mRgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mRgba, bMap2);
        int[] intArray2 = new int[bMap2.getWidth()*bMap2.getHeight()];
        bMap2.getPixels(intArray2, 0, bMap2.getWidth(), 0, 0, bMap2.getWidth(), bMap2.getHeight());
        LuminanceSource source2 = new RGBLuminanceSource(bMap2.getWidth(), bMap2.getHeight(),intArray2);

        BinaryBitmap bitmap2 = new BinaryBitmap(new HybridBinarizer(source2));

        Reader reader2 = new QRCodeReader();

        try {

            final Result result2 = reader2.decode(bitmap2);

            runOnUiThread(new Runnable() {

                public void run() {

                    m_QRcodeString2 = (result2.getText());
                    t_Hasil.setText(m_QRcodeString2);
                    t_Hasil.setVisibility(View.VISIBLE);
                    t_Hasil.startAnimation(anim2);
                    cameraBridgeViewBase.disableView();

                }
            });

        }

        catch (Exception e) {

        }
    }
}