package me.waver.slicer.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.waver.slicer.common.entity.SubResult;
import me.waver.slicer.entity.Subtitle;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author alex
 */
@Slf4j
public class SubtitleUtil {

    public static SubResult parseAssToSubTitle(String filePath, String videoStartTime) {
        SubResult result = SubResult.fail();
        List<String> lines = FileUtil.readLines(filePath, StandardCharsets.UTF_8);
        List<String> subtitleStrList = lines.stream().filter(str -> str.startsWith("Dialogue:") && !str.contains(",Default,,0000,0000,0000,,{\\an8")).collect(Collectors.toList());
        List<Subtitle> subtitleList = new ArrayList<>(subtitleStrList.size());
        if (CollUtil.isEmpty(subtitleStrList)) {
            return SubResult.fail("没有读到字幕数据");
        }
        long offset = 0;
        String currentLine = "";
        try {
            for (int i = 0; i < subtitleStrList.size(); i++) {
                String title = subtitleStrList.get(i);
                currentLine = title;
                String[] timeAndSentence = title.split(",Default,,0000,0000,0000,,");
                String[] timeArr = timeAndSentence[0].split(",");
                String start = timeArr[1];
                if (StrUtil.isNotBlank(videoStartTime) && "00:00:00".equals(videoStartTime) && i == 0) {
                    offset = TimeUtil.getOffset(videoStartTime, start);
                }
                String end = timeArr[2];
                String[] sentences = timeAndSentence[1].split("\\\\N\\{(.*?)}");
                String chinese = sentences[0];
                String english = sentences[1];
                Subtitle subtitle = Subtitle.builder()
                        .start(offset == 0 ? start : TimeUtil.calcByOffset(start, offset))
                        .end(offset == 0 ? end : TimeUtil.calcByOffset(end, offset))
                        .english(english)
                        .chinese(chinese)
                        .build();
                subtitleList.add(subtitle);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            result = SubResult.fail("第 [" + (subtitleList.size() + 1) + "] 句出现错误, 错误的行(不符合规则: 中文或英文缺失):" + currentLine + "\n错误日志:" + e.getMessage());
        } finally {

            if (CollUtil.isNotEmpty(subtitleList)) {
                for (int i = 0; i < subtitleList.size(); i++) {
                    while (i > 0 && !subtitleList.get(i - 1).getEnglish().endsWith(StrUtil.COMMA)) {
                        System.out.println("[" + i + "]" + subtitleList.get(i).getEnglish());
                        subtitleList.get(i).setStart(subtitleList.get(i - 1).getStart());
                        subtitleList.get(i).setChinese(subtitleList.get(i - 1).getChinese() + subtitleList.get(i).getChinese());
                        subtitleList.get(i).setEnglish(subtitleList.get(i - 1).getEnglish() + subtitleList.get(i).getEnglish());
                        subtitleList.remove(i - 1);
                        i--;
                    }
                }
                result = SubResult.ok(subtitleList);
            }
        }
        return result;
    }


    public static void sliceBySubTitle(String inputFilePath, String outputDir, String subtitlePath, String videoStartTime) {
        SubResult result = parseAssToSubTitle(subtitlePath, videoStartTime);
        if (result.isSucceed()) {
            List<Subtitle> subtitleList = result.getData();
            List<TimeRange> timeRangeList = TimeRange.getInstancesBySubTitleList(subtitleList);
            slice(inputFilePath, outputDir, timeRangeList);
        }
    }

    public static void slice(String filePath, String outputDir, List<TimeRange> timeRangeList) {
        String fileName = FileUtil.getPrefix(filePath);
        AtomicInteger index = new AtomicInteger(1);
        timeRangeList.forEach(timeRange -> {
            System.out.println("=====================================[" + index + "/" + timeRangeList.size() + "] =============================");
            boolean exist = FileUtil.exist(outputDir);
            String outputDirPath = outputDir;
            if (!exist) {
                File mkdir = FileUtil.mkdir(outputDir);
                outputDirPath = mkdir.getAbsolutePath();
            }
            String outputName = outputDirPath + fileName + StrUtil.UNDERLINE + index.getAndAdd(1) + StrUtil.UNDERLINE + System.currentTimeMillis() + ".m4a";
            String command = "ffmpeg -y -i " + filePath + " -ss " + timeRange.getStart() + " -to " + timeRange.getEnd() + " -vn -c:a copy " + outputName;
//            System.out.println(command);
//            String result = RuntimeUtil.execForStr(command);
//            System.out.println(result);
        });
    }

}


@Data
@NoArgsConstructor
@AllArgsConstructor
class TimeRange {
    private String start;
    private String end;

    public static TimeRange getInstance(Subtitle subtitle) {
        return new TimeRange(subtitle.getStart(), subtitle.getEnd());
    }

    public static List<TimeRange> getInstancesBySubTitleList(List<Subtitle> subtitleList) {
        List<TimeRange> timeRangeList = new ArrayList<>(subtitleList.size());
        subtitleList.forEach(subtitle -> timeRangeList.add(TimeRange.getInstance(subtitle)));
        return timeRangeList;
    }
}
