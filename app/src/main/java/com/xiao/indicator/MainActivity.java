package com.xiao.indicator;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiao.indicator.widget.FixedSpeedScroller;
import com.xiao.indicator.widget.indicator.NumberIndicater;
import com.xiao.indicator.widget.indicator.PointIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private NumberIndicater mNumberIndicater;
    private PagerAdapter mAdapter;
    private PointIndicator mPointIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mNumberIndicater = (NumberIndicater) findViewById(R.id.number_indicater);
        mPointIndicator = (PointIndicator) findViewById(R.id.point_indicater);

//        changeViewPagerSpeed();
        initAdapter();
    }

    private void changeViewPagerSpeed() {
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext());
            field.set(mViewPager, scroller);
            scroller.setmDuration(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initAdapter() {
        final List<ImageView> imageviews = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            int resId = getResources().getIdentifier("pic_" + i, "drawable", getPackageName());
            imageView.setImageResource(resId);
            imageviews.add(imageView);
        }
        mAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return imageviews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = imageviews.get(position);
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ImageView imageView = imageviews.get(position);
                container.removeView(imageView);
            }
        };
        mViewPager.setAdapter(mAdapter);
        mNumberIndicater.bindViewPager(mViewPager);
        mPointIndicator.bindViewPager(mViewPager);
//        startLoop();
    }

    private void startLoop() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                loop();
            }
        };
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(timerTask, 300, 3000);
    }

    private void loop() {
        int curPos = mViewPager.getCurrentItem();
        curPos = (++curPos) % mAdapter.getCount();
        mViewPager.setCurrentItem(curPos);
    }
}
