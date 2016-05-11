package com.im4j.dotindicator.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.im4j.dotindicator.LineIndicator;
import com.im4j.dotindicator.viewpager.AutoViewPager;
import com.im4j.dotindicator.viewpager.LoopViewPager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LoopViewPager loopViewPager;
    private LineIndicator loopViewPagerIndicator;

    private AutoViewPager autoViewPager;
    private LineIndicator autoViewPagerIndicator;

    private PagerAdapter createPagerAdapter(final List<Integer> colors) {
        return new PagerAdapter() {
            @Override
            public int getCount() {
                return colors.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView view = new TextView(container.getContext());
                view.setText(String.valueOf(position+1));
                view.setTextSize(30);
                view.setGravity(Gravity.CENTER);
                view.setBackgroundColor(colors.get(position));
                container.addView(view);
                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (object instanceof View)
                    container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loopViewPager = (LoopViewPager) findViewById(R.id.loopviewpager);
        loopViewPagerIndicator = (LineIndicator) findViewById(R.id.loopviewpager_indicator);

        autoViewPager = (AutoViewPager) findViewById(R.id.autoviewpager);
        autoViewPagerIndicator = (LineIndicator) findViewById(R.id.autoviewpager_indicator);

        initViewPager(loopViewPager, loopViewPagerIndicator);
        initViewPager(autoViewPager, autoViewPagerIndicator);

        autoViewPager.startPlay(3 * 1000);

    }

    private static final List<Integer> sColors = Arrays.asList(Color.GRAY, Color.GREEN, Color.LTGRAY, Color.BLACK);
    private void initViewPager(ViewPager viewPager, LineIndicator indicator) {
        viewPager.setAdapter(createPagerAdapter(sColors));
        indicator.setIndicatorBackground(Color.RED);
        indicator.setIndicatorSelectedBackground(Color.YELLOW);
        indicator.setViewPager(viewPager);
    }
}
