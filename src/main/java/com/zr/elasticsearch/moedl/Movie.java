package com.zr.elasticsearch.moedl;

import lombok.Data;

import java.util.List;

/**
 * 描述
 *
 * @author nicaide
 * @date 2019年12月23日 11:43:00
 */
@Data
public class Movie {

    private Integer id;

    private String title;

    private String overview;

    private String tagline;

    private List<Cast> cast;

    private List<Directors> directors;

}
