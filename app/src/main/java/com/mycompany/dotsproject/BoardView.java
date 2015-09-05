package com.mycompany.dotsproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends View {

    private final int NUM_CELLS = 6;
    private int m_cellWidth;
    private int m_cellHeight;

    private Rect m_rect = new Rect();
    private Paint m_rectPaint = new Paint();

    private RectF m_dot = new RectF();
    private Paint m_dotPaint = new Paint();

    private List<Integer> m_dotColors = new ArrayList<>();

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_rectPaint.setColor(Color.BLACK);
        m_rectPaint.setStyle(Paint.Style.STROKE);
        m_rectPaint.setStrokeWidth(2);
        m_rectPaint.setAntiAlias(true);

        m_dotPaint.setStyle(Paint.Style.FILL);
        m_dotPaint.setAntiAlias(true);

        /**
         * TODO: Figure out how to select different colors based on theme/difficulty
         */
        m_dotColors = getColors();
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
        // canvas.drawRect(m_rect, m_rectPaint);

        for(int row = 0; row < NUM_CELLS; row++) {
            for(int col = 0; col < NUM_CELLS; col++) {
                int x = col * m_cellWidth;
                int y = row * m_cellHeight;
                m_rect.set(x, y, x + m_cellWidth, y + m_cellHeight);
                m_rect.offset(getPaddingLeft(), getPaddingTop());
                canvas.drawRect(m_rect, m_rectPaint);

                m_dot.set(x, y, x + m_cellWidth, y + m_cellHeight);
                m_dot.offset(getPaddingLeft(), getPaddingTop());
                m_dot.inset(m_cellWidth * 0.1f, m_cellHeight * 0.1f);

                m_dotPaint.setColor(m_dotColors.get(new Random().nextInt(m_dotColors.size())));
                canvas.drawOval(m_dot, m_dotPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * TODO: We should make this method take in an indicator of difficulty and theme
     * and then choose the colors based off of that.
     * @return A list of integers which represent colors.
     */
    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);

        return colors;
    }
}
