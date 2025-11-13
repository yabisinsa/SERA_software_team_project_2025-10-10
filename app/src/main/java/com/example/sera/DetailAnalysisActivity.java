package com.example.sera;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class DetailAnalysisActivity extends BaseActivity {

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_EMOTION_TAG = "extra_emotion_tag";
    // public static final String EXTRA_EMOTION_STATS = "extra_emotion_stats"; // "기쁨 75% 슬픔 8%"
    public static final String EXTRA_EMOTION_MAP = "extra_emotion_map"; // Map을 위한 새로운 키

    private TextView tvDetailDateTitle;
    private TextView tvDetailTimeSubtitle;
    private TextView tvMainEmotion;
    private TextView tvMainEmotionProbability;
    private LinearLayout llEmotionBarsContainer;
    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_analysis);
        applyStarAnimation(STAR_IDS);

        // 툴바 설정
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish()); // 뒤로가기 버튼

        // 뷰 초기화
        tvDetailDateTitle = findViewById(R.id.tv_detail_date_title);
        tvDetailTimeSubtitle = findViewById(R.id.tv_detail_time_subtitle);
        tvMainEmotion = findViewById(R.id.tv_main_emotion);
        tvMainEmotionProbability = findViewById(R.id.tv_main_emotion_probability);
        llEmotionBarsContainer = findViewById(R.id.ll_emotion_bars_container);

        // Intent로부터 데이터 받기
        Intent intent = getIntent();
        if (intent != null) {
            String date = intent.getStringExtra(EXTRA_DATE);
            String time = intent.getStringExtra(EXTRA_TIME);
            String emotionTag = intent.getStringExtra(EXTRA_EMOTION_TAG);

            // ★★★ Map 데이터를 받도록 수정 ★★★
            Map<String, Integer> emotionMap = (Map<String, Integer>)
                    intent.getSerializableExtra(EXTRA_EMOTION_MAP);

            // 데이터 화면에 표시 (Date, Time, Tag는 그대로)
            if (date != null) {
                tvDetailDateTitle.setText(date + "의 기록");
            }
            if (time != null) {
                tvDetailTimeSubtitle.setText(time + "에 분석된 감정 기록입니다");
            }
            if (emotionTag != null) {
                String cleanEmotion = emotionTag.replaceAll("[^가-힣]", "").trim();
                tvMainEmotion.setText(cleanEmotion);
            }

            // Map을 기반으로 주요 감정 확률과 분포를 설정
            if (emotionMap != null && !emotionMap.isEmpty()) {
                // Map에서 가장 첫 번째(최고 확률) 값을 가져옴
                Map.Entry<String, Integer> topEntry = emotionMap.entrySet().iterator().next();
                int topPercentage = topEntry.getValue();

                tvMainEmotionProbability.setText(topPercentage + "%의 확률로 감지되었습니다");

                // 감정 분포 진행 바 동적으로 추가
                displayEmotionDistribution(emotionMap); // Map을 직접 전달
            }
        }
    }

    // 메서드 수정: 문자열 파싱 대신 Map을 받도록 변경
    private void displayEmotionDistribution(Map<String, Integer> emotionMap) {
        llEmotionBarsContainer.removeAllViews();

        // 1. Map의 Entry List를 생성
        List<Map.Entry<String, Integer>> list = new LinkedList<>(emotionMap.entrySet());

        // 2. ★★★ 값(퍼센트)을 기준으로 내림차순 정렬 ★★★
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                // 내림차순 (큰 값이 먼저 오도록): o2.getValue() - o1.getValue()
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // 3. 정렬된 리스트를 기반으로 새 Map (LinkedHashMap) 생성 (선택 사항이지만 안전함)
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        // 4. 정렬된 Map을 사용하여 View를 동적으로 추가
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            View itemView = inflater.inflate(R.layout.item_emotion_progress_bar, llEmotionBarsContainer, false);
            TextView tvEmotionName = itemView.findViewById(R.id.tv_emotion_name);
            TextView tvEmotionPercentage = itemView.findViewById(R.id.tv_emotion_percentage);
            ProgressBar pbEmotionProgress = itemView.findViewById(R.id.pb_emotion_progress);

            tvEmotionName.setText(entry.getKey());
            tvEmotionPercentage.setText(entry.getValue() + "%");
            pbEmotionProgress.setProgress(entry.getValue());

            // ProgressTint 색상 설정 (예시: 감정별로 다른 색상)
            int progressColor = getEmotionColor(entry.getKey());
            pbEmotionProgress.setProgressTintList(
                    android.content.res.ColorStateList.valueOf(progressColor));

            llEmotionBarsContainer.addView(itemView);
        }
    }

    // "기쁨 75% 슬픔 8%"와 같은 문자열을 파싱하여 Map으로 변환
    private Map<String, Integer> parseEmotionStats(String stats) {
        Map<String, Integer> emotionMap = new LinkedHashMap<>(); // 순서 유지를 위해 LinkedHashMap
        String[] pairs = stats.split(" "); // "기쁨 75%", "슬픔 8%"

        for (int i = 0; i < pairs.length; i += 2) {
            if (i + 1 < pairs.length) {
                String emotionName = pairs[i];
                try {
                    int percentage = Integer.parseInt(pairs[i+1].replace("%", ""));
                    emotionMap.put(emotionName, percentage);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return emotionMap;
    }

    // 감정에 따라 ProgressBar 색상을 다르게 설정하는 헬퍼 메서드
    private int getEmotionColor(String emotion) {
        switch (emotion) {
            case "기쁨":
                return Color.parseColor("#FFEB3B"); // 노란색
            case "슬픔":
                return Color.parseColor("#87CEEB"); // 하늘색
            case "분노":
                return Color.parseColor("#FF4500"); // 빨간색
            case "불안":
                return Color.parseColor("#B886FD"); // 보라색
            case "중립":
                return Color.parseColor("#D3D3D3"); // 밝은 회색
            default:
                return Color.parseColor("#FFFFFF"); // 기본 흰색
        }
    }
}