package com.example.sera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;

public class ProfileActivity extends BaseActivity {

    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_profile.xml 레이아웃 파일을 화면에 표시합니다.
        setContentView(R.layout.activity_profile);

        applyStarAnimation(STAR_IDS);

        // 1. 뒤로가기 버튼 연결 및 이벤트 설정
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // 현재 액티비티를 종료하고 이전 화면으로 돌아갑니다.
                }
            });
        }

        // 2. 로그아웃 카드뷰 연결 및 이벤트 설정
        CardView cardLogout = findViewById(R.id.card_logout);
        if (cardLogout != null) {
            cardLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 실제 로그아웃 로직 대신 토스트 메시지를 표시합니다.
                    Toast.makeText(ProfileActivity.this, "로그아웃을 시도합니다.", Toast.LENGTH_SHORT).show();

                    // TODO: 실제 앱에서는 여기에 로그아웃 처리 및 로그인 화면으로 이동하는 Intent 로직을 추가해야 합니다.
                }
            });
        }

        // 3. (선택적) 프로필 편집 버튼 연결
        // Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        // 여기에 편집 기능 시작 로직을 추가할 수 있습니다.
    }
}