package com.mycompany.dotsproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends View {

    private final int NUM_CELLS = 6;
    private int m_cellWidth;
    private int m_cellHeight;

    private Rect m_rect = new Rect();
    private Paint m_rectPaint = new Paint();

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_rectPaint.setColor(Color.BLACK);
        m_rectPaint.setStyle(Paint.Style.STROKE);
        m_rectPaint.setStrokeWidth(2);
        m_rectPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width  = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int size = Math.min(width, height);
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(),
                size + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        int boardWidth = (xNew - getPaddingLeft() - getPaddingRight());
        int boardHeight = (yNew - getPaddingTop() - getPaddingBottom());

        m_cellWidth = boardWidth / NUM_CELLS;
        m_cellHeight = boardHeight / NUM_CELLS;
    }


    /**
     * TODO: Draw the grid which will hold our dots.
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(m_rect, m_rectPaint);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
