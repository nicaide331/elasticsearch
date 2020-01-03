package com.zr.elasticsearch.moedl;

import lombok.Data;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 描述
 *
 * @author nicaide
 * @date 2020年01月02日 15:25:00
 */
@Data
@Document(indexName="book")
public class Book {

    private Integer id;

    private String name;

    private String publish;

    private String author;
}
