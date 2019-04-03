package org.yasukusury.onlinedocument.biz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yasukusury.onlinedocument.biz.entity.Comment;
import org.yasukusury.onlinedocument.biz.entity.User;

import java.util.Date;

/**
 * @author 30254
 * creadtedate: 2019/3/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String user;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
    private Date createDate;
    private String content;
    @Builder.Default
    private Long praise = 0L;

    public static CommentDto toDto(Comment comment, User user){
        return builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createDate(comment.getCreateTime())
                .user(user.getUsername())
                .praise(comment.getPraise())
                .build();
    }
}
