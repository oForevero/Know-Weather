package com.forever.weather.Collector;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2015-2019
 * FileName: ActivityCollector
 * Author: Forever
 * Date: 2019/3/19 17:44
 * Description: Collectorclass for everywhere can finish App.
 */
public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();
    
    /**
     * @Description: Method for add activity to activities.
     * @Author: Forever
     * @Date: 2019/3/19 17:46
     */ 
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    
    /**
     * @Description: Method for remove activity from activities.
     * @Author: Forever
     * @Date: 2019/3/19 17:47
     */ 
    public static void delActivity(Activity activity){
        activities.remove(activity);
    }
    
    /**
     * @Description: Method for finish All activity and clear Activity list.
     * @Author: Forever
     * @Date: 2019/3/19 17:48
     */ 
    public static void finishAll(){
        for (Activity activity:
             activities) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}
