package me.waver.slicer.common.entity;

import lombok.Data;
import me.waver.slicer.entity.Subtitle;

import java.util.List;

/**
 * @author Alex Waver
 * @date 2021/8/5 上午10:01
 */
@Data
public class SubResult {
    private boolean isSucceed;
    private String msg;
    private List<Subtitle> data;

    public SubResult(boolean isSucceed, String msg, List<Subtitle> data) {
        this.isSucceed = isSucceed;
        this.msg = msg;
        this.data = data;
    }

    public static SubResult ok() {
        return new SubResult(true, "success", null);
    }

    public static SubResult ok(String msg) {
        return new SubResult(true, msg, null);
    }

    public static SubResult ok(String msg, List<Subtitle> subtitleList) {
        return new SubResult(true, msg, subtitleList);
    }

    public static SubResult ok(List<Subtitle> subtitleList) {
        return new SubResult(true, "success", subtitleList);
    }

    public static SubResult fail() {
        return new SubResult(false, "error", null);
    }

    public static SubResult fail(String msg) {
        return new SubResult(false, msg, null);
    }

}
