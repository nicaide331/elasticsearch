package com.zr.elasticsearch.repository;

import com.zr.elasticsearch.moedl.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 描述
 *
 * @author nicaide
 * @date 2020年01月02日 15:24:00
 */
public interface BookRepository extends ElasticsearchRepository<Book, Integer> {
    List<Book> findByName(String name);
}
