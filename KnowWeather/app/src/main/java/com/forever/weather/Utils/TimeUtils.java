package com.forever.weather.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Copyright (C), 2015-2019
 * FileName: TimeUtils
 * Author: Forever
 * Date: 2019/3/21 15:41
 * Description: ${DESCRIPTION}
 */
public class TimeUtils {
    /**
     * 判断时间是否在时间段内
     * @param nowTime
     * @param beginTime
     * @param endTime
     * @return
     */
    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);
        Calendar end = Calendar.getInstance();
        end.setTime(endTime);
        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }
}
