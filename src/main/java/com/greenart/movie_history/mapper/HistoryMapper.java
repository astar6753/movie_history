package com.greenart.movie_history.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.greenart.movie_history.data.LiveLookupDataVO;
import com.greenart.movie_history.data.MovieAgeCntVO;
import com.greenart.movie_history.data.MovieInfoVO;
import com.greenart.movie_history.data.MovieVO;
import com.greenart.movie_history.data.testVO;

@Mapper
public interface HistoryMapper {
    public List<MovieAgeCntVO> selectMovieAgeCntInfo(String start_dt, String end_dt);
    public List<MovieAgeCntVO> selectMovieAgeCntInfo2(String start_dt, String end_dt);
    public List<String> selectSearchKeywordRank(String start_dt, String end_dt);
    public MovieVO selectMovie(String keyword);
    public List<String> selectMemberSearchKeywordRank(Integer seq);
    public Integer selectMovieGenreSeq(String keyword);
    public List<MovieInfoVO> selectMovieInfos(Integer genre_seq);
    public List<LiveLookupDataVO> selectLiveLookupDatas();

    public List<testVO> testCode1(Integer user_seq);
}
