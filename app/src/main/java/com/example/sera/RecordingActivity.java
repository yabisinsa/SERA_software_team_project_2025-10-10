package com.example.sera;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation; // 2. 별 애니메이션을 위해 Import
import android.view.animation.AnimationUtils; // 2. 별 애니메이션을 위해 Import
import java.util.Random; // 2. 별 애니메이션을 위해 Import
import android.content.Intent; // 1. 화면 전환을 위해 Import

// ViewBinding Import
import com.example.sera.databinding.ActivityRecordingBinding;

public class RecordingActivity extends AppCompatActivity {

    private ActivityRecordingBinding binding;
    private MainViewModel viewModel;
    private final int[] STAR_IDS = new int[]{
            R.id.star_13, R.id.star_14, R.id.star_15, R.id.star_16,
            R.id.star_17, R.id.star_18, R.id.star_19, R.id.star_20, R.id.star_21
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding 초기화
        binding = ActivityRecordingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 7. 별 애니메이션 코드 호출
        applyStarAnimation(STAR_IDS);

        setupClickListeners();
        setupObservers();
    }

    // --- 클릭 리스너 설정 ---
    private void setupClickListeners() {
        // 녹음 시작/중지 버튼
        binding.recordButton.setOnClickListener(v -> viewModel.onRecordToggle());
        binding.stopRecordButton.setOnClickListener(v -> viewModel.onRecordToggle());

        // 메뉴 버튼 클릭
        binding.menuButton.setOnClickListener(v -> {
            binding.menuGroup.setVisibility(View.VISIBLE);
        });

        // 뒷배경(dimOverlay) 클릭 시 메뉴 닫기
        binding.dimOverlay.setOnClickListener(v -> {
            binding.menuGroup.setVisibility(View.GONE);
        });

        // 메뉴 아이템 클릭
        binding.menuProfile.setOnClickListener(v -> {
            viewModel.onProfileClicked();
            binding.menuGroup.setVisibility(View.GONE);
        });

        binding.menuUpload.setOnClickListener(v -> {
            viewModel.onFileUploadClicked();
            binding.menuGroup.setVisibility(View.GONE);
        });

        binding.menuHistory.setOnClickListener(v -> {
            viewModel.onHistoryClicked();
            binding.menuGroup.setVisibility(View.GONE);
        });
    }

    // --- ViewModel LiveData 구독 ---
    private void setupObservers() {
        // isRecording 상태에 따라 UI 그룹 가시성 변경
        viewModel.isRecording().observe(this, isRecording -> {
            binding.idleGroup.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            binding.recordingGroup.setVisibility(isRecording ? View.VISIBLE : View.GONE);
            binding.menuButton.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        });

        // isAnalyzing 상태에 따라 UI 그룹 가시성 변경
        viewModel.isAnalyzing().observe(this, isAnalyzing -> {
            binding.analyzingGroup.setVisibility(isAnalyzing ? View.VISIBLE : View.GONE);
            if (isAnalyzing) {
                binding.idleGroup.setVisibility(View.GONE);
                binding.recordingGroup.setVisibility(View.GONE);
            }
        });

        // 시간 텍스트 업데이트
        viewModel.formattedTime.observe(this, timeString -> {
            binding.timeLabel.setText(timeString);
        });
        /*
        viewModel.navigateToResult().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                // ResultActivity를 띄우는 인텐트 생성
                Intent intent = new Intent(RecordingActivity.this, ResultActivity.class);
                startActivity(intent);

                // 신호(Event) 리셋
                viewModel.onResultNavigationDone();
            }
        });
        */
    }
    private void applyStarAnimation(int[] starIds) {
        // R.anim.star_float_twinkle이 res/anim 폴더에 있어야 합니다.
        final Animation starAnimation = AnimationUtils.loadAnimation(this, R.anim.star_float_twinkle);
        final Random random = new Random();

        for (int id : starIds) {
            final View starView = findViewById(id);
            if (starView != null) {
                long delayMillis = random.nextInt(1500);
                starView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        starView.startAnimation(starAnimation);
                    }
                }, delayMillis);
            }
        }
    }
}