/*
 * Copyright (C) 2013 The Android Open Source Project
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

package org.holoeverywhere.widget.datetimepicker.time;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.nineoldandroids.animation.Keyframe;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.animation.AnimatorProxy;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.R;

/**
 * A view to show a series of numbers in a circular pattern.
 */
public class RadialTextsView extends View {
    private final static String TAG = "RadialTextsView";
    private final Paint mPaint = new Paint();
    private final AnimatorProxy mProxy = AnimatorProxy.NEEDS_PROXY ? AnimatorProxy.wrap(this) : null;
    ObjectAnimator mDisappearAnimator;
    ObjectAnimator mReappearAnimator;
    private boolean mDrawValuesReady;
    private boolean mIsInitialized;
    private Typeface mTypeface;
    private String[] mTexts;
    private String[] mInnerTexts;
    private boolean mIs24HourMode;
    private boolean mHasInnerCircle;
    private float mCircleRadiusMultiplier;
    private float mAmPmCircleRadiusMultiplier;
    private float mNumbersRadiusMultiplier;
    private float mInnerNumbersRadiusMultiplier;
    private float mTextSizeMultiplier;
    private float mInnerTextSizeMultiplier;
    private int mXCenter;
    private int mYCenter;
    private float mCircleRadius;
    private boolean mTextGridValuesDirty;
    private float mTextSize;
    private float mInnerTextSize;
    private float[] mTextGridHeights;
    private float[] mTextGridWidths;
    private float[] mInnerTextGridHeights;
    private float[] mInnerTextGridWidths;
    private float mAnimationRadiusMultiplier;
    private InvalidateUpdateListener mInvalidateUpdateListener;
    private int mNumberTextColor;

    public RadialTextsView(Context context) {
        this(context, null);
    }

    public RadialTextsView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dateTimePickerStyle);
    }

    public RadialTextsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mIsInitialized = false;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateTimePicker, defStyle, R.style.Holo_DateTimePicker);
        mNumberTextColor = a.getColor(R.styleable.DateTimePicker_timeRadialTextColor, 0);
        a.recycle();
    }

    @Override
    public float getAlpha() {
        if (mProxy != null) {
            return mProxy.getAlpha();
        } else {
            return super.getAlpha();
        }
    }

    @Override
    public void setAlpha(float alpha) {
        if (mProxy != null) {
            mProxy.setAlpha(alpha);
        } else {
            super.setAlpha(alpha);
        }
    }

    @Override
    public float getRotation() {
        if (mProxy != null) {
            return mProxy.getRotation();
        } else {
            return super.getRotation();
        }
    }

    @Override
    public void setRotation(float rotation) {
        if (mProxy != null) {
            mProxy.setRotation(rotation);
        } else {
            super.setRotation(rotation);
        }
    }

    public void initialize(Resources res, String[] texts, String[] innerTexts,
                           boolean is24HourMode, boolean disappearsOut) {
        if (mIsInitialized) {
            Log.e(TAG, "This RadialTextsView may only be initialized once.");
            return;
        }

        // Set up the paint.
        mPaint.setColor(mNumberTextColor);
        mTypeface = FontLoader.ROBOTO_REGULAR.getTypeface(getContext());
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Align.CENTER);

        mTexts = texts;
        mInnerTexts = innerTexts;
        mIs24HourMode = is24HourMode;
        mHasInnerCircle = (innerTexts != null);

        // Calculate the radius for the main circle.
        if (is24HourMode) {
            mCircleRadiusMultiplier = Float.parseFloat(
                    res.getString(R.string.time_circle_radius_multiplier_24HourMode));
        } else {
            mCircleRadiusMultiplier = Float.parseFloat(
                    res.getString(R.string.time_circle_radius_multiplier));
            mAmPmCircleRadiusMultiplier =
                    Float.parseFloat(res.getString(R.string.time_ampm_circle_radius_multiplier));
        }

        // Initialize the widths and heights of the grid, and calculate the values for the numbers.
        mTextGridHeights = new float[7];
        mTextGridWidths = new float[7];
        if (mHasInnerCircle) {
            mNumbersRadiusMultiplier = Float.parseFloat(
                    res.getString(R.string.time_numbers_radius_multiplier_outer));
            mTextSizeMultiplier = Float.parseFloat(
                    res.getString(R.string.time_text_size_multiplier_outer));
            mInnerNumbersRadiusMultiplier = Float.parseFloat(
                    res.getString(R.string.time_numbers_radius_multiplier_inner));
            mInnerTextSizeMultiplier = Float.parseFloat(
                    res.getString(R.string.time_text_size_multiplier_inner));

            mInnerTextGridHeights = new float[7];
            mInnerTextGridWidths = new float[7];
        } else {
            mNumbersRadiusMultiplier = Float.parseFloat(
                    res.getString(R.string.time_time_numbers_radius_multiplier_normal));
            mTextSizeMultiplier = Float.parseFloat(
                    res.getString(R.string.time_time_text_size_multiplier_normal));
        }

        mAnimationRadiusMultiplier = 1;
        mInvalidateUpdateListener = new InvalidateUpdateListener();

        mTextGridValuesDirty = true;
        mIsInitialized = true;
    }

    /**
     * Allows for smoother animation.
     */
    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    /**
     * Used by the animation to move the numbers in and out.
     */
    public void setAnimationRadiusMultiplier(float animationRadiusMultiplier) {
        mAnimationRadiusMultiplier = animationRadiusMultiplier;
        mTextGridValuesDirty = true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        int viewWidth = getWidth();
        if (viewWidth == 0 || !mIsInitialized) {
            return;
        }

        if (!mDrawValuesReady) {
            mXCenter = getWidth() / 2;
            mYCenter = getHeight() / 2;
            mCircleRadius = Math.min(mXCenter, mYCenter) * mCircleRadiusMultiplier;
            if (!mIs24HourMode) {
                // We'll need to draw the AM/PM circles, so the main circle will need to have
                // a slightly higher center. To keep the entire view centered vertically, we'll
                // have to push it up by half the radius of the AM/PM circles.
                float amPmCircleRadius = mCircleRadius * mAmPmCircleRadiusMultiplier;
                mYCenter -= amPmCircleRadius / 2;
            }

            mTextSize = mCircleRadius * mTextSizeMultiplier;
            if (mHasInnerCircle) {
                mInnerTextSize = mCircleRadius * mInnerTextSizeMultiplier;
            }

            // Because the text positions will be static, pre-render the animations.
            renderAnimations();

            mTextGridValuesDirty = true;
            mDrawValuesReady = true;
        }

        // Calculate the text positions, but only if they've changed since the last onDraw.
        if (mTextGridValuesDirty) {
            float numbersRadius =
                    mCircleRadius * mNumbersRadiusMultiplier * mAnimationRadiusMultiplier;

            // Calculate the positions for the 12 numbers in the main circle.
            calculateGridSizes(numbersRadius, mXCenter, mYCenter,
                    mTextSize, mTextGridHeights, mTextGridWidths);
            if (mHasInnerCircle) {
                // If we have an inner circle, calculate those positions too.
                float innerNumbersRadius =
                        mCircleRadius * mInnerNumbersRadiusMultiplier * mAnimationRadiusMultiplier;
                calculateGridSizes(innerNumbersRadius, mXCenter, mYCenter,
                        mInnerTextSize, mInnerTextGridHeights, mInnerTextGridWidths);
            }
            mTextGridValuesDirty = false;
        }

        // Draw the texts in the pre-calculated positions.
        drawTexts(canvas, mTextSize, mTypeface, mTexts, mTextGridWidths, mTextGridHeights);
        if (mHasInnerCircle) {
            drawTexts(canvas, mInnerTextSize, mTypeface, mInnerTexts,
                    mInnerTextGridWidths, mInnerTextGridHeights);
        }
    }

    /**
     * Using the trigonometric Unit Circle, calculate the positions that the text will need to be
     * drawn at based on the specified circle radius. Place the values in the textGridHeights and
     * textGridWidths parameters.
     */
    private void calculateGridSizes(float numbersRadius, float xCenter, float yCenter,
                                    float textSize, float[] textGridHeights, float[] textGridWidths) {
        /*
         * The numbers need to be drawn in a 7x7 grid, representing the points on the Unit Circle.
         */
        float offset1 = numbersRadius;
        // cos(30) = a / r => r * cos(30) = a => r * sqrt(3)/2 = a
        float offset2 = numbersRadius * ((float) Math.sqrt(3)) / 2f;
        // sin(30) = o / r => r * sin(30) = o => r / 2 = a
        float offset3 = numbersRadius / 2f;
        mPaint.setTextSize(textSize);
        // We'll need yTextBase to be slightly lower to account for the text's baseline.
        yCenter -= (mPaint.descent() + mPaint.ascent()) / 2;

        textGridHeights[0] = yCenter - offset1;
        textGridWidths[0] = xCenter - offset1;
        textGridHeights[1] = yCenter - offset2;
        textGridWidths[1] = xCenter - offset2;
        textGridHeights[2] = yCenter - offset3;
        textGridWidths[2] = xCenter - offset3;
        textGridHeights[3] = yCenter;
        textGridWidths[3] = xCenter;
        textGridHeights[4] = yCenter + offset3;
        textGridWidths[4] = xCenter + offset3;
        textGridHeights[5] = yCenter + offset2;
        textGridWidths[5] = xCenter + offset2;
        textGridHeights[6] = yCenter + offset1;
        textGridWidths[6] = xCenter + offset1;
    }

    /**
     * Draw the 12 text values at the positions specified by the textGrid parameters.
     */
    private void drawTexts(Canvas canvas, float textSize, Typeface typeface, String[] texts,
                           float[] textGridWidths, float[] textGridHeights) {
        mPaint.setTextSize(textSize);
        mPaint.setTypeface(typeface);
        canvas.drawText(texts[0], textGridWidths[3], textGridHeights[0], mPaint);
        canvas.drawText(texts[1], textGridWidths[4], textGridHeights[1], mPaint);
        canvas.drawText(texts[2], textGridWidths[5], textGridHeights[2], mPaint);
        canvas.drawText(texts[3], textGridWidths[6], textGridHeights[3], mPaint);
        canvas.drawText(texts[4], textGridWidths[5], textGridHeights[4], mPaint);
        canvas.drawText(texts[5], textGridWidths[4], textGridHeights[5], mPaint);
        canvas.drawText(texts[6], textGridWidths[3], textGridHeights[6], mPaint);
        canvas.drawText(texts[7], textGridWidths[2], textGridHeights[5], mPaint);
        canvas.drawText(texts[8], textGridWidths[1], textGridHeights[4], mPaint);
        canvas.drawText(texts[9], textGridWidths[0], textGridHeights[3], mPaint);
        canvas.drawText(texts[10], textGridWidths[1], textGridHeights[2], mPaint);
        canvas.drawText(texts[11], textGridWidths[2], textGridHeights[1], mPaint);
    }

    /**
     * Render the animations for appearing and disappearing.
     */
    private void renderAnimations() {
        Keyframe kf0, kf1;

        // Set up animator for disappearing.
        kf0 = Keyframe.ofFloat(0f, 1f);
        kf1 = Keyframe.ofFloat(1f, 0.8f);
        PropertyValuesHolder radius = PropertyValuesHolder.ofKeyframe(
                "animationRadiusMultiplier", kf0, kf1);

        kf0 = Keyframe.ofFloat(0f, 1f);
        kf1 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder fade = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1);

        kf0 = Keyframe.ofFloat(0f, 0f);
        kf1 = Keyframe.ofFloat(1f, 70f);
        PropertyValuesHolder rotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1);

        mDisappearAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this, radius, fade, rotation).setDuration(300);
        mDisappearAnimator.addUpdateListener(mInvalidateUpdateListener);


        // Set up animator for reappearing.
        kf0 = Keyframe.ofFloat(0f, 0.8f);
        kf1 = Keyframe.ofFloat(1f, 1f);
        radius = PropertyValuesHolder.ofKeyframe(
                "animationRadiusMultiplier", kf0, kf1);

        kf0 = Keyframe.ofFloat(0f, 0f);
        kf1 = Keyframe.ofFloat(1f, 1f);
        fade = PropertyValuesHolder.ofKeyframe("alpha", kf0, kf1);

        kf0 = Keyframe.ofFloat(0f, -70f);
        kf1 = Keyframe.ofFloat(1f, 0f);
        rotation = PropertyValuesHolder.ofKeyframe("rotation", kf0, kf1);

        mReappearAnimator = ObjectAnimator.ofPropertyValuesHolder(
                this, radius, fade, rotation).setDuration(300);
        mReappearAnimator.addUpdateListener(mInvalidateUpdateListener);
    }

    public ObjectAnimator getDisappearAnimator() {
        if (!mIsInitialized || !mDrawValuesReady || mDisappearAnimator == null) {
            Log.e(TAG, "RadialTextView was not ready for animation.");
            return null;
        }

        return mDisappearAnimator;
    }

    public ObjectAnimator getReappearAnimator() {
        if (!mIsInitialized || !mDrawValuesReady || mReappearAnimator == null) {
            Log.e(TAG, "RadialTextView was not ready for animation.");
            return null;
        }

        return mReappearAnimator;
    }

    private class InvalidateUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            RadialTextsView.this.invalidate();
        }
    }
}
