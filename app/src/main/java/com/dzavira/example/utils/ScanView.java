package com.dzavira.example.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

import com.dzavira.example.R;

public class ScanView extends ViewGroup {
    private static final boolean DEFAULT_AUTO_FOCUS_BUTTON_VISIBLE = true;
    private static final boolean DEFAULT_FLASH_BUTTON_VISIBLE = true;
    private static final int DEFAULT_AUTO_FOCUS_BUTTON_VISIBILITY = VISIBLE;
    private static final int DEFAULT_FLASH_BUTTON_VISIBILITY = VISIBLE;
    private static final int DEFAULT_MASK_COLOR = 0x77000000;
    private static final int DEFAULT_FRAME_COLOR = Color.WHITE;
    private static final int DEFAULT_AUTO_FOCUS_BUTTON_COLOR = Color.WHITE;
    private static final int DEFAULT_FLASH_BUTTON_COLOR = Color.WHITE;
    private static final float DEFAULT_FRAME_THICKNESS_DP = 2f;
    private static final float DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1f;
    private static final float DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1f;
    private static final float DEFAULT_FRAME_CORNER_SIZE_DP = 50f;
    private static final float DEFAULT_FRAME_CORNERS_RADIUS_DP = 0f;
    private static final float DEFAULT_FRAME_SIZE = 0.75f;
    private static final float BUTTON_SIZE_DP = 56f;
    private static final float FOCUS_AREA_SIZE_DP = 20f;
    private SurfaceView mPreviewView;
    private ViewFinder mViewFinderView;
    private ImageView mAutoFocusButton;
    private ImageView mFlashButton;
    private Point mPreviewSize;
    private SizeListener mSizeListener;
    private CodeScan mCodeScanner;
    private int mButtonSize;
    private int mAutoFocusButtonColor;
    private int mFlashButtonColor;
    private int mFocusAreaSize;

    public ScanView(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    private void initialize(@NonNull final Context context, @Nullable final AttributeSet attrs,
                            @AttrRes final int defStyleAttr, @StyleRes final int defStyleRe) {
        mPreviewView = new SurfaceView(context);
        mPreviewView.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mViewFinderView = new ViewFinder(context);
        mViewFinderView.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        final float density = context.getResources().getDisplayMetrics().density;
        mButtonSize = Math.round(density * BUTTON_SIZE_DP);
        mFocusAreaSize = Math.round(density * FOCUS_AREA_SIZE_DP);
        mAutoFocusButton = new ImageView(context);
        mAutoFocusButton.setLayoutParams(new LayoutParams(mButtonSize, mButtonSize));
        mAutoFocusButton.setScaleType(ImageView.ScaleType.CENTER);
        mAutoFocusButton.setImageResource(R.drawable.ic_code_scanner_auto_focus_on);
        mAutoFocusButton.setOnClickListener(new AutoFocusClickListener());
        mFlashButton = new ImageView(context);
        mFlashButton.setLayoutParams(new LayoutParams(mButtonSize, mButtonSize));
        mFlashButton.setScaleType(ImageView.ScaleType.CENTER);
        mFlashButton.setImageResource(R.drawable.ic_code_scanner_flash_on);
        mFlashButton.setOnClickListener(new FlashClickListener());
        if (attrs == null) {
            mViewFinderView.setFrameAspectRatio(DEFAULT_FRAME_ASPECT_RATIO_WIDTH,
                    DEFAULT_FRAME_ASPECT_RATIO_HEIGHT);
            mViewFinderView.setMaskColor(DEFAULT_MASK_COLOR);
            mViewFinderView.setFrameColor(DEFAULT_FRAME_COLOR);
            mViewFinderView.setFrameThickness(Math.round(DEFAULT_FRAME_THICKNESS_DP * density));
            mViewFinderView.setFrameCornersSize(Math.round(DEFAULT_FRAME_CORNER_SIZE_DP * density));
            mViewFinderView
                    .setFrameCornersRadius(Math.round(DEFAULT_FRAME_CORNERS_RADIUS_DP * density));
            mViewFinderView.setFrameSize(DEFAULT_FRAME_SIZE);
            mAutoFocusButton.setColorFilter(DEFAULT_AUTO_FOCUS_BUTTON_COLOR);
            mFlashButton.setColorFilter(DEFAULT_FLASH_BUTTON_COLOR);
            mAutoFocusButton.setVisibility(DEFAULT_AUTO_FOCUS_BUTTON_VISIBILITY);
            mFlashButton.setVisibility(DEFAULT_FLASH_BUTTON_VISIBILITY);
        }
        else {
            TypedArray a = null;
            try {
                a = context.getTheme()
                        .obtainStyledAttributes(attrs, R.styleable.CodeScanner, defStyleAttr,
                                defStyleRes);
                setMaskColor(a.getColor(R.styleable.CodeScanner_maskColor, DEFAULT_MASK_COLOR));
                setFrameColor(
                        a.getColor(R.styleable.CodeScanner_frameColor, DEFAULT_FRAME_COLOR));
                setFrameThickness(
                        a.getDimensionPixelOffset(R.styleable.CodeScanner_frameThickness,
                                Math.round(DEFAULT_FRAME_THICKNESS_DP * density)));
                setFrameCornersSize(
                        a.getDimensionPixelOffset(R.styleable.CodeScanner_frameCornersSize,
                                Math.round(DEFAULT_FRAME_CORNER_SIZE_DP * density)));
                setFrameCornersRadius(
                        a.getDimensionPixelOffset(R.styleable.CodeScanner_frameCornersRadius,
                                Math.round(DEFAULT_FRAME_CORNERS_RADIUS_DP * density)));
                setFrameAspectRatio(a.getFloat(R.styleable.CodeScanner_frameAspectRatioWidth,
                        DEFAULT_FRAME_ASPECT_RATIO_WIDTH),
                        a.getFloat(R.styleable.CodeScanner_frameAspectRatioHeight,
                                DEFAULT_FRAME_ASPECT_RATIO_HEIGHT));
                setFrameSize(a.getFloat(R.styleable.CodeScanner_frameSize, DEFAULT_FRAME_SIZE));
                setAutoFocusButtonVisible(
                        a.getBoolean(R.styleable.CodeScanner_autoFocusButtonVisible,
                                DEFAULT_AUTO_FOCUS_BUTTON_VISIBLE));
                setFlashButtonVisible(a.getBoolean(R.styleable.CodeScanner_flashButtonVisible,
                        DEFAULT_FLASH_BUTTON_VISIBLE));
                setAutoFocusButtonColor(a.getColor(R.styleable.CodeScanner_autoFocusButtonColor,
                        DEFAULT_AUTO_FOCUS_BUTTON_COLOR));
                setFlashButtonColor(a.getColor(R.styleable.CodeScanner_flashButtonColor,
                        DEFAULT_FLASH_BUTTON_COLOR));
            }
            finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }
        addView(mPreviewView);
        addView(mViewFinderView);
        addView(mAutoFocusButton);
        addView(mFlashButton);
    }

    @Override
    protected void onLayout(final boolean changed, final int left, final int top, final int right,
                            final int bottom) {
        performLayout(right - left, bottom - top);
    }
    @Override
    protected void onSizeChanged(final int width, final int height, final int oldWidth,
                                 final int oldHeight) {
        performLayout(width, height);
        final SizeListener listener = mSizeListener;
        if (listener != null) {
            listener.onSizeChanged(width, height);
        }
    }
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(@NonNull final MotionEvent event) {
        final CodeScanner codeScanner = mCodeScanner;
        final Rect frameRect = getFrameRect();
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        if (codeScanner != null && frameRect != null &&
                codeScanner.isAutoFocusSupportedOrUnknown() && codeScanner.isTouchFocusEnabled() &&
                event.getAction() == MotionEvent.ACTION_DOWN && frameRect.isPointInside(x, y)) {
            final int areaSize = mFocusAreaSize;
            codeScanner.performTouchFocus(
                    new Rect(x - areaSize, y - areaSize, x + areaSize, y + areaSize)
                            .fitIn(frameRect));
        }
        return super.onTouchEvent(event);
    }


    public ScanView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScanView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    interface SizeListener {
        void onSizeChanged(int width, int height);
    }
}
