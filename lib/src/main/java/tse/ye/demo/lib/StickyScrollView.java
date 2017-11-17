package tse.ye.demo.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xieye on 2017/10/17.
 */

public class StickyScrollView extends ScrollView {
    public static final String TAG = StickyScrollView.class.getSimpleName();

    public static final int PAGE_TOP = 0;
    public static final int PAGE_BOTTOM = 1;
    public static final double PERCENT = 0.4;
    public static final int ANIMATION_DURATION = 250;
    public static final int TOUCH_DURATION = 150;
    public static final int ANIMATE_DURATION = 500;
    public static final float TOUCH_FRACTION = 0.4f;

    private ViewGroup mChildLayout;
    private View mTopChildView;

    private Context mContext;
    private OnPageChangeListener onPageChangeListener;
    //滑动类
    private Scroller mScroller;
    //view的高度
    private int screenHeight;
    //topview的高度与屏幕的高度差
    private int offsetDistance;
    //topview的高度
    private int topChildHeight;
    //用户是否在触控屏幕
    private boolean isTouch;
    //值为0时屏幕显示topview，值为1时屏幕显示bottomview
    private int currentPage;
    //用户按下屏幕的时间戳
    private long downTime;
    //用户抬起时的时间戳
    private long upTime;
    //用户按下屏幕的y坐标
    private int downY;
    //用户抬起的y坐标
    private int upY;
    //页面是否切换
    private boolean isPageChange;
    //需要消费touch事件的子view
    private List<View> mIgnoreViews = new ArrayList<>();
    //animate动画结束 view的显示状态
    private boolean isChildViewShow = true;
    //动画
    private ObjectAnimator animator;
    //是否允许动画
    private boolean mAnimateEnable = true;
    //动画时长
    private long mAnimateDuration = ANIMATE_DURATION;
    //我称之为粘性指数
    private float mFraction = TOUCH_FRACTION;
    private boolean mInited = false;

    private int currentY;
    private int lastY;

    public StickyScrollView(Context context) {
        this(context, null, 0);
    }

    public StickyScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.StickyScrollView);
        mAnimateEnable = typedArray.getBoolean(R.styleable.StickyScrollView_animateEnable, true);
        mAnimateDuration = typedArray.getInt(R.styleable.StickyScrollView_animateDuration, ANIMATE_DURATION);
        boolean showHeader = typedArray.getBoolean(R.styleable.StickyScrollView_showHeader, true);
        mFraction = typedArray.getFloat(R.styleable.StickyScrollView_animateEnable, TOUCH_FRACTION);
        if (!showHeader) {
            currentPage = PAGE_BOTTOM;
        }
        typedArray.recycle();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mChildLayout = (ViewGroup) getChildAt(0);
        mTopChildView = mChildLayout.getChildAt(0);
        topChildHeight = mTopChildView.getMeasuredHeight();
        screenHeight = getMeasuredHeight();
        offsetDistance = topChildHeight - screenHeight;
        if (!mInited) {
            mInited = true;
            if (currentPage == PAGE_BOTTOM) {
                scrollTo(0, topChildHeight);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setAnimateEnable(boolean enable) {
        this.mAnimateEnable = enable;
    }

    public void setAnimationDuration(long duration) {
        this.mAnimateDuration = duration;
    }

    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    public void setFraction(float fraction) {
        this.mFraction = fraction;
    }


    //添加不拦截touch事件的view
    public void addIgnoredView(View view) {
        if (!mIgnoreViews.contains(view)) {
            mIgnoreViews.add(view);
        }
    }

    public void removeIgnoredView(View view) {
        if (mIgnoreViews.contains(view)) {
            mIgnoreViews.remove(view);
        }
    }

    private boolean isInIgnoredView(MotionEvent ev) {

        Rect rect = new Rect();
        for (View v : mIgnoreViews) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY())) {
                return true;
            }
        }
        return false;
    }

    public void setToInterceptEvent(boolean toIntercept) {
        this.toInterceptEvent = toIntercept;
    }

    private boolean toInterceptEvent = true;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!toInterceptEvent) {
            return false;
        }
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                if (isInIgnoredView(ev)) {
//                    //不拦截事件
//                    Log.e(TAG, "onInterceptTouchEvent>>不拦截事件");
//                    return false;
//                } else {
//                    Log.e(TAG, "onInterceptTouchEvent>>拦截事件");
//                    return true;
//                }
//            case MotionEvent.ACTION_UP:
//                break;
//        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isInIgnoredView(ev)) {
            Log.e(TAG, "onTouchEvent>>不消费事件");
            return false;
        } else {
            Log.e(TAG, "onTouchEvent>>消费事件");
            currentY = (int) ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = (int) ev.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveY = currentY - lastY;
                    lastY = currentY;
                    if (currentPage == PAGE_BOTTOM) {
                        if (getScrollY() <= topChildHeight) {
                            //下拉
                            scrollBy(0, (int) (-moveY * mFraction));
                            return true;
                        } else {
                            return super.onTouchEvent(ev);
                        }
                    } else {
                        return super.onTouchEvent(ev);
                    }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    break;
            }
            return super.onTouchEvent(ev);
        }
        //    return super.onTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isInIgnoredView(ev)) {
            //往下传递
            Log.e(TAG, "dispatchTouchEvent>>传递事件");
            return super.dispatchTouchEvent(ev);
        } else {
            Log.e(TAG, "dispatchTouchEvent>>不传递事件");
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isTouch = true;
                    downY = (int) ev.getY();
                    downTime = System.currentTimeMillis();
                    if (mScroller != null) {
                        mScroller.forceFinished(true);
                        mScroller = null;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_UP:
                    isTouch = false;
                    upY = (int) ev.getY();
                    upTime = System.currentTimeMillis();
                    boolean isUpMove = upY - downY <= 0;//是否上划
                    //用户手指在屏幕上的时间
                    long duration = upTime - downTime;

                    //这里要确保点击事件不失效
                    //we force stop scroll when touch down
                    //in some case we need to finish scroll up or down
                    //if (Math.abs(upY - downY) > 50) {
//                    Lg.e(">>>ISN_T CLICK:" + Math.abs(upY - downY));
//                Log.e(TAG, ">>>getScrollY :" + getScrollY());
//                Log.e(TAG, ">>>offsetDistance :" + offsetDistance);
                    if (currentPage == PAGE_TOP) {
                        //下面的判断已经能确定用户是否往上滑
                        if (getScrollY() > offsetDistance) {
                            mScroller = new Scroller(mContext);
                            if (getScrollY() < (screenHeight * PERCENT + offsetDistance) && duration > TOUCH_DURATION) {
                                //基本可以无视
                                isPageChange = false;
                                scrollToTarget(PAGE_TOP);
                            } else if (getScrollY() > topChildHeight / 5) {
                                //切换到下界面 手势是上划且滑动的距离大于一定值
                                isPageChange = true;
                                scrollToTarget(PAGE_BOTTOM);

                            } else if (getScrollY() <= topChildHeight / 5) {
                                isPageChange = false;
                                scrollToTarget(PAGE_TOP);
                            } else if (getScrollY() > topChildHeight) {
                                isPageChange = true;
                                currentPage = PAGE_BOTTOM;
                            }
                            return false;
                        }
                    } else {
                        if (getScrollY() < topChildHeight) {
                            mScroller = new Scroller(mContext);
                            if (getScrollY() < topChildHeight / 2) {
                                //切换到上界面
                                isPageChange = true;
                                scrollToTarget(PAGE_TOP);
                            } else {
                                isPageChange = false;
                                scrollToTarget(PAGE_BOTTOM);
                            }
                            return false;
                        }
                    }

                    break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void fling(int velocityY) {
//        if (currentPage == PAGE_BOTTOM) {
//            velocityY = velocityY / 4;
//        }
        super.fling(velocityY);
    }

    /**
     * 滚动到指定位置
     */
    private void scrollToTarget(int currentPage) {
        int delta;
        if (currentPage == PAGE_TOP) {
            delta = getScrollY();
            mScroller.startScroll(0, getScrollY(), 0, -delta, ANIMATION_DURATION);
            this.currentPage = PAGE_TOP;
        } else if (currentPage == PAGE_BOTTOM) {
            delta = getScrollY() - topChildHeight;
            mScroller.startScroll(0, getScrollY(), 0, -delta, ANIMATION_DURATION);
            this.currentPage = PAGE_BOTTOM;
        }
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true
        super.computeScroll();
        if (mScroller == null) {
            return;
        }
        if (mScroller.computeScrollOffset()) {
            this.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            if (mScroller.isFinished()) {
                mScroller = null;
                if (onPageChangeListener != null && isPageChange)
                    onPageChangeListener.OnPageChange(currentPage);
            }
        }
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //滚动时的监听,当用户触屏滑动时不监听，t == getScrollY

//        Log.e(TAG, "getScrollY:" + getScrollY());
//        Log.e(TAG, "topChildHeight / 2:" + topChildHeight / 2);
        if (getScrollY() < topChildHeight / 2) {
            toggle(true);
        } else {
            toggle(false);
        }
        if (currentPage == PAGE_TOP) {
            if (getScrollY() > offsetDistance && !isTouch) {
                if (mScroller == null) {
                    //用于控制当滑动到分界线时停止滚动
                    scrollTo(0, offsetDistance);
                } else {
                    scrollToTarget(PAGE_TOP);
                }
            }
        } else if (currentPage == PAGE_BOTTOM) {
            //Log.e(TAG, "getScrollY() < topChildHeight()>>" + (getScrollY() < topChildHeight) + "//isTouch//" + isTouch);
            if (getScrollY() < topChildHeight && !isTouch) {
                if (mScroller == null) {
                    scrollTo(0, topChildHeight);
                } else {
                    scrollToTarget(PAGE_BOTTOM);
                }
            }
        }
    }

    private void toggle(final boolean show) {
        if (show ^ isChildViewShow && mAnimateEnable) {
            if (animator != null && animator.isRunning()) {
                animator.cancel();
            }
            PropertyValuesHolder translateHolder = PropertyValuesHolder.ofFloat("Y", show ? 0 : -topChildHeight / 2);
            PropertyValuesHolder alphaHolder = PropertyValuesHolder.ofFloat("alpha", show ? 1.0f : 0.4f);
            animator = ObjectAnimator.ofPropertyValuesHolder(mTopChildView, translateHolder, alphaHolder).setDuration(mAnimateDuration);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isChildViewShow = show;
                    super.onAnimationEnd(animation);
                }
            });
            animator.start();
        }
    }

    /**
     * 切换页面完成后的回调
     */
    public interface OnPageChangeListener {
        void OnPageChange(int currentPage);
    }

}
