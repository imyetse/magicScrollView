package tse.ye.demo.demos;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by xieye on 2017/11/8.
 */

public class CustomViewPager extends ViewPager {
    private ArrayList<Integer> childCenterXAbs = new ArrayList<>();
    private SparseIntArray childIndex = new SparseIntArray();

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.e(CustomViewPager.class.getSimpleName(), "onTouchEvent():"+ev.getAction());
        return super.onTouchEvent(ev);
    }

    public CustomViewPager(Context context) {
        super(context);
        init();
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClipToPadding(false);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    /**
     * @return 第n个位置的child 的绘制索引
     */
    @Override
    protected int getChildDrawingOrder(int childCount, int n) {
        if (n == 0 || childIndex.size() != childCount) {
            childCenterXAbs.clear();
            childIndex.clear();
            int viewCenterX = getViewCenterX(this);
            for (int i = 0; i < childCount; ++i) {
                int indexAbs = Math.abs(viewCenterX - getViewCenterX(getChildAt(i)));
                //两个距离相同，后来的那个做自增，从而保持abs不同
                if (childIndex.get(indexAbs, -1) != -1) {
                    ++indexAbs;
                }
                childCenterXAbs.add(indexAbs);
                childIndex.append(indexAbs, i);
            }
            Collections.sort(childCenterXAbs);//1,0,2  0,1,2
        }
        //那个item距离中心点远一些，就先draw它。（最近的就是中间放大的item,最后draw）
        return childIndex.get(childCenterXAbs.get(childCount - 1 - n));
    }

    private int getViewCenterX(View view) {
        int[] array = new int[2];
        view.getLocationOnScreen(array);
        return array[0] + view.getWidth() / 2;
    }
}
