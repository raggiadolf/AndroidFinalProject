package com.mycompany.dotsproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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

    private Rect m_rect       = new Rect();
    private Paint m_rectPaint = new Paint();

    private RectF m_dot      = new RectF();
    private Paint m_dotPaint = new Paint();

    private Path m_path       = new Path();
    private Paint m_pathPaint = new Paint();

    private List<Point> m_cellPath = new ArrayList<Point>();

    private List<Integer> m_dotColors = new ArrayList<>();

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_rectPaint.setColor(Color.BLACK);
        m_rectPaint.setStyle(Paint.Style.STROKE);
        m_rectPaint.setStrokeWidth(2);
        m_rectPaint.setAntiAlias(true);

        m_dotPaint.setStyle(Paint.Style.FILL);
        m_dotPaint.setAntiAlias(true);

        m_pathPaint.setColor(Color.BLACK); // TODO: Do this in getColors() instead?
        m_pathPaint.setStrokeWidth(10);
        m_pathPaint.setStrokeJoin(Paint.Join.ROUND);
        m_pathPaint.setStrokeCap(Paint.Cap.ROUND);
        m_pathPaint.setStyle(Paint.Style.STROKE);
        m_pathPaint.setAntiAlias(true);

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
     *
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

                m_dotPaint.setColor(m_dotColors.get(new Random().nextInt(m_dotColors.size()))); // TODO: Instantiate random somewhere else
                canvas.drawOval(m_dot, m_dotPaint);
            }
        }

        if(!m_cellPath.isEmpty()) {
            m_path.reset();
            Point point = m_cellPath.get(0);
            m_path.moveTo(colToX(point.x) + (m_cellWidth / 2), rowToY(point.y) + (m_cellHeight / 2));

            for(int i = 1; i < m_cellPath.size(); i++) {
                point = m_cellPath.get(i);
                m_path.lineTo(colToX(point.x) + (m_cellWidth / 2), rowToY(point.y) + (m_cellHeight / 2));
            }

            canvas.drawPath(m_path, m_pathPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            m_cellPath.add(new Point(xToCol(x), yToRow(y)));
        } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if(!m_cellPath.isEmpty()) {
                int col = xToCol(x);
                int row = yToRow(y);
                Point last = m_cellPath.get(m_cellPath.size() - 1);
                if(col != last.x || row != last.y) {
                    m_cellPath.add(new Point(col, row));
                }
                invalidate();
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            m_cellPath.clear();
            invalidate();
        }
        return true;
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

    private int xToCol(int x) {
        return (x - getPaddingLeft()) / m_cellWidth;
    }

    private int yToRow(int y) {
        return (y - getPaddingTop()) / m_cellHeight;
    }

    private int colToX(int col) {
        return col * m_cellWidth + getPaddingLeft();
    }

    private int rowToY(int row) {
        return row * m_cellHeight + getPaddingTop();
    }

    private void snapToGrid(RectF circle) {
        int col = xToCol((int)circle.left);
        int row = yToRow((int)circle.top);

        int x = colToX(col) + (int) (m_cellWidth - circle.width()) / 2;
        int y = rowToY(row) + (int) (m_cellHeight - circle.height()) / 2;

        circle.offsetTo(x, y);
    }
}
