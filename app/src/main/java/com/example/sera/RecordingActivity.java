package com.example.sera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import android.os.Looper;

import com.example.sera.databinding.ActivityRecordingBinding;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecordingActivity extends BaseActivity {

    private ActivityRecordingBinding binding;
    private MainViewModel viewModel;
    private final int[] STAR_IDS = new int[]{
            R.id.star_13, R.id.star_14, R.id.star_15, R.id.star_16,
            R.id.star_17, R.id.star_18, R.id.star_19, R.id.star_20, R.id.star_21
    };

    // [설정] 에뮬레이터 주소
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    public static class AudioResponse {
        @SerializedName("predicted_gender") public String predictedGender;
        @SerializedName("predicted_emotion") public String predictedEmotion;
        @SerializedName("probabilities") public Map<String, Float> probabilities;
    }

    public interface SimpleApiService {
        @Multipart
        @POST("predict_emotion/")
        Call<AudioResponse> predictEmotion(@Part MultipartBody.Part file);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        applyStarAnimation(STAR_IDS);
        setupClickListeners();
        setupObservers();
    }

    private void setupClickListeners() {
        binding.recordButton.setOnClickListener(v -> viewModel.onRecordToggle());
        binding.stopRecordButton.setOnClickListener(v -> viewModel.onRecordToggle());
        binding.menuButton.setOnClickListener(v -> binding.menuGroup.setVisibility(View.VISIBLE));
        binding.dimOverlay.setOnClickListener(v -> binding.menuGroup.setVisibility(View.GONE));

        binding.menuProfile.setOnClickListener(v -> { startActivity(new Intent(this, ProfileActivity.class)); binding.menuGroup.setVisibility(View.GONE); });
        binding.menuUpload.setOnClickListener(v -> { startActivity(new Intent(this, FileuploadActivity.class)); binding.menuGroup.setVisibility(View.GONE); });
        binding.menuHistory.setOnClickListener(v -> { startActivity(new Intent(this, HistoryActivity.class)); binding.menuGroup.setVisibility(View.GONE); });
    }

    private void setupObservers() {
        viewModel.isRecording().observe(this, isRecording -> updateUiState());
        viewModel.isAnalyzing().observe(this, isAnalyzing -> updateUiState());
        viewModel.formattedTime.observe(this, time -> binding.timeLabel.setText(time));

        viewModel.navigateToResult().observe(this, item -> {
            if (item != null) {
                String filePath = viewModel.getLastRecordedFilePath();
                uploadFileWithFallback(filePath);
                viewModel.onResultNavigationDone();
            }
        });
    }

    private void uploadFileWithFallback(String filePath) {
        File file = new File(filePath);

        if (!file.exists() || file.length() < 10) {
            Log.e("Upload", "파일 없음. 데모 모드");
            runDemoMode();
            return;
        }

        Toast.makeText(this, "AI 분석 중...", Toast.LENGTH_SHORT).show();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SimpleApiService service = retrofit.create(SimpleApiService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        service.predictEmotion(body).enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Upload", "서버 응답 성공!");
                    AudioResponse result = response.body();

                    // [핵심] 영어 결과를 한국어로 번역해서 전달
                    goToResult(result.predictedEmotion, result.predictedGender, result.probabilities);
                } else {
                    Log.e("Upload", "서버 에러. 데모 모드");
                    runDemoMode();
                }
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
                Log.e("Upload", "연결 실패. 데모 모드");
                runDemoMode();
            }
        });
    }

    private void runDemoMode() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            HashMap<String, Float> demoProb = new HashMap<>();
            demoProb.put("happy", 0.85f); // 영문 키로 시뮬레이션
            demoProb.put("neutral", 0.10f);
            demoProb.put("sad", 0.05f);

            goToResult("happy", "female", demoProb);
            Toast.makeText(this, "데모 모드 결과입니다.", Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    // ==========================================================================
    // [수정됨] 영어 -> 한국어 번역 기능이 추가된 결과 처리 함수
    // ==========================================================================
    private void goToResult(String englishEmotion, String gender, Map<String, Float> probabilities) {

        // 1. 날짜 포맷
        long now = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("a h:mm", Locale.KOREA);
        String dateStr = dateFormat.format(new Date(now));
        String timeStr = timeFormat.format(new Date(now));

        // 2. [번역] 영문 감정("happy") -> 한글 감정("기쁨")
        String koreanEmotion = translateEmotion(englishEmotion);
        String emotionTag = getEmojiForEmotion(koreanEmotion) + " " + koreanEmotion;

        // 3. [번역] 확률 맵 키값도 모두 한글로 변환 + 퍼센트 정수화
        Map<String, Integer> intProbabilities = new HashMap<>();
        if (probabilities != null) {
            for (Map.Entry<String, Float> entry : probabilities.entrySet()) {
                String korKey = translateEmotion(entry.getKey()); // 키 번역 (happy -> 기쁨)
                int percent = (int)(entry.getValue() * 100);      // 0.85 -> 85

                // 중복된 키가 있으면 더해줌 (예: calm + neutral -> 중립)
                intProbabilities.put(korKey, intProbabilities.getOrDefault(korKey, 0) + percent);
            }
        }

        // 4. 보따리 싸서 보내기
        AnalysisItem item = new AnalysisItem(dateStr, timeStr, emotionTag, intProbabilities);
        Intent intent = new Intent(RecordingActivity.this, ResultActivity.class);
        intent.putExtra("ANALYSIS_RESULT_ITEM", item);

        startActivity(intent);
        finish();
    }

    // [새 메서드] 영한 번역기
    private String translateEmotion(String english) {
        if (english == null) return "중립";
        String lower = english.toLowerCase().trim();

        if (lower.contains("happy") || lower.contains("joy")) return "기쁨";
        if (lower.contains("sad")) return "슬픔";
        if (lower.contains("angry") || lower.contains("anger")) return "분노";
        if (lower.contains("fear") || lower.contains("anx")) return "불안";
        if (lower.contains("neu") || lower.contains("calm")) return "중립";
        if (lower.contains("surp")) return "기쁨"; // 놀람은 기쁨으로 처리
        if (lower.contains("disg")) return "분노"; // 혐오는 분노로 처리

        // 이미 한글인 경우 그대로 반환
        if (lower.equals("기쁨") || lower.equals("슬픔") || lower.equals("분노") || lower.equals("불안") || lower.equals("중립")) return english;

        return "중립"; // 모르는 단어는 중립 처리
    }

    private String getEmojiForEmotion(String emotion) {
        switch (emotion) {
            case "기쁨": return "😊";
            case "슬픔": return "😢";
            case "분노": return "😡";
            case "불안": return "😨";
            default: return "😐";
        }
    }

    private void updateUiState() {
        boolean isRecording = Boolean.TRUE.equals(viewModel.isRecording().getValue());
        boolean isAnalyzing = Boolean.TRUE.equals(viewModel.isAnalyzing().getValue());

        if (isRecording) {
            binding.idleGroup.setVisibility(View.GONE);
            binding.analyzingGroup.setVisibility(View.GONE);
            binding.recordingGroup.setVisibility(View.VISIBLE);
            binding.menuButton.setVisibility(View.GONE);
        } else if (isAnalyzing) {
            binding.idleGroup.setVisibility(View.GONE);
            binding.recordingGroup.setVisibility(View.GONE);
            binding.analyzingGroup.setVisibility(View.VISIBLE);
            binding.menuButton.setVisibility(View.VISIBLE);
        } else {
            binding.analyzingGroup.setVisibility(View.GONE);
            binding.recordingGroup.setVisibility(View.GONE);
            binding.idleGroup.setVisibility(View.VISIBLE);
            binding.menuButton.setVisibility(View.VISIBLE);
        }
    }
}