package org.yasukusury.onlinedocument.commons.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 30254
 * creadtedate: 2018/1/13
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MdResult {

    private String url;
    private Integer success;
    private String msg;
}
