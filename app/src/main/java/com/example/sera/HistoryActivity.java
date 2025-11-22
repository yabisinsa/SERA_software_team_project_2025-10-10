package com.example.sera;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView; // 안내 문구용

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private AnalysisAdapter adapter; // (주의: AnalysisAdapter 클래스가 있어야 합니다)

    // 데이터가 없을 때 보여줄 안내 문구 (레이아웃에 있다면 연결, 없다면 생략 가능)
    // private TextView emptyView;

    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        applyStarAnimation(STAR_IDS);

        ImageButton backButton = findViewById(R.id.historyBackButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.rv_analysis_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 1. 어댑터 초기화 (처음엔 빈 리스트)
        adapter = new AnalysisAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 2. 저장된 데이터 불러오기
        loadHistoryData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면에 돌아올 때마다 갱신
        loadHistoryData();
    }

    // [핵심 수정] 샘플 데이터 대신 진짜 데이터를 불러오는 함수
    private void loadHistoryData() {
        // HistoryManager에서 저장된 리스트 가져오기
        List<AnalysisItem> savedList = HistoryManager.getInstance(this).getHistory();

        if (savedList != null && !savedList.isEmpty()) {
            // 데이터가 있으면 어댑터에 전달
            adapter.setItems(savedList); // (어댑터에 setItems 메서드가 필요하거나, 생성자로 전달)
            adapter.notifyDataSetChanged();
        } else {
            // 데이터가 없으면 (빈 화면 처리 등을 여기서 할 수 있음)
            // Toast.makeText(this, "저장된 기록이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}