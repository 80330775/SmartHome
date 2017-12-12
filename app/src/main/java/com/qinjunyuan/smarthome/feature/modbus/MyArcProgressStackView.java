/*
 * Copyright (C) 2015 Basil Miller
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qinjunyuan.smarthome.feature.modbus;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.qinjunyuan.smarthome.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyArcProgressStackView extends View {

    // Default values
    private final static float DEFAULT_START_ANGLE = 270.0F;
    private final static float DEFAULT_SWEEP_ANGLE = 360.0F;
    private final static float DEFAULT_DRAW_WIDTH_FRACTION = 0.7F;
    private final static float DEFAULT_MODEL_OFFSET = 5.0F;
    private final static float DEFAULT_SHADOW_RADIUS = 30.0F;
    private final static float DEFAULT_SHADOW_DISTANCE = 15.0F;
    private final static float DEFAULT_SHADOW_ANGLE = 90.0F;
    private final static int DEFAULT_ANIMATION_DURATION = 350;
    private final static int DEFAULT_ACTION_MOVE_ANIMATION_DURATION = 150;

    // Max and min progress values
    private final static float MAX_PROGRESS = 100.0F;
    private final static float MIN_PROGRESS = 0.0F;

    // Max and min fraction values
    private final static float MAX_FRACTION = 1.0F;
    private final static float MIN_FRACTION = 0.0F;

    // Max and min end angle
    private final static float MAX_ANGLE = 360.0F;
    private final static float MIN_ANGLE = 0.0F;

    // Min shadow
    private final static float MIN_SHADOW = 0.0F;

    // Action move constants
    private final static float POSITIVE_ANGLE = 90.0F;
    private final static float NEGATIVE_ANGLE = 270.0F;
    private final static int POSITIVE_SLICE = 1;
    private final static int NEGATIVE_SLICE = -1;
    private final static int DEFAULT_SLICE = 0;
    private final static int ANIMATE_ALL_INDEX = -2;
    private final static int DISABLE_ANIMATE_INDEX = -1;

    // Default colors
    private final static int DEFAULT_SHADOW_COLOR = Color.parseColor("#8C000000");

    // Start and end angles
    private float mStartAngle;
    private float mSweepAngle;

    // Progress models
    private List<? super Model> mModels = new ArrayList<>();

    // Progress and text paints
    private final Paint mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setStyle(Style.STROKE);
        }
    };
    private final TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setTextAlign(Align.LEFT);
        }
    };
    private final Paint mLevelPaint = new Paint(Paint.ANTI_ALIAS_FLAG) {
        {
            setDither(true);
            setStyle(Paint.Style.FILL_AND_STROKE);
            setPathEffect(new CornerPathEffect(0.5F));
        }
    };

    // ValueAnimator and interpolator for progress animating
    private final ValueAnimator mProgressAnimator = new ValueAnimator();
    private ValueAnimator.AnimatorListener mAnimatorListener;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private Interpolator mInterpolator;
    private int mAnimationDuration;
    private float mAnimatedFraction;

    // Square size of view
    private int mSize;

    // Offsets for handling and radius of progress models
    private float mProgressModelSize;
    private float mProgressModelOffset;
    private float mDrawWidthFraction;
    private float mDrawWidthDimension;

    // Shadow variables
    private float mShadowRadius;
    private float mShadowDistance;
    private float mShadowAngle;

    // Boolean variables
    private boolean mIsAnimated;
    private boolean mIsShadowed;
    private boolean mIsRounded;
    private boolean mIsDragged;
    private boolean mIsModelBgEnabled;
    private boolean mIsLeveled;

    // Colors
    private int mShadowColor;
    private int mTextColor;
    private int mPreviewModelBgColor;

    // Action move variables
    private int mActionMoveModelIndex = DISABLE_ANIMATE_INDEX;
    private int mActionMoveLastSlice = 0;
    private int mActionMoveSliceCounter;
    private boolean mIsActionMoved;

    // Text typeface
    private Typeface mTypeface;

    // Indicator orientation
    private IndicatorOrientation mIndicatorOrientation;

    // Is >= VERSION_CODES.HONEYCOMB
    private boolean mIsFeaturesAvailable;

    public MyArcProgressStackView(final Context context) {
        this(context, null);
    }

    public MyArcProgressStackView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyArcProgressStackView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Init CPSV

        // Always draw
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null);

        // Detect if features available
        mIsFeaturesAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        // Retrieve attributes from xml
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyArcProgressStackView);
        try {
            setIsAnimated(
                    typedArray.getBoolean(R.styleable.MyArcProgressStackView_mapsv_animated, true)
            );
            setIsShadowed(
                    typedArray.getBoolean(R.styleable.MyArcProgressStackView_mapsv_shadowed, true)
            );
            setIsRounded(
                    typedArray.getBoolean(R.styleable.MyArcProgressStackView_mapsv_rounded, false)
            );
            setIsDragged(
                    typedArray.getBoolean(R.styleable.MyArcProgressStackView_mapsv_dragged, false)
            );
            setIsLeveled(
                    typedArray.getBoolean(R.styleable.MyArcProgressStackView_mapsv_leveled, false)
            );
            setTypeface(
                    typedArray.getString(R.styleable.MyArcProgressStackView_mapsv_typeface)
            );
            setTextColor(
                    typedArray.getColor(
                            R.styleable.MyArcProgressStackView_mapsv_text_color,
                            Color.WHITE
                    )
            );
            setShadowRadius(
                    typedArray.getDimension(
                            R.styleable.MyArcProgressStackView_mapsv_shadow_radius,
                            DEFAULT_SHADOW_RADIUS
                    )
            );
            setShadowDistance(
                    typedArray.getDimension(
                            R.styleable.MyArcProgressStackView_mapsv_shadow_distance,
                            DEFAULT_SHADOW_DISTANCE
                    )
            );
            setShadowAngle(
                    typedArray.getInteger(
                            R.styleable.MyArcProgressStackView_mapsv_shadow_angle,
                            (int) DEFAULT_SHADOW_ANGLE
                    )
            );
            setShadowColor(
                    typedArray.getColor(
                            R.styleable.MyArcProgressStackView_mapsv_shadow_color,
                            DEFAULT_SHADOW_COLOR
                    )
            );
            setAnimationDuration(
                    typedArray.getInteger(
                            R.styleable.MyArcProgressStackView_mapsv_animation_duration,
                            DEFAULT_ANIMATION_DURATION
                    )
            );
            setStartAngle(
                    typedArray.getInteger(
                            R.styleable.MyArcProgressStackView_mapsv_start_angle,
                            (int) DEFAULT_START_ANGLE
                    )
            );
            setSweepAngle(
                    typedArray.getInteger(
                            R.styleable.MyArcProgressStackView_mapsv_sweep_angle,
                            (int) DEFAULT_SWEEP_ANGLE
                    )
            );
            setProgressModelOffset(
                    typedArray.getDimension(
                            R.styleable.MyArcProgressStackView_mapsv_model_offset,
                            DEFAULT_MODEL_OFFSET
                    )
            );
            setModelBgEnabled(
                    typedArray.getBoolean(
                            R.styleable.MyArcProgressStackView_mapsv_model_bg_enabled, false
                    )
            );

            // Set orientation
            final int orientationOrdinal =
                    typedArray.getInt(R.styleable.MyArcProgressStackView_mapsv_indicator_orientation, 0);
            setIndicatorOrientation(
                    orientationOrdinal == 0 ? IndicatorOrientation.VERTICAL : IndicatorOrientation.HORIZONTAL
            );

            // Retrieve interpolator
            Interpolator interpolator = null;
            try {
                final int interpolatorId = typedArray.getResourceId(
                        R.styleable.MyArcProgressStackView_mapsv_interpolator, 0
                );
                interpolator = interpolatorId == 0 ? null :
                        AnimationUtils.loadInterpolator(context, interpolatorId);
            } catch (Resources.NotFoundException exception) {
                interpolator = null;
                exception.printStackTrace();
            } finally {
                setInterpolator(interpolator);
            }

            // Set animation info if is available
            if (mIsFeaturesAvailable) {
                mProgressAnimator.setFloatValues(MIN_FRACTION, MAX_FRACTION);
                mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(final ValueAnimator animation) {
                        mAnimatedFraction = (float) animation.getAnimatedValue();
                        if (mAnimatorUpdateListener != null)
                            mAnimatorUpdateListener.onAnimationUpdate(animation);

                        postInvalidate();
                    }
                });
            }

            // Check whether draw width dimension or fraction
            if (typedArray.hasValue(R.styleable.MyArcProgressStackView_mapsv_draw_width)) {
                final TypedValue drawWidth = new TypedValue();
                typedArray.getValue(R.styleable.MyArcProgressStackView_mapsv_draw_width, drawWidth);
                if (drawWidth.type == TypedValue.TYPE_DIMENSION)
                    setDrawWidthDimension(
                            drawWidth.getDimension(context.getResources().getDisplayMetrics())
                    );
                else setDrawWidthFraction(drawWidth.getFraction(MAX_FRACTION, MAX_FRACTION));
            } else setDrawWidthFraction(DEFAULT_DRAW_WIDTH_FRACTION);

            // Set preview models
            if (isInEditMode()) {
                String[] preview = null;
                try {
                    final int previewId = typedArray.getResourceId(
                            R.styleable.MyArcProgressStackView_mapsv_preview_colors, 0
                    );
                    preview = previewId == 0 ? null : typedArray.getResources().getStringArray(previewId);
                } catch (Exception exception) {
                    preview = null;
                    exception.printStackTrace();
                } finally {
                    if (preview == null)
                        preview = typedArray.getResources().getStringArray(R.array.default_preview);

                    final Random random = new Random();
                    for (String previewColor : preview) {
                        Model model = new Model(null, 0, 1, "", "", 0.0F, 100.0F, false);
                        model.setColor(Color.parseColor(previewColor));
                        model.mProgress = random.nextInt((int) MAX_PROGRESS);
                        mModels.add(model);
                    }
                    measure(mSize, mSize);
                }

                // Set preview model bg color
                mPreviewModelBgColor = typedArray.getColor(
                        R.styleable.MyArcProgressStackView_mapsv_preview_bg,
                        Color.LTGRAY
                );
            }
        } finally {
            typedArray.recycle();
        }
    }

    public ValueAnimator getProgressAnimator() {
        return mProgressAnimator;
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setAnimationDuration(final long animationDuration) {
        mAnimationDuration = (int) animationDuration;
        mProgressAnimator.setDuration(animationDuration);
    }

    public ValueAnimator.AnimatorListener getAnimatorListener() {
        return mAnimatorListener;
    }

    public void setAnimatorListener(final ValueAnimator.AnimatorListener animatorListener) {
        if (mAnimatorListener != null) mProgressAnimator.removeListener(mAnimatorListener);

        mAnimatorListener = animatorListener;
        mProgressAnimator.addListener(animatorListener);
    }

    public ValueAnimator.AnimatorUpdateListener getAnimatorUpdateListener() {
        return mAnimatorUpdateListener;
    }

    public void setAnimatorUpdateListener(final ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        mAnimatorUpdateListener = animatorUpdateListener;
    }

    public float getStartAngle() {
        return mStartAngle;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setStartAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float startAngle) {
        mStartAngle = Math.max(MIN_ANGLE, Math.min(startAngle, MAX_ANGLE));
        postInvalidate();
    }

    public float getSweepAngle() {
        return mSweepAngle;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setSweepAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float sweepAngle) {
        mSweepAngle = Math.max(MIN_ANGLE, Math.min(sweepAngle, MAX_ANGLE));
        postInvalidate();
    }

    public List<? super Model> getModels() {
        return mModels;
    }

    public void setModels(final List<? super Model> models) {
        mModels.clear();
        mModels = models;
        requestLayout();
    }

    public int getSize() {
        return mSize;
    }

    public float getProgressModelSize() {
        return mProgressModelSize;
    }

    public boolean isAnimated() {
        return mIsAnimated;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setIsAnimated(final boolean isAnimated) {
        mIsAnimated = mIsFeaturesAvailable && isAnimated;
    }

    public boolean isShadowed() {
        return mIsShadowed;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setIsShadowed(final boolean isShadowed) {
        mIsShadowed = mIsFeaturesAvailable && isShadowed;
        resetShadowLayer();
        requestLayout();
    }

    public boolean isModelBgEnabled() {
        return mIsModelBgEnabled;
    }

    public void setModelBgEnabled(final boolean modelBgEnabled) {
        mIsModelBgEnabled = modelBgEnabled;
        postInvalidate();
    }

    public boolean isRounded() {
        return mIsRounded;
    }

    public void setIsRounded(final boolean isRounded) {
        mIsRounded = isRounded;
        if (mIsRounded) {
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        } else {
            mProgressPaint.setStrokeCap(Paint.Cap.BUTT);
            mProgressPaint.setStrokeJoin(Paint.Join.MITER);
        }
        requestLayout();
    }

    public boolean isDragged() {
        return mIsDragged;
    }

    public void setIsDragged(final boolean isDragged) {
        mIsDragged = isDragged;
    }

    public boolean isLeveled() {
        return mIsLeveled;
    }

    public void setIsLeveled(final boolean isLeveled) {
        mIsLeveled = mIsFeaturesAvailable && isLeveled;
        requestLayout();
    }

    public Interpolator getInterpolator() {
        return (Interpolator) mProgressAnimator.getInterpolator();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setInterpolator(final Interpolator interpolator) {
        mInterpolator = interpolator == null ? new AccelerateDecelerateInterpolator() : interpolator;
        mProgressAnimator.setInterpolator(mInterpolator);
    }

    public float getProgressModelOffset() {
        return mProgressModelOffset;
    }

    public void setProgressModelOffset(final float progressModelOffset) {
        mProgressModelOffset = progressModelOffset;
        requestLayout();
    }

    public float getDrawWidthFraction() {
        return mDrawWidthFraction;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setDrawWidthFraction(@FloatRange(from = MIN_FRACTION, to = MAX_FRACTION) final float drawWidthFraction) {
        // Divide by half for radius and reset
        mDrawWidthFraction = Math.max(MIN_FRACTION, Math.min(drawWidthFraction, MAX_FRACTION)) * 0.5F;
        mDrawWidthDimension = MIN_FRACTION;
        requestLayout();
    }

    public float getDrawWidthDimension() {
        return mDrawWidthDimension;
    }

    public void setDrawWidthDimension(final float drawWidthDimension) {
        mDrawWidthFraction = MIN_FRACTION;
        mDrawWidthDimension = drawWidthDimension;
        requestLayout();
    }

    public float getShadowDistance() {
        return mShadowDistance;
    }

    public void setShadowDistance(final float shadowDistance) {
        mShadowDistance = shadowDistance;
        resetShadowLayer();
        requestLayout();
    }

    public float getShadowAngle() {
        return mShadowAngle;
    }

    @SuppressLint("SupportAnnotationUsage")
    @FloatRange
    public void setShadowAngle(@FloatRange(from = MIN_ANGLE, to = MAX_ANGLE) final float shadowAngle) {
        mShadowAngle = Math.max(MIN_ANGLE, Math.min(shadowAngle, MAX_ANGLE));
        resetShadowLayer();
        requestLayout();
    }

    public float getShadowRadius() {
        return mShadowRadius;
    }

    public void setShadowRadius(final float shadowRadius) {
        mShadowRadius = shadowRadius > MIN_SHADOW ? shadowRadius : MIN_SHADOW;
        resetShadowLayer();
        requestLayout();
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public void setShadowColor(final int shadowColor) {
        mShadowColor = shadowColor;
        resetShadowLayer();
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(final int textColor) {
        mTextColor = textColor;
        mTextPaint.setColor(textColor);
        postInvalidate();
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public void setTypeface(final String typeface) {
        Typeface tempTypeface;
        try {
            if (isInEditMode()) return;
            tempTypeface = Typeface.createFromAsset(getContext().getAssets(), typeface);
        } catch (Exception e) {
            tempTypeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
            e.printStackTrace();
        }

        setTypeface(tempTypeface);
    }

    public void setTypeface(final Typeface typeface) {
        mTypeface = typeface;
        mTextPaint.setTypeface(typeface);
        postInvalidate();
    }

    public IndicatorOrientation getIndicatorOrientation() {
        return mIndicatorOrientation;
    }

    public void setIndicatorOrientation(final IndicatorOrientation indicatorOrientation) {
        mIndicatorOrientation = indicatorOrientation;
    }

    // Reset shadow layer
    private void resetShadowLayer() {
        if (isInEditMode()) return;

        final float newDx =
                (float) ((mShadowDistance) * Math.cos((mShadowAngle - mStartAngle) / 180.0F * Math.PI));
        final float newDy =
                (float) ((mShadowDistance) * Math.sin((mShadowAngle - mStartAngle) / 180.0F * Math.PI));
        if (mIsShadowed)
            mProgressPaint.setShadowLayer(mShadowRadius, newDx, newDy, mShadowColor);
        else mProgressPaint.clearShadowLayer();
    }

    // Set start elevation pin if gradient round progress
    private void setLevelShadowLayer() {
        if (isInEditMode()) return;

        if (mIsShadowed || mIsLeveled) {
            final float shadowOffset = mShadowRadius * 0.5f;
            mLevelPaint.setShadowLayer(
                    shadowOffset, 0.0f, -shadowOffset, adjustColorAlpha(mShadowColor, 0.5f)
            );
        } else mLevelPaint.clearShadowLayer();
    }

    // Adjust color alpha(used for shadow reduce)
    private int adjustColorAlpha(final int color, final float factor) {
        return Color.argb(
                Math.round(Color.alpha(color) * factor),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    // Animate progress
    public void animateProgress() {
        if (!mIsAnimated || mProgressAnimator == null) return;
        if (mProgressAnimator.isRunning()) {
            if (mAnimatorListener != null) mProgressAnimator.removeListener(mAnimatorListener);
            mProgressAnimator.cancel();
        }
        // Set to animate all models
        mActionMoveModelIndex = ANIMATE_ALL_INDEX;
        mProgressAnimator.setDuration(mAnimationDuration);
        mProgressAnimator.setInterpolator(mInterpolator);
        if (mAnimatorListener != null) {
            mProgressAnimator.removeListener(mAnimatorListener);
            mProgressAnimator.addListener(mAnimatorListener);
        }
        mProgressAnimator.start();
    }

    // Animate progress
    private void animateActionMoveProgress() {
        if (!mIsAnimated || mProgressAnimator == null) return;
        if (mProgressAnimator.isRunning()) return;

        mProgressAnimator.setDuration(DEFAULT_ACTION_MOVE_ANIMATION_DURATION);
        mProgressAnimator.setInterpolator(null);
        if (mAnimatorListener != null) mProgressAnimator.removeListener(mAnimatorListener);
        mProgressAnimator.start();
    }

    // Get the angle of action move model
    private float getActionMoveAngle(final float x, final float y) {
        //Get radius
        final float radius = mSize * 0.5F;

        // Get degrees without offset
        float degrees = (float) ((Math.toDegrees(Math.atan2(y - radius, x - radius)) + 360.0F) % 360.0F);
        if (degrees < 0) degrees += 2.0F * Math.PI;

        // Get point with offset relative to start angle
        final float newActionMoveX =
                (float) (radius * Math.cos((degrees - mStartAngle) / 180.0F * Math.PI));
        final float newActionMoveY =
                (float) (radius * Math.sin((degrees - mStartAngle) / 180.0F * Math.PI));

        // Set new angle with offset
        degrees = (float) ((Math.toDegrees(Math.atan2(newActionMoveY, newActionMoveX)) + 360.0F) % 360.0F);
        if (degrees < 0) degrees += 2.0F * Math.PI;

        return degrees;
    }

    private void handleActionMoveModel(final MotionEvent event) {
        if (mActionMoveModelIndex == DISABLE_ANIMATE_INDEX) return;

        // Get current move angle
        float currentAngle = getActionMoveAngle(event.getX(), event.getY());

        // Check if angle in slice zones
        final int actionMoveCurrentSlice;
        if (currentAngle > MIN_ANGLE && currentAngle < POSITIVE_ANGLE)
            actionMoveCurrentSlice = POSITIVE_SLICE;
        else if (currentAngle > NEGATIVE_ANGLE && currentAngle < MAX_ANGLE)
            actionMoveCurrentSlice = NEGATIVE_SLICE;
        else actionMoveCurrentSlice = DEFAULT_SLICE;

        // Check for handling counter
        if (actionMoveCurrentSlice != 0 &&
                ((mActionMoveLastSlice == NEGATIVE_SLICE && actionMoveCurrentSlice == POSITIVE_SLICE) ||
                        (actionMoveCurrentSlice == NEGATIVE_SLICE && mActionMoveLastSlice == POSITIVE_SLICE))) {
            if (mActionMoveLastSlice == NEGATIVE_SLICE) mActionMoveSliceCounter++;
            else mActionMoveSliceCounter--;

            // Limit counter for 1 and -1, we don`t need take the race
            if (mActionMoveSliceCounter > 1) mActionMoveSliceCounter = 1;
            else if (mActionMoveSliceCounter < -1) mActionMoveSliceCounter = -1;
        }
        mActionMoveLastSlice = actionMoveCurrentSlice;

        // Set total traveled angle
        float actionMoveTotalAngle = currentAngle + (MAX_ANGLE * mActionMoveSliceCounter);
        final Model model = (Model) mModels.get(mActionMoveModelIndex);

        // Check whether traveled angle out of limit
        if (actionMoveTotalAngle < MIN_ANGLE || actionMoveTotalAngle > MAX_ANGLE) {
            actionMoveTotalAngle =
                    actionMoveTotalAngle > MAX_ANGLE ? MAX_ANGLE + 1.0F : -1.0F;
            currentAngle = actionMoveTotalAngle;
        }

        // Set model progress and invalidate
        float touchProgress = Math.round(MAX_PROGRESS / mSweepAngle * currentAngle);
        model.mProgress = touchProgress;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (!mIsDragged) return super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionMoveModelIndex = DISABLE_ANIMATE_INDEX;
                // Get current move angle and check whether touched angle is in sweep angle zone
                float currentAngle = getActionMoveAngle(event.getX(), event.getY());
                if (currentAngle > mSweepAngle && currentAngle < MAX_ANGLE) break;

                for (int i = 0; i < mModels.size(); i++) {
                    final Model model = (Model) mModels.get(i);
                    // Check if our model contains touch points
                    if (model.mBounds.contains(event.getX(), event.getY())) {
                        // Check variables for handle touch in progress model zone
                        float modelRadius = model.mBounds.width() * 0.5F;
                        float modelOffset = mProgressModelSize * 0.5F;
                        float mainRadius = mSize * 0.5F;

                        // Get distance between 2 points
                        final float distance = (float) Math.sqrt(Math.pow(event.getX() - mainRadius, 2) +
                                Math.pow(event.getY() - mainRadius, 2));
                        if (distance > modelRadius - modelOffset && distance < modelRadius + modelOffset) {
                            mActionMoveModelIndex = i;
                            mIsActionMoved = true;
                            handleActionMoveModel(event);
                            animateActionMoveProgress();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActionMoveModelIndex == DISABLE_ANIMATE_INDEX && !mIsActionMoved) break;
                if (mProgressAnimator.isRunning()) break;
                handleActionMoveModel(event);
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            default:
                // Reset values
                mActionMoveLastSlice = DEFAULT_SLICE;
                mActionMoveSliceCounter = 0;
                mIsActionMoved = false;
                break;
        }

        // If we have parent, so requestDisallowInterceptTouchEvent
        if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(true);

        return true;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        // 单位px
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int height = MeasureSpec.getSize(heightMeasureSpec);

        // Get size for square dimension
        if (width > height) mSize = height;
        else mSize = width;

        // divider = 控件宽或高其中之一 * 百分比 = 条条总距离的一半（百分比默认值为0.7,设置之后mDrawWidthFraction只有百分比的一半,表示一半的条条）
        final float divider = mDrawWidthFraction == 0 ? mDrawWidthDimension : mSize * mDrawWidthFraction;
        // 表示一个条条的距离（距离就是宽或高,因为宽和高都是一样的,正方形的控件）
        mProgressModelSize = divider / (mModels.size() - 1);
        //画条条的原理是在条条的中心点画,然后把画笔宽度设置为条条宽度,所以paintOffset为条条的一半
        final float paintOffset = mProgressModelSize * 0.5F;
        //mIsShadowed默认为true,mShadowRadius和mShadowDistance也都有默认值
        final float shadowOffset = mIsShadowed ? (mShadowRadius + mShadowDistance) : 0.0F;

        // Set bound with offset for models
        for (int i = 0; i < mModels.size(); i++) {
            final Model model = (Model) mModels.get(i);
            final float modelOffset = (mProgressModelSize * i) +
                    (paintOffset + shadowOffset) - (mProgressModelOffset * i);

            //画条条需要一个正方形,第一个条条是最外围的
            model.mBounds.set(
                    modelOffset, modelOffset,
                    mSize - modelOffset, mSize - modelOffset
            );

            //设置的是条条的颜色
            if (model.mColors != null && model.mSweepGradient == null)
                model.mSweepGradient = new SweepGradient(
                        model.mBounds.centerX(), model.mBounds.centerY(), model.mColors, null
                );
        }
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        final float radius = mSize * 0.5F;
        for (int ii = 0; ii < mModels.size(); ii++) {
            final Model model = (Model) mModels.get(ii);
            if (!model.mUseCenter) {
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
                // 设置字体画笔的参数,getTextBounds是把最后一个参数（一个长方形或正方形,要画的字只能画在这个框框里）设置好,setTextSize的单位是px
                mTextPaint.setTextSize(mProgressModelSize * 0.4F);
                String text = TextUtils.ellipsize(model.text, mTextPaint, radius, TextUtils.TruncateAt.END).toString();
                mTextPaint.getTextBounds(
                        text, 0, text.length(),
                        model.mTextBounds
                );
                float titleX = radius - mShadowRadius - mShadowDistance;
                float titleY = model.mBounds.top + model.mTextBounds.height() * 0.5F;
                canvas.drawText(text, titleX, titleY, mTextPaint);


                mTextPaint.setTextAlign(Paint.Align.CENTER);
                final String percentProgress = String.format(Locale.CHINA, "%d%%", (int) model.mProgress);
                mTextPaint.setTextSize(mProgressModelSize * 0.25f);
                mTextPaint.getTextBounds(
                        percentProgress, 0, percentProgress.length(), model.mTextBounds
                );
                canvas.drawText(
                        percentProgress,
                        model.mBounds.left,
                        model.mBounds.centerY() - model.mTextBounds.height(),
                        mTextPaint
                );
            } else {
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                mTextPaint.setTextSize(model.mBounds.height() * 0.35F);
                mTextPaint.getTextBounds(
                        model.text, 0, model.text.length(),
                        model.mTextBounds
                );
                canvas.drawText(model.text, radius, radius, mTextPaint);
            }
        }
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        canvas.save();
        // Shader（着色器）要旋转画布才行, 还有pathMeasure.getPosTan也是旋转画布比较好, 因为getPosTan记录了xy的坐标
        canvas.rotate(mStartAngle, radius, radius);

        for (int i = 0; i < mModels.size() - 1; i++) {
            final Model model = (Model) mModels.get(i);
            //progressFraction范围是0-1,用model的progress（范围是0-100）除于100
            float progressFraction = mIsAnimated && !isInEditMode() ? (model.mLastProgress + (mAnimatedFraction *
                    (model.mProgress - model.mLastProgress))) / MAX_PROGRESS :
                    model.mProgress / MAX_PROGRESS;
            if (i != mActionMoveModelIndex && mActionMoveModelIndex != ANIMATE_ALL_INDEX)
                progressFraction = model.mProgress / MAX_PROGRESS;
            // progress是最终的角度数
            final float progress = progressFraction * mSweepAngle;

            // Check if model have gradient
            final boolean isGradient = model.mColors != null;
            // Set width of progress
            mProgressPaint.setStrokeWidth(mProgressModelSize);
            // Set model arc progress
            model.mPath.reset();
            // 第二个参数是起始角度,第三个参数是 要扫过的角度,这时候还没开始画,只是把参数设置给了Path
            // 这个圆弧路径的起始点是条条的中心点, 所有的操作都在这条圆弧上进行
            // path的长度在这里不是360度的, 而是画多少长度就是多少
            model.mPath.addArc(model.mBounds, 0.0F, progress);

            // Draw gradient progress or solid
            resetShadowLayer();
            mProgressPaint.setShader(null);
            mProgressPaint.setStyle(Paint.Style.STROKE);

            if (mIsModelBgEnabled) {
                //画条条的背景
                mProgressPaint.setColor(isInEditMode() ? mPreviewModelBgColor : model.mBgColor);
                canvas.drawArc(model.mBounds, 0.0F, mSweepAngle, false, mProgressPaint);
                if (!isInEditMode()) mProgressPaint.clearShadowLayer();
            }

            // 设置画笔颜色,判断颜色是否渐变,model设置多个颜色的话就为true
            if (isGradient) {
                if (!mIsModelBgEnabled) {
                    canvas.drawPath(model.mPath, mProgressPaint);

                    if (!isInEditMode()) mProgressPaint.clearShadowLayer();
                }
                //mSweepGradient是一个关于渐变颜色的类,具体上网查
                mProgressPaint.setShader(model.mSweepGradient);
            } else mProgressPaint.setColor(model.mColor);

            // 终于画条条了...
            mProgressPaint.setAlpha(255);
            canvas.drawPath(model.mPath, mProgressPaint);

            // 如果是编辑模式就直接开始画下一个条条
//            if (isInEditMode()) continue;

            // Get pos and tan at final path point
//            model.mPathMeasure.setPath(model.mPath, false);
//            model.mPathMeasure.getPosTan(model.mPathMeasure.getLength(), model.mPos, model.mTan);


            // Check if gradient and have rounded corners, because we must to create elevation effect
            // for start progress corner
//            if ((isGradient || mIsLeveled) && mIsRounded && progress != 0) {
//                model.mPathMeasure.getPosTan(0.0F, model.mPos, model.mTan);
//
//                // Set paint for overlay rounded gradient with shadow
//                setLevelShadowLayer();
//                //noinspection ResourceAsColor
//                mLevelPaint.setColor(isGradient ? model.mColors[0] : model.mColor);
//
//                // Get bounds of start pump
//                final float halfSize = mProgressModelSize * 0.5F;
//                final RectF arcRect = new RectF(
//                        model.mPos[0] - halfSize, model.mPos[1] - halfSize,
//                        model.mPos[0] + halfSize, model.mPos[1] + halfSize + 2.0F
//                );
//                canvas.drawArc(arcRect, 0.0F, -180.0F, true, mLevelPaint);
//            }
        }

        // Restore after drawing
        canvas.restore();
    }

    public void update() {
        for (int i = 0; i < mModels.size(); i++) {
            ((Model) mModels.get(i)).update(null, null);
        }
        invalidate();
    }

    public static class Model extends AbsView {
        private final float mDecimal;
        private String mTitle;
        private String mUnit;
        private String text = "";
        private float mLastProgress = MIN_PROGRESS;
        private float mProgress = MIN_PROGRESS;
        private final float mMin;
        private final float mMax;
        private final boolean mUseCenter;

        private int mColor;
        private int mBgColor;
        private int[] mColors;

        private StringBuilder stringBuilder = new StringBuilder();

        private final RectF mBounds = new RectF();
        private final Rect mTextBounds = new Rect();

        private final Path mPath = new Path();
        private SweepGradient mSweepGradient;

        public Model(final Parameter parameter, final int positionOfValues, final float decimal, final String title, final String unit, final float min, final float max, final boolean useCenter) {
            super(parameter, positionOfValues);
            mDecimal = decimal;
            mTitle = title;
            mUnit = unit;
            mMin = min;
            mMax = max;
            mUseCenter = useCenter;
            stringBuilder.append(title).append(unit);
        }

        @Override
        public void update(View v, Context context) {
            if (parameter.getValue(positionOfValues) == -1) {
                text = stringBuilder.replace(mTitle.length(), stringBuilder.length() - mUnit.length(), "???").toString();
            } else {
                float fValue = Math.max(mMin, Math.min(parameter.getValue(positionOfValues), mMax));
                mLastProgress = mProgress;
                final float progress = (fValue - mMin) / (mMax - mMin) * 100;
                mProgress = (int) Math.max(MIN_PROGRESS, Math.min(progress, MAX_PROGRESS));
                if (mDecimal != 1) {
                    fValue = fValue / mDecimal;
                    text = stringBuilder.replace(mTitle.length(), stringBuilder.length() - mUnit.length(), String.valueOf(fValue)).toString();
                } else {
                    text = stringBuilder.replace(mTitle.length(), stringBuilder.length() - mUnit.length(), String.valueOf((int) fValue)).toString();
                }
            }
        }

        public void setColor(final int color) {
            mColor = color;
        }

        public void setBgColor(final int bgColor) {
            mBgColor = bgColor;
        }

        public void setColors(final int[] colors) {
            if (colors != null && colors.length >= 2) mColors = colors;
            else mColors = null;
        }
    }

    private enum IndicatorOrientation {
        HORIZONTAL, VERTICAL
    }
}
