package com.azweb.scheellarsen.widgets;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author Niko View Container to place the view wanted to be dragged
 */
public class DragDropView extends FrameLayout {
    private int mTopMagin;
    private int mLeftMagin;
    private Context mContext;

    /**
     * Default Constructor
     *
     * @param context
     */
    public DragDropView(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * Default Constructor
     *
     * @param context
     * @param attrs
     */
    public DragDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     * Default Constructor
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public DragDropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    /**
     * Adding draggable object to the dragView
     *
     * @param - x horizontal position of the view
     * @param - y vertical position of the view
     * @param - width width of the view
     * @param - height height of the view
     */
    public void AddDraggableView(View draggableObject, int x, int y, int width,
                                 int height) {
        LayoutParams lpDraggableView = new LayoutParams(width, height);
        lpDraggableView.gravity = Gravity.CENTER;
        lpDraggableView.leftMargin = x;
        lpDraggableView.topMargin = y;
        if (draggableObject instanceof ImageView) {
            ImageView ivDrag = (ImageView) draggableObject;
            ivDrag.setLayoutParams(lpDraggableView);
            ivDrag.setOnTouchListener(OnTouchToDrag);
            this.addView(ivDrag);
        }

    }

    /**
     * Draggable object ontouch listener Handle the movement of the object when
     * dragged and dropped
     */
    private OnTouchListener OnTouchToDrag = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            LayoutParams dragParam = (LayoutParams) v
                    .getLayoutParams();
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE: {
                    if (!isLandscape()) {
                        dragParam.topMargin = (int) event.getRawY() - (int) (v.getHeight() * 1.7);
                        dragParam.leftMargin = (int) event.getRawX() - (int) (v.getWidth() * 0.7);
                    } else {
                        dragParam.topMargin = (int) event.getRawY() - (int) (v.getHeight());
                        dragParam.leftMargin = (int) event.getRawX() - (int) (v.getWidth() * 1.2);
                    }
                    v.setLayoutParams(dragParam);
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    dragParam.height = v.getHeight();
                    dragParam.width = v.getWidth();
                    if (!isLandscape()) {
                        dragParam.topMargin = (int) event.getRawY() - (int) (v.getHeight() * 1.7);
                        dragParam.leftMargin = (int) event.getRawX() - (int) (v.getWidth() * 0.7);
                    } else {
                        dragParam.topMargin = (int) event.getRawY() - (int) (v.getHeight());
                        dragParam.leftMargin = (int) event.getRawX() - (int) (v.getWidth() * 1.2);
                    }
                    v.setLayoutParams(dragParam);
                    break;
                }
                case MotionEvent.ACTION_DOWN: {
                    dragParam.height = v.getHeight();
                    dragParam.width = v.getWidth();
                    v.setLayoutParams(dragParam);

                    break;
                }
            }
            return true;
        }

    };

    public int getTopMaginCurrent() {
        return mTopMagin;
    }

    public int getLeftMaginCurrent() {
        return mLeftMagin;
    }

    public boolean isLandscape() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return true;
        } else {
            return false;
        }
    }
}
