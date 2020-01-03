package com.zr.elasticsearch.demo;

import com.zr.elasticsearch.util.RestApi;
import com.zr.elasticsearch.util.SearchApi;
import org.junit.Test;

/**
 * 描述
 *
 * @author nicaide
 * @date 2019年12月27日 11:20:00
 */
public class RestTest {

    private RestApi restApi = new RestApi();
    private SearchApi searchApi = new SearchApi();


    @Test
    public void getApi() {
        String documentId = "193";
        restApi.getElasticSearch(documentId);
    }

    @Test
    public void search() {
        new SearchApi().search();
    }


    @Test
    public void getBoolQuery() {
        searchApi.boolQuery();
    }

    @Test
    public void getFunctionQuery() {
        searchApi.boolFunctionQuery();
    }


    @Test
    public void getExactQuery() {
        searchApi.exactQuery("Star Trek Patrick Stewart");
    }

}
