package com.dzavira.example.utils;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.BarcodeFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    @MainThread
    public CodeScan(@NonNull final Context context, @NonNull final ScanView view,
                       final int cameraId) {
        this(context, view);
        mCameraId = cameraId;
    }
    public int getCamera() {
        return mCameraId;
    }
    @MainThread
    public void setCamera(final int cameraId) {
        synchronized (mInitializeLock) {
            if (mCameraId != cameraId) {
                mCameraId = cameraId;
                if (mInitialized) {
                    final boolean previewActive = mPreviewActive;
                    releaseResources();
                    if (previewActive) {
                        initialize();
                    }
                }
            }
        }
    }
    @NonNull
    public List<BarcodeFormat> getFormats() {
        return mFormats;
    }
    @MainThread
    public void setFormats(@NonNull final List<BarcodeFormat> formats) {
        synchronized (mInitializeLock) {
            mFormats = Objects.requireNonNull(formats);
            if (mInitialized) {
                final DecoderWrapper decoderWrapper = mDecoderWrapper;
                if (decoderWrapper != null) {
                    decoderWrapper.getDecoder().setFormats(formats);
                }
            }
        }
    }
    @Nullable
    public DecodeCallback getDecodeCallback() {
        return mDecodeCallback;
    }
    public void setDecodeCallback(@Nullable final DecodeCallback decodeCallback) {
        synchronized (mInitializeLock) {
            mDecodeCallback = decodeCallback;
            if (mInitialized) {
                final DecoderWrapper decoderWrapper = mDecoderWrapper;
                if (decoderWrapper != null) {
                    decoderWrapper.getDecoder().setCallback(decodeCallback);
                }
            }
        }
    }
    @Nullable
    public ErrorCallback getErrorCallback() {
        return mErrorCallback;
    }
    public void setErrorCallback(@Nullable final ErrorCallback errorCallback) {
        mErrorCallback = errorCallback;
    }
    @NonNull
    public ScanMode getScanMode() {
        return mScanMode;
    }
    public void setScanMode(@NonNull final ScanMode scanMode) {
        mScanMode = Objects.requireNonNull(scanMode);
    }
    public int getZoom() {
        return mZoom;
    }
    public void setZoom(final int zoom) {
        if (zoom < 0) {
            throw new IllegalArgumentException("Zoom value must be greater than or equal to zero");
        }
        synchronized (mInitializeLock) {
            if (zoom != mZoom) {
                mZoom = zoom;
                if (mInitialized) {
                    final DecoderWrapper decoderWrapper = mDecoderWrapper;
                    if (decoderWrapper != null) {
                        final Camera camera = decoderWrapper.getCamera();
                        final Parameters parameters = camera.getParameters();
                        Utils.setZoom(parameters, zoom);
                        camera.setParameters(parameters);
                    }
                }
            }
        }
        mZoom = zoom;
    }
    public boolean isTouchFocusEnabled() {
        return mTouchFocusEnabled;
    }
    public void setTouchFocusEnabled(final boolean touchFocusEnabled) {
        mTouchFocusEnabled = touchFocusEnabled;
    }
    public boolean isAutoFocusEnabled() {
        return mAutoFocusEnabled;
    }
    @MainThread
    public void setAutoFocusEnabled(final boolean autoFocusEnabled) {
        synchronized (mInitializeLock) {
            final boolean changed = mAutoFocusEnabled != autoFocusEnabled;
            mAutoFocusEnabled = autoFocusEnabled;
            mScannerView.setAutoFocusEnabled(autoFocusEnabled);
            final DecoderWrapper decoderWrapper = mDecoderWrapper;
            if (mInitialized && mPreviewActive && changed && decoderWrapper != null &&
                    decoderWrapper.isAutoFocusSupported()) {
                setAutoFocusEnabledInternal(autoFocusEnabled);
            }
        }
    }
    @NonNull
    public AutoFocusMode getAutoFocusMode() {
        return mAutoFocusMode;
    }
    @MainThread
    public void setAutoFocusMode(@NonNull final AutoFocusMode autoFocusMode) {
        synchronized (mInitializeLock) {
            mAutoFocusMode = Objects.requireNonNull(autoFocusMode);
            if (mInitialized && mAutoFocusEnabled) {
                setAutoFocusEnabledInternal(true);
            }
        }
    }
    public void setAutoFocusInterval(final long autoFocusInterval) {
        mSafeAutoFocusInterval = autoFocusInterval;
    }
    public boolean isFlashEnabled() {
        return mFlashEnabled;
    }

    @MainThread
    public void setFlashEnabled(final boolean flashEnabled) {
        synchronized (mInitializeLock) {
            final boolean changed = mFlashEnabled != flashEnabled;
            mFlashEnabled = flashEnabled;
            mScannerView.setFlashEnabled(flashEnabled);
            final DecoderWrapper decoderWrapper = mDecoderWrapper;
            if (mInitialized && mPreviewActive && changed && decoderWrapper != null &&
                    decoderWrapper.isFlashSupported()) {
                setFlashEnabledInternal(flashEnabled);
            }
        }
    }
    public boolean isPreviewActive() {
        return mPreviewActive;
    }
    @MainThread
    public void startPreview() {
        synchronized (mInitializeLock) {
            if (!mInitialized && !mInitialization) {
                initialize();
                return;
            }
        }
        if (!mPreviewActive) {
            mSurfaceHolder.addCallback(mSurfaceCallback);
            startPreviewInternal(false);
        }
    }

    @MainThread
    public void stopPreview() {
        if (mInitialized && mPreviewActive) {
            mSurfaceHolder.removeCallback(mSurfaceCallback);
            stopPreviewInternal(false);
        }
    }
    @MainThread
    public void releaseResources() {
        if (mInitialized) {
            if (mPreviewActive) {
                stopPreview();
            }
            releaseResourcesInternal();
        }
    }
    @SuppressWarnings("SuspiciousNameCombination")
    void performTouchFocus(final Rect viewFocusArea) {
        synchronized (mInitializeLock) {
            if (mInitialized && mPreviewActive && !mTouchFocusing) {
                try {
                    setAutoFocusEnabled(false);
                    final DecoderWrapper decoderWrapper = mDecoderWrapper;
                    if (mPreviewActive && decoderWrapper != null &&
                            decoderWrapper.isAutoFocusSupported()) {
                        final Point imageSize = decoderWrapper.getImageSize();
                        int imageWidth = imageSize.getX();
                        int imageHeight = imageSize.getY();
                        final int orientation = decoderWrapper.getDisplayOrientation();
                        if (orientation == 90 || orientation == 270) {
                            final int width = imageWidth;
                            imageWidth = imageHeight;
                            imageHeight = width;
                        }
                        final Rect imageArea =
                                Utils.getImageFrameRect(imageWidth, imageHeight, viewFocusArea,
                                        decoderWrapper.getPreviewSize(),
                                        decoderWrapper.getViewSize());
                        final Camera camera = decoderWrapper.getCamera();
                        camera.cancelAutoFocus();
                        final Parameters parameters = camera.getParameters();
                        Utils.configureFocusArea(parameters, imageArea, imageWidth, imageHeight,
                                orientation);
                        Utils.configureFocusModeForTouch(parameters);
                        camera.setParameters(parameters);
                        camera.autoFocus(mTouchFocusCallback);
                        mTouchFocusing = true;
                    }
                } catch (final Exception ignored) {
                }
            }
        }
    }
    boolean isAutoFocusSupportedOrUnknown() {
        final DecoderWrapper wrapper = mDecoderWrapper;
        return wrapper == null || wrapper.isAutoFocusSupported();
    }

    boolean isFlashSupportedOrUnknown() {
        final DecoderWrapper wrapper = mDecoderWrapper;
        return wrapper == null || wrapper.isFlashSupported();
    }

    private void initialize() {
        initialize(mScannerView.getWidth(), mScannerView.getHeight());
    }

    private void initialize(final int width, final int height) {
        mViewWidth = width;
        mViewHeight = height;
        if (width > 0 && height > 0) {
            mInitialization = true;
            mInitializationRequested = false;
            new InitializationThread(width, height).start();
        } else {
            mInitializationRequested = true;
        }
    }
    private void startPreviewInternal(final boolean internal) {
        try {
            final DecoderWrapper decoderWrapper = mDecoderWrapper;
            if (decoderWrapper != null) {
                final Camera camera = decoderWrapper.getCamera();
                camera.setPreviewCallback(mPreviewCallback);
                camera.setPreviewDisplay(mSurfaceHolder);
                if (!internal && decoderWrapper.isFlashSupported() && mFlashEnabled) {
                    setFlashEnabledInternal(true);
                }
                camera.startPreview();
                mStoppingPreview = false;
                mPreviewActive = true;
                mSafeAutoFocusing = false;
                mSafeAutoFocusAttemptsCount = 0;
                if (decoderWrapper.isAutoFocusSupported() && mAutoFocusEnabled) {
                    final Rect frameRect = mScannerView.getFrameRect();
                    if (frameRect != null) {
                        final Parameters parameters = camera.getParameters();
                        Utils.configureDefaultFocusArea(parameters, decoderWrapper, frameRect);
                        camera.setParameters(parameters);
                    }
                    if (mAutoFocusMode == AutoFocusMode.SAFE) {
                        scheduleSafeAutoFocusTask();
                    }
                }
            }
        } catch (final Exception ignored) {
        }
    }

    private void startPreviewInternalSafe() {
        if (mInitialized && !mPreviewActive) {
            startPreviewInternal(true);
        }
    }

    private void stopPreviewInternal(final boolean internal) {
        try {
            final DecoderWrapper decoderWrapper = mDecoderWrapper;
            if (decoderWrapper != null) {
                final Camera camera = decoderWrapper.getCamera();
                camera.cancelAutoFocus();
                final Parameters parameters = camera.getParameters();
                if (!internal && decoderWrapper.isFlashSupported() && mFlashEnabled) {
                    Utils.setFlashMode(parameters, Parameters.FLASH_MODE_OFF);
                }
                camera.setParameters(parameters);
                camera.setPreviewCallback(null);
                camera.stopPreview();
            }
        } catch (final Exception ignored) {
        }
        mStoppingPreview = false;
        mPreviewActive = false;
        mSafeAutoFocusing = false;
        mSafeAutoFocusAttemptsCount = 0;
    }

    private void stopPreviewInternalSafe() {
        if (mInitialized && mPreviewActive) {
            stopPreviewInternal(true);
        }
    }

    private void releaseResourcesInternal() {
        mInitialized = false;
        mInitialization = false;
        mStoppingPreview = false;
        mPreviewActive = false;
        mSafeAutoFocusing = false;
        final DecoderWrapper decoderWrapper = mDecoderWrapper;
        if (decoderWrapper != null) {
            mDecoderWrapper = null;
            decoderWrapper.release();
        }
    }

    private void setFlashEnabledInternal(final boolean flashEnabled) {
        try {
            final DecoderWrapper decoderWrapper = mDecoderWrapper;
            if (decoderWrapper != null) {
                final Camera camera = decoderWrapper.getCamera();
                final Parameters parameters = camera.getParameters();
                if (parameters == null) {
                    return;
                }
                if (flashEnabled) {
                    Utils.setFlashMode(parameters, Parameters.FLASH_MODE_TORCH);
                } else {
                    Utils.setFlashMode(parameters, Parameters.FLASH_MODE_OFF);
                }
                camera.setParameters(parameters);
            }
        } catch (final Exception ignored) {
        }
    }
    private void setAutoFocusEnabledInternal(final boolean autoFocusEnabled) {
        try {
            final DecoderWrapper decoderWrapper = mDecoderWrapper;
            if (decoderWrapper != null) {
                final Camera camera = decoderWrapper.getCamera();
                camera.cancelAutoFocus();
                mTouchFocusing = false;
                final Parameters parameters = camera.getParameters();
                final AutoFocusMode autoFocusMode = mAutoFocusMode;
                if (autoFocusEnabled) {
                    Utils.setAutoFocusMode(parameters, autoFocusMode);
                } else {
                    Utils.disableAutoFocus(parameters);
                }
                if (autoFocusEnabled) {
                    final Rect frameRect = mScannerView.getFrameRect();
                    if (frameRect != null) {
                        Utils.configureDefaultFocusArea(parameters, decoderWrapper, frameRect);
                    }
                }
                camera.setParameters(parameters);
                if (autoFocusEnabled) {
                    mSafeAutoFocusAttemptsCount = 0;
                    mSafeAutoFocusing = false;
                    if (autoFocusMode == AutoFocusMode.SAFE) {
                        scheduleSafeAutoFocusTask();
                    }
                }
            }
        } catch (final Exception ignored) {
        }
    }

    private void safeAutoFocusCamera() {
        if (!mInitialized || !mPreviewActive) {
            return;
        }
        final DecoderWrapper decoderWrapper = mDecoderWrapper;
        if (decoderWrapper == null || !decoderWrapper.isAutoFocusSupported() ||
                !mAutoFocusEnabled) {
            return;
        }
        if (mSafeAutoFocusing && mSafeAutoFocusAttemptsCount < SAFE_AUTO_FOCUS_ATTEMPTS_THRESHOLD) {
            mSafeAutoFocusAttemptsCount++;
        } else {
            try {
                final Camera camera = decoderWrapper.getCamera();
                camera.cancelAutoFocus();
                camera.autoFocus(mSafeAutoFocusCallback);
                mSafeAutoFocusAttemptsCount = 0;
                mSafeAutoFocusing = true;
            } catch (final Exception e) {
                mSafeAutoFocusing = false;
            }
        }
        scheduleSafeAutoFocusTask();
    }

    private void scheduleSafeAutoFocusTask() {
        if (mSafeAutoFocusTaskScheduled) {
            return;
        }
        mSafeAutoFocusTaskScheduled = true;
        mMainThreadHandler.postDelayed(mSafeAutoFocusTask, mSafeAutoFocusInterval);
    }
    private final class ScannerSizeListener implements ScanView.SizeListener {
        @Override
        public void onSizeChanged(final int width, final int height) {
            synchronized (mInitializeLock) {
                if (width != mViewWidth || height != mViewHeight) {
                    final boolean previewActive = mPreviewActive;
                    if (mInitialized) {
                        releaseResources();
                    }
                    if (previewActive || mInitializationRequested) {
                        initialize(width, height);
                    }
                }
            }
        }
    }
}
