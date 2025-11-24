package com.example.sera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class FileuploadActivity extends BaseActivity {

    private static final int PICK_FILE_REQUEST_CODE = 100;

    private CardView uploadCardView;
    private TextView dragDropHintTextView; // 이 텍스트뷰를 파일 이름 표시용으로 사용
    private TextView supportedFormatsTextView; // 이 텍스트뷰를 파일 종류 표시용으로 사용
    private ImageButton closeButton;

    private Uri selectedFileUri = null;

    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileupload);

        applyStarAnimation(STAR_IDS);

        // UI 요소 초기화 (activity_fileupload.xml의 ID 사용)
        uploadCardView = findViewById(R.id.upload_card_view);
        dragDropHintTextView = findViewById(R.id.tv_drag_drop_hint);
        supportedFormatsTextView = findViewById(R.id.tv_supported_formats);
        closeButton = findViewById(R.id.btn_close);

        // 닫기 버튼 기능
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }

        // 초기 상태: 업로드 카드 클릭 시 파일 탐색기 호출
        if (uploadCardView != null) {
            uploadCardView.setOnClickListener(v -> openFileSelector());
        }

        // 초기 화면에 안내 문구 대신 "파일을 선택해주세요" 같은 메시지를 표시하고 싶다면,
        // 이곳에서 dragDropHintTextView.setText("파일을 선택하세요.") 등으로 설정할 수 있습니다.
    }

    private void openFileSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "업로드할 파일을 선택하세요."),
                    PICK_FILE_REQUEST_CODE
            );
        } catch (Exception e) {
            Toast.makeText(this, "파일 선택기를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 파일 선택 결과를 처리하고 UI를 업데이트하는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedFileUri = data.getData();

                // ⭐ 파일 URI에서 이름 및 종류 정보 추출 ⭐
                String[] fileDetails = getFileDetailsFromUri(selectedFileUri);
                String fileName = fileDetails[0];
                String fileExtension = fileDetails[1];

                // 1. dragDropHintTextView에 파일 이름만 표시 (안내 문구 대체)
                if (dragDropHintTextView != null) {
                    dragDropHintTextView.setText(fileName);
                }

                // 2. supportedFormatsTextView에 파일 종류와 상태 표시
                if (supportedFormatsTextView != null) {
                    supportedFormatsTextView.setText("종류: " + fileExtension.toUpperCase() + " | 분석 준비 완료");
                }

                Toast.makeText(this, "파일이 선택되었습니다.", Toast.LENGTH_LONG).show();

                // 3. 카드를 다시 누르면 AnalysisActivity로 이동하도록 리스너 변경
                uploadCardView.setOnClickListener(v -> startAnalysis());

            } else {
                Toast.makeText(this, "선택된 파일 정보가 없습니다.", Toast.LENGTH_LONG).show();
                // 파일 선택이 취소된 경우, 다시 선택할 수 있도록 리스너 유지
                uploadCardView.setOnClickListener(v -> openFileSelector());
            }
        }
    }

    // URI를 이용해 파일 이름과 확장자를 추출하는 유틸리티 함수
    private String[] getFileDetailsFromUri(Uri uri) {
        String fileName = "알 수 없는 파일";
        String fileExtension = "UNKNOWN";

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        // 파일 확장자 추출
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            fileExtension = fileName.substring(lastDot + 1);
        }

        return new String[]{fileName, fileExtension};
    }

    // 분석 Activity로 전환하는 함수
    private void startAnalysis() {
        if (selectedFileUri != null) {
            Intent analysisIntent = new Intent(this, AnalysisActivity.class);
            analysisIntent.putExtra("FILE_URI", selectedFileUri.toString());
            analysisIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(analysisIntent);
            finish();
        } else {
            Toast.makeText(this, "선택된 파일이 없습니다. 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
            openFileSelector();
        }
    }
}