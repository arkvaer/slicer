package me.waver.slicer.common.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.util.StrUtil;

/**
 * @author Alex Waver
 * @date 2021/8/5 上午9:46
 */
public class TimeUtil {
    /**
     * 计算 HH:mm:ss.mmm 或者 HH:mm:ss 格式时间 毫秒数
     *
     * @param time 时间字符串
     * @return ms 毫秒数
     */
    public static long transTimeToMs(String time) {
        long ms = -1;
        if (StrUtil.isNotBlank(time)) {
            String[] times = time.split(":");
            long hour = Long.parseLong(times[0]) * DateUnit.HOUR.getMillis();
            long minute = Long.parseLong(times[1]) * DateUnit.MINUTE.getMillis();
            String[] secondsAndMillis = times[2].split("\\.");
            long second = Long.parseLong(secondsAndMillis[0]) * DateUnit.SECOND.getMillis();
            long millis = 0;
            if (secondsAndMillis.length > 1) {
                millis = Long.parseLong(secondsAndMillis[1]);
            }
            ms = hour + minute + second + millis;
            return ms;
        }
        return ms;
    }

    public static String formatMs(long ms) {
        long hour = ms / DateUnit.HOUR.getMillis();
        long lastMinute = ms - (hour * DateUnit.HOUR.getMillis());
        long minute = lastMinute / DateUnit.MINUTE.getMillis();
        long lastSecond = lastMinute - (minute * DateUnit.MINUTE.getMillis());
        long second = lastSecond / DateUnit.SECOND.getMillis();
        long millis = ms % DateUnit.SECOND.getMillis();
        return hour + ":" + minute + ":" + second + "." + millis;
    }


    public static long getOffset(String real, String start) {
        return TimeUtil.transTimeToMs(real) - TimeUtil.transTimeToMs(start);
    }

    public static String calcByOffset(String time, long offset) {
        long timeToMs = transTimeToMs(time);
        return formatMs(timeToMs + offset);
    }

}
