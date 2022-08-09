package com.greenart.movie_history.data;

import java.util.Date;

import lombok.Data;

@Data
public class MovieInfoVO {
    private Integer mi_seq;
    private Integer mi_genre_seq;
    private String mi_title;
    private Integer mi_viewing_age;
    private Integer mi_showing_status;
    private String mi_country;
    private Integer mi_year;
    private Date mi_opening_dt;
    private Integer mi_running_time;
    private Integer genre_seq;
    private String genre_name;
    private String img;
}
