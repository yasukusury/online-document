package org.yasukusury.onlinedocument.biz.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.yasukusury.onlinedocument.commons.base.BaseESEntity;

/**
 * @author 30254
 * creadtedate: 2019/2/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "online_document_chapter", type = "_doc", replicas = 0)
public class ESChapter extends BaseESEntity {

    @Builder
    public ESChapter(String id, Long bookId, String content) {
        super(id);
        this.bookId = bookId;
        this.content = content;
    }

    private Long bookId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    public static final String INDEX_NAME = "online_document_chapter";
}
