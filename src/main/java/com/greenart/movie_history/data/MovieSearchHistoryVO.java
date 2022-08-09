package com.greenart.movie_history.data;

import java.util.Date;

import lombok.Data;

@Data
public class MovieSearchHistoryVO {
    private Integer msh_seq;
    private Integer msh_ai_seq;
    private String msh_keyword;
    private Date msh_search_time;
}
