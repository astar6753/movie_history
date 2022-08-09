package com.greenart.movie_history.data;

import lombok.Data;

@Data
public class LiveLookupDataVO {
    private String id;
    private Integer gen;
    private Integer age;
    private String title;
    private String genre;
    private Integer movie_year;
    private String movie_country;

    @Override
    public String toString(){
        return id+","+gen+","+age+","+title+","+genre+","+movie_year+","+movie_country;
    }
}