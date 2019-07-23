package com.example.yiyou_newversion.ui;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.yiyou_newversion.R;
import com.example.yiyou_newversion.fragment.GuideFunctionFragment;
import com.example.yiyou_newversion.fragment.GuideHomeFragment;
import com.example.yiyou_newversion.fragment.NoScrollViewPager;
import com.example.yiyou_newversion.fragment.PersonalFragment;
import com.example.yiyou_newversion.model.Data;

import java.util.ArrayList;

public class GuideMainActivity extends AppCompatActivity {

    //本次点击“返回键”的时刻
    private long mExitTime;
    private NoScrollViewPager mViewPager;
    private BottomNavigationView mBottomNavigationView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_guide);

        mViewPager = findViewById(R.id.mViewPager);
        mViewPager.setNoScroll(true);
        mBottomNavigationView =  findViewById(R.id.mBottom);
        mBottomNavigationView.setItemIconTintList(null);

        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (Data.CurrentTeamName == "none") {
                    Toast.makeText(GuideMainActivity.this, "请先去<首页>选择一个队伍", Toast.LENGTH_SHORT).show();
                    mViewPager.setCurrentItem(0);
                } else {
                    mViewPager.setCurrentItem(1);
                }
            }
        };

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btm_home:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.btm_function:
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(mRunnable);
                            }
                        });
                        thread.start();
                        break;
                    case R.id.btm_personal:
                        mViewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new NoScrollViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //在ArrayList中装入底部导航栏的三个fragment
        final ArrayList<Fragment> fragmentList = new ArrayList<>(3);
        fragmentList.add(new GuideHomeFragment());//首页
        fragmentList.add(new GuideFunctionFragment());//功能
        fragmentList.add(new PersonalFragment());//个人

        FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {

                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        };

        mViewPager.setAdapter(mFragmentPagerAdapter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                //大于2000ms则认为是误操作，使用Toast进行提示
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                //并记录下本次点击“返回键”的时刻，以便下次进行判断
                mExitTime = System.currentTimeMillis();
            } else {
                //小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
