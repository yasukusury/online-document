package org.yasukusury.onlinedocument.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yasukusury.onlinedocument.biz.entity.Chapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 30254
 * creadtedate: 2019/1/12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDto {

    private Long id;

    private Long pid;

    private Integer seq;

    private String name;

    private Boolean url;

    private Boolean valid;

    public static ChapterDto toDto(Chapter chapter) {
        return ChapterDto.builder()
                .id(chapter.getId())
                .pid(chapter.getPid())
                .seq(chapter.getSeq())
                .name(chapter.getName())
                .url(chapter.getUrl())
                .valid(chapter.getValid())
                .build();
    }

}
