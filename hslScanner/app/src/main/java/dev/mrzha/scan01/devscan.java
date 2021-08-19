package dev.mrzha.scan01;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.camera2.CameraManager;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import dev.mrzha.scan01.common.HybridBinarizer;
import dev.mrzha.scan01.qrcode.QRCodeReader;
import dev.mrzha.scan01.reader.BinaryBitmap;
import dev.mrzha.scan01.reader.ChecksumException;
import dev.mrzha.scan01.reader.FormatException;
import dev.mrzha.scan01.reader.LuminanceSource;
import dev.mrzha.scan01.reader.RGBLuminanceSource;
import dev.mrzha.scan01.reader.Reader;
import dev.mrzha.scan01.reader.Result;


public class devscan extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    //view holder
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
    pandora pnd = new pandora(devscan.this);
    private Button btn_lagi;
    private CameraManager mCameraManager;
    private String mCameraId;
    private ToggleButton toggleButton;
    private JavaCameraView Jcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devscan);
        pnd.beri_akses();
        //initialize treshold
     //   threshold = 100;
        anim1 = AnimationUtils.loadAnimation(this,R.anim.bounce);
        anim3 = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        cameraBridgeViewBase = (JavaCameraView) findViewById(R.id.cameraViewer);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        t_Hasil = findViewById(R.id.t_result);
        btn_lagi = findViewById(R.id.b_lagi);
        toggleButton = findViewById(R.id.onOffFlashlight);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    Jcm.turnOnFlash();
                }
                else{
                    Jcm.turnOffFlash();
                }
            }
        });
        btn_lagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_lagi.startAnimation(anim1);
                t_Hasil.setText("");
                t_Hasil.startAnimation(anim3);
                t_Hasil.setVisibility(View.GONE);
                cameraBridgeViewBase.enableView();
            }
        });
        //boolean isFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
      //  t_cek = findViewById(R.id.cek);
//        if (!isFlash) {
//       //     showNoFlashError();
//        }
//        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            mCameraId = mCameraManager.getCameraIdList()[0];
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:

//                        bwIMG = new Mat();
//                        dsIMG = new Mat();
//                        hsvIMG = new Mat();
//                        lrrIMG = new Mat();
//                        urrIMG = new Mat();
//                        usIMG = new Mat();
//                        cIMG = new Mat();
//                        hovIMG = new Mat();
//                        approxCurve = new MatOfPoint2f();
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
    //    qr_code2 = new Mat (200, 200, CvType.CV_8UC1);
      //  mRgba = new Mat (200, 200, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
      //  qr_code2.release();
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
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
//        Imgproc.pyrDown(mRgba, dsIMG, new Size(mRgba.cols() / 2, mRgba.rows() / 2));
//        Imgproc.pyrUp(dsIMG, usIMG, mRgba.size());
//        Imgproc.Canny(usIMG, bwIMG, 0, threshold);
//        Imgproc.dilate(bwIMG, bwIMG, new Mat(), new Point(-1, 1), 1);
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        cIMG = bwIMG.clone();
//        Imgproc.findContours(cIMG, contours, hovIMG, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

//        mRGBAT= mRgba.t();
//        Core.flip(mRgba.t(), mRGBAT, 1);
//        Imgproc.resize(mRGBAT, mRGBAT, mRgba.size());

        try {
            scans();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

//        for (MatOfPoint cnt : contours) {
//            MatOfPoint2f curve = new MatOfPoint2f(cnt.toArray());
//            Imgproc.approxPolyDP(curve, approxCurve, 0.02 * Imgproc.arcLength(curve, true), true);
//            int numberVertices = (int) approxCurve.total();
//            double contourArea = Imgproc.contourArea(cnt);
//            if (Math.abs(contourArea) < 10) {
//                continue;
//            }
//            if (numberVertices >= 4 && numberVertices <= 6) {
//                List<Double> cos = new ArrayList<>();
//                for (int j = 2; j < numberVertices + 1; j++) {
//                    cos.add(angle(approxCurve.toArray()[j % numberVertices], approxCurve.toArray()[j - 2], approxCurve.toArray()[j - 1]));
//                }
//                Collections.sort(cos);
//                double mincos = cos.get(0);
//                double maxcos = cos.get(cos.size() - 1);
//                if (numberVertices == 4 && mincos >= -0.1 && maxcos <= 0.3) {
//                    setLabel(mRgba, "[]", cnt);
//                }
//            }
//
//        }
        return mRgba;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(getApplicationContext(), "There is a problem", Toast.LENGTH_SHORT).show();
        } else {
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    private static double angle(Point pt1, Point pt2, Point pt0) {
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    private void setLabel(Mat im, String label, MatOfPoint contour) {
        int fontface = Core.FONT_HERSHEY_SIMPLEX;
        double scale = 3;//0.4;
        int thickness = 3;//1;
        int[] baseline = new int[1];
        Size text = Imgproc.getTextSize(label, fontface, scale, thickness, baseline);
        Rect r = Imgproc.boundingRect(contour);
        Point pt = new Point(r.x + ((r.width - text.width) / 2),r.y + ((r.height + text.height) / 2));
        Imgproc.putText(im, label, pt, fontface, scale, new Scalar(250, 5, 99), thickness);
    }
}

