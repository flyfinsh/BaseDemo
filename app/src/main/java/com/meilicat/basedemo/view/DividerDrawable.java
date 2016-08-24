package com.meilicat.basedemo.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class DividerDrawable extends Drawable {
    private Paint paint;
    private int leftCornerHight;
    private int rightCornerHight;
    private DividerOrientation orientation = DividerOrientation.BOTTOM;

    public DividerDrawable()
    {
        super();
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {

        Rect bounds = getBounds();
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;
        switch (orientation) {
        case LEFT:
            left = bounds.left;
            top = bounds.top;
            right = bounds.left;
            bottom = bounds.bottom;
            break;
        case TOP:
            break;
        case RIGHT:

            break;
        case BOTTOM:
            // 画底线
            left = bounds.left;
            top = bounds.bottom;
            right = bounds.right;
            bottom = bounds.bottom;
            // 左边弯钩
            if (leftCornerHight > 0) {
                canvas.drawLine(0, bounds.bottom - leftCornerHight, 0, bounds.bottom, paint);
            }

            // 右边弯钩
            if (rightCornerHight > 0) {
                canvas.drawLine(bounds.right, bounds.bottom - rightCornerHight, bounds.right, bounds.bottom, paint);
            }
            break;

        default:
            break;
        }
        
        canvas.drawLine(left, top, right, bottom, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    public void setDividerColor(int color) {
        paint.setColor(color);
    }

    public void setStrokeWidth(int width) {
        paint.setStrokeWidth(width);
    }

    public void setLeftCornerHight(int leftCornerHight) {
        this.leftCornerHight = leftCornerHight;
    }

    public void setRightCornerHight(int rightCornerHight) {
        this.rightCornerHight = rightCornerHight;
    }

    public void setDividerOrientaion(DividerOrientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    public static enum DividerOrientation {
        LEFT, TOP, RIGHT, BOTTOM
    }
}
