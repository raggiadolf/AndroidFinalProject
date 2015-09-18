package com.mycompany.dotsproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class represents the gameplay board in our app.
 * It draws the dots and the path.
 * We own a 2d array that holds the color value for each dot
 * and then we own a second 2d array that holds the actual RectF objects
 * that represent the dots.
 *
 * This is done so that the randomization and comparisons of colors
 * is quick, since that is what we do most of.
 *
 * We then work with the RectF array when we are animating the dots,
 * since we then need access to a particular dot to do animation on it.
 *
 * This view also handles the logic that checks whether we can select particular
 * dots in a row and such.
 */
public class BoardView extends View {

    private MovesGameActivity context = (MovesGameActivity) getContext();

    private int num_cells;
    private int m_cellWidth;
    private int m_cellHeight;
    private int m_centerx;
    private int m_centery;

    private Paint m_dotPaint = new Paint();

    private Path m_path       = new Path();
    private Paint m_pathPaint = new Paint();

    private float dotWidth;

    private List<Point> m_cellPath = new ArrayList<>();
    private Point m_endPoint       = new Point();
    private Point m_startPoint     = new Point();

    private List<Integer> m_dotColors = new ArrayList<>();

    private List<ArrayList<Integer>> m_boardDotColors = new ArrayList<>();
    private List<ArrayList<RectF>> m_boardDots        = new ArrayList<>();

    private OnMoveEventHandler m_moveHandler = null;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        m_dotPaint.setStyle(Paint.Style.FILL);
        m_dotPaint.setAntiAlias(true);

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

        setupBoard();
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
     * Goes through the 2d list of colors and colors each dot to its
     * corresponding color.
     * We also draw the path that we are currently tracking.
     * We also draw a single line that follows the finger from a point
     * of origin, which is the currently last selected dot.
     * @param canvas the canvas we draw onto
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
            if(!(m_startPoint.x == 0) && !(m_endPoint.x == 0)) {
                canvas.drawLine(m_startPoint.x, m_startPoint.y, m_endPoint.x, m_endPoint.y, m_pathPaint);
            }
        }
    }

    /**
     * Sets up the animations that make our dots disappear and reapper.
     * The first animation(that makes the dot disappear), also randomizes
     * the color for that particular dot, once its animation ends. This
     * is done so that the user won't notice when the dot changes color.
     *
     * The animation simply gradually changes the size of the dot down to
     * zero, and then the second animation reverses that process.
     * We need the dotWidth variable since we are working with ratios
     * and not fixed sizes.
     *
     * @param row the row which the dot is in
     * @param col the col which the dot is in
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
     * Handles the touch input from the user.
     *
     * Action down: We add the first path, and set
     * the color of the path as that dot's color.
     *
     * Action move: We continue adding to the path
     * if we are on an adjacent, same-colored dot.
     * otherwise we draw a line from the last dot
     * to the finger.
     * We also pop from the path if we are backtracking
     * on it.
     *
     * Action up: We handle the animations for the dots that are
     * on the path. we also use our handler to inform our activity
     * that we just performed a move, sending the score for that move
     * to the activity.
     *
     * @param event the event that was just caught
     * @return true iff we are the ones that handle the event.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            m_cellPath.add(new Point(xToCol(x), yToRow(y)));
            m_pathPaint.setColor(m_boardDotColors.get(yToRow(y)).get(xToCol(x)));
            m_startPoint.set(x, y);
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if(!m_cellPath.isEmpty()) {
                int col = xToCol(x);
                int row = yToRow(y);
                Point last = m_cellPath.get(m_cellPath.size() - 1);

                if (!m_cellPath.contains(new Point(col, row))
                        && checkIfCellIsLegal(row, col, last.y, last.x)) {
                    m_cellPath.add(new Point(col, row));
                } else if(m_cellPath.size() > 1){ // Remove if backtracking
                    Point secondToLast = m_cellPath.get(m_cellPath.size() - 2);
                    if(row == secondToLast.y && col == secondToLast.x) {
                        m_cellPath.remove(m_cellPath.size() - 1);
                    }
                }
                m_centerx = colToX(m_cellPath.get(m_cellPath.size() - 1).x) + m_cellWidth / 2;
                m_centery = rowToY(m_cellPath.get(m_cellPath.size() - 1).y) + m_cellHeight / 2;

                m_startPoint.set(m_centerx, m_centery);
                m_endPoint.set(x, y);
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

            m_startPoint.set(0, 0);
            m_endPoint.set(0, 0);
            invalidate();
        }
        return true;
    }

    /**
     * Sets a list of colors that we use to randomize the board.
     * @return A list of integers which represent colors.
     */
    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.color1));
        colors.add(getResources().getColor(R.color.color2));
        colors.add(getResources().getColor(R.color.color3));
        colors.add(getResources().getColor(R.color.color4));
        colors.add(getResources().getColor(R.color.color5));

        return colors;
    }

    /**
     * Sets up the board originally with random colors.
     */
    private void setupBoard() {
        for(int row = 0; row < num_cells; row++) {
            for(int col = 0; col < num_cells; col++) {
                if(m_boardDotColors.get(row).get(col) == null) {
                    m_boardDotColors.get(row).set(col, m_dotColors.get(new Random().nextInt(m_dotColors.size())));
                }
            }
        }
    }

    /**
     * Helper function to convert x to column
     * @param x the x-coordinate we want to convert
     * @return the x converted to column
     */
    private int xToCol(int x) {
        return (x - getPaddingLeft()) / m_cellWidth;
    }

    /**
     * Helper function to convert y to row
     * @param y the y-coordinate we want to convert
     * @return the y converted to row
     */
    private int yToRow(int y) {
        return (y - getPaddingTop()) / m_cellHeight;
    }

    /**
     * Helper function to convert column to x-coordinate
     * @param col the column we want to convert
     * @return the column converted to a x-coordinate
     */
    private int colToX(int col) {
        return col * m_cellWidth + getPaddingLeft();
    }

    /**
     * Helper function to convert row to y-coordinate
     * @param row the row we want to convert
     * @return the row converted to a y-coordinate
     */
    private int rowToY(int row) {
        return row * m_cellHeight + getPaddingTop();
    }

    /**
     * Checks if the cell we are trying to place a path on is a legal cell.
     * That is, is it out of bounds, does it have the same color, and is it
     * adjacent to the last cell in the path.
     * @param currRow The current row selected
     * @param currCol The current column selected
     * @param lastRow The last row selected
     * @param lastCol The last column selected
     * @return a true iff the cell is legal, otherwise false.
     */
    private boolean checkIfCellIsLegal(int currRow, int currCol, int lastRow, int lastCol) {
        return currRow < num_cells && currRow >= 0 && currCol < num_cells && currCol >= 0
                && m_boardDotColors.get(currRow).get(currCol).equals(m_boardDotColors.get(lastRow).get(lastCol))
                && ((Math.abs(currRow - lastRow) == 1 && currCol == lastCol)
                || (Math.abs(currCol - lastCol) == 1 && currRow == lastRow));
    }

    /**
     * Sets up the handler that talks to the activity
     * @param handler our handler
     */
    public void setMoveEventHandler( OnMoveEventHandler handler ) {
        m_moveHandler = handler;
    }

    /**
     *
     * @return the size of the board
     */
    public int getBoardSize() {
        return num_cells;
    }

    /**
     * Randomizes the color for a given dot
     * @param row the row that the dot is in
     * @param col the column that the dot is in
     */
    private void randomizeDotColor(int row, int col) {
        m_boardDotColors.get(row).set(col, m_dotColors.get(new Random().nextInt(m_dotColors.size())));
    }

    /**
     * Shuffles the colors on the dots currently
     * on the board
     */
    public void shuffleBoard() {
        for(ArrayList<Integer> list : m_boardDotColors) {
            Collections.shuffle(list);
        }

        Collections.shuffle(m_boardDotColors);
        invalidate();
    }
}
