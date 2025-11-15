package com.example.sera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.widget.Button;

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

        // 2. 로그아웃 버튼 -> 로그인 화면으로 이동
        CardView cardLogout = findViewById(R.id.card_logout);
        if (cardLogout != null) {
            cardLogout.setOnClickListener(v -> {
                Toast.makeText(ProfileActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                // 로그인 화면(MainActivity)으로 이동
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                // [중요] 이전의 모든 Activity 스택을 지우고 새 화면을 띄움
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }

        // 3. 프로필 편집 버튼 -> ProfileEditActivity로 이동
        Button btnEditProfile = findViewById(R.id.btn_edit_profile);
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
                startActivity(intent);
            });
        }
    }
}