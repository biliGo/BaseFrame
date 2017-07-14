package com.lsl.base;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.reflect.TypeToken;
import com.lsl.base.bean.ContractBean;
import com.lsl.base.common.BLog;
import com.lsl.base.common.BaseActivity;
import com.lsl.base.common.BaseBean;
import com.lsl.base.net.OkHttp;
import com.lsl.base.net.cache.CacheMode;
import com.lsl.base.net.callback.StringDialogCallback;
import com.lsl.base.parser.BaseBeanParser;
import com.lsl.base.utils.ContractUtil;

import java.text.ParseException;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {
        //test
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @OnClick(R.id.fab)
    public void fab(){
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title("OkHttp");
        builder.content("是否导入通讯录");
        builder.positiveText("确认");
        builder.negativeText("取消");
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                Toast.makeText(MainActivity.this,"onPositive",Toast.LENGTH_SHORT).show();
                getContract();
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Toast.makeText(MainActivity.this,"onNegative",Toast.LENGTH_SHORT).show();
            }
        });
        MaterialDialog dialog =builder.show();
    }

    private void getContract(){
       OkHttp.get("http://192.168.10.204:8080/user_contacts/getList")
               .tag(this)
               .cacheMode(CacheMode.NO_CACHE)
               .execute(new StringDialogCallback(this) {
                   @Override
                   public void onSuccess(String s, Call call, Response response) {
                       BaseBean<ContractBean> contractBean = null;
                       try {
                           contractBean=new BaseBeanParser<BaseBean<ContractBean>>(
                                   new TypeToken<BaseBean<ContractBean>>() {
                                   }).parse(s.getBytes());
                       } catch (ParseException e) {
                           e.printStackTrace();
                       }
                       BLog.i(contractBean.getData().getPush().get(0).getName());
                       ContractUtil.addContactList(MainActivity.this, contractBean.getData().getPush());
//                       Toast.makeText(MainActivity.this,"s="+s,Toast.LENGTH_SHORT).show();
                       Toast.makeText(MainActivity.this,"插入成功",Toast.LENGTH_SHORT).show();
                   }
               });
    }



}
