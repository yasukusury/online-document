package org.yasukusury.onlinedocument.biz.service;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.yasukusury.onlinedocument.biz.dto.BookDto;
import org.yasukusury.onlinedocument.biz.dto.ChapterESo;
import org.yasukusury.onlinedocument.biz.entity.Book;
import org.yasukusury.onlinedocument.biz.entity.Chapter;
import org.yasukusury.onlinedocument.biz.entity.ESBook;
import org.yasukusury.onlinedocument.biz.entity.ESChapter;
import org.yasukusury.onlinedocument.biz.repository.ESBookRepository;
import org.yasukusury.onlinedocument.biz.repository.ESChapterRepository;
import org.yasukusury.onlinedocument.commons.json.SearchResult;
import org.yasukusury.onlinedocument.commons.utils.MapTool;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author 30254
 * creadtedate: 2019/2/25
 */
@Service
public class SearchService {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private ESChapterRepository esChapterRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private ESBookRepository esBookRepository;

    @Getter
    private List<String> hotTags;
    @Getter
    private List<String> maxTags;

    @PostConstruct
    @Scheduled(cron = "0 0 0 ? * 1")
    public void weekTask(){
        hotTags = searchHotTags();
        maxTags = searchMaxTags();
    }

    public List<SearchResult> searchChapter(Page<ESChapter> page) {
        List<SearchResult> resultList = new ArrayList<>(page.getSize());
        List<String> ids = new ArrayList<>(page.getSize());
        for (ESChapter esc : page){
            ids.add(esc.getId());
        }
        List<ChapterESo> chapterESos = chapterService.listESoById(ids);
        Map<Long, ChapterESo> esoMap = MapTool.mapByField("getChapterId", chapterESos);
        page.forEach(v -> {
            ChapterESo eso = esoMap.get(Long.parseLong(v.getId()));
            SearchResult result = SearchResult.builder().id(v.getId()).build();
            String title = eso.getBookName() + " :: ";
            String url = "/book/document/b" + v.getBookId() + "?nav=";
            title += eso.getChapterName();
            url += v.getId();
            result.setUrl(url);
            result.setTitle(title);
            result.setHighlight(v.getContent());
            resultList.add(result);
        });
        return resultList;
    }

    public List<BookDto> searchBook(Page<ESBook> page) {
        List<Long> ids = new ArrayList<>();
        page.forEach(v -> ids.add(Long.parseLong(v.getId())));
        return bookService.listDtoById(ids, true);
    }

    private List<String> searchMaxTags() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(0, 1))
                .addAggregation(AggregationBuilders.terms("tag1").field("tag1"))
                .addAggregation(AggregationBuilders.terms("tag2").field("tag2"))
                .addAggregation(AggregationBuilders.terms("tag3").field("tag3"))
                .build();

        AggregatedPage<ESBook> esBooks = template.queryForPage(query, ESBook.class);
        Map<String, Aggregation> aggregationMap = esBooks.getAggregations().asMap();

        Map<String, MyBucket> bucketMap = new HashMap<>();
        calTerms(bucketMap, aggregationMap);
        List<MyBucket> buckets = new ArrayList<>(bucketMap.values());
        buckets.sort((v1, v2) -> (int) (v2.count - v1.count));
        List<String> terms = new ArrayList<>(8);
        for (int i = 0; i < 8 && i < buckets.size(); i++) {
            terms.add(buckets.get(i).key);
        }
        return terms;
    }

    private List<String> searchHotTags() {
        SearchQuery query = new NativeSearchQueryBuilder()
                .withPageable(PageRequest.of(0, 1))
                .withFilter(new RangeQueryBuilder("update_time").from("now - 1W", true))
                .addAggregation(AggregationBuilders.terms("tag1").field("tag1"))
                .addAggregation(AggregationBuilders.terms("tag2").field("tag2"))
                .addAggregation(AggregationBuilders.terms("tag3").field("tag3"))
                .build();

        AggregatedPage<ESBook> esBooks = template.queryForPage(query, ESBook.class);
        Map<String, Aggregation> aggregationMap = esBooks.getAggregations().asMap();

        Map<String, MyBucket> bucketMap = new HashMap<>();
        calTerms(bucketMap, aggregationMap);
        List<MyBucket> buckets = new ArrayList<>(bucketMap.values());
        buckets.sort((v1, v2) -> (int) (v2.count - v1.count));
        List<String> terms = new ArrayList<>(8);
        for (int i = 0; i < 8 && i < buckets.size(); i++) {
            terms.add(buckets.get(i).key);
        }
        return terms;
    }

    private void calTerms(Map<String, MyBucket> bucketMap, Map<String, Aggregation> aggregationMap) {
        Consumer<Terms.Bucket> consumer = v -> {
            MyBucket myBucket = bucketMap.get(v.getKeyAsString());
            if (myBucket != null) {
                myBucket.count = myBucket.count + v.getDocCount();
            } else {
                bucketMap.put(v.getKeyAsString(), new MyBucket(v.getKeyAsString(), v.getDocCount()));
            }
        };
        ((StringTerms) aggregationMap.get("tag1")).getBuckets().forEach(consumer);
        ((StringTerms) aggregationMap.get("tag2")).getBuckets().forEach(consumer);
        ((StringTerms) aggregationMap.get("tag3")).getBuckets().forEach(consumer);
    }

    private static class MyBucket {
        public String key;
        public Long count;

        MyBucket(String key, Long count) {
            this.key = key;
            this.count = count;
        }
    }
}
