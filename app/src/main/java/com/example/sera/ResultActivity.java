package com.example.sera; // 사용자님의 패키지 이름

// --- [추가] 필요한 클래스들을 Import 합니다 ---
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;
// --- [추가] ---

// ViewBinding Import
import com.example.sera.databinding.ActivityResultBinding;

public class ResultActivity extends BaseActivity { // BaseActivity 상속

    private ActivityResultBinding binding;
    private AnalysisItem currentItem; // 1. [추가] 전달받은 데이터를 저장할 변수

    // 별 ID 배열 (그대로)
    private final int[] STAR_IDS = new int[]{
            R.id.star_13, R.id.star_14, R.id.star_15, R.id.star_16,
            R.id.star_17, R.id.star_18, R.id.star_19, R.id.star_20, R.id.star_21
    };

    // --- [수정] onCreate 메서드 ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding 초기화 (그대로)
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 별 애니메이션 코드 호출 (그대로)
        applyStarAnimation(STAR_IDS);

        // 2. [추가] Intent에서 데이터 꺼내기
        currentItem = (AnalysisItem) getIntent().getSerializableExtra("ANALYSIS_RESULT_ITEM");

        // 3. [추가] 데이터가 있으면 UI에 채우기
        if (currentItem != null) {
            populateUi(currentItem);
        } else {
            // (오류 처리: 데이터를 못 받았을 경우)
            binding.mainEmotionText.setText("오류");
            Toast.makeText(this, "데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }

        // 뒤로가기 버튼 (그대로)
        binding.resultBackButton.setOnClickListener(v -> {
            finish();
        });

        // 4. [수정] 저장/공유 버튼이 새 메서드를 호출하도록 변경
        binding.saveButton.setOnClickListener(v -> saveRecord());
        binding.shareButton.setOnClickListener(v -> shareResult());
    }

    // --- [추가] 0단계, 1번 작업에서 추가한 메서드들 ---

    /**
     * 5. [새 메서드] 전달받은 AnalysisItem으로 UI를 채웁니다.
     */
    private void populateUi(AnalysisItem item) {
        Map<String, Integer> map = item.getEmotionMap();

        // 1. 주요 감정 추출
        String mainEmotion = item.getEmotionTag().replaceAll("[^가-힣]", "").trim();
        int mainPercent = map.getOrDefault(mainEmotion, 0);

        // 2. 감정에 따라 동적으로 색상 ID를 선택
        int mainColorResId;   // e.g., #F9C74F (불투명, 텍스트용)
        int mainBgColorResId; // e.g., #20F9C74F (투명, 카드배경용)

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
            default: // 기본값은 '기쁨'
                mainColorResId = R.color.emotion_joy_main;
                mainBgColorResId = R.color.emotion_joy_bg;
                break;
        }

        // 3. 실제 색상 값 가져오기
        int mainColor = ContextCompat.getColor(this, mainColorResId);
        int mainBgColor = ContextCompat.getColor(this, mainBgColorResId);

        // 4. 주요 감정 UI 업데이트
        binding.mainEmotionText.setText(mainEmotion);
        binding.mainEmotionText.setTextColor(mainColor);

        binding.mainEmotionPercent.setText(mainPercent + "%");
        binding.mainEmotionPercent.setTextColor(mainColor);

        binding.mainEmotionCardBg.setCardBackgroundColor(mainBgColor); // 카드 배경색


        // 6. 감정 분포 (텍스트/프로그레스바 채우기)
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

        // 7. 인사이트 (감정에 맞는 텍스트로 변경)
        binding.insightText.setText(getInsightMessage(mainEmotion));
    }

    /**
     * 6. [새 메서드] Figma 코드의 인사이트 메시지 헬퍼
     */
    private String getInsightMessage(String emotion) {
        switch (emotion) {
            case "기쁨": return "기쁨의 감정이 높게 나타났네요! 오늘 있었던 좋은 일을 간단히 메모해보는 건 어떨까요?";
            case "슬픔": return "슬픔의 감정이 감지되었습니다. 힘든 시간을 보내고 계시는군요. 감정을 인정하고 표현하는 것만으로도 큰 도움이 됩니다.";
            case "분노": return "분노의 감정이 나타났습니다. 깊게 숨을 들이쉬고 천천히 내쉬어보세요.";
            case "불안": return "불안한 감정이 감지되었습니다. 현재 걱정되는 것들을 글로 적어보거나, 가벼운 산책으로 마음을 진정시켜보세요.";
            default: return "비교적 평온한 감정 상태를 보이고 있습니다. 현재의 안정적인 상태를 유지하며, 오늘 하루를 돌아보는 시간을 가져보세요.";
        }
    }

    /**
     * 7. [새 메서드] 2번 작업 (기록 저장하기)
     */
    private void saveRecord() {
        if (currentItem == null) {
            Toast.makeText(this, "저장할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. HistoryManager에 현재 아이템 저장
        HistoryManager.getInstance().addHistory(currentItem);

        // 2. (Q2.1) 저장 완료 메시지 알림
        Toast.makeText(this, "기록이 저장되었습니다.", Toast.LENGTH_SHORT).show();

        // 3. HistoryActivity로 이동
        Intent intent = new Intent(ResultActivity.this, HistoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // 4. 현재 ResultActivity 종료
        finish();
    }

    /**
     * 8. [새 메서드] 3번 작업 (공유하기)
     */
    private void shareResult() {
        if (currentItem == null) {
            Toast.makeText(this, "공유할 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. 공유할 텍스트 생성
        String mainEmotion = binding.mainEmotionText.getText().toString();
        String mainPercent = binding.mainEmotionPercent.getText().toString();

        String shareText = "방금 S.E.R.A.에서 음성 감정을 분석했어요!\n" +
                "저의 주요 감정은 '" + mainEmotion + "' (" + mainPercent + ") 입니다. 🥳";

        // 3. Android 공유 인텐트 생성
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // 4. 공유 창 (Chooser) 띄우기
        startActivity(Intent.createChooser(shareIntent, "결과 공유하기"));
    }
}