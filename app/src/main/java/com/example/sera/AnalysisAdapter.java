package com.example.sera;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar; // [필수] 프로그레스 바
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;
import java.util.Collections;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    private List<AnalysisItem> items;

    public AnalysisAdapter(List<AnalysisItem> items) {
        this.items = items;
    }

    public void setItems(List<AnalysisItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_analysis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // [수정] percentText와 progressBar 변수 추가
        TextView dateText, timeText, emotionText, percentText;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            // [수정] XML의 ID와 연결 (findViewById)
            dateText = itemView.findViewById(R.id.tv_date);
            timeText = itemView.findViewById(R.id.tv_time);
            emotionText = itemView.findViewById(R.id.tv_emotion);

            // 새로 추가된 뷰들 연결
            percentText = itemView.findViewById(R.id.tv_percent);
            progressBar = itemView.findViewById(R.id.progressBar_confidence);
        }

        void bind(AnalysisItem item) {
            if (dateText != null) dateText.setText(item.getDate());
            if (timeText != null) timeText.setText(item.getTime());
            if (emotionText != null) emotionText.setText(item.getEmotionTag());

            // [핵심] 여기서 진짜 데이터를 계산해서 뷰에 넣어줍니다!
            int maxPercent = 0;
            Map<String, Integer> map = item.getEmotionMap();

            // 데이터가 있으면 가장 높은 값 찾기
            if (map != null && !map.isEmpty()) {
                try {
                    maxPercent = Collections.max(map.values());
                } catch (Exception e) {
                    maxPercent = 0;
                }
            }

            // [핵심] 퍼센트 텍스트 업데이트 ("85%" -> "진짜값%")
            if (percentText != null) {
                percentText.setText(maxPercent + "%");
            }

            // [핵심] 프로그레스 바 게이지 업데이트
            if (progressBar != null) {
                progressBar.setProgress(maxPercent);
            }
        }
    }
}