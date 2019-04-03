package org.yasukusury.onlinedocument.commons.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author 30254
 * creadtedate: 2019/2/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseESEntity implements Serializable {

    @Id
    private String id;

    public static final String TYPE_NAME = "_doc";
}
