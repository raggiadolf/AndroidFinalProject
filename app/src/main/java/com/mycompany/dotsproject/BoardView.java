package com.mycompany.dotsproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class BoardView extends View {

    private MovesGameActivity context = (MovesGameActivity) getContext();

    private int num_cells;
    private int m_cellWidth;
    private int m_cellHeight;

    private Paint m_dotPaint = new Paint();

    private Path m_path       = new Path();
    private Paint m_pathPaint = new Paint();

    private float dotWidth;

    private List<Point> m_cellPath = new ArrayList<>();

    private List<Integer> m_dotColors = new ArrayList<>();

    private List<ArrayList<Integer>> m_boardDotColors = new ArrayList<>();
    private List<ArrayList<RectF>> m_boardDots = new ArrayList<>();

    private OnMoveEventHandler m_moveHandler = null;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_dotPaint.setStyle(Paint.Style.FILL);
        m_dotPaint.setAntiAlias(true);

        m_pathPaint.setColor(Color.BLACK); // TODO: Do this in getColors() instead?
        m_pathPaint.setStrokeWidth(10);
        m_pathPaint.setStrokeJoin(Paint.Join.ROUND);
        m_pathPaint.setStrokeCap(Paint.Cap.ROUND);
        m_pathPaint.setStyle(Paint.Style.STROKE);
        m_pathPaint.setAntiAlias(true);

        m_dotColors = getColors();

        num_cells = this.context.getSize();

        for(int row = 0; row < num_cells; row++) {
            m_boardDotColors.add(new ArrayList<Integer>());
            m_boardDots.add(new ArrayList<RectF>());
            for(int col = 0; col < num_cells; col++) {
                m_boardDotColors.get(row).add(col, null);
                m_boardDots.get(row).add(col, new RectF());
            }
        }

        updateBoard();
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

        m_cellWidth = boardWidth / num_cells;
        m_cellHeight = boardHeight / num_cells;

        for(int row = 0; row < num_cells; row++) {
            for(int col = 0; col < num_cells; col++) {
                int x = col * m_cellWidth;
                int y = row * m_cellHeight;
                m_boardDots.get(row).get(col).set(x, y, x + m_cellWidth, y + m_cellHeight);
                m_boardDots.get(row).get(col).offset(getPaddingLeft(), getPaddingTop());
                m_boardDots.get(row).get(col).inset(m_cellWidth * 0.1f, m_cellHeight * 0.1f);
            }
        }

        dotWidth = m_boardDots.get(0).get(0).width();
    }

    /**
     * TODO: Document
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {

        for(int row = 0; row < num_cells; row++) {
            for(int col = 0; col < num_cells; col++) {
                m_dotPaint.setColor(m_boardDotColors.get(row).get(col));
                canvas.drawOval(m_boardDots.get(row).get(col), m_dotPaint);
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

    /**
     * TODO: Document, Refactor
     * @param row
     * @param col
     */
    public void startAnimation(final int row, final int col) {
        final RectF dot = m_boardDots.get(row).get(col);
        ValueAnimator anim1 = new ValueAnimator();
        anim1.setDuration(500);
        anim1.setFloatValues(0.0f, 1.0f);
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                float diffHorizontal = (dot.right - dot.left) * (ratio - 1f);
                float diffVertical = (dot.bottom - dot.top) * (ratio - 1f);
                dot.top    -= diffVertical / 8f;
                dot.bottom += diffVertical / 8f;

                dot.left  -= diffHorizontal / 8f;
                dot.right += diffHorizontal / 8f;
                invalidate();
            }
        });
        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                randomizeDotColor(row, col);
            }
        });

        ValueAnimator anim2 = new ValueAnimator();
        anim2.setDuration(500);
        anim2.setFloatValues(0.0f, 1.0f);
        anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float ratio = (float) animation.getAnimatedValue();
                float diffHorizontal = (dotWidth - (dot.right - dot.left)) * (ratio - 1f);
                float diffVertical = (dotWidth - (dot.bottom - dot.top)) * (ratio - 1f);
                dot.top    += diffVertical / 8f;
                dot.bottom -= diffVertical / 8f;

                dot.left  += diffHorizontal / 8f;
                dot.right -= diffHorizontal / 8f;
                invalidate();
            }
        });

        AnimatorSet set = new AnimatorSet();

        set.playSequentially(anim1, anim2);
        set.start();
    }

    /**
     * TODO: DOCUMENT!
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            m_cellPath.add(new Point(xToCol(x), yToRow(y)));
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if(!m_cellPath.isEmpty()) {
                int col = xToCol(x);
                int row = yToRow(y);
                Point last = m_cellPath.get(m_cellPath.size() - 1);

                if (!m_cellPath.contains(new Point(col, row))
                        && checkIfCellIsLegal(row, col, last.y, last.x)) {
                    m_cellPath.add(new Point(col, row));
                    if(context.useSound()) {
                        // TODO: Play dotsgone for path
                    }
                } else if(m_cellPath.size() > 1){ // Remove if backtracking
                    Point secondToLast = m_cellPath.get(m_cellPath.size() - 2);
                    if(row == secondToLast.y && col == secondToLast.x) {
                        m_cellPath.remove(m_cellPath.size() - 1);
                        if(context.useSound()) {
                            // TODO: Play dotsgone for path removal
                        }
                    }
                }
                invalidate();
            }
        }
        else if(event.getAction() == MotionEvent.ACTION_UP) {
            if(m_cellPath.size() > 1) {
                for (Point p : m_cellPath) {
                    startAnimation(p.y, p.x);
                }
                if(m_moveHandler != null) {
                    m_moveHandler.onMove(m_cellPath.size());
                }
            }
            m_cellPath.clear();
        }
        return true;
    }

    /**
     * TODO: We should make this method take in an indicator of theme
     * and then choose the colors based off of that.
     * @return A list of integers which represent colors.
     */
    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);
        colors.add(Color.CYAN);

        return colors;
    }

    /**
     * TODO: Document, refactor, rename to setupBoard?
     */
    private void updateBoard() {
        for(int row = 0; row < num_cells; row++) {
            for(int col = 0; col < num_cells; col++) {
                if(m_boardDotColors.get(row).get(col) == null) {
                    m_boardDotColors.get(row).set(col, m_dotColors.get(new Random().nextInt(m_dotColors.size())));
                }
            }
        }
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

    private boolean checkIfCellIsLegal(int currRow, int currCol, int lastRow, int lastCol) {
        return currRow < num_cells && currRow >= 0 && currCol < num_cells && currCol >= 0
                && m_boardDotColors.get(currRow).get(currCol).equals(m_boardDotColors.get(lastRow).get(lastCol))
                && ((Math.abs(currRow - lastRow) == 1 && currCol == lastCol)
                || (Math.abs(currCol - lastCol) == 1 && currRow == lastRow));
    }

    public void setMoveEventHandler( OnMoveEventHandler handler ) {
        m_moveHandler = handler;
    }

    public int getBoardSize() {
        return num_cells;
    }

    private void randomizeDotColor(int row, int col) {
        m_boardDotColors.get(row).set(col, m_dotColors.get(new Random().nextInt(m_dotColors.size())));
    }

    public void shuffleBoard() {
        for(ArrayList<Integer> list : m_boardDotColors) {
            Collections.shuffle(list);
        }

        Collections.shuffle(m_boardDotColors);
        invalidate();
    }
}
