# 1. 기본 이미지 선택 (파이썬 3.11 슬림 버전)
FROM python:3.11-slim

# 2. 시스템 의존성 설치 (매우 중요!)
# librosa가 오디오 파일을 제대로 읽으려면 'ffmpeg'가 필요합니다.
# soundfile은 'libsndfile1'이 필요합니다.
RUN apt-get update && apt-get install -y \
    ffmpeg \
    libsndfile1 \
    && rm -rf /var/lib/apt/lists/*

# 3. 작업 디렉터리 설정
WORKDIR /app

# 4. 필요한 파일들을 컨테이너 안으로 복사
# (모델, 코드, 라이브러리 목록 등 모든 것을 복사)
COPY . .

# 5. 파이썬 라이브러리 설치
RUN pip install --no-cache-dir -r requirements.txt

# 6. 서버가 8000번 포트를 사용한다고 알림
EXPOSE 8000

# 7. 서버 실행
# (gunicorn을 사용해 0.0.0.0:8000 포트에서 gender_predictor.py의 'app'을 실행)
CMD ["gunicorn", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "gender_predictor:app", "--bind", "0.0.0.0:8000"]