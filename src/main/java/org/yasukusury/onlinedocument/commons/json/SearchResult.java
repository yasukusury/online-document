package org.yasukusury.onlinedocument.commons.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 30254
 * creadtedate: 2019/2/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResult{

    private String id;
    private String title;
    private String highlight;
    private String url;
}
