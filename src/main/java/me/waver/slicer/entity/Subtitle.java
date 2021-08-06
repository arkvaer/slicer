package me.waver.slicer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字幕对象
 *
 * @author alex
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subtitle {
    private String start;
    private String end;
    private String english;
    private String chinese;
}
