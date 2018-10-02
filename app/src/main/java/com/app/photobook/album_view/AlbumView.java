package com.app.photobook.album_view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.OverScroller;

import com.app.photobook.R;
import com.app.photobook.curl.IPhotoView;

public class AlbumView extends View {

    public static final float LAYOUT_WIDTH_OFFSET = 2.0f;

    private static float MAX_ZOOM = LAYOUT_WIDTH_OFFSET;
    private static float MIN_ZOOM = IPhotoView.DEFAULT_MIN_SCALE;
    private float animHeight;
    private Page backGroundPage;
    int canvasColor = 0;
    private Page currentLeftPage;
    private Page currentRightPage;
    private long defaultAnimatingTime = 20;
    private VectorPoint down = new VectorPoint();
    private boolean dragged;
    private RectF dst;
    private AlbumGesture gestureDeletate = new C06461();
    private GestureDetector gestureDetector;
    Canvas globalCanvas;
    private float globalMaxLeft;
    private float globalMaxRight;
    private float heightToAnimate;
    private int index;
    private boolean isAnimating;
    private boolean isFlip;
    private boolean isFlipping;
    private boolean isNext;
    private boolean isPortate;
    private boolean isReversAnimation;
    private boolean isScaling;
    private boolean isScalingStart;
    private boolean isTop;
    private VectorPoint mA;
    private VectorPoint mB;
    private VectorPoint mC;
    private Rect mClipBound = new Rect();
    private Paint mCurlEdgePaint;
    private VectorPoint mD;
    private VectorPoint mE;
    private VectorPoint mF;
    private VectorPoint mFOld;
    private VectorPoint mOldTouch;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = IPhotoView.DEFAULT_MIN_SCALE;
    public OverScroller mScroller;
    Paint mShadow = new Paint();
    private Page nextLeftPage;
    private Page nextRightPage;
    private OnPageChangedListener pageChangedDelegate;
    private float pageCurlHeight;
    private float pageCurlWidth;
    private PageProvider pageProvider;
    private Page previousLeftPage;
    private Page previousRightPage;
    private Matrix scaleMatrix = new Matrix();
    private float scalePointX;
    private float scalePointY;
    private long startTimeOfAnimation;
    private float startX;
    private float startY;
    private long timeToAnimate;
    private float viewHeight;
    private float viewWidth;
    private float widthToAnimate;
    private float distance;

    public interface AlbumGesture {
        void onDoubleTap();

        void onSingleTapConfirmed();
    }

    private class MyGestureListener extends SimpleOnGestureListener {
        private MyGestureListener() {
        }

        public boolean onDoubleTap(MotionEvent e) {
            AlbumView.this.scalePointX = AlbumView.this.viewWidth / LAYOUT_WIDTH_OFFSET;
            AlbumView.this.scalePointY = AlbumView.this.viewHeight / LAYOUT_WIDTH_OFFSET;
            AlbumView.this.gestureDeletate.onDoubleTap();
            if (AlbumView.this.mScaleFactor != IPhotoView.DEFAULT_MIN_SCALE) {
                AlbumView.this.mScaleFactor = IPhotoView.DEFAULT_MIN_SCALE;
                AlbumView.this.isScaling = false;
                AlbumView.this.scrollTo(0, 0);
            } else if (!(AlbumView.this.isAnimating || AlbumView.this.isFlipping)) {
                AlbumView.this.mScaleFactor = LAYOUT_WIDTH_OFFSET;
                AlbumView.this.isScaling = true;
            }
            AlbumView.this.invalidate();
            return true;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!AlbumView.this.isFlip) {
                AlbumView.this.gestureDeletate.onSingleTapConfirmed();
            }
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
            AlbumView.this.mScroller.fling(AlbumView.this.getScrollX(), AlbumView.this.getScrollY(), -((int) vX), -((int) vY), (int) AlbumView.this.globalMaxLeft, (int) AlbumView.this.globalMaxRight, 0, (int) AlbumView.this.viewHeight);
            AlbumView.this.invalidate();
            return true;
        }

        public boolean onDown(MotionEvent e) {
            if (!AlbumView.this.mScroller.isFinished()) {
                AlbumView.this.mScroller.forceFinished(true);
            }
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (AlbumView.this.mScaleFactor != IPhotoView.DEFAULT_MIN_SCALE) {
                float rightBoundry = AlbumView.this.globalMaxRight;
                float leftBoundry = AlbumView.this.globalMaxLeft;
                float maxBottom = AlbumView.this.viewHeight;
                int x = 0;
                int y = 0;
                if (distanceX < 0.0f) {
                    if (((float) AlbumView.this.mClipBound.left) < leftBoundry) {
                        int leftShift = ((int) leftBoundry) - AlbumView.this.mClipBound.left;
                        if (leftShift > 50) {
                            x = leftShift;
                        }
                    } else {
                        x = (int) distanceX;
                    }
                } else if (((float) AlbumView.this.mClipBound.right) > rightBoundry) {
                    int rightShift = ((int) rightBoundry) - AlbumView.this.mClipBound.right;
                    if (rightShift < -50) {
                        x = rightShift;
                    }
                } else {
                    x = (int) distanceX;
                }
                if (distanceY < 0.0f) {
                    if (AlbumView.this.mClipBound.top < 0) {
                        int topShift = 0 - AlbumView.this.mClipBound.top;
                        if (topShift > 50) {
                            y = topShift;
                        }
                    } else {
                        y = (int) distanceY;
                    }
                } else if (AlbumView.this.mClipBound.bottom > ((int) maxBottom)) {
                    int bottomShift = ((int) maxBottom) - AlbumView.this.mClipBound.bottom;
                    if (bottomShift < -50) {
                        y = bottomShift;
                    }
                } else {
                    y = (int) distanceY;
                }
                AlbumView.this.scrollBy(x, y);
            } else {
                AlbumView.this.scrollTo(0, 0);
            }
            return true;
        }
    }

    public interface OnPageChangedListener {
        void onPageChanged(int i);
    }

    private class Page {
        Bitmap bitmap;
        Matrix matrix;
        RectF src;

        public Page(Bitmap bitmap, RectF dst, boolean isRight) {
            if (bitmap != null) {
                this.bitmap = bitmap;
                this.src = new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
                setPagematrix(dst, isRight);
            }
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            this.src = new RectF(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
        }

        public float getWidth() {
            return this.bitmap != null ? ((float) this.bitmap.getWidth()) * getScale() : 0.0f;
        }

        public void drawCanvas(Canvas canvas, float c, float angle, float x, float y) {
            Log.d("tag", "drawCanvas :: ");
            if (this.bitmap != null) {
                Log.d("tag", "drawCanvas :: not null");
                Matrix newMatrix = new Matrix();
                newMatrix.set(this.matrix);
                newMatrix.postTranslate(c, 0.0f);
                newMatrix.postRotate(angle, x, y);
                canvas.drawBitmap(this.bitmap, newMatrix, AlbumView.this.mCurlEdgePaint);
            }
        }

        public void recycle() {
            if (this.bitmap != null) {
                this.bitmap.recycle();
            }
        }

        public void setPagematrix(RectF dst, boolean isRight) {
            this.matrix = new Matrix();
            if (isRight) {
                this.matrix.setRectToRect(this.src, dst, ScaleToFit.START);
                this.matrix.postTranslate(0.0f, (AlbumView.this.viewHeight - (((float) this.bitmap.getHeight()) * AlbumView.this.getValue(this.matrix, 0))) / LAYOUT_WIDTH_OFFSET);
                this.matrix.postTranslate(AlbumView.this.viewWidth / LAYOUT_WIDTH_OFFSET, 0.0f);
                return;
            }
            this.matrix.setRectToRect(this.src, dst, ScaleToFit.END);
            this.matrix.postTranslate(0.0f, -((AlbumView.this.viewHeight - (((float) this.bitmap.getHeight()) * AlbumView.this.getValue(this.matrix, 0))) / LAYOUT_WIDTH_OFFSET));
        }

        public void rotate(float angle) {
            this.matrix.postRotate(angle);
        }

        public void translate(float dx, float dy) {
            this.matrix.postTranslate(dx, dy);
        }

        public void drawCanvas(Canvas canvas) {
            if (this.bitmap == null) {
                Rect dst = new Rect(0, 0, (int) AlbumView.this.viewWidth, (int) AlbumView.this.viewHeight);
                canvas.drawColor(AlbumView.this.getResources().getColor(R.color.black_50_opacity));
                return;
            }
            Paint drawPaint = new Paint();
            drawPaint.setAntiAlias(false);
            drawPaint.setFilterBitmap(false);
            canvas.drawBitmap(this.bitmap, this.matrix, drawPaint);
        }

        public float getScale() {
            return AlbumView.this.getValue(this.matrix, 0);
        }

        public float getHeight() {
            return this.bitmap != null ? ((float) this.bitmap.getHeight()) * getScale() : 0.0f;
        }

        public void get3DFlip(Canvas canvas, float degree) {
            Matrix _3Dmatrix = new Matrix();
            Camera camera = new Camera();
            camera.save();
            camera.rotateY(degree);
            camera.getMatrix(_3Dmatrix);
            camera.restore();
            _3Dmatrix.postConcat(this.matrix);
            canvas.drawBitmap(this.bitmap, _3Dmatrix, null);
        }
    }

    public interface PageProvider {
        Bitmap getPage(int i);

        Bitmap getPage(int i, int i2, int i3);

        int getPagesCount();
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScale(ScaleGestureDetector detector) {
            Log.d("tag", "start");
            if (AlbumView.this.isAnimating || AlbumView.this.isFlipping) {
                AlbumView.this.mScaleFactor = IPhotoView.DEFAULT_MIN_SCALE;
            } else {
                AlbumView.access$832(AlbumView.this, detector.getScaleFactor());
                AlbumView.this.mScaleFactor = Math.max(AlbumView.MIN_ZOOM, Math.min(AlbumView.this.mScaleFactor, AlbumView.MAX_ZOOM));
            }
            AlbumView.this.isScaling = ((double) AlbumView.this.mScaleFactor) > 1.0d;
            AlbumView.this.invalidate();
            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            Log.d("tag", "begin");
            if (!(AlbumView.this.isAnimating || AlbumView.this.isFlipping)) {
                AlbumView.this.isScalingStart = true;
            }
            AlbumView.this.scalePointX = (detector.getFocusX() / AlbumView.this.mScaleFactor) + ((float) AlbumView.this.mClipBound.left);
            AlbumView.this.scalePointY = (detector.getFocusY() / AlbumView.this.mScaleFactor) + ((float) AlbumView.this.mClipBound.top);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            AlbumView.this.isScalingStart = false;
            super.onScaleEnd(detector);
        }
    }

    private class VectorPoint {
        float f24x;
        float f25y;

        private VectorPoint() {
        }
    }

    class C06461 implements AlbumGesture {
        C06461() {
        }

        public void onSingleTapConfirmed() {
        }

        public void onDoubleTap() {
        }
    }

    static float access$832(AlbumView x0, float x1) {
        float f = x0.mScaleFactor * x1;
        x0.mScaleFactor = f;
        return f;
    }

    public AlbumView(Context context) {
        super(context);
    }

    public AlbumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPortraitMode(boolean isPortate) {
        this.isPortate = isPortate;
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.canvasColor = getResources().getColor(R.color.black_50_opacity);
        this.viewWidth = (float) MeasureSpec.getSize(widthMeasureSpec);
        this.viewHeight = (float) MeasureSpec.getSize(heightMeasureSpec);
        this.dst = new RectF(0.0f, 0.0f, this.viewWidth / LAYOUT_WIDTH_OFFSET, this.viewHeight);
        this.mScroller = new OverScroller(getContext());
        this.gestureDetector = new GestureDetector(getContext(), new MyGestureListener());
        this.gestureDetector.setIsLongpressEnabled(true);
        this.mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        Page page = new Page(this.pageProvider.getPage(2), this.dst, false);
        this.backGroundPage = new Page(BitmapFactory.decodeResource(getResources(), R.color.black_50_opacity), this.dst, false);
        setVectorPoints(this.viewWidth, this.viewHeight);
        setPages(this.index, this.dst);
        this.mCurlEdgePaint = new Paint();
        this.mCurlEdgePaint.setColor(-1);
        this.mCurlEdgePaint.setAntiAlias(true);
        this.mCurlEdgePaint.setStyle(Style.FILL);
        this.mCurlEdgePaint.setShadowLayer(50.0f, -5.0f, 5.0f, -1728053248);
        this.mShadow.setAntiAlias(true);
        this.mShadow.setShadowLayer(50.0f, 10.0f, 10.0f, ViewCompat.MEASURED_STATE_MASK);
    }

    private void setVectorPoints(float right, float bottom) {
        this.mA = new VectorPoint();
        this.mB = new VectorPoint();
        this.mC = new VectorPoint();
        this.mD = new VectorPoint();
        this.mE = new VectorPoint();
        this.mF = new VectorPoint();
        this.mFOld = new VectorPoint();
        this.mOldTouch = new VectorPoint();
        this.mB.f24x = right;
        this.mB.f25y = bottom;
        this.mC.f24x = right;
        this.mC.f25y = 0.0f;
        this.mF.f24x = right - 10.0f;
        this.mF.f25y = bottom - 10.0f;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.viewWidth = (float) w;
        this.viewHeight = (float) h;
    }

    @SuppressLint({"DrawAllocation"})
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cX = ((float) canvas.getWidth()) / LAYOUT_WIDTH_OFFSET;
        float cY = ((float) canvas.getHeight()) / LAYOUT_WIDTH_OFFSET;
        if (isScaling() || this.isScalingStart) {
            canvas.save();
            canvas.scale(this.mScaleFactor, this.mScaleFactor, this.scalePointX, this.scalePointY);
            canvas.drawColor(this.canvasColor);
            if (this.index == 0) {
                canvas.translate((-this.currentRightPage.getWidth()) / LAYOUT_WIDTH_OFFSET, 0.0f);
            }
            if (this.pageProvider.getPagesCount() % 2 == 0 && this.index == this.pageProvider.getPagesCount() / 2) {
                canvas.translate(this.currentLeftPage.getWidth() / LAYOUT_WIDTH_OFFSET, 0.0f);
            }
            drawForeground(canvas);
            this.mClipBound = canvas.getClipBounds();
            canvas.restore();
            return;
        }
        canvas.save();
        scrollTo(0, 0);
        canvas.scale(this.mScaleFactor, this.mScaleFactor, this.scalePointX, this.scalePointY);
        canvas.drawColor(this.canvasColor);
        if (this.index == 0) {
            canvas.translate((-this.currentRightPage.getWidth()) / LAYOUT_WIDTH_OFFSET, 0.0f);
        }
        if (this.pageProvider.getPagesCount() % 2 == 0 && this.index == this.pageProvider.getPagesCount() / 2) {
            canvas.translate(this.currentLeftPage.getWidth() / LAYOUT_WIDTH_OFFSET, 0.0f);
        }
        setCurlPoint();
        drawForeground(canvas);
        this.mClipBound = canvas.getClipBounds();
        this.globalMaxRight = (float) canvas.getClipBounds().right;
        this.globalMaxLeft = (float) canvas.getClipBounds().left;
        if (!(!this.isFlipping || isScaling() || this.isScalingStart)) {
            drawBackground(canvas);
            drawPageCureEdge(canvas);
        }
        if (!(!this.isAnimating || isScaling() || this.isScalingStart)) {
            calculatingFingerTouch();
            postInvalidate();
        }
        canvas.restore();
    }

    public Canvas getGlobalCanvas() {
        return this.globalCanvas;
    }

    public void setGlobalCanvas(Canvas globalCanvas) {
        this.globalCanvas = globalCanvas;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.isAnimating && this.index != 0) {
            return false;
        }
        this.gestureDetector.onTouchEvent(event);
        this.mScaleDetector.onTouchEvent(event);
        if (!(this.isScaling || this.isScalingStart)) {
            switch (event.getAction()) {
                case 0:
                    this.down.f24x = event.getX();
                    this.down.f25y = event.getY();
                    RectF dst = new RectF(0.0f, 0.0f, this.viewWidth / LAYOUT_WIDTH_OFFSET, this.viewHeight);
                    this.isNext = true;
                    recyclePrevAndNext();
                    this.isTop = event.getY() < this.viewHeight / LAYOUT_WIDTH_OFFSET;
                    if (event.getX() > (this.viewWidth * LAYOUT_WIDTH_OFFSET) / IPhotoView.DEFAULT_MAX_SCALE || this.index == 0) {
                        if (this.index == this.pageProvider.getPagesCount() / 2) {
                            return false;
                        }
                        if (this.index != 0 || event.getX() >= (this.viewWidth * LAYOUT_WIDTH_OFFSET) / IPhotoView.DEFAULT_MAX_SCALE) {
                            this.isNext = true;
                            this.isFlip = true;
                            this.nextLeftPage = new Page(this.pageProvider.getPage((this.index * 2) + 1), dst, false);
                            this.nextRightPage = new Page(this.pageProvider.getPage((this.index * 2) + 2), dst, true);
                            break;
                        }
                        this.isFlip = false;
                        return true;
                    } else if (event.getX() >= (this.viewWidth * IPhotoView.DEFAULT_MIN_SCALE) / IPhotoView.DEFAULT_MAX_SCALE && this.index != this.pageProvider.getPagesCount() / 2) {
                        this.isFlip = false;
                        return true;
                    } else if (this.index != this.pageProvider.getPagesCount() / 2 || event.getX() <= (this.viewWidth * IPhotoView.DEFAULT_MIN_SCALE) / IPhotoView.DEFAULT_MAX_SCALE) {
                        this.isNext = false;
                        this.isFlip = true;
                        this.isFlipping = false;
                        this.previousLeftPage = new Page(this.pageProvider.getPage((this.index * 2) - 3), dst, false);
                        this.previousRightPage = new Page(this.pageProvider.getPage((this.index * 2) - 2), dst, true);
                        break;
                    } else {
                        this.isFlip = false;
                        return true;
                    }
                case 1:
                    if (this.isNext) {
                        this.startX = this.mF.f24x;
                        this.startY = this.mF.f25y;
                        if (this.isReversAnimation) {
                            this.isReversAnimation = true;
                            this.widthToAnimate = this.viewWidth - this.startX;
                            if (this.isTop) {
                                this.heightToAnimate = this.startY;
                            } else {
                                this.heightToAnimate = this.viewHeight - this.startY;
                            }
                            this.timeToAnimate = (long) (((float) this.defaultAnimatingTime) * (this.widthToAnimate / this.viewWidth));
                        } else {
                            this.widthToAnimate = this.startX;
                            if (this.isTop) {
                                this.heightToAnimate = this.startY;
                            } else {
                                this.heightToAnimate = this.viewHeight - this.startY;
                            }
                            this.timeToAnimate = (long) (((float) this.defaultAnimatingTime) * (this.startX / this.viewWidth));
                        }
                    } else {
                        this.startX = this.mF.f24x;
                        this.startY = this.mF.f25y;
                        if (this.isReversAnimation) {
                            this.widthToAnimate = this.viewWidth - this.startX;
                            if (this.isTop) {
                                this.heightToAnimate = this.startY;
                            } else {
                                this.heightToAnimate = this.viewHeight - this.startY;
                            }
                            this.timeToAnimate = (long) ((((float) this.defaultAnimatingTime) * this.widthToAnimate) / this.viewWidth);
                        } else {
                            this.widthToAnimate = this.startX;
                            if (this.isTop) {
                                this.heightToAnimate = this.startY;
                            } else {
                                this.heightToAnimate = this.viewHeight - this.startY;
                            }
                            this.timeToAnimate = (long) ((((float) this.defaultAnimatingTime) * this.widthToAnimate) / this.viewWidth);
                        }
                    }
                    this.isAnimating = this.isFlip;
                    this.startTimeOfAnimation = System.currentTimeMillis();
                    this.mOldTouch.f24x = this.mF.f24x;
                    invalidate();
                    break;
                case 2:
                    double angle = Math.atan2((double) (this.viewHeight - this.mF.f25y), (double) ((this.viewWidth / LAYOUT_WIDTH_OFFSET) - this.mF.f24x));
                    double angleInDegree = Math.toDegrees(angle);
                    double height = ((double) this.viewHeight) - (((double) (this.viewHeight - this.mF.f25y)) / Math.sin(-angle));
                    this.isFlipping = this.isFlip;
                    this.mF.f24x = event.getX() + 0.3f;
                    this.mF.f25y = event.getY() + 0.3f;
                    this.isReversAnimation = this.mOldTouch.f24x < event.getX();
                    invalidate();
                    break;
            }
            this.mOldTouch.f24x = this.mF.f24x;
        }
        return true;
    }

    public void setIndex(int index) {
        this.index = index;
        setPages(index, this.dst);
        invalidate();
    }

    private void calculatingFingerTouch() {
        float spendedTime = (float) (System.currentTimeMillis() - this.startTimeOfAnimation);
        if (spendedTime >= ((float) this.timeToAnimate)) {
            gotoNextPage();
        } else if (this.isNext) {
            if (this.isReversAnimation) {
                this.mF.f24x = this.startX + (this.widthToAnimate * (spendedTime / ((float) this.timeToAnimate)));
                return;
            }
            this.mF.f24x = this.startX - (this.widthToAnimate * (spendedTime / ((float) this.timeToAnimate)));
        } else if (this.isReversAnimation) {
            this.mF.f24x = this.startX + ((this.widthToAnimate * spendedTime) / ((float) this.timeToAnimate));
        } else {
            this.mF.f24x = this.startX - ((this.widthToAnimate * spendedTime) / ((float) this.timeToAnimate));
        }
    }

    private void gotoNextPage() {
        if (this.isNext) {
            if (!(this.index * 2 == this.pageProvider.getPagesCount() || this.isReversAnimation)) {
                this.index++;
            }
        } else if (this.index != 0 && this.isReversAnimation) {
            this.index--;
        }
        setPages(this.index, this.dst);
        this.isAnimating = false;
    }

    private void drawForeground(Canvas canvas) {
        canvas.save();
        if (this.currentLeftPage != null) {
            this.currentLeftPage.drawCanvas(canvas);
        }
        if (this.currentRightPage != null) {
            this.currentRightPage.drawCanvas(canvas);
        }
        canvas.restore();
    }

    private void drawBackground(Canvas canvas) {
        canvas.save();
        canvas.clipPath(getBackGroundClipPath());
        if (this.isNext) {
            if (this.nextRightPage != null) {
                this.nextRightPage.drawCanvas(canvas);
            }
        } else if (this.previousLeftPage != null) {
            this.previousLeftPage.drawCanvas(canvas);
        }
        canvas.restore();
    }

    private Path getBackGroundClipPath() {
        Path path = new Path();
        path.moveTo(this.mA.f24x, this.mA.f25y);
        path.lineTo(this.mB.f24x, this.mB.f25y);
        path.lineTo(this.mC.f24x, this.mC.f25y);
        path.lineTo(this.mD.f24x, this.mD.f25y);
        path.lineTo(this.mA.f24x, this.mA.f25y);
        return path;
    }

    private void drawPageCureEdge(Canvas canvas) {
        canvas.clipPath(getPageCurlPath());
        float a = this.mA.f24x - this.mF.f24x;
        float b = this.mA.f25y - this.mF.f25y;
        float c = (float) Math.sqrt((double) ((a * a) + (b * b)));
        float angle = getAngle(this.mF.f24x, this.mF.f25y, this.mA.f24x, this.mA.f25y);
        if (this.isNext) {
            if (this.nextLeftPage != null) {
                this.nextLeftPage.drawCanvas(canvas, this.mA.f24x - c, angle - 180.0f, this.mA.f24x, this.mA.f25y);
            }
        } else if (this.previousRightPage != null) {
            this.previousRightPage.drawCanvas(canvas, (this.mA.f24x + c) - this.viewWidth, angle, this.mA.f24x, this.mA.f25y);
        }
    }

    private Path getPageCurlPath() {
        Path path = new Path();
        path.moveTo(this.mA.f24x, this.mA.f25y);
        path.lineTo(this.mD.f24x, this.mD.f25y);
        path.lineTo(this.mE.f24x, this.mE.f25y);
        path.lineTo(this.mF.f24x, this.mF.f25y);
        path.lineTo(this.mA.f24x, this.mA.f25y);
        return path;
    }

    private void setPages(int i, RectF dst) {
        recyclePages();
        this.pageChangedDelegate.onPageChanged(this.index);
        Matrix canvasMatrix = new Matrix();
        this.isNext = false;
        this.isAnimating = false;
        this.isFlipping = false;
        if (i == 0) {
            this.currentRightPage = new Page(this.pageProvider.getPage(0), dst, true);
            this.currentLeftPage = null;
        } else if (i * 2 == this.pageProvider.getPagesCount()) {
            this.currentLeftPage = new Page(this.pageProvider.getPage((i * 2) - 1), dst, false);
            this.currentRightPage = null;
        } else {
            this.currentLeftPage = new Page(this.pageProvider.getPage((i * 2) - 1), dst, false);
            this.currentRightPage = new Page(this.pageProvider.getPage(i * 2), dst, true);
        }
    }

    private void initilzeVectorPoint() {
        this.mB.f24x = this.viewWidth;
        this.mB.f25y = this.viewHeight;
        this.mC.f24x = this.viewWidth;
        this.mC.f25y = 0.0f;
        this.mF.f24x = this.viewWidth - 10.0f;
        this.mF.f25y = this.viewHeight - 10.0f;
    }

    private void recyclePages() {
        if (this.currentLeftPage != null) {
            this.currentLeftPage.recycle();
        }
        if (this.currentRightPage != null) {
            this.currentRightPage.recycle();
        }
        recyclePrevAndNext();
    }

    private void recyclePrevAndNext() {
        if (this.previousLeftPage != null) {
            this.previousLeftPage.recycle();
        }
        if (this.previousRightPage != null) {
            this.previousRightPage.recycle();
        }
        if (this.nextLeftPage != null) {
            this.nextLeftPage.recycle();
        }
        if (this.nextRightPage != null) {
            this.nextRightPage.recycle();
        }
    }

    public void setPageProvider(PageProvider pageProvider) {
        this.pageProvider = pageProvider;
    }

    protected float getValue(Matrix matrix, int whichValue) {
        float[] mMatrixValues = new float[9];
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    public void setCurlPoint() {
        float angle;
        if (this.isNext) {
            if (this.isTop) {
                this.mB.f24x = this.viewWidth;
                this.mB.f25y = 0.0f;
                this.mC.f24x = this.viewWidth;
                this.mC.f25y = this.viewHeight;
                angle = getAngle(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y) - 180.0f;
                this.mA.f25y = this.mB.f25y;
                this.mA.f24x = this.mB.f24x - ((float) (((double) (getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y) / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle))));
                if (this.mA.f24x <= this.mB.f24x / LAYOUT_WIDTH_OFFSET) {
                    this.mA.f24x = 0.0f;
                    this.mF.f24x = this.mFOld.f24x;
                    this.mF.f25y = this.mFOld.f25y;
                    angle = getAngle(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y) - 180.0f;
                    this.mA.f24x = this.mB.f24x - ((float) (((double) (getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y) / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle))));
                }
                this.mD.f24x = this.viewWidth;
                this.mD.f25y = (float) (((double) (getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y) / LAYOUT_WIDTH_OFFSET)) / Math.sin(Math.toRadians((double) (angle - -180.0f))));
                this.mE.f24x = this.mD.f24x;
                this.mE.f25y = this.mD.f25y;
            } else {
                this.mB.f24x = this.viewWidth;
                this.mB.f25y = this.viewHeight;
                this.mC.f24x = this.viewWidth;
                this.mC.f25y = 0.0f;
                angle = getAngle(this.mF.f24x, this.mF.f25y, this.viewWidth, this.viewHeight) - 180.0f;
                this.mA.f25y = this.viewHeight;
                this.mA.f24x = this.viewWidth - ((float) (((double) (getDistance(this.mF.f24x, this.mF.f25y, this.viewWidth, this.viewHeight) / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle))));
                if (this.mA.f24x <= this.viewWidth / LAYOUT_WIDTH_OFFSET) {
                    this.mA.f24x = 0.0f;
                    this.mF.f24x = this.mFOld.f24x;
                    this.mF.f25y = this.mFOld.f25y;
                    angle = getAngle(this.mF.f24x, this.mF.f25y, this.viewWidth, this.viewHeight) - 180.0f;
                    this.mA.f24x = this.viewWidth - ((float) (((double) (getDistance(this.mF.f24x, this.mF.f25y, this.viewWidth, this.viewHeight) / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle))));
                }
                this.mD.f24x = this.viewWidth;
                this.mD.f25y = this.viewHeight - ((float) (((double) (getDistance(this.mF.f24x, this.mF.f25y, this.viewWidth, this.viewHeight) / LAYOUT_WIDTH_OFFSET)) / Math.sin(Math.toRadians((double) angle))));
                this.mE.f24x = this.mD.f24x;
                this.mE.f25y = this.mD.f25y;
            }
        } else if (this.isTop) {
            this.mB.f24x = 0.0f;
            this.mB.f25y = 0.0f;
            this.mC.f24x = 0.0f;
            this.mC.f25y = this.viewHeight;
            angle = getAngle(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
            distance = getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
            this.mA.f25y = this.mB.f24x;
            this.mA.f24x = (float) (((double) (distance / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle)));
            if (this.mA.f24x >= this.viewWidth / LAYOUT_WIDTH_OFFSET) {
                this.mA.f24x = this.viewWidth / LAYOUT_WIDTH_OFFSET;
                this.mF.f24x = this.mFOld.f24x;
                this.mF.f25y = this.mFOld.f25y;
                angle = getAngle(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
                distance = getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
                this.mA.f24x = (float) (((double) (distance / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle)));
            }
            this.mD.f24x = this.mC.f24x;
            this.mD.f25y = (float) (((double) (distance / LAYOUT_WIDTH_OFFSET)) / Math.sin(Math.toRadians((double) (-180.0f - angle))));
            this.mE.f24x = this.mD.f24x;
            this.mE.f25y = this.mD.f25y;
        } else {
            this.mB.f24x = 0.0f;
            this.mB.f25y = this.viewHeight;
            this.mC.f24x = 0.0f;
            this.mC.f25y = 0.0f;
            angle = getAngle(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
            distance = getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
            this.mA.f25y = this.viewHeight;
            this.mA.f24x = (float) (((double) (distance / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle)));
            if (this.mA.f24x >= this.viewWidth / LAYOUT_WIDTH_OFFSET) {
                this.mA.f24x = this.viewWidth / LAYOUT_WIDTH_OFFSET;
                this.mF.f24x = this.mFOld.f24x;
                this.mF.f25y = this.mFOld.f25y;
                angle = getAngle(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
                distance = getDistance(this.mF.f24x, this.mF.f25y, this.mB.f24x, this.mB.f25y);
                this.mA.f24x = (float) (((double) (distance / LAYOUT_WIDTH_OFFSET)) / Math.cos(Math.toRadians((double) angle)));
            }
            this.mD.f24x = this.mC.f24x;
            this.mD.f25y = this.viewHeight - ((float) (((double) (distance / LAYOUT_WIDTH_OFFSET)) / Math.sin(Math.toRadians((double) (360.0f - angle)))));
            this.mE.f24x = this.mD.f24x;
            this.mE.f25y = this.mD.f25y;
        }
        this.mFOld.f24x = this.mF.f24x;
        this.mFOld.f25y = this.mF.f25y;
    }

    private float getAngle(float x, float y, float centerX, float centerY) {
        return (-180.0f + ((float) ((int) Math.toDegrees(Math.atan2((double) (centerY - y), (double) (centerX - x)))))) % 360.0f;
    }


    private float getDistance(float x, float y, float centerX, float centerY) {
        float a = centerX - x;
        float b = centerY - y;
        return (float) Math.sqrt((double) ((a * a) + (b * b)));
    }

    public void setGesture(AlbumGesture delegate) {
        this.gestureDeletate = delegate;
    }

    public void setOnPageChangeListener(OnPageChangedListener pageChangedDelegate) {
        this.pageChangedDelegate = pageChangedDelegate;
    }

    public boolean isScaling() {
        return this.isScaling;
    }

    public void setScaling(boolean isScaling) {
        this.isScaling = isScaling;
    }

    public float getmScaleFactor() {
        return this.mScaleFactor;
    }

    public void setmScaleFactor(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }
}
