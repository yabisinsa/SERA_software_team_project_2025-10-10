package com.example.sera;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import java.util.Map;

import com.example.sera.databinding.ActivityResultBinding;

public class ResultActivity extends BaseActivity {

    private ActivityResultBinding binding;
    private AnalysisItem currentItem;

    private final int[] STAR_IDS = new int[]{
            R.id.star_13, R.id.star_14, R.id.star_15, R.id.star_16,
            R.id.star_17, R.id.star_18, R.id.star_19, R.id.star_20, R.id.star_21
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applyStarAnimation(STAR_IDS);

        // Intent에서 데이터 꺼내기
        currentItem = (AnalysisItem) getIntent().getSerializableExtra("ANALYSIS_RESULT_ITEM");

        if (currentItem != null) {
            populateUi(currentItem);
        } else {
            binding.mainEmotionText.setText("오류");
            Toast.makeText(this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        binding.resultBackButton.setOnClickListener(v -> finish());

        // [핵심] 저장 버튼 클릭 시 saveRecord 호출
        binding.saveButton.setOnClickListener(v -> saveRecord());
        binding.shareButton.setOnClickListener(v -> shareResult());
    }

    // UI 채우기 메서드
    private void populateUi(AnalysisItem item) {
        Map<String, Integer> map = item.getEmotionMap();
        String mainEmotion = item.getEmotionTag().replaceAll("[^가-힣]", "").trim();
        int mainPercent = map.getOrDefault(mainEmotion, 0);

        int mainColorResId;
        int mainBgColorResId;

        switch (mainEmotion) {
            case "슬픔":
                mainColorResId = R.color.emotion_sadness_main;
                mainBgColorResId = R.color.emotion_sadness_bg;
                break;
            case "분노":
                mainColorResId = R.color.emotion_anger_main;
                mainBgColorResId = R.color.emotion_anger_bg;
                break;
            case "불안":
                mainColorResId = R.color.emotion_anxiety_main;
                mainBgColorResId = R.color.emotion_anxiety_bg;
                break;
            case "중립":
                mainColorResId = R.color.emotion_neutral_main;
                mainBgColorResId = R.color.emotion_neutral_bg;
                break;
            case "기쁨":
            default:
                mainColorResId = R.color.emotion_joy_main;
                mainBgColorResId = R.color.emotion_joy_bg;
                break;
        }

        int mainColor = ContextCompat.getColor(this, mainColorResId);
        int mainBgColor = ContextCompat.getColor(this, mainBgColorResId);

        binding.mainEmotionText.setText(mainEmotion);
        binding.mainEmotionText.setTextColor(mainColor);
        binding.mainEmotionPercent.setText(mainPercent + "%");
        binding.mainEmotionPercent.setTextColor(mainColor);
        binding.mainEmotionCardBg.setCardBackgroundColor(mainBgColor);

        binding.percentJoy.setText(map.getOrDefault("기쁨", 0) + "%");
        binding.progressJoy.setProgress(map.getOrDefault("기쁨", 0));

        binding.percentSadness.setText(map.getOrDefault("슬픔", 0) + "%");
        binding.progressSadness.setProgress(map.getOrDefault("슬픔", 0));

        binding.percentAnger.setText(map.getOrDefault("분노", 0) + "%");
        binding.progressAnger.setProgress(map.getOrDefault("분노", 0));

        binding.percentAnxiety.setText(map.getOrDefault("불안", 0) + "%");
        binding.progressAnxiety.setProgress(map.getOrDefault("불안", 0));

        binding.percentNeutral.setText(map.getOrDefault("중립", 0) + "%");
        binding.progressNeutral.setProgress(map.getOrDefault("중립", 0));

        binding.insightText.setText(getInsightMessage(mainEmotion));
    }

    private String getInsightMessage(String emotion) {
        switch (emotion) {
            case "기쁨": return "기쁨의 감정이 높게 나타났네요! 오늘 있었던 좋은 일을 간단히 메모해보는 건 어떨까요?";
            case "슬픔": return "슬픔의 감정이 감지되었습니다. 힘든 시간을 보내고 계시는군요. 감정을 인정하고 표현하는 것만으로도 큰 도움이 됩니다.";
            case "분노": return "분노의 감정이 나타났습니다. 깊게 숨을 들이쉬고 천천히 내쉬어보세요.";
            case "불안": return "불안한 감정이 감지되었습니다. 현재 걱정되는 것들을 글로 적어보거나, 가벼운 산책으로 마음을 진정시켜보세요.";
            default: return "비교적 평온한 감정 상태를 보이고 있습니다. 현재의 안정적인 상태를 유지하며, 오늘 하루를 돌아보는 시간을 가져보세요.";
        }
    }

    // [핵심 수정] 기록 저장 메서드
    private void saveRecord() {
        if (currentItem == null) {
            Toast.makeText(this, "저장할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // [수정됨] getInstance(this)를 사용하여 저장 관리자를 불러옵니다.
        HistoryManager.getInstance(this).addHistory(currentItem);

        Toast.makeText(this, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show();

        // 히스토리 화면으로 이동 (기존 스택 정리)
        Intent intent = new Intent(ResultActivity.this, HistoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void shareResult() {
        if (currentItem == null) return;
        String mainEmotion = binding.mainEmotionText.getText().toString();
        String shareText = "S.E.R.A. 감정 분석 결과: " + mainEmotion;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "결과 공유하기"));
    }
}