package org.yasukusury.onlinedocument.commons.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 30254
 * creadtedate: 2018/12/1
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    private Object data;
    private boolean success;
    private String msg;
}
