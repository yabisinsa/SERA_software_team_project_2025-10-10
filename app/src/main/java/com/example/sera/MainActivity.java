package com.example.sera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

// 1. ViewBinding Import (build.gradle 설정 후)
import com.example.sera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // 2. ViewBinding 변수 선언
    private ActivityMainBinding binding;

    // 3. ViewModel 변수 선언
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 4. ViewBinding 초기화
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 5. ViewModel 초기화 (Java 방식)
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 6. UI 이벤트(클릭)를 ViewModel로 연결
        setupClickListeners();

        // 7. ViewModel의 LiveData를 구독하여 UI 변경
        setupObservers();
    }

    // setupClickListeners 함수를 찾아서 아래처럼 수정합니다.
    private void setupClickListeners() {
        // 녹음 시작/중지 버튼
        binding.recordButton.setOnClickListener(v -> viewModel.onRecordToggle());
        binding.stopRecordButton.setOnClickListener(v -> viewModel.onRecordToggle());

        // [수정] 메뉴 버튼 클릭 리스너
        binding.menuButton.setOnClickListener(v -> {
            // 메뉴 그룹을 보이게 함
            binding.menuGroup.setVisibility(View.VISIBLE);
        });

        // [추가] 뒷배경(dimOverlay) 클릭 시 메뉴 닫기
        binding.dimOverlay.setOnClickListener(v -> {
            binding.menuGroup.setVisibility(View.GONE);
        });

        // [추가] 메뉴 아이템 클릭 리스너 (기능은 ViewModel에 연결)
        binding.menuProfile.setOnClickListener(v -> {
            viewModel.onProfileClicked();
            binding.menuGroup.setVisibility(View.GONE); // 메뉴 닫기
        });

        binding.menuUpload.setOnClickListener(v -> {
            viewModel.onFileUploadClicked();
            binding.menuGroup.setVisibility(View.GONE); // 메뉴 닫기
        });

        binding.menuHistory.setOnClickListener(v -> {
            viewModel.onHistoryClicked();
            binding.menuGroup.setVisibility(View.GONE); // 메뉴 닫기
        });
    }

    private void setupObservers() {
        // isRecording 상태에 따라 UI 그룹 가시성 변경
        viewModel.isRecording().observe(this, isRecording -> {
            binding.idleGroup.setVisibility(isRecording ? View.GONE : View.VISIBLE);
            binding.recordingGroup.setVisibility(isRecording ? View.VISIBLE : View.GONE);

            // 녹음 중일 땐 메뉴 버튼 숨김
            binding.menuButton.setVisibility(isRecording ? View.GONE : View.VISIBLE);
        });

        // isAnalyzing 상태에 따라 UI 그룹 가시성 변경
        viewModel.isAnalyzing().observe(this, isAnalyzing -> {
            binding.analyzingGroup.setVisibility(isAnalyzing ? View.VISIBLE : View.GONE);
            // 분석 중일 땐 다른 화면 숨김
            if (isAnalyzing) {
                binding.idleGroup.setVisibility(View.GONE);
                binding.recordingGroup.setVisibility(View.GONE);
            }
        });

        // 시간 텍스트 업데이트 (React의 formatTime)
        viewModel.formattedTime.observe(this, timeString -> {
            binding.timeLabel.setText(timeString);
        });
    }
}