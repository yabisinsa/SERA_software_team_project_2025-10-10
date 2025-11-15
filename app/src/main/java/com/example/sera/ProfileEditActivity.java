package com.example.sera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;

// ViewBinding Import
import com.example.sera.databinding.ActivityProfileEditBinding;

public class ProfileEditActivity extends BaseActivity { // 1. BaseActivity 상속

    private ActivityProfileEditBinding binding;
    private Uri newProfileImageUri = null; // 1. [추가] 선택된 이미지 URI를 저장할 변수
    private ActivityResultLauncher<Intent> galleryLauncher; // 2. [추가] 갤러리 실행기

    // 2. 별 ID 배열 (background_layout.xml과 일치)
    private final int[] STAR_IDS = new int[]{
            R.id.star_13, R.id.star_14, R.id.star_15, R.id.star_16,
            R.id.star_17, R.id.star_18, R.id.star_19, R.id.star_20, R.id.star_21
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 3. 별 애니메이션 적용
        applyStarAnimation(STAR_IDS);

        // 3. [추가] 갤러리 실행기 초기화 (onCreate에 추가)
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // 4. [추가] 갤러리에서 이미지를 선택했을 때의 콜백
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        newProfileImageUri = result.getData().getData();
                        // 5. [추가] CircleImageView에 선택한 이미지 표시
                        binding.ivProfileImage.setImageURI(newProfileImageUri);
                    }
                });

        // 4. 리스너 연결
        setupClickListeners();

        // 5. TODO: ProfileActivity에서 이름, 이메일, 사진 URI를 Intent로 받아와서
        //    binding.editTextName.setText(...) 등으로 채워줘야 합니다.
    }

    private void setupClickListeners() {
        // "취소" 버튼
        binding.btnCancel.setOnClickListener(v -> {
            finish(); // 현재 액티비티 종료
        });

        // "저장" 버튼
        binding.btnSave.setOnClickListener(v -> {

            String newName = binding.editTextName.getText().toString();

            if (newProfileImageUri != null) {
                // TODO: newProfileImageUri를 서버에 업로드하거나, 내부 저장소에 복사
                System.out.println("새 이미지 저장: " + newProfileImageUri.toString());
            }
            // TODO: 새 이름 저장
            System.out.println("새 이름 저장: " + newName);

            Toast.makeText(this, "프로필이 저장되었습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 저장 후 현재 액티비티 종료
        });

        // "사진 변경" 버튼
        binding.btnEditImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        // "비밀번호 변경" 버튼
        binding.cardChangePassword.setOnClickListener(v -> {
            // TODO: 비밀번호 변경 Activity로 이동
            Toast.makeText(this, "비밀번호 변경 (구현 필요)", Toast.LENGTH_SHORT).show();
        });

        // "회원 탈퇴" 버튼
        binding.btnDeleteAccount.setOnClickListener(v -> {
            showDeleteDialog(); // 팝업창 띄우기
        });
    }

    /**
     * Figma 디자인과 동일한 '회원 탈퇴' 확인 팝업창을 띄웁니다.
     */
    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("정말 탈퇴하시겠습니까?")
                .setMessage("회원 탈퇴 시 모든 데이터가 삭제되며 복구할 수 없습니다. 정말로 S.E.R.A. 서비스를 탈퇴하시겠습니까?")
                .setPositiveButton("탈퇴하기", (dialog, which) -> {
                    // TODO: 실제 회원 탈퇴 로직 구현
                    Toast.makeText(this, "회원 탈퇴가 처리되었습니다.", Toast.LENGTH_SHORT).show();
                    // TODO: 모든 Activity를 종료하고 로그인 화면(MainActivity)으로 이동
                })
                .setNegativeButton("취소", (dialog, which) -> {
                    dialog.dismiss(); // 팝업창 닫기
                })
                .show();
    }
}