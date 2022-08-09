package com.greenart.movie_history.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.greenart.movie_history.data.AccountInfoVO;
import com.greenart.movie_history.data.MovieLookupHistoryVO;
import com.greenart.movie_history.data.MovieSearchHistoryVO;

@Mapper
public interface DataGeneratorMapper {
    
    public void insertAccountInfos(List<AccountInfoVO> datas);
    public List<Integer> selectAccountSeqList();
    public List<Integer> selectMovieSeqList();
    public void insertLookupHistoryDatas(List<MovieLookupHistoryVO> datas);
    public List<String> selectMovieNames();
    public void insertSearchHistoryDatas(List<MovieSearchHistoryVO> datas);

}
