package com.example.sera;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AnalysisActivity extends AppCompatActivity {

    private TextView analysisStatusTextView;
    private Uri receivedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_analysis.xml 레이아웃 연결 (이 파일이 있어야 합니다!)
        setContentView(R.layout.activity_analysis);

        // UI 요소 초기화 (activity_analysis.xml에 tv_analysis_status ID가 있어야 합니다.)
        analysisStatusTextView = findViewById(R.id.tv_analysis_status);

        // 1. Intent에서 파일 URI 받기
        if (getIntent().hasExtra("FILE_URI")) {
            try {
                // String 형태로 전달된 URI를 Uri 객체로 변환
                String uriString = getIntent().getStringExtra("FILE_URI");
                receivedFileUri = Uri.parse(uriString);

                // 2. 받은 파일 URI를 화면에 표시하여 파일이 넘어왔음을 확인
                if (analysisStatusTextView != null) {
                    analysisStatusTextView.setText("분석 준비 중...\n파일이 성공적으로 수신되었습니다.\nURI: " + receivedFileUri.toString());
                }

                Toast.makeText(this, "파일 수신 완료. 분석을 시작합니다.", Toast.LENGTH_SHORT).show();

                // 3. TODO: 이곳에 실제 파일을 읽고 분석하는 코드를 추가합니다.

            } catch (Exception e) {
                // URI 파싱 또는 수신 과정에서 오류 발생 시
                Toast.makeText(this, "파일 정보 수신 오류 발생.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
                finish(); // 오류 발생 시 Activity 종료
            }
        } else {
            // 파일 URI 없이 Activity가 시작된 경우
            Toast.makeText(this, "분석할 파일 정보가 없습니다.", Toast.LENGTH_LONG).show();
            finish();
        }
    }
}