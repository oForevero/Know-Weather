package com.forever.weather.Utils;

import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Copyright (C), 2015-2019
 * FileName: MyToast
 * Author: Forever
 * Date: 2019/3/20 9:06
 * Description: 自定义toast的显示时间
 */
public class MyToast {
    static Timer timer = new Timer();
    public static void showMToast(final Toast toast, int time){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0 , 3000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, time);
    }
}
