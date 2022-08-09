package com.greenart.movie_history.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greenart.movie_history.data.AccountInfoVO;
import com.greenart.movie_history.data.MovieAgeCntVO;
import com.greenart.movie_history.data.MovieLookupHistoryVO;
import com.greenart.movie_history.data.MovieSearchHistoryVO;
import com.greenart.movie_history.data.MovieVO;
import com.greenart.movie_history.mapper.DataGeneratorMapper;
import com.greenart.movie_history.mapper.HistoryMapper;

@RestController
@RequestMapping("/api")
public class HistoryAPIContoroller {
    @Autowired DataGeneratorMapper generate_mapper;
    @Autowired HistoryMapper history_mapper;
    @Value("${history.dir.path}") String path;
    @Value("${history.file.encoding}") String encoding;
    @Value("${history.spool.path}") String spoolDir;    

    private static String[] alpha_lower = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private static String[] alpha_upper = new String[26];
    private static String[] first_name = {"김","이","박","최","정","강","조","윤","장","임","한","오","서","신","권"};
    private static String[] middle_name = {"주","정","승","소","윤","민","하","호","정","서","홍","기","인","태","상"};
    private static String[] last_name = {"민","진","혁","원","미","성","람","진","준","숙","원","호","수","은","율"};
    private String makeName(){
        String name = "";
        name+=first_name[(int)(Math.random()*first_name.length)];
        name+=middle_name[(int)(Math.random()*first_name.length)];
        name+=last_name[(int)(Math.random()*first_name.length)];
        return name;
    }
    
    private String makeID(Integer len){
        String id = "";
        for(int i=0; i<len; i++){
            int r = (int)(Math.random()*2);
            if(r==0){
                id+= alpha_lower[(int)(Math.random()*alpha_lower.length)];
            }
            else{
                id+= alpha_upper[(int)(Math.random()*alpha_upper.length)];
            }
        }
        return id;
    }
    
    public static Date makeDateTime(Integer min, Integer max, Boolean genTime){
        Integer diff = max - min;
        Integer year = (int)(Math.random()*diff+min);
        Integer month = (int)(Math.random()*12+1);
        Integer date = (int)(Math.random()*28+1);
        // dt = year+"-"+leadingZero(month)+"-"+leadingZero(date)+" 00:00:00";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DATE,date);
        if(!genTime){
            c.set(Calendar.HOUR,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
        }
        else{        
            c.set(Calendar.HOUR,(int)(Math.random()*24));
            c.set(Calendar.MINUTE,(int)(Math.random()*60));
            c.set(Calendar.SECOND,(int)(Math.random()*60));
        }
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
    }
    
    public static String leadingZero(Integer n){
        return n<10?"0"+n:""+n;
    }

    public HistoryAPIContoroller() {
        for(int i=0; i<26; i++) {alpha_upper[i] = alpha_lower[i].toUpperCase();}
    }

    // 더미 user 생성
    @GetMapping("/user/gen")
    public Map<String, Object> generateRandomUsers(@RequestParam Integer cnt) {
        List<AccountInfoVO> userDatas = new ArrayList<AccountInfoVO>();

        for(int i=0; i<cnt; i++){
            AccountInfoVO user = new AccountInfoVO();
            user.setAi_id(makeID((int)(Math.random()*6+6)));
            user.setAi_name(makeName());
            user.setAi_pwd("4g9CwF3G17sk3kn/7DQTeQ==");
            user.setAi_birth(makeDateTime(1950, 2010, false));
            user.setAi_reg_dt(makeDateTime(2020, 2022, false));
            user.setAi_gen((int)(Math.random()*3+1));
            userDatas.add(user);
            //1234// 4g9CwF3G17sk3kn/7DQTeQ==
        }   
        // System.out.println(userDatas);
        generate_mapper.insertAccountInfos(userDatas);
        return null;
    }

    // MovieLookupHistory 더미 생성
    @GetMapping("/history/lookup/gen")
    public String generateRandomHistory(@RequestParam Integer cnt) {
        List<Integer> accountList = generate_mapper.selectAccountSeqList();
        List<Integer> movieList = generate_mapper.selectMovieSeqList();
        List<MovieLookupHistoryVO> historyList = new ArrayList<MovieLookupHistoryVO>();

        for(int i=0; i<cnt; i++){
            MovieLookupHistoryVO data = new MovieLookupHistoryVO();
            Integer r = (int)(Math.random()*accountList.size());
            data.setMlh_ai_seq(accountList.get(r));
            r = (int)(Math.random()*movieList.size());
            data.setMlh_mi_seq(movieList.get(r));
            data.setMlh_lookup_time(makeDateTime(2020, 2023, true));
            
            historyList.add(data);
        }
        generate_mapper.insertLookupHistoryDatas(historyList);
        
        return "inserted "+cnt+" history data(s)";
    }

    //영화별 연령대별 조회수 통계 로그 파일 생성
    @GetMapping("/movie/age/cnt")
    public String getMovieAgeCnt(
        @RequestParam @Nullable String start_dt,
        @RequestParam @Nullable String end_dt,
        @RequestParam @Nullable Boolean moveToSpool
    ) throws IOException {
        if(moveToSpool == null) moveToSpool = false;

        List<MovieAgeCntVO> list = history_mapper.selectMovieAgeCntInfo(start_dt, end_dt);
        String filePath = path+"/agecnt_";
        String fileName = "";
        Calendar c = Calendar.getInstance();
        Long timestamp = c.getTimeInMillis();
        if(start_dt != null && end_dt != null){
            start_dt = start_dt.replaceAll("-","");
            end_dt = start_dt.replaceAll("-","");
            fileName = start_dt + end_dt + timestamp + ".csv";
            filePath += fileName;
        }
        else{
            fileName += "previoust_day" + timestamp + ".csv";
            filePath += fileName;
        }
        File file = new File(filePath);
        file.createNewFile();
        //시스템에서 설정한 인코딩을 따르는 방식
        // BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        //인코딩 방식을 직접 설정해서 내보내는 방식
        BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(file), encoding
            )
        );
        for(MovieAgeCntVO data : list) {
            bw.write(data.toString());
            bw.newLine();
        }
        bw.close();

        if(moveToSpool){
            File from = new File(filePath);
            File target = new File(spoolDir+"/"+fileName);
            try{
                Files.move(from.toPath(),target.toPath(),StandardCopyOption.REPLACE_EXISTING);
            }catch (IOException ioe){
                return "파일 이동에 실패했습니다. (sourceFIle : "+filePath+")";
            }
        }
        return filePath;
    }

    //test start_dt만 입력했을때 start_dt 하루동안 둘다입력=기간 둘다null=오늘하루
    @GetMapping("/movie/age/cnt2")
    public String getMovieAgeCnt2(
        @RequestParam @Nullable String start_dt,
        @RequestParam @Nullable String end_dt
    ){
        List<MovieAgeCntVO> list = history_mapper.selectMovieAgeCntInfo(start_dt, end_dt);
        for(MovieAgeCntVO data : list) {
            System.out.println(data);
        }
        return "test";
    }

    // MovieSearchHistory 더미 생성
    @GetMapping("/history/search/gen")
    public String getHistorySearchGen(@RequestParam Integer cnt) {
        List<Integer> accountList = generate_mapper.selectAccountSeqList();
        List<String> keywords = new ArrayList<String>();
        List<String> movieNames = generate_mapper.selectMovieNames();

        for(String s : movieNames){
            String[] split = s.replaceAll(":", " ").replaceAll("[0-9]", "").split(" ");
            for(String word : split){
                if(!word.trim().equals(""))
                keywords.add(word);
            }
        }

        List<MovieSearchHistoryVO> historyList = new ArrayList<MovieSearchHistoryVO>();
        for(int i=0; i<cnt; i++){
            MovieSearchHistoryVO data = new MovieSearchHistoryVO();
            data.setMsh_ai_seq(accountList.get((int)(Math.random()*accountList.size())));
            data.setMsh_keyword(keywords.get((int)(Math.random()*keywords.size())));
            data.setMsh_search_time(makeDateTime(2020, 2023, true));
            historyList.add(data);
        }

        generate_mapper.insertSearchHistoryDatas(historyList);
        return null;
    }
    
    //키워드 검색순위를 기간별 조회
    @GetMapping("/keyword/rank")
    public Map<String,Object> getKeywordRank(
        @RequestParam @Nullable String start_dt, @RequestParam @Nullable String end_dt
        ) {
            Map<String,Object> resultMap = new LinkedHashMap<String,Object>();
            List<String> rank = history_mapper.selectSearchKeywordRank(start_dt, end_dt);
            resultMap.put("status",true);
            resultMap.put("list",rank);
            return resultMap;
    }

    @GetMapping("/movie/search/rank")
    public Map<String,Object> getMovieSearchRank(
        @RequestParam @Nullable String start_dt, @RequestParam @Nullable String end_dt
        ) {
            List<String> keywordRank = history_mapper.selectSearchKeywordRank(start_dt, end_dt);
            Map<String,Object> resultMap = new LinkedHashMap<String,Object>();
            List<MovieVO> movieRank = new ArrayList<MovieVO>();

            for(String keyword : keywordRank){
                MovieVO data = history_mapper.selectMovie(keyword);
                if(data==null) continue;
                Boolean duplicated = false;
                for(MovieVO mv : movieRank){
                    if(data.getSeq()==mv.getSeq()) duplicated = true;
                }
                if(!duplicated) movieRank.add(data);
            }
            resultMap.put("status", true);
            resultMap.put("list", movieRank);
            return resultMap;
    }

    // 사용자가 조회한 검색어 통계에서 관련 영화를 1개 뽑아서
    // 그 영화의 장르를 확인하고 동일 장르의 영화를 추천
    @GetMapping("/member/recommend")
    public Map<String,Object> getMemberRecommend(@RequestParam Integer seq){
        Map<String,Object> resultMap = new LinkedHashMap<String,Object>();

        //유저번호로부터 검색한 키워드를 검색수 높은 순으로 조회
        List<String> keywordList = history_mapper.selectMemberSearchKeywordRank(seq);
        System.out.println(keywordList);
        Map<Integer,Integer> genreCntMap = new HashMap<Integer,Integer>();
        for(String keyword : keywordList){
            //검색어로부터 영화제목를 검색해서 제일 근사값 하나의 장르번호를 조회
            Integer i = history_mapper.selectMovieGenreSeq(keyword);
            if(genreCntMap.get(i)!=null) {
                Integer cnt = genreCntMap.get(i);
                cnt++;
                genreCntMap.put(i, cnt);
            }
            else{  
                genreCntMap.put(i, 1);
            }
        }
        System.out.println(genreCntMap);
        Integer genre_seq = 0;
        Integer max = 0;
        for(Integer key : genreCntMap.keySet()){
            if(max<genreCntMap.get(key)){
                max=genreCntMap.get(key);
                genre_seq=key;
            }
        }
        // System.out.println("1위 장르 : "+genre_seq+" / 검색 수 : "+max);
        resultMap.put("recommend",history_mapper.selectMovieInfos(genre_seq));
        return resultMap;
    }
        
}



