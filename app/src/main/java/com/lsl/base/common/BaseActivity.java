package com.lsl.base.common;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;

import com.lsl.base.R;
import com.lsl.base.utils.StatusBarCompat;

import butterknife.ButterKnife;

/**
 * Created by Forrest
 * on 2017/7/3 11:44
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected int statusBarColor = 0;    //状态栏颜色
    public Toolbar mCommonToolbar;       //导航栏

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //设置状态栏颜色
        if (statusBarColor == 0){
            StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }else if (statusBarColor == -1){
            StatusBarCompat.compat(this,statusBarColor);
        }
        //绑定UI
        ButterKnife.bind(this);

        //ToolBar
        mCommonToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        if (mCommonToolbar != null) {
            initToolBar();
            setSupportActionBar(mCommonToolbar);
        }

    }

    public abstract int getLayoutId();

    public abstract void initToolBar();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    protected void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

}
