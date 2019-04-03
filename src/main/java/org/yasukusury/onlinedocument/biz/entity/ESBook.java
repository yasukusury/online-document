package org.yasukusury.onlinedocument.biz.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.yasukusury.onlinedocument.commons.base.BaseESEntity;

import java.util.Date;

/**
 * @author 30254
 * creadtedate: 2019/2/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(indexName = "online_document_book", type = "_doc", replicas = 0)
public class ESBook extends BaseESEntity {

    @Builder
    public ESBook(String id, Long author, String name, String tag1, String tag2, String tag3, String description, Date updateTime) {
        super(id);
        this.author = author;
        this.name = name;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.description = description;
        this.updateTime = updateTime;
    }

    @Field(type = FieldType.Keyword)
    private Long author;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Keyword)
    private String tag1;

    @Field(type = FieldType.Keyword)
    private String tag2;

    @Field(type = FieldType.Keyword)
    private String tag3;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Date)
    private Date updateTime;

    public static final String INDEX_NAME = "online_document_book";
}
