package com.example.sera;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.util.Random; // Random 클래스 추가

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Button signUpButton;
    private TextView loginLinkTextView;

    // 별 ID 배열 정의 (activity_signup.xml에 추가된 ID)
    private final int[] STAR_IDS = new int[]{
            R.id.star_7, R.id.star_8, R.id.star_9,
            R.id.star_10, R.id.star_11, R.id.star_12
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // 애니메이션 적용
        applyStarAnimation(STAR_IDS); // ⭐ 별 애니메이션 적용 메서드 호출

        // XML 요소 연결
        nameEditText = findViewById(R.id.editText_name);
        emailEditText = findViewById(R.id.editText_email);
        passwordEditText = findViewById(R.id.editText_password);
        confirmPasswordEditText = findViewById(R.id.editText_confirm_password);
        signUpButton = findViewById(R.id.button_signup);
        loginLinkTextView = findViewById(R.id.textView_login_link);

        // 1. 가입하기 버튼 클릭 이벤트 처리
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "모든 필드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(SignUpActivity.this, "회원가입 완료! 이름: " + name, Toast.LENGTH_LONG).show();
            }
        });

        // 2. 로그인 링크 클릭 이벤트 처리: MainActivity로 돌아가기
        loginLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 지정된 View ID 배열에 star_float_twinkle 애니메이션을 랜덤 딜레이와 함께 적용합니다.
     */
    private void applyStarAnimation(int[] starIds) {
        final Animation starAnimation = AnimationUtils.loadAnimation(this, R.anim.star_float_twinkle);
        final Random random = new Random();

        for (int id : starIds) {
            final View starView = findViewById(id);
            if (starView != null) {
                // 0ms ~ 1500ms 사이의 랜덤 딜레이 생성
                long delayMillis = random.nextInt(1500);

                // 딜레이 후에 애니메이션 시작
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