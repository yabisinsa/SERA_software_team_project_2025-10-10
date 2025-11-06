package com.example.sera;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;

// ViewBinding Import
import com.example.sera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding 초기화
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewModel 초기화
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

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
    }
}