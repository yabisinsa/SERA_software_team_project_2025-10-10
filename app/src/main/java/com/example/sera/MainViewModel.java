package com.example.sera;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    // --- React의 useState 선언부 ---
    // private final: 내부에서만 값을 변경 (MutableLiveData)
    // public: 외부에서는 읽기만 가능 (LiveData)

    private final MutableLiveData<Boolean> _isRecording = new MutableLiveData<>(false);
    public LiveData<Boolean> isRecording() { return _isRecording; }

    private final MutableLiveData<Boolean> _isAnalyzing = new MutableLiveData<>(false);
    public LiveData<Boolean> isAnalyzing() { return _isAnalyzing; }

    private final MutableLiveData<Long> _recordingTime = new MutableLiveData<>(0L); // 초

    // --- React의 formatTime 로직 (LiveData 변환) ---
    public LiveData<String> formattedTime = Transformations.map(_recordingTime, seconds -> {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    });

    // --- React의 useEffect 타이머 로직 ---
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    public MainViewModel() {
        // 타이머 로직 정의
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

    // --- React의 handleRecordToggle ---
    public void onRecordToggle() {
        if (Boolean.TRUE.equals(_isRecording.getValue())) {
            // 녹음 중지 -> 분석 시작
            _isRecording.setValue(false);
            timerHandler.removeCallbacks(timerRunnable); // 타이머 중지
            System.out.println("Stop recording and start analyzing");

            // 분석 시뮬레이션 (React의 setTimeout 3초)
            _isAnalyzing.setValue(true);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                _isAnalyzing.setValue(false);
                System.out.println("Analysis complete");
            }, 3000); // 3초 딜레이

        } else {
            // 녹음 시작
            _recordingTime.setValue(0L); // 녹음 시간 리셋
            _isRecording.setValue(true);
            timerHandler.post(timerRunnable); // 타이머 시작
            System.out.println("Start recording");
        }
    }

    // ViewModel이 파괴될 때 핸들러 콜백 제거
    @Override
    protected void onCleared() {
        super.onCleared();
        timerHandler.removeCallbacks(timerRunnable);
    }

    // --- 나머지 핸들러 ---
    public void onProfileClicked() { System.out.println("View profile"); }
    public void onFileUploadClicked() { System.out.println("File upload"); }
    public void onHistoryClicked() { System.out.println("View history"); }
}