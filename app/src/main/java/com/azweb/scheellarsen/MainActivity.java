package com.azweb.scheellarsen;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.azweb.scheellarsen.fragments.TabVideoFragment.VideoFragment;
import com.azweb.scheellarsen.fragments.BaseContainerFragment;
import com.azweb.scheellarsen.fragments.TabHomeFragment;
import com.azweb.scheellarsen.fragments.TabImpresstionFragment;
import com.azweb.scheellarsen.fragments.TabInfomationFragment;
import com.azweb.scheellarsen.fragments.TabProductFragment;
import com.azweb.scheellarsen.fragments.TabVideoFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAB_HOME = "tab_home";
    private static final String TAB_PRODUCT = "tab_product";
    private static final String TAB_VIDEO = "tab_video";
    private static final String TAB_IMPRESSTION = "tab_impresstion";
    private static final String TAB_INFORMATION = "tab_infomation";
    /**
     * The duration of the animation sliding up the video in portrait.
     */
    private static final int ANIMATION_DURATION_MILLIS = 300;
    /**
     * The padding between the video list and the video in landscape orientation.
     */
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;
    private FragmentTabHost mTabHost;
    public ImageView mBtnBack;
    private View videoBox;
    private View closeButton;
    private VideoFragment videoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {

        videoBox = findViewById(R.id.video_box);
        closeButton = findViewById(R.id.close_button);
        videoBox.setVisibility(View.GONE);
        closeButton.setOnClickListener(this);
        mBtnBack = (ImageView) findViewById(R.id.btnBack);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_HOME),
                R.drawable.tab_indicator_gen, getString(R.string.tab_home_title), R.drawable.tab_indicator_home), TabHomeFragment.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_PRODUCT),
                R.drawable.tab_indicator_gen, getString(R.string.tab_product_title), R.drawable.tab_indicator_product), TabProductFragment.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_VIDEO),
                R.drawable.tab_indicator_gen, getString(R.string.tab_video_title), R.drawable.tab_indicator_video), TabVideoFragment.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_IMPRESSTION),
                R.drawable.tab_indicator_gen, getString(R.string.tab_impresstion_title), R.drawable.tab_indicator_impresstion), TabImpresstionFragment.class, null);
        mTabHost.addTab(setIndicator(MainActivity.this, mTabHost.newTabSpec(TAB_INFORMATION),
                R.drawable.tab_indicator_gen, getString(R.string.tab_infomation_title), R.drawable.tab_indicator_infomation), TabInfomationFragment.class, null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case TAB_HOME:
                        mBtnBack.setVisibility(View.GONE);
                        hideVideo();
                        break;
                    case TAB_PRODUCT:
                        mBtnBack.setVisibility(View.GONE);
                        hideVideo();
                        break;
                    case TAB_VIDEO:
                        mBtnBack.setVisibility(View.GONE);
                        hideVideo();
                        break;
                    case TAB_IMPRESSTION:
                        mBtnBack.setVisibility(View.GONE);
                        hideVideo();
                        break;
                    case TAB_INFORMATION:
                        mBtnBack.setVisibility(View.GONE);

                        break;
                }
            }
        });
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onBackPressed() {
        boolean isPopFragment = false;
        String currentTabTag = mTabHost.getCurrentTabTag();
        if (currentTabTag.equals(TAB_HOME)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_HOME)).popFragment();
        } else if (currentTabTag.equals(TAB_PRODUCT)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_PRODUCT)).popFragment();
        } else if (currentTabTag.equals(TAB_VIDEO)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_VIDEO)).popFragment();
        } else if (currentTabTag.equals(TAB_IMPRESSTION)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_IMPRESSTION)).popFragment();
        } else if (currentTabTag.equals(TAB_INFORMATION)) {
            isPopFragment = ((BaseContainerFragment) getSupportFragmentManager().findFragmentByTag(TAB_INFORMATION)).popFragment();
        }

        if (currentTabTag.equals(TAB_PRODUCT) && getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else if (!isPopFragment) {
            finish();
        }
    }

    private TabSpec setIndicator(Context ctx, TabSpec spec,
                                 int resid, String string, int genresIcon) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.activity_tab_item, null);
        v.setBackgroundResource(resid);
        TextView tv = (TextView) v.findViewById(R.id.txt_tabtxt);
        ImageView img = (ImageView) v.findViewById(R.id.img_tabtxt);

        tv.setText(string);
        img.setBackgroundResource(genresIcon);
        return spec.setIndicator(v);
    }

    public void openVideo(String videoId) {
        videoBox.setVisibility(View.VISIBLE);
        videoFragment =
                (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoFragment.setVideoId(videoId);
        // The videoBox is INVISIBLE if no video was previously selected, so we need to show it now.
        if (videoBox.getVisibility() != View.VISIBLE) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Initially translate off the screen so that it can be animated in from below.
                videoBox.setTranslationY(videoBox.getHeight());
            }
            videoBox.setVisibility(View.VISIBLE);
        }

        // If the fragment is off the screen, we animate it in.
        if (videoBox.getTranslationY() > 0) {
            videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_button:
                hideVideo();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void hideVideo() {
        if (videoFragment != null) {
            videoFragment.pause();
            videoBox.animate()
                    .translationYBy(videoBox.getHeight())
                    .setDuration(ANIMATION_DURATION_MILLIS)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            videoBox.setVisibility(View.INVISIBLE);
                        }
                    });
        }
    }
}
