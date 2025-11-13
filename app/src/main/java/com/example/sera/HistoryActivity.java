package com.example.sera;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AnalysisAdapter adapter;
    private List<AnalysisItem> analysisItemList;
    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. activity_history.xml 레이아웃을 화면에 설정
        setContentView(R.layout.activity_history);
        //배경 애니메이션
        applyStarAnimation(STAR_IDS);

        // 2. 툴바 뒤로가기 버튼 설정 (선택 사항)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            // 뒤로가기 기능 구현
            finish();
        });

        // 3. RecyclerView 찾기
        recyclerView = findViewById(R.id.rv_analysis_history);

        // 4. (중요) 샘플 데이터 만들기 (스크린샷에 있던 데이터!)
        analysisItemList = new ArrayList<>();
       // 1.
        Map<String, Integer> map1 = new LinkedHashMap<>();
        map1.put("기쁨", 75);
        map1.put("중립", 10);
        map1.put("슬픔", 8);
        map1.put("불안", 5);
        map1.put("분노", 2);
        analysisItemList.add(new AnalysisItem(
                "2025년 10월 19일", "오후 7:30", "😊 기쁨", map1
        ));

        // 2.
        Map<String, Integer> map2 = new LinkedHashMap<>();
        map2.put("슬픔", 62);
        map2.put("불안", 18);
        map2.put("중립", 15);
        map2.put("기쁨", 3);
        map2.put("분노", 2);
        analysisItemList.add(new AnalysisItem(
                "2025년 10월 18일", "오후 3:15", "😢 슬픔", map2
        ));

        // 3.
        Map<String, Integer> map3 = new LinkedHashMap<>();
        map3.put("불안", 55);
        map3.put("중립", 25);
        map3.put("슬픔", 10);
        map3.put("기쁨", 5);
        map3.put("분노", 5);
        analysisItemList.add(new AnalysisItem(
                "2025년 10월 17일", "오전 10:45", "😰 불안", map3
        ));

        // 4.
        Map<String, Integer> map4 = new LinkedHashMap<>();
        map4.put("기쁨", 80);
        map4.put("중립", 12);
        map4.put("불안", 5);
        map4.put("슬픔", 2);
        map4.put("분노", 1);
        analysisItemList.add(new AnalysisItem(
                "2025년 10월 16일", "오후 9:20", "😊 기쁨", map4
        ));

        // 5. 스크롤 테스트를 위한 추가 항목
        Map<String, Integer> map5 = new LinkedHashMap<>();
        map5.put("분노", 70);
        map5.put("슬픔", 15);
        map5.put("불안", 5);
        map5.put("중립", 5);
        map5.put("기쁨", 5);
        analysisItemList.add(new AnalysisItem(
                "2025년 10월 15일", "오후 6:00", "😡 분노", map5
        ));

        // 5. 어댑터 생성 (데이터 리스트를 넣어줌)
        adapter = new AnalysisAdapter(analysisItemList);

        // 6. RecyclerView에 LayoutManager와 Adapter 설정
        // 리스트를 세로로 스크롤되게 함
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리스트에 어댑터를 연결! (이걸 해야 화면에 보임)
        recyclerView.setAdapter(adapter);
    }
}