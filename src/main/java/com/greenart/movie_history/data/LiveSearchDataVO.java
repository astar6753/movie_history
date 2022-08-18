package com.greenart.movie_history.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Data;

@Data
public class LiveSearchDataVO {
    private String keyword;
    private Date search_time;
    private String id;
    private Integer age;
    private Integer gen;

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return formatter.format(search_time)+","+id+","+keyword+","+age+","+gen;
    }
}
