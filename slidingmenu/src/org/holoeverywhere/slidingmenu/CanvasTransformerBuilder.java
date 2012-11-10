
package org.holoeverywhere.slidingmenu;

import org.holoeverywhere.slidingmenu.SlidingMenu.CanvasTransformer;

import android.graphics.Canvas;
import android.view.animation.Interpolator;

public class CanvasTransformerBuilder {

    private static Interpolator lin = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            return t;
        }
    };

    private CanvasTransformer mTrans;

    public CanvasTransformer concatTransformer(final CanvasTransformer t) {
        initTransformer();
        mTrans = new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                mTrans.transformCanvas(canvas, percentOpen);
                t.transformCanvas(canvas, percentOpen);
            }
        };
        return mTrans;
    }

    private void initTransformer() {
        if (mTrans == null) {
            mTrans = new CanvasTransformer() {
                @Override
                public void transformCanvas(Canvas canvas, float percentOpen) {
                }
            };
        }
    }

    public CanvasTransformer rotate(final int openedDeg, final int closedDeg,
            final int px, final int py) {
        return rotate(openedDeg, closedDeg, px, py, lin);
    }

    public CanvasTransformer rotate(final int openedDeg, final int closedDeg,
            final int px, final int py, final Interpolator interp) {
        initTransformer();
        mTrans = new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                mTrans.transformCanvas(canvas, percentOpen);
                float f = interp.getInterpolation(percentOpen);
                canvas.rotate((openedDeg - closedDeg) * f + closedDeg,
                        px, py);
            }
        };
        return mTrans;
    }

    public CanvasTransformer translate(final int openedX, final int closedX,
            final int openedY, final int closedY) {
        return translate(openedX, closedX, openedY, closedY, lin);
    }

    public CanvasTransformer translate(final int openedX, final int closedX,
            final int openedY, final int closedY, final Interpolator interp) {
        initTransformer();
        mTrans = new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                mTrans.transformCanvas(canvas, percentOpen);
                float f = interp.getInterpolation(percentOpen);
                canvas.translate((openedX - closedX) * f + closedX,
                        (openedY - closedY) * f + closedY);
            }
        };
        return mTrans;
    }

    public CanvasTransformer zoom(final int openedX, final int closedX,
            final int openedY, final int closedY,
            final int px, final int py) {
        return zoom(openedX, closedX, openedY, closedY, px, py, lin);
    }

    public CanvasTransformer zoom(final int openedX, final int closedX,
            final int openedY, final int closedY,
            final int px, final int py, final Interpolator interp) {
        initTransformer();
        mTrans = new CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                mTrans.transformCanvas(canvas, percentOpen);
                float f = interp.getInterpolation(percentOpen);
                canvas.scale((openedX - closedX) * f + closedX,
                        (openedY - closedY) * f + closedY, px, py);
            }
        };
        return mTrans;
    }

}
