package com.greenart.movie_history.component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.greenart.movie_history.api.HistoryAPIContoroller;
import com.greenart.movie_history.data.LiveLookupDataVO;
import com.greenart.movie_history.data.LiveSearchDataVO;
import com.greenart.movie_history.data.MovieAgeCntVO;
import com.greenart.movie_history.data.MovieLookupHistoryVO;
import com.greenart.movie_history.data.MovieSearchHistoryVO;
import com.greenart.movie_history.mapper.DataGeneratorMapper;
import com.greenart.movie_history.mapper.HistoryMapper;

@Component
public class HistoryScheduleComponent {
    @Autowired DataGeneratorMapper generate_mapper;
    @Autowired HistoryMapper history_mapper;
    @Value("${history.dir.path}") String path;
    @Value("${history.file.encoding}") String encoding;
    @Value("${history.spool.path}") String spoolDir;

    // 초 분 시 일 월 년
    // 숫자를 그대로 쓰는 경우 - 그 시간 자체
    // */숫자 = 매 숫자시간마다
    // cron="*/2 * * * * *"  2초마다
    // cron="0 20 4 * * *"  4시 20분 00초 마다
    // cron="0 */20 4 * * *"  4시 정각부터 20분마다 5시전까지

    // @Scheduled(cron="*/2 * * * * *")
    // public void someSchedule(){
    //     LocalTime t = LocalTime.now();
    //     System.out.println(t.toString().split("\\.")[0]);
    // }

    // 1분에 최소50 최대165
    // 1분마다
    @Scheduled(cron="*/5 * * * * *")
    public void makeLookupHistorySchedule(){
        List<Integer> accountList = generate_mapper.selectAccountSeqList();
        List<Integer> movieList = generate_mapper.selectMovieSeqList();
        List<MovieLookupHistoryVO> historyList = new ArrayList<MovieLookupHistoryVO>();
        // 1분에 최대30 난수
        Integer cnt = (int)(Math.random()*30+1);
        for(int i=0; i<cnt; i++){
            MovieLookupHistoryVO data = new MovieLookupHistoryVO();
            Integer r = (int)(Math.random()*accountList.size());
            data.setMlh_ai_seq(accountList.get(r));
            r = (int)(Math.random()*movieList.size());
            data.setMlh_mi_seq(movieList.get(r));
            data.setMlh_lookup_time(new Date());
            
            historyList.add(data);
        }
        generate_mapper.insertLookupHistoryDatas(historyList);
        System.out.println("generate "+cnt+" datas -"+LocalDateTime.now().toString());
    }

    //4시간마다
    @Scheduled(cron="0 0 4 * * *")
    public void exportLookupHistorySchedule() throws Exception {
        List<MovieAgeCntVO> list = history_mapper.selectMovieAgeCntInfo(null, null);
        String filePath = path+"/agecnt_";
        Calendar c = Calendar.getInstance();
        Long timestamp = c.getTimeInMillis();
        
        // filePath += "lookup_history_"+LocalDate.now().toString()+"_"+timestamp+".csv";
        String fileName = LocalDate.now().toString()+"_"+timestamp+".csv";
        filePath += fileName;

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
        
        File from = new File(filePath);
        File target = new File(spoolDir+"/"+fileName);
        try{
            Files.move(from.toPath(),target.toPath(),StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException ioe){
            System.out.println("파일 이동에 실패했습니다. (sourceFIle : "+filePath+")");
        }
        
    }

    @Scheduled(cron="*/5 * * * * *")
    public void exportLiveLookupDataVO() throws IOException {
        List<LiveLookupDataVO> list = history_mapper.selectLiveLookupDatas();
        File liveLogFile = new File("/home/movie/live_data/LookupLiveData.log");

        //인코딩 방식을 직접 설정해서 내보내는 방식 
        // append null 새 파일로 작성
        // append true 아래에추가로작성
        // append false 덮어쓰기
        BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(liveLogFile, true), encoding
            )
        );
        for(LiveLookupDataVO data : list) {
            bw.write(data.toString());
            bw.newLine();
        }
        bw.close();
    }

    @Scheduled(cron="*/5 * * * * *") 
    public void makeSearchHistorySchedule() {
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
        Integer cnt  = (int)(Math.random()*70+20);

        List<MovieSearchHistoryVO> historyList = new ArrayList<MovieSearchHistoryVO>();
        for(int i=0; i<cnt; i++){
            MovieSearchHistoryVO data = new MovieSearchHistoryVO();
            data.setMsh_ai_seq(accountList.get((int)(Math.random()*accountList.size())));
            data.setMsh_keyword(keywords.get((int)(Math.random()*keywords.size())));
            data.setMsh_search_time(new Date());
            // System.out.println(data);
            historyList.add(data);
        }
        System.out.println("Generated "+cnt+" random search history");
        generate_mapper.insertSearchHistoryDatas(historyList);
    }

    @Scheduled(cron="*/5 * * * * *")
    public void exportLiveSearchDataA() throws Exception {
        List<LiveSearchDataVO> list = history_mapper.selectLiveSearchDatasA();
        File liveLogFile = new File("/home/movie/live_data/SearchLiveDataA.log");
        if(!liveLogFile.exists()){
            liveLogFile.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(liveLogFile, true), encoding
            )
        );
        for(LiveSearchDataVO data : list) {
            // System.out.println(data.toString());
            bw.write(data.toString());
            bw.newLine();
            
        }
        bw.close();
        
    }
    
    @Scheduled(cron="*/5 * * * * *")
    public void exportLiveSearchDataB() throws Exception {
        List<LiveSearchDataVO> list = history_mapper.selectLiveSearchDatasA();
        File liveLogFile = new File("/home/movie/live_data/SearchLiveDataB.log");
        if(!liveLogFile.exists()){
            liveLogFile.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(liveLogFile, true), encoding
            )
        );
        for(LiveSearchDataVO data : list) {
            // System.out.println(data.toString());
            bw.write(data.toString());
            bw.newLine();
            
        }
        bw.close();
        
    }
}
