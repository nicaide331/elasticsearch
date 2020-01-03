package com.zr.elasticsearch.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 描述
 *
 * @author nicaide
 * @date 2019年12月23日 11:50:00
 */
public class Utils {

    public static final String INDEX = "zr";
    public static final String TYPE = "movie";

    public static final String URL = "127.0.0.1:9200/_xpack/sql?format=txt";
    static String  DRIVER = "org.elasticsearch.xpack.sql.jdbc.jdbc.JdbcDriver";

    public static Properties connectionProperties(){
        Properties properties = new Properties();
//        properties.put("user", "test_admin");
//        properties.put("password", "x-pack-test-password");
        return properties;
    }

    public static String getPath(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        String path = null;
        try {
            path = resource.getFile().getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }


    public static void createIndex(String index) {
        RestHighLevelClient client = Connection.getConnection();

        CreateIndexRequest request = new CreateIndexRequest(index);
        try {
            client.indices().create(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void insertBook() {
        System.out.println("开始");

        RestHighLevelClient client = Connection.getConnection();
        Map map = new HashMap();
        File file = new File(Utils.getPath("book.json"));
        String input = null;



    }

    public static void bitchInsert() {

        System.out.println("开始");

        RestHighLevelClient client = Connection.getConnection();
        Map map = new HashMap();
        File file = new File(Utils.getPath("book.json"));

        String input = null;
        JSONObject jsonObject = null;
        try {
            input = FileUtils.readFileToString(file, "UTF-8");
            jsonObject = JSON.parseObject(input);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将读取的数据转换为JSONObject
//        System.out.println(jsonObject);

        BulkRequest bulkRequest = new BulkRequest();

        for (JSONObject.Entry jojo : jsonObject.entrySet()){
            Map<Object, Object> movieMap = new HashMap<>();
            JSONObject j = (JSONObject) jojo.getValue();
//            JSONArray castArray = (JSONArray) j.get("cast");
//            List<Cast> cast = castArray.toJavaList(Cast.class);
//            List<String> names = new ArrayList<>();
//            for (Cast c : cast) {
//                names.add(c.getName());
//            }
//            ListCast listCast = new ListCast();
//            listCast.setName(names);
//            Map castMap = new HashMap();
//            castMap.put("name", listCast.getName());
//            j.put("cast", castMap);
            IndexRequest indexRequest = new IndexRequest("book", String.valueOf(jojo.getKey())).source(j);
            bulkRequest.add(indexRequest);
        }

        try {
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("结束");
    }

    public static void close() {
        try {
            Connection.getConnection().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
