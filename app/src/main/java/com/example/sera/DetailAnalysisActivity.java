package com.example.sera;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.cardview.widget.CardView;
import android.content.res.ColorStateList;
import android.widget.TextView; // TextView import 추가

import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class DetailAnalysisActivity extends BaseActivity {

    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_TIME = "extra_time";
    public static final String EXTRA_EMOTION_TAG = "extra_emotion_tag";
    public static final String EXTRA_EMOTION_MAP = "extra_emotion_map";

    private TextView tvDetailDateTitle;
    private TextView tvDetailTimeSubtitle;
    private TextView tvMainEmotion;
    private TextView tvMainEmotionProbability;
    private LinearLayout llEmotionBarsContainer;
    private CardView cardEmotionChip;

    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_analysis);

        // 기존의 applyStarAnimation(STAR_IDS); 호출은 주석 처리합니다.
        // 애니메이션이 멈춰있는 문제를 해결하기 위해 뷰 로딩 후 post()로 실행합니다.
        // applyStarAnimation(STAR_IDS);

        ImageButton backButton = findViewById(R.id.detailBackButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        tvDetailDateTitle = findViewById(R.id.tv_detail_date_title);
        tvDetailTimeSubtitle = findViewById(R.id.tv_detail_time_subtitle);
        tvMainEmotion = findViewById(R.id.tv_main_emotion);
        tvMainEmotionProbability = findViewById(R.id.tv_main_emotion_probability);
        llEmotionBarsContainer = findViewById(R.id.ll_emotion_bars_container);
        cardEmotionChip = findViewById(R.id.card_emotion_chip);

        // 뷰 초기화가 완료된 후, UI 스레드에서 애니메이션을 시작하여 멈춤 문제를 해결합니다.
        getWindow().getDecorView().post(() -> applyStarAnimation(STAR_IDS));

        Intent intent = getIntent();
        if (intent != null) {
            String date = intent.getStringExtra(EXTRA_DATE);
            String time = intent.getStringExtra(EXTRA_TIME);
            String emotionTag = intent.getStringExtra(EXTRA_EMOTION_TAG);

            Map<String, Integer> emotionMap = (Map<String, Integer>)
                    intent.getSerializableExtra(EXTRA_EMOTION_MAP);

            if (date != null) {
                tvDetailDateTitle.setText(date + "의 기록");
            }
            if (time != null) {
                tvDetailTimeSubtitle.setText(time + "에 분석된 감정 기록입니다");
            }

            // Map을 기반으로 주요 감정 확률과 분포를 설정
            if (emotionMap != null && !emotionMap.isEmpty()) {

                List<Map.Entry<String, Integer>> list = new LinkedList<>(emotionMap.entrySet());
                // 퍼센트 기준으로 내림차순 정렬
                Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

                if (!list.isEmpty()) {
                    String topEmotionName = list.get(0).getKey();
                    int topPercentage = list.get(0).getValue();

                    // 1. 감정 색상 가져오기
                    int emotionColor = getEmotionColor(topEmotionName);

                    // 2. 주요 감정 텍스트 설정
                    tvMainEmotion.setText(topEmotionName);

                    // 3. 주요 감정 카드 배경색 및 텍스트 색상 설정
                    int backgroundColor = Color.argb(
                            (int)(0.12 * 255), // 12% 투명도 (0x20에 가까움)
                            Color.red(emotionColor),
                            Color.green(emotionColor),
                            Color.blue(emotionColor)
                    );
                    cardEmotionChip.setCardBackgroundColor(ColorStateList.valueOf(backgroundColor));
                    tvMainEmotion.setTextColor(emotionColor);

                    // 4. 확률 텍스트 설정 및 색상 변경
                    tvMainEmotionProbability.setText(topPercentage + "%");
                    tvMainEmotionProbability.setTextColor(emotionColor);

                    // 감정 분포 진행 바 동적으로 추가
                    displayEmotionDistribution(emotionMap);
                }
            }
        }
    }

    // 감정 분포를 표시하는 메서드 (변경 없음)
    private void displayEmotionDistribution(Map<String, Integer> emotionMap) {
        llEmotionBarsContainer.removeAllViews();

        List<Map.Entry<String, Integer>> list = new LinkedList<>(emotionMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Map.Entry<String, Integer> entry : list) {
            View itemView = inflater.inflate(R.layout.item_emotion_progress_bar, llEmotionBarsContainer, false);
            TextView tvEmotionName = itemView.findViewById(R.id.tv_emotion_name);
            TextView tvEmotionPercentage = itemView.findViewById(R.id.tv_emotion_percentage);
            ProgressBar pbEmotionProgress = itemView.findViewById(R.id.pb_emotion_progress);

            tvEmotionName.setText(entry.getKey());
            tvEmotionPercentage.setText(entry.getValue() + "%");
            pbEmotionProgress.setProgress(entry.getValue());

            int progressColor = getEmotionColor(entry.getKey());
            pbEmotionProgress.setProgressTintList(ColorStateList.valueOf(progressColor));

            llEmotionBarsContainer.addView(itemView);
        }
    }

    // 감정에 따라 ProgressBar 색상을 다르게 설정하는 헬퍼 메서드 (변경 없음)
    private int getEmotionColor(String emotion) {
        switch (emotion) {
            case "기쁨":
                return Color.parseColor("#F9C74F"); // Yellow
            case "슬픔":
                return Color.parseColor("#4C6EF5"); // Blue
            case "분노":
                return Color.parseColor("#FA5252"); // Red
            case "불안":
                return Color.parseColor("#BE4BDB"); // Purple
            case "중립":
                return Color.parseColor("#ADB5BD"); // Gray
            default:
                return Color.parseColor("#FFFFFF"); // Default White
        }
    }
}