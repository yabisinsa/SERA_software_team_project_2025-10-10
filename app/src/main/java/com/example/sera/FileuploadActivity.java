package com.example.sera;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

// 통신 및 파일 처리를 위한 Import
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class FileuploadActivity extends BaseActivity {

    private static final int PICK_FILE_REQUEST_CODE = 100;

    private CardView uploadCardView;
    private TextView dragDropHintTextView;
    private TextView supportedFormatsTextView;
    private ImageButton closeButton;

    private Uri selectedFileUri = null;

    private final int[] STAR_IDS = new int[]{
            R.id.star_1, R.id.star_2, R.id.star_3,
            R.id.star_4, R.id.star_5, R.id.star_6
    };

    // [설정] 에뮬레이터 주소 (실제 폰 사용 시 PC IP로 변경)
    private static final String BASE_URL = "http://10.0.2.2:8000/";

    // --- 데이터 모델 ---
    public static class AudioResponse {
        @SerializedName("predicted_gender") public String predictedGender;
        @SerializedName("predicted_emotion") public String predictedEmotion;
        @SerializedName("probabilities") public Map<String, Float> probabilities;
    }

    // --- API 인터페이스 ---
    public interface SimpleApiService {
        @Multipart
        @POST("predict_emotion/")
        Call<AudioResponse> predictEmotion(@Part MultipartBody.Part file);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileupload);

        applyStarAnimation(STAR_IDS);

        uploadCardView = findViewById(R.id.upload_card_view);
        dragDropHintTextView = findViewById(R.id.tv_drag_drop_hint);
        supportedFormatsTextView = findViewById(R.id.tv_supported_formats);
        closeButton = findViewById(R.id.btn_close);

        if (closeButton != null) {
            closeButton.setOnClickListener(v -> finish());
        }

        if (uploadCardView != null) {
            uploadCardView.setOnClickListener(v -> openFileSelector());
        }
    }

    private void openFileSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "업로드할 오디오 파일을 선택하세요."),
                    PICK_FILE_REQUEST_CODE
            );
        } catch (Exception e) {
            Toast.makeText(this, "파일 선택기를 열 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                selectedFileUri = data.getData();

                String[] fileDetails = getFileDetailsFromUri(selectedFileUri);
                String fileName = fileDetails[0];
                String fileExtension = fileDetails[1];

                if (dragDropHintTextView != null) dragDropHintTextView.setText(fileName);
                if (supportedFormatsTextView != null) supportedFormatsTextView.setText("종류: " + fileExtension.toUpperCase() + " | 터치하여 분석 시작");

                Toast.makeText(this, "파일 선택 완료! 다시 눌러 분석을 시작하세요.", Toast.LENGTH_LONG).show();

                uploadCardView.setOnClickListener(v -> processSelectedFile());

            } else {
                Toast.makeText(this, "파일 정보 없음", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processSelectedFile() {
        if (selectedFileUri == null) {
            Toast.makeText(this, "파일을 다시 선택해주세요.", Toast.LENGTH_SHORT).show();
            openFileSelector();
            return;
        }

        File tempFile = createTempFileFromUri(selectedFileUri);

        if (tempFile != null && tempFile.exists()) {
            uploadFileWithFallback(tempFile.getAbsolutePath());
        } else {
            Toast.makeText(this, "파일 변환 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private File createTempFileFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File tempFile = new File(getCacheDir(), "upload_temp_audio.mp4");
            OutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            Log.e("FileUtil", "파일 변환 중 오류: " + e.getMessage());
            return null;
        }
    }

    private void uploadFileWithFallback(String filePath) {
        File file = new File(filePath);

        if (!file.exists() || file.length() < 10) {
            Log.e("Upload", "파일 없음. 데모 모드");
            runDemoMode();
            return;
        }

        Toast.makeText(this, "AI 분석 요청 중...", Toast.LENGTH_SHORT).show();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SimpleApiService service = retrofit.create(SimpleApiService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        service.predictEmotion(body).enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Upload", "서버 분석 성공!");
                    AudioResponse result = response.body();
                    // [핵심] 서버 결과를 번역해서 전달
                    goToResult(result.predictedEmotion, result.predictedGender, result.probabilities);
                } else {
                    Log.e("Upload", "서버 에러. 데모 모드 전환");
                    runDemoMode();
                }
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
                Log.e("Upload", "연결 실패. 데모 모드 전환");
                runDemoMode();
            }
        });
    }

    private void runDemoMode() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            HashMap<String, Float> demoProb = new HashMap<>();
            demoProb.put("happy", 0.85f); // 영문 키로 테스트
            demoProb.put("neutral", 0.10f);
            demoProb.put("sad", 0.05f);

            goToResult("happy", "female", demoProb);
            Toast.makeText(this, "데모 결과입니다.", Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    // ==========================================================================
    // [수정됨] 번역 기능이 추가된 결과 처리 함수
    // ==========================================================================
    private void goToResult(String englishEmotion, String gender, Map<String, Float> probabilities) {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA);
        SimpleDateFormat timeFormat = new SimpleDateFormat("a h:mm", Locale.KOREA);

        String dateStr = dateFormat.format(date);
        String timeStr = timeFormat.format(date);

        // 1. [번역] 영문 감정 -> 한글 감정
        String koreanEmotion = translateEmotion(englishEmotion);
        String emotionTag = getEmojiForEmotion(koreanEmotion) + " " + koreanEmotion;

        // 2. [번역] 확률 맵 키값도 번역 + 퍼센트 정수화
        Map<String, Integer> intProbabilities = new HashMap<>();
        if (probabilities != null) {
            for (Map.Entry<String, Float> entry : probabilities.entrySet()) {
                String korKey = translateEmotion(entry.getKey()); // 키 번역
                int percent = (int)(entry.getValue() * 100);

                // 중복된 키(예: happy, joy -> 기쁨)가 있으면 확률을 합칩니다
                intProbabilities.put(korKey, intProbabilities.getOrDefault(korKey, 0) + percent);
            }
        }

        // 3. AnalysisItem 생성
        AnalysisItem item = new AnalysisItem(dateStr, timeStr, emotionTag, intProbabilities);

        // 4. 전달
        Intent intent = new Intent(FileuploadActivity.this, ResultActivity.class);
        intent.putExtra("ANALYSIS_RESULT_ITEM", item);

        startActivity(intent);
        finish();
    }

    // [새 메서드] 영한 번역기 (RecordingActivity와 동일)
    private String translateEmotion(String english) {
        if (english == null) return "중립";
        String lower = english.toLowerCase().trim();

        if (lower.contains("happy") || lower.contains("joy")) return "기쁨";
        if (lower.contains("sad")) return "슬픔";
        if (lower.contains("angry") || lower.contains("anger")) return "분노";
        if (lower.contains("fear") || lower.contains("anx")) return "불안";
        if (lower.contains("neu") || lower.contains("calm")) return "중립";
        if (lower.contains("surp")) return "기쁨";
        if (lower.contains("disg")) return "분노";

        // 이미 한글인 경우
        if (lower.equals("기쁨") || lower.equals("슬픔") || lower.equals("분노") || lower.equals("불안") || lower.equals("중립")) return english;

        return "중립";
    }

    private String getEmojiForEmotion(String emotion) {
        switch (emotion) {
            case "기쁨": return "😊";
            case "슬픔": return "😢";
            case "분노": return "😡";
            case "불안": return "😨";
            case "중립": return "😐";
            default: return "🤖";
        }
    }

    private String[] getFileDetailsFromUri(Uri uri) {
        String fileName = "알 수 없는 파일";
        String fileExtension = "UNKNOWN";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) fileName = cursor.getString(nameIndex);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) fileExtension = fileName.substring(lastDot + 1);
        return new String[]{fileName, fileExtension};
    }
}