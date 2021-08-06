package me.waver.slicer.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * @author Alex Waver
 * @date 2021/8/4 下午4:30
 */

@Slf4j
public class SubtitleUtilTest {
    @Test
    public void parseAssToSubTitle() {
        String subtitlePath = "/home/alex/source/Videos/sub/Coco.ass";
        String videoPath = "/home/alex/source/Videos/Coco.mp4";
        String outputDir = "/home/alex/source/ffmpeg_output/";
        SubtitleUtil.sliceBySubTitle(videoPath, outputDir, subtitlePath, StrUtil.EMPTY);

    }

    @Test
    public void timeOffsetTest() {
        String real = "00:01:00";
        String now = "0:01:15.10";
        long time = TimeUtil.transTimeToMs(real) - TimeUtil.transTimeToMs(now);
        TimeUtil.formatMs(time);

    }


}
