package com.greenart.movie_history.data;

import java.util.Date;

import lombok.Data;

@Data
public class MovieLookupHistoryVO {
    private Integer mlh_seq;
    private Integer mlh_ai_seq;
    private Integer mlh_mi_seq;
    private Date mlh_lookup_time;
}
