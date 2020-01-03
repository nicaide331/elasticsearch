package com.zr.elasticsearch.util;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.index.search.MultiMatchQuery;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 描述
 *
 * @author nicaide
 * @date 2019年12月27日 16:17:00
 */
public class SearchApi {

    public void search() {
        SearchRequest request = new SearchRequest("zr");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(searchSourceBuilder);

        try {
            SearchResponse response = Connection.getConnection().search(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buildSearch() {
        SearchRequest request = new SearchRequest("zr");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("title", "The Collection"));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(searchSourceBuilder);

        try {
            SearchResponse response = Connection.getConnection().search(request, RequestOptions.DEFAULT);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void matchSearch()  {

        SearchRequest request = new SearchRequest("zr");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", "Star Trek");
        //模糊查询
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
        //前缀查询长度
        matchQueryBuilder.prefixLength(3);

        matchQueryBuilder.maxExpansions(10);

        searchSourceBuilder.query(matchQueryBuilder);

        try {
            SearchResponse response = Connection.getConnection().search(request, RequestOptions.DEFAULT);

            //遍历循环结构
            getSearchHits(response);

            //HTTP状态码
            RestStatus restStatus = response.status();
            //查询占用的时间
            TimeValue took = response.getTook();

            //是否超时
            boolean timeOut = response.isTimedOut();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * P199
     * 例子：7.2
     */
    public void boolQuery() {

        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();


        //要返回的字段
        String[] includes = {"title", "cast"};
        //排除的字段
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);

        boolQueryBuilder.should(QueryBuilders.matchQuery("title", "Star")).boost(0.1f);
        boolQueryBuilder.should(QueryBuilders.multiMatchQuery("William Shatner", "title", "cast.name", "overview", "directors.name"));
        sourceBuilder.query(boolQueryBuilder);
        request.source(sourceBuilder);

        try {
            SearchResponse response = Connection.getConnection().search(request, RequestOptions.DEFAULT);
            getSearchHits(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void boolFunctionQuery() {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("title", "Star Trek");
        //模糊查询
        matchQueryBuilder.fuzziness(Fuzziness.AUTO);
        //前缀查询长度
        matchQueryBuilder.prefixLength(3);

        matchQueryBuilder.maxExpansions(10);

        ScoreFunctionBuilder<?> scoreFunctionBuilder = ScoreFunctionBuilders.weightFactorFunction(2.5f);
        FunctionScoreQueryBuilder functionScoreQueryBuilder = new FunctionScoreQueryBuilder(matchQueryBuilder, scoreFunctionBuilder);
        sourceBuilder.query(functionScoreQueryBuilder);

        request.source(sourceBuilder);

        try {
            SearchResponse response = Connection.getConnection().search(request, RequestOptions.DEFAULT);
            getSearchHits(response);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exactQuery (String userSearch) {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //基于短语的精确查询
//        MatchPhraseQueryBuilder matchPhraseQueryBuilder = new MatchPhraseQueryBuilder("title", "Star Trek");
//        matchPhraseQueryBuilder.boost(1000);
//        boolQueryBuilder.should(matchPhraseQueryBuilder);

        //基准查询
        boolQueryBuilder.should(QueryBuilders.multiMatchQuery(userSearch, "title", "cast.name", "overview", "directors.name")
                .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS));
        //片名查询放大1000倍
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("title", userSearch).boost(1000));
        //人名全名查询放大100倍
        boolQueryBuilder.should(QueryBuilders.multiMatchQuery(userSearch, "cast.name.bigrammed", "directors.name.bigrammed")
                .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                .boost(100));
        //人名精准查询放大1000倍
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("cast.name", "william shatner").boost(1000));
        boolQueryBuilder.should(QueryBuilders.matchPhraseQuery("directors.name", "william shatner").boost(1000));

        boolQueryBuilder.should(QueryBuilders.multiMatchQuery("title", "cast.name", "overview", "directors.name")
                .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS));

        sourceBuilder.query(boolQueryBuilder);
        request.source(sourceBuilder);

        try {
            SearchResponse response = Connection.getConnection().search(request, RequestOptions.DEFAULT);
            getSearchHits(response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 遍历循环查询结果
     * @param response
     */
    public static void getSearchHits(SearchResponse response) {
        SearchHits searchHits =  response.getHits();

        Iterator iterable =  searchHits.iterator();
        while (iterable.hasNext()) {
            SearchHit searchHit = (SearchHit) iterable.next();
            Map<String, Object> map = searchHit.getSourceAsMap();
            for (Map.Entry movie : map.entrySet()) {
                System.out.println(movie);
            }
        }
    }




}
