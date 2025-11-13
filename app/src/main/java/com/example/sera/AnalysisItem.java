package com.example.sera;

import java.util.Map;

public class AnalysisItem {
    // 1. 필요한 기본 데이터 변수 선언
    String date;         // "2025년 10월 19일"
    String time;         // "오후 7:30"
    String emotionTag;   // "😊 기쁨" (가장 높은 감정)

    // 2. 모든 감정 데이터를 Map 형태로 저장
    // Map<감정 이름, 퍼센트>
    Map<String, Integer> emotionMap;

    // 3. 생성자 수정: 모든 데이터를 Map으로 받도록 변경
    public AnalysisItem(String date, String time, String emotionTag, Map<String, Integer> emotionMap) {
        this.date = date;
        this.time = time;
        this.emotionTag = emotionTag;
        this.emotionMap = emotionMap;
    }

    // 4. Getter 수정
    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getEmotionTag() {
        return emotionTag;
    }

    // 5. 모든 감정 데이터를 가져오는 새로운 Getter
    public Map<String, Integer> getEmotionMap() {
        return emotionMap;
    }

    // 6. 리스트 표시용(상위 2개) 문자열을 생성하는 헬퍼 메서드 추가
    public String getTopTwoStats() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Integer> entry : emotionMap.entrySet()) {
            if (count >= 2) break; // 상위 2개만

            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("% ");
            count++;
        }
        return sb.toString().trim();
    }
}