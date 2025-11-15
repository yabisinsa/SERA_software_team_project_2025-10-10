# 1. 기본 이미지 선택 (파이썬 3.11 슬림 버전)
FROM python:3.11-slim

# 2. 시스템 의존성 설치 (매우 중요!)
# librosa를 위한 'ffmpeg'와 'libsndfile1',
# 모델 다운로드를 위한 'curl'을 설치합니다.
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libsndfile1 \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 3. 작업 디렉터리 설정
WORKDIR /app

# 4. 필요한 파일들을 컨테이너 안으로 복사
# (주의: .pkl 파일은 .gitignore에 있으므로 여기서 복사되지 않습니다.)
COPY . .

# 5. 파이썬 라이브러리 설치
RUN pip install --no-cache-dir -r requirements.txt

# --- 6. (!!수정!!) GitHub Release에서 모델 파일 다운로드 ---
# 1단계에서 복사한 URL을 "PASTE_..._URL_HERE" 부분에 정확히 붙여넣으세요.
RUN curl -L -o emotion_model.pkl "https://github.com/yabisinsa/SERA_software_team_project_2025-10-10/releases/download/v1.0.0-models/emotion_model.pkl"
RUN curl -L -o gender_model.pkl "https://github.com/yabisinsa/SERA_software_team_project_2025-10-10/releases/download/v1.0.0-models/gender_model.pkl"
# --- (!!수정 완료!!) ---

# 7. 서버가 8000번 포트를 사용한다고 알림
EXPOSE 8000

# 8. 서버 실행
CMD ["gunicorn", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "gender_predictor:app", "--bind", "0.0.0.0:8000"]