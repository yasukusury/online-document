package org.yasukusury.onlinedocument.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yasukusury
 * creadtedate: 2019/3/27
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterESo {

    private Long bookId;
    private Long chapterId;
    private String bookName;
    private String chapterName;
}
