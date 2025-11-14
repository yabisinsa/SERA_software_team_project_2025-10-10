package com.example.sera; // 사용자님의 패키지 이름

import java.util.ArrayList;
import java.util.List;

/**
 * 앱 전역에서 기록 리스트를 공유하기 위한 Singleton 클래스 (임시 저장소)
 */
public class HistoryManager {

    private static HistoryManager instance;
    private List<AnalysisItem> historyList;

    // private 생성자
    private HistoryManager() {
        historyList = new ArrayList<>();
    }

    // HistoryManager의 유일한 인스턴스를 가져오는 메서드
    public static synchronized HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    /**
     * 기록을 리스트 맨 위에 추가합니다 (최신순)
     * @param item 저장할 분석 아이템
     */
    public void addHistory(AnalysisItem item) {
        historyList.add(0, item);
    }

    /**
     * 저장된 모든 기록 리스트를 반환합니다
     * @return 기록 리스트 (List<AnalysisItem>)
     */
    public List<AnalysisItem> getHistory() {
        return historyList;
    }
}