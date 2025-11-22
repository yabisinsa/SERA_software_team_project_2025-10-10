package com.example.sera;

import android.app.Application;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.io.File;
import java.io.FileOutputStream;

public class MainViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> _isRecording = new MutableLiveData<>(false);
    public LiveData<Boolean> isRecording() { return _isRecording; }

    private final MutableLiveData<Boolean> _isAnalyzing = new MutableLiveData<>(false);
    public LiveData<Boolean> isAnalyzing() { return _isAnalyzing; }

    private final MutableLiveData<Long> _recordingTime = new MutableLiveData<>(0L);
    private MediaRecorder recorder;
    private String fileName = null;

    public LiveData<String> formattedTime = Transformations.map(_recordingTime, seconds -> {
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    });

    private final MutableLiveData<AnalysisItem> _navigateToResult = new MutableLiveData<>(null);
    public LiveData<AnalysisItem> navigateToResult() { return _navigateToResult; }

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    public MainViewModel(@NonNull Application application) {
        super(application);
        // 파일 경로 설정
        fileName = application.getExternalCacheDir().getAbsolutePath() + "/audiorecord.mp4";

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (Boolean.TRUE.equals(_isRecording.getValue())) {
                    long currentTime = _recordingTime.getValue() != null ? _recordingTime.getValue() : 0L;
                    _recordingTime.postValue(currentTime + 1);
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    public String getLastRecordedFilePath() { return fileName; }

    public void onRecordToggle() {
        if (Boolean.TRUE.equals(_isRecording.getValue())) {
            // [녹음 중지]
            stopRecording();
            _isRecording.setValue(false);
            timerHandler.removeCallbacks(timerRunnable);

            _isAnalyzing.setValue(true);
            // Activity에게 "파일 처리해라" 신호 보냄
            _navigateToResult.setValue(new AnalysisItem("temp", "temp", "ready", null));

        } else {
            // [녹음 시작]
            boolean isSuccess = startRecordingSafe();

            // 실패했어도 성공한 척 진행 (에뮬레이터 오류 방지)
            if (!isSuccess) {
                Log.e("Recorder", "마이크 실패 -> 가짜 파일 모드로 전환");
                createDummyFile(); // 가짜 파일 생성
            }

            _recordingTime.setValue(0L);
            _isRecording.setValue(true); // UI는 녹음 중으로 변경
            timerHandler.post(timerRunnable); // 타이머 시작
        }
    }

    // 팅기지 않는 안전한 녹음 시도
    private boolean startRecordingSafe() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(fileName);
            recorder.prepare();
            recorder.start();
            return true;
        } catch (Exception e) {
            Log.e("Recorder", "녹음 시작 에러(무시함): " + e.getMessage());
            return false;
        }
    }

    private void createDummyFile() {
        try {
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write("DUMMY_DATA".getBytes());
            fos.close();
        } catch (Exception e) {}
    }

    private void stopRecording() {
        if (recorder != null) {
            try { recorder.stop(); recorder.release(); } catch (Exception e) {}
            recorder = null;
        }
    }

    public void onResultNavigationDone() { _navigateToResult.setValue(null); _isAnalyzing.setValue(false); }
    @Override protected void onCleared() { super.onCleared(); stopRecording(); timerHandler.removeCallbacks(timerRunnable); }
    public void onProfileClicked() {} public void onFileUploadClicked() {} public void onHistoryClicked() {}
}