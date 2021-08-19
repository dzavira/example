package dev.mrzha.scan01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class fire_scan extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String    TAG = "OCVSample::Activity";

    private static final int       VIEW_MODE_RGBA     		   = 0;
    private static final int       VIEW_MODE_HOUGH_CIRCLES     = 1;
    private static final int       VIEW_MODE_CANNY    		   = 2;
    private static final int       VIEW_MODE_HOUGH_LINES 	   = 5;

    private int                    mViewMode;
    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;

    private MenuItem               mItemPreviewRGBA;
    private MenuItem               mItemPreviewHoughCircles;
    private MenuItem               mItemPreviewCanny;
    private MenuItem               mItemPreviewFeatures;

    private CameraBridgeViewBase   mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("mixed_sample");

                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public fire_scan() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_devscan);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.cameraViewer);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called oncreatemenuoption");
        mItemPreviewRGBA = menu.add("Preview RGBA");
        mItemPreviewHoughCircles= menu.add("Hough Circles");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewFeatures = menu.add("Hough Line");
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mOpenCvCameraView !=null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mOpenCvCameraView !=null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height,width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mIntermediateMat.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        final int viewMode = mViewMode;
        WindowManager wm = (WindowManager) this.getSystemService(this.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getHeight();
        int height = display.getWidth();

        switch (viewMode){
            case VIEW_MODE_HOUGH_CIRCLES:
                mRgba = inputFrame.rgba();
                Mat mGrayScale = new Mat(height + height / 2, width, CvType.CV_8UC1);
                Mat circle = new Mat();

                // parameter
                int iCannyUpperThreshold = 100;
                int iMinRadius = 20;
                int iMaxRadius = 400;
                int iAccumulator = 100;

                Imgproc.cvtColor(mRgba, mGrayScale, Imgproc.COLOR_RGB2GRAY, 4);
                Imgproc.HoughCircles(mGrayScale,circle, Imgproc.CV_HOUGH_GRADIENT,1.0, mGrayScale.rows() / 8,
                        iCannyUpperThreshold,iAccumulator, iMinRadius,iMaxRadius);
       if (circle.cols()>0)
           for (int x = 0; x< circle.cols();x++)
           {
               double vCircle[] = circle.get(0,x);

               if(vCircle==null)
                   break;

               Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
               int radius = (int)Math.round(vCircle[2]);

               // draw circle
               Imgproc.circle(mRgba, pt, radius, new Scalar(0,255,0), 3);
               Imgproc.circle(mRgba, pt, 3, new Scalar(0,0,255), 3);
           }
        break;

            case VIEW_MODE_RGBA:
                mRgba = inputFrame.rgba();
                break;
            case VIEW_MODE_CANNY:
                mRgba = inputFrame.rgba();
                Imgproc.Canny(inputFrame.gray(), mIntermediateMat,80,100);
                Imgproc.cvtColor(mIntermediateMat,mRgba,Imgproc.COLOR_GRAY2RGBA,4);
                break;
            case VIEW_MODE_HOUGH_LINES:
                mRgba = inputFrame.rgba();

                Mat thresholdImage = new Mat(height + height / 2, width, CvType.CV_8UC1);
                Imgproc.Canny(thresholdImage,thresholdImage,80,100,3,false);

                // parameters
                Mat lines = new Mat();
                int threshold = 50;
                int minLineSize = 20;
                int lineGap = 20;

                Imgproc.HoughLinesP(thresholdImage, lines, 1, Math.PI/180, threshold, minLineSize, lineGap);

                // draw line
                for (int x = 0; x < lines.cols() && x < 1; x++){
                    double[] vec = lines.get(0, x);
                    double x1 = vec[0],
                            y1 = vec[1],
                            x2 = vec[2],
                            y2 = vec[3];
                    Point start = new Point(x1, y1);
                    Point end = new Point(x2, y2);

                    Imgproc.line(mRgba, start, end, new Scalar(255,0,0), 3);
                }
                break;
        }
        return mRgba;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

        if (item == mItemPreviewRGBA) {
            mViewMode = VIEW_MODE_RGBA;
        } else if (item == mItemPreviewHoughCircles) {
            mViewMode = VIEW_MODE_HOUGH_CIRCLES;
        } else if (item == mItemPreviewCanny) {
            mViewMode = VIEW_MODE_CANNY;
        } else if (item == mItemPreviewFeatures) {
            mViewMode = VIEW_MODE_HOUGH_LINES;
        }

        return true;
    }

    public native void FindFeatures(long matAddrGr, long matAddrRgba);
}






