package com.example.sera; // 사용자님의 패키지 이름

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

// ViewBinding Import
import com.example.sera.databinding.ActivityResultBinding;

public class ResultActivity extends BaseActivity {

    private ActivityResultBinding binding;

    private final int[] STAR_IDS = new int[]{
            R.id.star_13, R.id.star_14, R.id.star_15, R.id.star_16,
            R.id.star_17, R.id.star_18, R.id.star_19, R.id.star_20, R.id.star_21
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding 초기화
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 7. 별 애니메이션 코드 호출
        applyStarAnimation(STAR_IDS);

        // 뒤로가기 버튼
        binding.resultBackButton.setOnClickListener(v -> {
            finish(); // 액티비티 종료 (MainActivity/RecordingActivity로 돌아감)
        });

        // 저장/공유 버튼 (일단 로그만)
        binding.saveButton.setOnClickListener(v -> System.out.println("Save clicked"));
        binding.shareButton.setOnClickListener(v -> System.out.println("Share clicked"));

        // (추후) 여기서 ViewModel이나 Intent로부터
        // 실제 분석 데이터를 받아서 텍스트뷰와 프로그레스바를 채우면 됩니다.
        // 예: binding.mainEmotionText.setText(emotionData.getName());
    }
}