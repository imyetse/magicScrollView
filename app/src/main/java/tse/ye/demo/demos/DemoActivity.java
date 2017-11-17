package tse.ye.demo.demos;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import tse.ye.demo.lib.StickyScrollView;

/**
 * Created by xieye on 2017/11/7.
 */

public class DemoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit);
        ggBanner = findViewById(R.id.banner);
        scrollView = findViewById(R.id.scrollView);
        List<Integer> sourses = new ArrayList<>();
        sourses.add(R.mipmap.banner1);
        sourses.add(R.mipmap.banner2);
        sourses.add(R.mipmap.banner3);
        ggBanner.setData(sourses);
        scrollView.addIgnoredView(ggBanner);
    }

    GGBanner ggBanner;
    StickyScrollView scrollView;
}
