package io.nemesis.ninder.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import io.nemesis.ninder.R;

public class PageIndicator extends View {

    private final Paint black;
    private final Paint white;

    private int maxDots;
    private int currentDot;
    private final int dotRadius;
    private final int hPading;
    private final int vPading;
    private int rows;
    private final int circleDiameter;

    private final static boolean HIDE_SINGLE_DOT = true; //do not show if single dot

    public PageIndicator(Context context) {
        this(context, null, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        black = new Paint();
        black.setColor(context.getResources().getColor(R.color.indicator_dot_selected));
        black.setStyle(Style.FILL_AND_STROKE);
        black.setStrokeWidth(getResources().getDimension(R.dimen.indicator_dot_stroke_width));
        black.setAntiAlias(true);

        white = new Paint();
        white.setColor(context.getResources().getColor(R.color.indicator_dot_unselected_stroke));
        white.setStyle(Style.STROKE);
        white.setStrokeWidth(getResources().getDimension(R.dimen.indicator_dot_stroke_width));
        white.setAntiAlias(true);

        maxDots = 0;
        currentDot = 0;

        dotRadius = (int) Math.ceil(getResources().getDimension(
                R.dimen.indicator_dot_radius));
        hPading = (int) Math.ceil(getResources().getDimension(
                R.dimen.indicator_dot_horizontal_padding));
        vPading = (int) Math.ceil(getResources().getDimension(
                R.dimen.indicator_dot_vertical_padding));

        rows = 0;
        circleDiameter = dotRadius * 2;
    }

    public void setDots(int current, int max) {
        this.currentDot = current;
        this.maxDots = max;
        if (0 < max) {
            rows = 1;
        }
        requestLayout();
    }

    public void setDots(int current, int max, boolean animate) {
        if (animate) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
            anim.setDuration(200);
            anim.start();
        }
        setDots(current, max);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int vMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // I do want vertical padding on both before first and after last element
        int requiredWidth = (circleDiameter * maxDots) + ((maxDots + 2) * vPading);
        int requiredHeight = 0;
        rows = 0;
        if (MeasureSpec.EXACTLY != vMode) {
            if (requiredWidth < width) {
                rows++;
                requiredHeight = rowHeight();
                setMeasuredDimension(width, requiredHeight);
                return;
            }
        }

        do {
            requiredHeight += rowHeight();
            rows++;
            if (width < requiredWidth) {
                requiredWidth -= width;
            } else {
                break;
            }
        } while (requiredHeight < height);

        if (MeasureSpec.EXACTLY == hMode || requiredHeight > height)
            requiredHeight = height;
        setMeasuredDimension(width, requiredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (maxDots == 1 && HIDE_SINGLE_DOT) {
            return;
        }
        int counter = 0;
        for (int r = 0; r < rows; r++) {
            canvas.save();
            int dotsInLine = getWidth() + vPading / (circleDiameter + vPading);
            if (dotsInLine > maxDots - counter)
                dotsInLine = maxDots - counter;

            int reqLineWidth = dotsInLine * circleDiameter
                    + ((Math.max(0, dotsInLine - 1)) * vPading);
            int cx = (getWidth() - reqLineWidth) / 2;
            canvas.translate(cx, r * (circleDiameter) + vPading);
            for (int i = 0; i < dotsInLine; i++) {
                canvas.save();
                canvas.translate(i * (circleDiameter + vPading), 0);
                if (counter == currentDot)
                    canvas.drawCircle(dotRadius, dotRadius, dotRadius, black);
                else
                    canvas.drawCircle(dotRadius, dotRadius, dotRadius, white);

                counter++;
                canvas.restore();
            }
            canvas.restore();
        }
    }

    public void updateProgress(int current) {
        if (current < 0 && current > maxDots)
            throw new IllegalArgumentException(
                    getClass().getName()
                            + "::updateProgress: current progress cannot be negative or surpass the maximum progress value");

        int old = currentDot;
        currentDot = current;

        if (old != currentDot)
            invalidate();
    }

    private int rowHeight() {
        return circleDiameter + (2 * hPading);
    }

    public int getCurrentDot() {
        return currentDot;
    }

    public int getMaxDots() {
        return maxDots;
    }
}