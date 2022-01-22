package com.dzavira.example.utils;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.google.zxing.BarcodeFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CodeScan {
    public static final List<BarcodeFormat> ALL_FORMATS =
            Collections.unmodifiableList(Arrays.asList(BarcodeFormat.values()));

    public static final List<BarcodeFormat> ONE_DIMENSIONAL_FORMATS = Collections.unmodifiableList(
            Arrays.asList(BarcodeFormat.CODABAR, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93,
                    BarcodeFormat.CODE_128, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13,
                    BarcodeFormat.ITF, BarcodeFormat.RSS_14, BarcodeFormat.RSS_EXPANDED,
                    BarcodeFormat.UPC_A, BarcodeFormat.UPC_E, BarcodeFormat.UPC_EAN_EXTENSION));

    public static final List<BarcodeFormat> TWO_DIMENSIONAL_FORMATS = Collections.unmodifiableList(
            Arrays.asList(BarcodeFormat.AZTEC, BarcodeFormat.DATA_MATRIX, BarcodeFormat.MAXICODE,
                    BarcodeFormat.PDF_417, BarcodeFormat.QR_CODE));

    public static final int CAMERA_BACK = -1;

    public static final int CAMERA_FRONT = -2;

    private static final List<BarcodeFormat> DEFAULT_FORMATS = ALL_FORMATS;
    private static final ScanMode DEFAULT_SCAN_MODE = ScanMode.SINGLE;
    private static final AutoFocusMode DEFAULT_AUTO_FOCUS_MODE = AutoFocusMode.SAFE;
    private static final boolean DEFAULT_AUTO_FOCUS_ENABLED = true;
    private static final boolean DEFAULT_TOUCH_FOCUS_ENABLED = true;
    private static final boolean DEFAULT_FLASH_ENABLED = false;
    private static final long DEFAULT_SAFE_AUTO_FOCUS_INTERVAL = 2000L;
    private static final int SAFE_AUTO_FOCUS_ATTEMPTS_THRESHOLD = 2;
    private final Object mInitializeLock = new Object();
    private final Context mContext;
    private final Handler mMainThreadHandler;
    private final CodeScannerView mScannerView;
    private final SurfaceHolder mSurfaceHolder;
    private final SurfaceHolder.Callback mSurfaceCallback;
    private final Camera.PreviewCallback mPreviewCallback;
    private final Camera.AutoFocusCallback mTouchFocusCallback;
    private final Camera.AutoFocusCallback mSafeAutoFocusCallback;
    private final Runnable mSafeAutoFocusTask;
    private final Runnable mStopPreviewTask;
    private final DecoderStateListener mDecoderStateListener;
    private volatile List<BarcodeFormat> mFormats = DEFAULT_FORMATS;
    private volatile ScanMode mScanMode = DEFAULT_SCAN_MODE;
    private volatile AutoFocusMode mAutoFocusMode = DEFAULT_AUTO_FOCUS_MODE;
    private volatile DecodeCallback mDecodeCallback = null;
    private volatile ErrorCallback mErrorCallback = null;
    private volatile DecoderWrapper mDecoderWrapper = null;
    private volatile boolean mInitialization = false;
    private volatile boolean mInitialized = false;
    private volatile boolean mStoppingPreview = false;
    private volatile boolean mAutoFocusEnabled = DEFAULT_AUTO_FOCUS_ENABLED;
    private volatile boolean mFlashEnabled = DEFAULT_FLASH_ENABLED;
    private volatile long mSafeAutoFocusInterval = DEFAULT_SAFE_AUTO_FOCUS_INTERVAL;
    private volatile int mCameraId = CAMERA_BACK;
    private volatile int mZoom = 0;
    private boolean mTouchFocusEnabled = DEFAULT_TOUCH_FOCUS_ENABLED;
    private boolean mTouchFocusing = false;
    private boolean mPreviewActive = false;
    private boolean mSafeAutoFocusing = false;
    private boolean mSafeAutoFocusTaskScheduled = false;
    private boolean mInitializationRequested = false;
    private int mSafeAutoFocusAttemptsCount = 0;
    private int mViewWidth = 0;
    private int mViewHeight = 0;


    @MainThread
    public CodeScan(@NonNull final Context context, @NonNull final ScanView view) {
        mContext = context;
        mScannerView = view;
        mSurfaceHolder = view.getPreviewView().getHolder();
        mMainThreadHandler = new Handler();
        mSurfaceCallback = new SurfaceCallback();
        mPreviewCallback = new PreviewCallback();
        mTouchFocusCallback = new TouchFocusCallback();
        mSafeAutoFocusCallback = new SafeAutoFocusCallback();
        mSafeAutoFocusTask = new SafeAutoFocusTask();
        mStopPreviewTask = new StopPreviewTask();
        mDecoderStateListener = new DecoderStateListener();
        mScannerView.setCodeScanner(this);
        mScannerView.setSizeListener(new ScannerSizeListener());
    }
}
