package org.yasukusury.onlinedocument.biz.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.dto.ChapterESo;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.biz.entity.ESChapter;
import org.yasukusury.onlinedocument.biz.mapper.ChapterMapper;
import org.yasukusury.onlinedocument.biz.repository.ESChapterRepository;
import org.yasukusury.onlinedocument.commons.base.BaseService;
import org.yasukusury.onlinedocument.commons.json.SearchResult;
import org.yasukusury.onlinedocument.commons.utils.MapTool;
import org.yasukusury.onlinedocument.commons.utils.StringTool;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 30254
 * creadtedate: 2019/1/11
 */
@Service
public class ChapterService extends BaseService<ChapterMapper, Chapter> {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private ESChapterRepository repository;

    public ESChapter queryEsChapterById(String id) {
        return repository.findById(id).orElse(null);
    }

    public Page<ESChapter> searchChapter(String field, String keyword, Pageable pageable) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withHighlightFields(new HighlightBuilder.Field("content").numOfFragments(3))
                .withPageable(pageable)
                .withQuery(new TermQueryBuilder(field, keyword)).build();

        return template.queryForPage(query, ESChapter.class, new SearchResultMapper() {
                    @Override
                    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable page) {
                        ArrayList<ESChapter> results = new ArrayList<>();
                        SearchHits hits = response.getHits();
                        if (hits.getHits().length > 0) {
                            for (SearchHit searchHit : hits) {
                                Text[] fragments = searchHit.getHighlightFields().get(field).getFragments();
                                StringBuilder sb = new StringBuilder(fragments[0].toString());
                                for (int i = 1; i < fragments.length; i++) {
                                    sb.append("...").append(fragments[i].toString());
                                }
                                Map<String, Object> source = searchHit.getSource();
                                ESChapter esChapter = ESChapter.builder()
                                        .id(source.get("id").toString())
                                        .bookId(Long.parseLong(source.get("bookId").toString()))
                                        .content(sb.toString())
                                        .build();
                                results.add(esChapter);
                            }
                        }
                        return new AggregatedPageImpl<>((List<T>) results);
                    }
                }
        );
    }

    public void indexESChapter(ESChapter esChapter, String html) {
        esChapter.setContent(disHtml(html));
//        IndexQuery indexQuery = new IndexQueryBuilder().withId(commentPraise.getId().toString()).withIndexName(ESChapter.INDEX_NAME).withType(ESChapter.TYPE_NAME)
//                .withObject(esChapter).build();
//        template.index(indexQuery);
        repository.index(esChapter);
    }

    public void deleteESChapter(ESChapter esChapter) {
//        DeleteQuery deleteQuery = new DeleteQuery();
//        deleteQuery.setIndex(ESChapter.INDEX_NAME);
//        deleteQuery.setType(ESChapter.TYPE_NAME);
//        new QueryBuilder().queryName()
//        deleteQuery.setQuery();
        repository.delete(esChapter);
    }

    public List<ChapterESo> listESoById(Collection<String> ids) {
        if (ids.size() == 0){
            return new ArrayList<>();
        }
        List<Long> list = new ArrayList<>(ids.size());
        for (String id : ids) {
            list.add(Long.parseLong(id));
        }
        return baseMapper.listESoById(list);
    }

    public int countUrlBookChapter(Long book) {
        return baseMapper.countUrlBookChapter(book);
    }

    private String disHtml(String src) {
        String s = RegExUtils.replaceAll(src, "<[^>]+?>", " ");
        return RegExUtils.replaceAll(s, "\\s", " ");
    }

    public void updateChapters(List<Chapter> chapters, Book book){

        List<Chapter> newChapters = chapters.stream().filter(c -> c.getId() <= 0).collect(Collectors.toList());
        List<Chapter> oldChapters = chapters.stream().filter(c -> c.getId() > 0 && c.getDeleteTime() == null).collect(Collectors.toList());
        List<Chapter> deleteChapters = chapters.stream().filter(c -> c.getDeleteTime() != null).collect(Collectors.toList());
        if (deleteChapters.size() > 0) {
            removeBatchById(deleteChapters);
        }
        if (oldChapters.size() > 0) {
            updateBatchById(oldChapters);
        }
        if (newChapters.size() > 0) {
            newChapters.forEach(v->v.setBook(book.getId()));
            saveBatch(newChapters);
        }

    }

    public void warpBook(Book book) {
        List<Chapter> chapters = listByExample(Chapter.builder().book(book.getId()).build());
        book.setChaptersR(chapters);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {

        return super.removeByIds(idList);
    }
}
