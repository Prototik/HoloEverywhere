
package org.holoeverywhere.graphics;

import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;

public class PointF extends android.graphics.PointF {
    public PointF() {

    }

    public PointF(float x, float y) {
        super(x, y);
    }

    public PointF(MotionEvent event) {
        set(event);
    }

    public PointF(MotionEvent event, int pointerIndex) {
        set(event, pointerIndex);
    }

    public float distance(float x, float y) {
        return (float) Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
    }

    public float distance(MotionEvent event) {
        return distance(event.getX(), event.getY());
    }

    public float distance(MotionEvent event, int pointerIndex) {
        return distance(MotionEventCompat.getX(event, pointerIndex),
                MotionEventCompat.getY(event, pointerIndex));
    }

    public float distance(PointF point) {
        return distance(point.x, point.y);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof PointF) {
            PointF f = (PointF) o;
            return f.x == x && f.y == y;
        } else {
            return false;
        }
    }

    public void set(MotionEvent event) {
        set(event.getX(), event.getY());
    }

    public void set(MotionEvent event, int pointerIndex) {
        set(MotionEventCompat.getX(event, pointerIndex),
                MotionEventCompat.getY(event, pointerIndex));
    }
}
