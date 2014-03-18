/*
 * Copyright (C) 2008 The Android Open Source Project
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

package org.holoeverywhere.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.R;

import java.lang.ref.WeakReference;

public class ViewStubHolo extends View {
    private int mLayoutResource = 0;
    private int mInflatedId;
    private WeakReference<View> mInflatedViewRef;
    private LayoutInflater mInflater;
    private OnInflateListener mInflateListener;

    public ViewStubHolo(Context context) {
        super(context);
        initialize();
    }

    public ViewStubHolo(Context context, int layoutResource) {
        super(context);
        mLayoutResource = layoutResource;
        initialize();
    }

    public ViewStubHolo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ViewStubHolo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewStub, defStyle, 0);
        mInflatedId = a.getResourceId(R.styleable.ViewStub_android_inflatedId, NO_ID);
        mLayoutResource = a.getResourceId(R.styleable.ViewStub_android_layout, 0);
        a.recycle();
        initialize();
    }

    private void initialize() {
        setVisibility(GONE);
        setWillNotDraw(true);
    }

    public int getInflatedId() {
        return mInflatedId;
    }

    public void setInflatedId(int inflatedId) {
        mInflatedId = inflatedId;
    }

    public int getLayoutResource() {
        return mLayoutResource;
    }

    public void setLayoutResource(int layoutResource) {
        mLayoutResource = layoutResource;
    }

    public void setLayoutInflater(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(0, 0);
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
    }

    @Override
    public void setVisibility(int visibility) {
        if (mInflatedViewRef != null) {
            View view = mInflatedViewRef.get();
            if (view != null) {
                view.setVisibility(visibility);
            } else {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
        } else {
            super.setVisibility(visibility);
            if (visibility == VISIBLE || visibility == INVISIBLE) {
                inflate();
            }
        }
    }

    public View inflate() {
        final ViewParent viewParent = getParent();

        if (viewParent != null && viewParent instanceof ViewGroup) {
            if (mLayoutResource != 0) {
                final ViewGroup parent = (ViewGroup) viewParent;
                final LayoutInflater factory;
                if (mInflater != null) {
                    factory = mInflater;
                } else {
                    factory = LayoutInflater.from(getContext());
                }
                final View view = factory.inflate(mLayoutResource, parent,
                        false);

                if (mInflatedId != NO_ID) {
                    view.setId(mInflatedId);
                }

                final int index = parent.indexOfChild(this);
                parent.removeViewInLayout(this);

                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                if (layoutParams != null) {
                    parent.addView(view, index, layoutParams);
                } else {
                    parent.addView(view, index);
                }

                mInflatedViewRef = new WeakReference<View>(view);

                if (mInflateListener != null) {
                    mInflateListener.onInflate(this, view);
                }

                return view;
            } else {
                throw new IllegalArgumentException("ViewStubHolo must have a valid layoutResource");
            }
        } else {
            throw new IllegalStateException("ViewStubHolo must have a non-null ViewGroup viewParent");
        }
    }

    public void setOnInflateListener(OnInflateListener inflateListener) {
        mInflateListener = inflateListener;
    }

    public static interface OnInflateListener {
        void onInflate(ViewStubHolo stub, View inflated);
    }
}
