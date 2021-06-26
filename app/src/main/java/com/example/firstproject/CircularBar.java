package com.example.firstproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

public class CircularBar extends View {

    public static int INVALID_VALUE = -1;
    public static final int MAX = 100;
    public static final int MIN = 0;

    /**
     * Offset = -90 indica ca progresul incepe de la ora 12 pe ceas
     */
    private static final int ANGLE_OFFSET = -90;

    /**
     * Valoarea curenta.
     */
    private int mPoints = MIN;

    /**
     * Valoarea minima a valorii de progres.
     */
    private int mMin = MIN;

    /**
     * Valoarea maxima pe care o poate avea progresul
     */
    private int mMax = MAX;

    /**
     * Valoarea de +/- pentru fiecare miscare a progresului
     */
    private int mStep = 1;

    /**
     * desenul pentru forma pe care apasam pe cerc
     */
    private Drawable mIndicatorIcon;


    private int mProgressWidth = 12;
    private int mArcWidth = 12;
    private boolean mClockwise = true;
    private boolean mEnabled = true;

    //
    // variabilele interne
    //
    /**
     * Actualizarea numarului de puncte pentru a determina daca se modifica progresul anterior.
     */
    private int mUpdateTimes = 0;
    private float mPreviousProgress = -1;
    private float mCurrentProgress = 0;

    /**
     * Determinam cand am ajuns la valoarea maxima.
     */
    private boolean isMax = false;

    /**
     * Determinam cand am ajuns la valoarea minima.
     */
    private boolean isMin = false;

    private int mArcRadius = 0;
    private RectF mArcRect = new RectF();
    private Paint mArcPaint;

    private float mProgressSweep = 0;
    private Paint mProgressPaint;

    private float mTextSize = 72;
    private Paint mTextPaint;
    private Rect mTextRect = new Rect();

    private int mTranslateX;
    private int mTranslateY;

    // (x, y) coordonatele iconitei de apasare
    private int mIndicatorIconX;
    private int mIndicatorIconY;

    /**
     * Unghiul curent de cerc.
     */
    private double mTouchAngle;
    private OnCircularBarChangeListener mOnCircularBarChangeListener;

    public CircularBar(Context context) {
        super(context);
        init(context, null);
    }

    public CircularBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        float density = getResources().getDisplayMetrics().density;

        int arcColor = ContextCompat.getColor(context, R.color.color_arc);
        int progressColor = ContextCompat.getColor(context, R.color.color_progress);
        int textColor = ContextCompat.getColor(context, R.color.color_text);
        mProgressWidth = (int) (mProgressWidth * density);
        mArcWidth = (int) (mArcWidth * density);
        mTextSize = (int) (mTextSize * density);

        mIndicatorIcon = ContextCompat.getDrawable(context, R.drawable.indicator);

        if (attrs != null) {
            // Initializam atributele
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.CircularBar, 0, 0);

            Drawable indicatorIcon = a.getDrawable(R.styleable.CircularBar_indicatorIcon);
            if (indicatorIcon != null)
                mIndicatorIcon = indicatorIcon;

            int indicatorIconHalfWidth = mIndicatorIcon.getIntrinsicWidth() / 2;
            int indicatorIconHalfHeight = mIndicatorIcon.getIntrinsicHeight() / 2;
            mIndicatorIcon.setBounds(-indicatorIconHalfWidth, -indicatorIconHalfHeight, indicatorIconHalfWidth,
                    indicatorIconHalfHeight);

            mPoints = a.getInteger(R.styleable.CircularBar_points, mPoints);
            mMin = a.getInteger(R.styleable.CircularBar_min, mMin);
            mMax = a.getInteger(R.styleable.CircularBar_max, mMax);
            mStep = a.getInteger(R.styleable.CircularBar_step, mStep);

            mProgressWidth = (int) a.getDimension(R.styleable.CircularBar_progressWidth, mProgressWidth);
            progressColor = a.getColor(R.styleable.CircularBar_progressColor, progressColor);

            mArcWidth = (int) a.getDimension(R.styleable.CircularBar_arcWidth, mArcWidth);
            arcColor = a.getColor(R.styleable.CircularBar_arcColor, arcColor);

            mTextSize = (int) a.getDimension(R.styleable.CircularBar_textSize, mTextSize);
            textColor = a.getColor(R.styleable.CircularBar_textColor, textColor);

            mClockwise = a.getBoolean(R.styleable.CircularBar_clockwise,
                    mClockwise);
            mEnabled = a.getBoolean(R.styleable.CircularBar_enabled, mEnabled);
            a.recycle();
        }

        // verificare interval puncte minim si maxim setate de noi
        mPoints = Math.min(mPoints, mMax);
        mPoints = Math.max(mPoints, mMin);

        mProgressSweep = (float) mPoints / valuePerDegree();

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mTextPaint = new Paint();
        mTextPaint.setColor(textColor);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int min = Math.min(width, height);

        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);

        int arcDiameter = min - getPaddingLeft();
        mArcRadius = arcDiameter / 2;
        float top = height / 2 - (arcDiameter / 2);
        float left = width / 2 - (arcDiameter / 2);
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        updateIndicatorIconPosition();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mClockwise) {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }

        // desenam textul
        String textPoint = String.valueOf(mPoints);
        mTextPaint.getTextBounds(textPoint, 0, textPoint.length(), mTextRect);
        // centram textul
        int xPos = getWidth() / 2 - mTextRect.width() / 2;
        int yPos = (int) ((mArcRect.centerY()) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));
        canvas.drawText(String.valueOf(mPoints), xPos, yPos, mTextPaint);

        // desenam cercul si progresul acestuia
        canvas.drawArc(mArcRect, ANGLE_OFFSET, 360, false, mArcPaint);
        canvas.drawArc(mArcRect, ANGLE_OFFSET, mProgressSweep, false, mProgressPaint);

        if (mEnabled) {
            // desenam iconita
            canvas.translate(mTranslateX - mIndicatorIconX, mTranslateY - mIndicatorIconY);
            mIndicatorIcon.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mOnCircularBarChangeListener != null)
                        mOnCircularBarChangeListener.onStartTrackingTouch(this);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mOnCircularBarChangeListener != null)
                        mOnCircularBarChangeListener.onStopTrackingTouch(this);
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (mOnCircularBarChangeListener != null)
                        mOnCircularBarChangeListener.onStopTrackingTouch(this);
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mIndicatorIcon != null && mIndicatorIcon.isStateful()) {
            int[] state = getDrawableState();
            mIndicatorIcon.setState(state);
        }
        invalidate();
    }

    /**
     * Actualizam componentele interfetei grafice la evenimentele tactile.
     *
     * @param event MotionEvent
     */
    private void updateOnTouch(MotionEvent event) {
        setPressed(true);
        mTouchAngle = convertTouchEventPointToAngle(event.getX(), event.getY());
        int progress = convertAngleToProgress(mTouchAngle);
        updateProgress(progress, true);
    }

    private double convertTouchEventPointToAngle(float xPos, float yPos) {
        // Transformam coordonatele apasarii pe axul ceruclui in coordonate cardinale
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        x = (mClockwise) ? x : -x;
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2));
        angle = (angle < 0) ? (angle + 360) : angle;
        return angle;
    }

    private int convertAngleToProgress(double angle) {
        return (int) Math.round(valuePerDegree() * angle);
    }

    private float valuePerDegree() {
        return (float) (mMax) / 360.0f;
    }

    private void updateIndicatorIconPosition() {
        int thumbAngle = (int) (mProgressSweep + 90);
        mIndicatorIconX = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mIndicatorIconY = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void updateProgress(int progress, boolean fromUser) {

        // punctele aproape de minim si maxim
        final int maxDetectValue = (int) ((double) mMax * 0.95);
        final int minDetectValue = (int) ((double) mMax * 0.05) + mMin;

        mUpdateTimes++;
        if (progress == INVALID_VALUE) {
            return;
        }

        // evitam atingerea accidentala pentru a deveni maxim din punctul initial
        if (progress > maxDetectValue && mPreviousProgress == INVALID_VALUE) {
//					progress, mPreviousProgress, mCurrentProgress, isMax ? "Max" : "");
            return;
        }


        // actualizam progresul trecut si cel prezent
        if (mUpdateTimes == 1) {
            mCurrentProgress = progress;
        } else {
            mPreviousProgress = mCurrentProgress;
            mCurrentProgress = progress;
        }

        mPoints = progress - (progress % mStep);

        /**
         * Determinati daca atingeti max sau min pentru a bloca evenimentul de actualizare a punctului.
         *
         * Când atingeți max, progresul va scădea de la max (sau maxDetectPoints ~ max
         * la min (sau min ~ minDetectPoints) și invers.
         *
         * Dacă atingeti max sau min, opriti cresterea / scaderea pentru a evita depașirea max / min.
         */
        if (mUpdateTimes > 1 && !isMin && !isMax) {
            if (mPreviousProgress >= maxDetectValue && mCurrentProgress <= minDetectValue &&
                    mPreviousProgress > mCurrentProgress) {
                isMax = true;
                progress = mMax;
                mPoints = mMax;
                if (mOnCircularBarChangeListener != null) {
                    mOnCircularBarChangeListener
                            .onPointsChanged(this, progress, fromUser);
                    return;
                }
            } else if ((mCurrentProgress >= maxDetectValue
                    && mPreviousProgress <= minDetectValue
                    && mCurrentProgress > mPreviousProgress) || mCurrentProgress <= mMin) {
                isMin = true;
                progress = mMin;
                mPoints = mMin;
                if (mOnCircularBarChangeListener != null) {
                    mOnCircularBarChangeListener
                            .onPointsChanged(this, progress, fromUser);
                    return;
                }
            }
            invalidate();
        } else {

            // Detectam frontul ridicator de la minim sau coborator de la maxim
            if (isMax & (mCurrentProgress < mPreviousProgress) && mCurrentProgress >= maxDetectValue) {
                isMax = false;
            }
            if (isMin
                    && (mPreviousProgress < mCurrentProgress)
                    && mPreviousProgress <= minDetectValue && mCurrentProgress <= minDetectValue
                    && mPoints >= mMin) {
                isMin = false;
            }
        }

        if (!isMax && !isMin) {
            progress = Math.min(progress, mMax);
            progress = Math.max(progress, mMin);

            if (mOnCircularBarChangeListener != null) {
                progress = progress - (progress % mStep);

                mOnCircularBarChangeListener
                        .onPointsChanged(this, progress, fromUser);
            }

            mProgressSweep = (float) progress / valuePerDegree();
//			if (mPreviousProgress != mCurrentProgress)
            updateIndicatorIconPosition();
            invalidate();
        }
    }

    public interface OnCircularBarChangeListener {

        /**
         * Notificare ca valoarea s-a schimbat.
         *
         * @param swagPoints Vedem ca valoarea a fost schimbata.
         * @param points     Valoarea curenta
         * @param fromUser   Adevarata daca valoarea a fost schimbata de catre utilizator.
         */
        void onPointsChanged(CircularBar swagPoints, int points, boolean fromUser);

        void onStartTrackingTouch(CircularBar swagPoints);

        void onStopTrackingTouch(CircularBar swagPoints);

        void onProgressChanged(CircularBar seekBar, int progress, boolean fromUser);
    }

    public void setPoints(int points) {
        points = Math.min(points, mMax);
        points = Math.max(points, mMin);
        updateProgress(points, false);
    }

    public int getPoints() {
        return mPoints;
    }

    public int getProgressWidth() {
        return mProgressWidth;
    }

    public void setProgressWidth(int mProgressWidth) {
        this.mProgressWidth = mProgressWidth;
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(int mArcWidth) {
        this.mArcWidth = mArcWidth;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    public void setClockwise(boolean isClockwise) {
        mClockwise = isClockwise;
    }

    public boolean isClockwise() {
        return mClockwise;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public int getProgressColor() {
        return mProgressPaint.getColor();
    }

    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    public int getArcColor() {
        return mArcPaint.getColor();
    }

    public void setArcColor(int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    public void setTextColor(int textColor) {
        mTextPaint.setColor(textColor);
        invalidate();
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int mMax) {
        if (mMax <= mMin)
            throw new IllegalArgumentException("Max should not be less than min.");
        this.mMax = mMax;
    }

    public int getMin() {
        return mMin;
    }

    public void setMin(int min) {
        if (mMax <= mMin)
            throw new IllegalArgumentException("Min should not be greater than max.");
        mMin = min;
    }

    public int getStep() {
        return mStep;
    }

    public void setStep(int step) {
        mStep = step;
    }

    public void setOnCircularBarChangeListener(OnCircularBarChangeListener onCircularBarChangeListener) {
        mOnCircularBarChangeListener = onCircularBarChangeListener;
    }
}