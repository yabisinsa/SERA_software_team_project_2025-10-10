package com.example.sera;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 기록을 앱 내부 저장소(SharedPreferences)에 영구 저장하는 관리자 클래스
 * 앱을 껐다 켜도 데이터가 유지됩니다.
 */
public class HistoryManager {

    private static HistoryManager instance;
    private List<AnalysisItem> historyList;
    private SharedPreferences prefs;
    private Gson gson;

    // 생성자: Context를 받아서 저장소(Prefs)를 엽니다.
    private HistoryManager(Context context) {
        prefs = context.getSharedPreferences("sera_history_prefs", Context.MODE_PRIVATE);
        gson = new Gson();
        historyList = new ArrayList<>();
        loadHistory(); // 앱이 켜질 때 저장된 기록을 불러옵니다.
    }

    // [중요] 싱글톤 패턴: Context를 파라미터로 받도록 수정됨
    public static synchronized HistoryManager getInstance(Context context) {
        if (instance == null) {
            instance = new HistoryManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 기록을 리스트 맨 위에 추가하고, 즉시 파일로 저장합니다.
     */
    public void addHistory(AnalysisItem item) {
        historyList.add(0, item); // 최신순 저장을 위해 맨 앞에 추가
        saveHistory(); // 변경 사항을 파일에 저장
    }

    /**
     * 저장된 모든 기록 리스트를 반환합니다.
     */
    public List<AnalysisItem> getHistory() {
        return historyList;
    }

    // --- 내부 저장 로직 (JSON 변환) ---

    // 리스트 -> 문자열(JSON) 변환 후 저장
    private void saveHistory() {
        String json = gson.toJson(historyList);
        prefs.edit().putString("history_list", json).apply();
    }

    // 문자열(JSON) -> 리스트 변환 후 불러오기
    private void loadHistory() {
        String json = prefs.getString("history_list", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<AnalysisItem>>() {}.getType();
            historyList = gson.fromJson(json, type);
        }

        if (historyList == null) {
            historyList = new ArrayList<>();
        }
    }

    // 기록 전체 삭제 (테스트용)
    public void clearAll() {
        historyList.clear();
        saveHistory();
    }
}