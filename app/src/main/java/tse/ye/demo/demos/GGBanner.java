package tse.ye.demo.demos;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xieye on 2017/11/8.
 */

public class GGBanner extends RelativeLayout {
    private Context context;
    private CustomViewPager viewPager;

    public GGBanner(Context context) {
        super(context);
        this.context = context;
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(GGBanner.class.getSimpleName(), "onTouchEvent()");
        return super.onTouchEvent(event);
    }

    public GGBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private List<Integer> mSourceList = new ArrayList<>();
    private List<View> mViews = new ArrayList<>();
    private int mSourceCount;
    private LayoutInflater layoutInflater;
    private GGBannerAdapter adapter;

    public void setData(List<Integer> sources) {
        if (mSourceList.size() == sources.size()) {
            return;
        }
        mSourceList.clear();
        this.mSourceList.addAll(sources);
        if (mSourceList.size() == 1) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) viewPager.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            viewPager.setLayoutParams(layoutParams);
        } else if (mSourceList.size() > 1) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
            int margin = 50;
            layoutParams.setMargins(margin, 0, margin, 0);
            viewPager.setLayoutParams(layoutParams);
        }

        mViews.clear();
        mSourceCount = mSourceList.size();
        for (int i = 0; i <= mSourceCount * 2 + 1; i++) {
            //TODO 这里是关键
            if (mSourceCount == 0) {
                break;
            }

            int recInfo;
            int realPosition;
            if (i == 0) {
                realPosition = toRealPosition(mSourceCount - 1);
                recInfo = mSourceList.get(realPosition);
            } else if (i == mSourceCount + 1) {
                realPosition = toRealPosition(0);
                recInfo = mSourceList.get(0);
            } else {
                realPosition = toRealPosition(i - 1);
                recInfo = mSourceList.get(realPosition);
            }
            // recInfo.getrType() == 101

            Log.e(GGBanner.class.getSimpleName(), "setViewData normal item index>>>>" + realPosition);
            View view = layoutInflater.inflate(R.layout.item_viewpager_image, null);
            view.setTag(realPosition);
            ImageView imageView = (ImageView) view.findViewById(R.id.banner_image);
            imageView.setTag(recInfo);
            imageView.setImageResource(recInfo);
            mViews.add(view);

            if (mSourceCount == 1) {
                //mSourceCount==1
                break;
            }
        }

        adapter = new GGBannerAdapter(mViews);
        adapter.setUpViewViewPager(viewPager);
    }

    /**
     * 返回真实的位置
     *
     * @param position
     * @return 下标从0开始
     */
    public int toRealPosition(int position) {
        int realPosition = (position) % mSourceCount;
        if (realPosition < 0)
            realPosition += mSourceCount;
        return realPosition;
    }

    private void init() {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_banner_view, this, true);
        viewPager = view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        viewPager.setPageTransformer(true, new CoverTransformer(viewPager));
    }

    public static class GGBannerAdapter extends PagerAdapter {

        private ArrayList<View> views = new ArrayList<>();
        private ViewPager viewPager;

        public GGBannerAdapter(List<View> views) {
            this.views.addAll(views);
        }

        /**
         * 初始化Adapter和设置当前选中的Item
         *
         * @param vPager
         */
        public void setUpViewViewPager(ViewPager vPager) {
            this.viewPager = vPager;
            this.viewPager.setAdapter(this);
            this.viewPager.getAdapter().notifyDataSetChanged();
            int currentItem = 1;
            //设置当前选中的Item
            viewPager.setCurrentItem(currentItem);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = viewPager.getCurrentItem();
            if (position == getCount() - 2) {
                //跳到中间
                position = position - 3;
                if (position >= 0) {
                    viewPager.setCurrentItem(position, false);
                }

            } else if (position == 1) {
                //跳到中间
                position = position + 3;
                if (position < getCount()) {
                    viewPager.setCurrentItem(position, false);
                }
            }
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View imageView = views.get(position);
            if (imageView == null) {
                return null;
            }
            if (container.indexOfChild(imageView) == -1) {
                container.addView(imageView);
            }

            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}