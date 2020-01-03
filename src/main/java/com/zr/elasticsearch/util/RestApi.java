package com.zr.elasticsearch.util;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.Strings;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author nicaide
 * @date 2019年12月27日 15:06:00
 */
public class RestApi {

    public void indexApi() {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "Richard");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "trying out Elasticsearch");
        IndexRequest request = new IndexRequest("zr")
                .id("1")
                .source(jsonMap);
    }

    public void getElasticSearch (String documentId){
        GetRequest request = new GetRequest(Utils.INDEX, documentId);
        //不获取数据源数据,默认获取的
//        request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        //要返回的字段
        String[] includes = {"title", "cast"};
//        //排除的字段
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);

//        request.fetchSourceContext(new FetchSourceContext(false));  // 禁用 _source 字段
//        request.storedFields("_none_"); // 禁止存储任何字段

        //返回的 GetResponse 对象包含要请求的文档数据（包含元数据和字段）
        try {
            GetResponse getResponse = Connection.getConnection().get(request, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                long version = getResponse.getVersion();
                String sourceAsString = getResponse.getSourceAsString(); // string 形式
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap(); // map
                byte[] sourceAsBytes = getResponse.getSourceAsBytes(); // 字节形式
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
