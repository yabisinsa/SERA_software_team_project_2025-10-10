package com.example.sera;

import android.content.Intent; // Intent 사용을 위해 추가
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.List;

// 1. RecyclerView.Adapter를 상속받고, 우리가 만들 ViewHolder를 지정
public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder> {

    // 2. 표시할 데이터 리스트
    private List<AnalysisItem> itemList;

    // 3. 생성자: 밖(Activity)에서 데이터 리스트를 받아옴
    public AnalysisAdapter(List<AnalysisItem> itemList) {
        this.itemList = itemList;
    }

    // 4. list_item_analysis.xml 레이아웃을 '객체'로 만들어주는 부분
    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 'list_item_analysis' 레이아웃을 View 객체로 만듦 (Inflate)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_analysis, parent, false);
        return new AnalysisViewHolder(view); // ViewHolder에 담아서 반환
    }

    // 5. 생성된 ViewHolder에 실제 데이터를 '바인딩' (연결)해주는 부분
    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        // 리스트에서 현재 위치(position)에 맞는 데이터를 가져옴
        AnalysisItem item = itemList.get(position);

        // ViewHolder의 뷰들에 데이터를 설정
        holder.tv_date.setText(item.getDate());
        holder.tv_time.setText(item.getTime());
        holder.chip_emotion.setText(item.getEmotionTag());

        // ★★★ 수정: 리스트에는 상위 2개 감정만 표시하도록 변경 ★★★
        holder.tv_emotion_stats.setText(item.getTopTwoStats());

        // ▼▼▼ 리스트 아이템 클릭 리스너 (데이터 전달 부분도 수정) ▼▼▼
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailAnalysisActivity.class);

            // ★★★ 수정: Map 데이터를 DetailActivity로 직접 전달하기 위해 Serializable 사용 ★★★
            intent.putExtra(DetailAnalysisActivity.EXTRA_DATE, item.getDate());
            intent.putExtra(DetailAnalysisActivity.EXTRA_TIME, item.getTime());
            intent.putExtra(DetailAnalysisActivity.EXTRA_EMOTION_TAG, item.getEmotionTag());
            intent.putExtra(DetailAnalysisActivity.EXTRA_EMOTION_MAP, (java.io.Serializable) item.getEmotionMap()); // Map 전달

            holder.itemView.getContext().startActivity(intent);
        });
        // ▲▲▲ 리스트 아이템 클릭 리스너 추가 끝 ▲▲▲
    }

    // 6. 리스트에 아이템이 총 몇 개인지 알려주는 부분
    @Override
    public int getItemCount() {
        return itemList.size();
    }


    // 7. ViewHolder 클래스 (제일 중요!)
    public static class AnalysisViewHolder extends RecyclerView.ViewHolder {

        TextView tv_date;
        TextView tv_time;
        Chip chip_emotion;
        TextView tv_emotion_stats;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_time = itemView.findViewById(R.id.tv_time);
            chip_emotion = itemView.findViewById(R.id.chip_emotion);
            tv_emotion_stats = itemView.findViewById(R.id.tv_emotion_stats);
        }
    }
}