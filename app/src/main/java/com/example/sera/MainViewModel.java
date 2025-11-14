package com.example.sera;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    // --- State 선언부 ---
    private final MutableLiveData<Boolean> _isRecording = new MutableLiveData<>(false);
    public LiveData<Boolean> isRecording() { return _isRecording; }

    private final MutableLiveData<Boolean> _isAnalyzing = new MutableLiveData<>(false);
    public LiveData<Boolean> isAnalyzing() { return _isAnalyzing; }

    private final MutableLiveData<Long> _recordingTime = new MutableLiveData<>(0L); // 초

    // --- 시간 포맷팅 ---
    public LiveData<String> formattedTime = Transformations.map(_recordingTime, seconds -> {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    });
    // --- 결과 화면 이동 신호 ---
    private final MutableLiveData<AnalysisItem> _navigateToResult = new MutableLiveData<>(null);
    public LiveData<AnalysisItem> navigateToResult() { return _navigateToResult; }

    // --- 타이머 로직 ---
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    public MainViewModel() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (Boolean.TRUE.equals(_isRecording.getValue())) {
                    long currentTime = _recordingTime.getValue() != null ? _recordingTime.getValue() : 0L;
                    _recordingTime.postValue(currentTime + 1);
                    timerHandler.postDelayed(this, 1000); // 1초 뒤에 다시 실행
                }
            }
        };
    }

    // --- 녹음 토글 핸들러 ---
    public void onRecordToggle() {
        if (Boolean.TRUE.equals(_isRecording.getValue())) {
            // 녹음 중지 -> 분석 시작
            _isRecording.setValue(false);
            timerHandler.removeCallbacks(timerRunnable);
            System.out.println("Stop recording and start analyzing");

            // 분석 시뮬레이션 (3초)
            _isAnalyzing.setValue(true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                _isAnalyzing.setValue(false);

                // --- [수정됨] "기쁨" 더미 데이터 대신 랜덤으로 생성 ---

                // 1. 샘플 데이터 목록 생성
                List<AnalysisItem> sampleItems = new ArrayList<>();
                Random random = new Random();

                // 샘플 1: 기쁨 (Figma 디자인과 동일)
                Map<String, Integer> map1 = new LinkedHashMap<>();
                map1.put("기쁨", 75);
                map1.put("슬픔", 8);
                map1.put("분노", 5);
                map1.put("불안", 7);
                map1.put("중립", 5);
                sampleItems.add(new AnalysisItem("2025년 11월 14일", "오후 3:40", "😊 기쁨", map1));

                // 샘플 2: 슬픔 (HistoryActivity 참고)
                Map<String, Integer> map2 = new LinkedHashMap<>();
                map2.put("슬픔", 62);
                map2.put("불안", 18);
                map2.put("중립", 15);
                map2.put("기쁨", 3);
                map2.put("분노", 2);
                sampleItems.add(new AnalysisItem("2025년 11월 14일", "오후 3:41", "😢 슬픔", map2));

                // 샘플 3: 분노 (HistoryActivity 참고)
                Map<String, Integer> map3 = new LinkedHashMap<>();
                map3.put("분노", 70);
                map3.put("슬픔", 15);
                map3.put("불안", 5);
                map3.put("중립", 5);
                map3.put("기쁨", 5);
                sampleItems.add(new AnalysisItem("2025년 11월 14일", "오후 3:42", "😡 분노", map3));

                // 2. 목록(0, 1, 2) 중에서 랜덤으로 하나 선택
                AnalysisItem randomItem = sampleItems.get(random.nextInt(sampleItems.size()));

                // 3. 그 랜덤 아이템을 ResultActivity로 전달
                _navigateToResult.postValue(randomItem);

            }, 3000);

        } else {
            // 녹음 시작
            _recordingTime.setValue(0L);
            _isRecording.setValue(true);
            timerHandler.post(timerRunnable);
            System.out.println("Start recording");
        }
    }

    // --- [추가] 결과 화면 이동 신호 리셋 ---
    public void onResultNavigationDone() {
        _navigateToResult.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        timerHandler.removeCallbacks(timerRunnable);
    }

    // --- 메뉴 핸들러 ---
    public void onProfileClicked() { System.out.println("View profile"); }
    public void onFileUploadClicked() { System.out.println("File upload"); }
    public void onHistoryClicked() { System.out.println("View history"); }
}