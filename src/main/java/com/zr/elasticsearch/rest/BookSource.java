package com.zr.elasticsearch.rest;

import com.alibaba.fastjson.JSON;
import com.zr.elasticsearch.moedl.Book;
import com.zr.elasticsearch.repository.BookRepository;
import com.zr.elasticsearch.util.RestClientConfig;
import com.zr.elasticsearch.util.Utils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.List;
import java.util.Properties;

import static com.zr.elasticsearch.util.Utils.connectionProperties;

/**
 * 描述
 *
 * @author nicaide
 * @date 2020年01月02日 15:29:00
 */
@RestController
public class BookSource {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    RestClientConfig restClientConfig;


    @PostMapping("/book")
    public void getBookByName() {

        String address = "jdbc:es://http://" + Utils.URL;
        Properties connectionProperties = connectionProperties();

        try {
            Connection connection = DriverManager.getConnection(address);
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(
                    "SELECT * FROM twitter ");

            System.out.println(results);
        } catch (SQLException e) {
            e.printStackTrace();
        }

//        List<Book> bookList =  bookRepository.findByName("普通高等教育");
//        System.out.println(bookList);
    }
}
