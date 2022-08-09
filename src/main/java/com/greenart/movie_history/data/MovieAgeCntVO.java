package com.greenart.movie_history.data;

import lombok.Data;

@Data
public class MovieAgeCntVO {
    private String genre_name;
    private String mi_title;
    private String mi_country;
    private Integer mi_year;
    private Integer age;
    private Integer cnt;

    @Override
    public String toString(){
        return genre_name+","+mi_title+","+mi_country+","+mi_year+","+age+","+cnt;
    }
}
