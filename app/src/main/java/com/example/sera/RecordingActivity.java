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

public class RecordingActivity extends BaseActivity {

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

        // 메뉴 프로필 클릭
        binding.menuProfile.setOnClickListener(v -> {
            // 1. ProfileActivity로 가는 Intent 생성
            Intent intent = new Intent(RecordingActivity.this, ProfileActivity.class);
            startActivity(intent);

            binding.menuGroup.setVisibility(View.GONE);
        });
        //메뉴 파일 업로드 클릭
        binding.menuUpload.setOnClickListener(v -> {
            // 2. FileuploadActivity로 가는 Intent 생성
            Intent intent = new Intent(RecordingActivity.this, FileuploadActivity.class);
            startActivity(intent);

            binding.menuGroup.setVisibility(View.GONE);
        });

        binding.menuHistory.setOnClickListener(v -> {
            // 1. HistoryActivity로 가는 Intent 생성
            Intent intent = new Intent(RecordingActivity.this, HistoryActivity.class);
            startActivity(intent);

            binding.menuGroup.setVisibility(View.GONE);
        });
    }

    // --- ViewModel LiveData 구독 ---
    private void setupObservers() {
        // isRecording 또는 isAnalyzing이 변경될 때마다 updateUiState() 함수를 호출
        viewModel.isRecording().observe(this, isRecording -> updateUiState());
        viewModel.isAnalyzing().observe(this, isAnalyzing -> updateUiState());

        // 시간 텍스트는 별도로 업데이트
        viewModel.formattedTime.observe(this, timeString -> {
            binding.timeLabel.setText(timeString);
        });

        // 결과 화면 이동 관찰자 (주석 해제)
        viewModel.navigateToResult().observe(this, shouldNavigate -> {
            if (shouldNavigate) {
                Intent intent = new Intent(RecordingActivity.this, ResultActivity.class);
                startActivity(intent);
                viewModel.onResultNavigationDone();
            }
        });
    }
    private void updateUiState() {
        // ViewModel에서 현재 상태 값을 가져옴
        boolean isRecording = viewModel.isRecording().getValue() != null && viewModel.isRecording().getValue();
        boolean isAnalyzing = viewModel.isAnalyzing().getValue() != null && viewModel.isAnalyzing().getValue();

        if (isRecording) {
            // "녹음 중" 상태
            binding.idleGroup.setVisibility(View.GONE);
            binding.analyzingGroup.setVisibility(View.GONE);
            binding.recordingGroup.setVisibility(View.VISIBLE);
            binding.menuButton.setVisibility(View.GONE);
        } else if (isAnalyzing) {
            // "분석 중" 상태
            binding.idleGroup.setVisibility(View.GONE);
            binding.recordingGroup.setVisibility(View.GONE);
            binding.analyzingGroup.setVisibility(View.VISIBLE);
            binding.menuButton.setVisibility(View.VISIBLE);
        } else {
            // "대기 중" 상태 (녹음 중도 아니고, 분석 중도 아닐 때)
            binding.analyzingGroup.setVisibility(View.GONE);
            binding.recordingGroup.setVisibility(View.GONE);
            binding.idleGroup.setVisibility(View.VISIBLE);
            binding.menuButton.setVisibility(View.VISIBLE);
        }
    }
}