package com.example.sera;

import java.io.Serializable;
import java.util.Map;

public class AnalysisItem implements Serializable {
    private String date;       // 날짜 (예: 2025년 11월 20일)
    private String time;       // 시간 (예: 오후 9:30)
    private String emotionTag; // 감정 태그 (예: 😊 기쁨)
    private Map<String, Integer> emotionMap; // 감정 상세 데이터

    public AnalysisItem(String date, String time, String emotionTag, Map<String, Integer> emotionMap) {
        this.date = date;
        this.time = time;
        this.emotionTag = emotionTag;
        this.emotionMap = emotionMap;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getEmotionTag() { return emotionTag; }
    public Map<String, Integer> getEmotionMap() { return emotionMap; }
}