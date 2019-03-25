package com.forever.weather.BaseClass;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.forever.weather.Collector.ActivityCollector;
import com.forever.weather.Utils.MyToast;

/**
 * Copyright (C), 2015-2019
 * FileName: BaseActivity
 * Author: Forever
 * Date: 2019/3/19 17:44
 * Description: 基于所有activity的基类activity
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setStatusBarFullTransparent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.delActivity(this);
    }

    protected abstract void initControl();

    public abstract void bindListener();
    
    /**
     * @Description: Method for set app full screen.
     * @Author: Forever
     * @Date: 2019/3/22 15:06
     */ 
    private void setFullScreen(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * @Description: Method for when back key click two times,Quit the app.
     * @Author: Forever
     * @Date: 2019/3/20 9:03
     */ 
    long now = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if(System.currentTimeMillis() - now >2000){
                MyToast.showMToast(Toast.makeText(BaseActivity.this, "再点一次返回键退出程序", Toast.LENGTH_SHORT),1000 );
                now = System.currentTimeMillis();
            }else {
                ActivityCollector.finishAll();
            }
        }
        return true;
    }

    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
