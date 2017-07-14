package com.lsl.base.example;

import android.os.Bundle;

import com.lsl.base.R;
import com.lsl.base.common.BaseActivity;

/**
 * Created by Forrest
 * on 2017/7/3 14:38
 */

public class CacheActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.sample_cache_activity;
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
        mCommonToolbar.setTitle("网络缓存基本用法");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}
