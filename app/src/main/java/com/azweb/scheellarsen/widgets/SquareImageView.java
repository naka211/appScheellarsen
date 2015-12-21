package com.azweb.scheellarsen.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Thaind on 11/10/2015.
 */
public class SquareImageView extends ImageView {
    private int squareWidth = 0;

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int)(widthMeasureSpec * 0.75);
        int height = width;
        setMeasuredDimension(width, height);
    }
}
