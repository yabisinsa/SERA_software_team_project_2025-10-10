package com.example.sera; // 사용자님의 패키지 이름

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import java.util.Random;

// 모든 Activity가 상속받을 '부모' Activity
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 지정된 View ID 배열에 star_float_twinkle 애니메이션을 랜덤 딜레이와 함께 적용합니다.
     * 이 메서드는 모든 자식 Activity (Main, SignUp, Recording, Result)에서 사용할 수 있습니다.
     */
    protected void applyStarAnimation(int[] starIds) {
        // R.anim.star_float_twinkle 파일이 res/anim 폴더에 있어야 합니다.
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