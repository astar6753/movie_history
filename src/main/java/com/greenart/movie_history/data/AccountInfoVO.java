package com.greenart.movie_history.data;

import java.util.Date;

import lombok.Data;

@Data
public class AccountInfoVO {
    private Integer ai_seq;
    private String ai_id;
    private String ai_pwd;
    private String ai_name;
    private String ai_nickname;
    private Date ai_birth;
    private Integer ai_gen;
    private Date ai_reg_dt;
    private Integer ai_status;
    private String ai_profile_img;
}
