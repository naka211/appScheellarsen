package com.azweb.scheellarsen.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ThaiNguyen on 7/10/2015.
 */
public class RectangleImageView extends ImageView {
    private int squareWidth = 0;

    public RectangleImageView(Context context) {
        super(context);
    }

    public RectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RectangleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = (width * 2) / 3;
        setMeasuredDimension(width, height);
    }
}
