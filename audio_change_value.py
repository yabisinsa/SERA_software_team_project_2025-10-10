import uvicorn
from fastapi import FastAPI, File, UploadFile, HTTPException
import librosa
import numpy as np
import io

app = FastAPI()

def extract_features(y, sr):
    """오디오 데이터(y)와 샘플링 레이트(sr)로부터 특징 벡터를 추출"""
    
    features = {}
    
    # 1. F0 (Pitch) - pyin 사용
    f0, voiced_flag, voiced_probs = librosa.pyin(
        y, 
        fmin=librosa.note_to_hz('C2'), 
        fmax=librosa.note_to_hz('C7'),
        sr=sr
    )
    # NaN (음성이 아닌 부분)을 제외하고 계산
    valid_f0 = f0[~np.isnan(f0)]
    features['f0_mean'] = float(np.mean(valid_f0)) if len(valid_f0) > 0 else 0.0
    features['f0_std'] = float(np.std(valid_f0)) if len(valid_f0) > 0 else 0.0

    # 2. RMS (Energy/Loudness)
    rms = librosa.feature.rms(y=y)
    features['rms_mean'] = float(np.mean(rms))
    features['rms_std'] = float(np.std(rms))

    # 3. MFCC (Mel-Frequency Cepstral Coefficients) - 20개
    mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=20)
    
    # 각 MFCC 계수의 평균과 표준편차를 저장
    mfccs_mean = np.mean(mfccs, axis=1)
    mfccs_std = np.std(mfccs, axis=1)
    
    for i in range(20):
        features[f'mfcc{i+1}_mean'] = float(mfccs_mean[i])
        features[f'mfcc{i+1}_std'] = float(mfccs_std[i])

    # 4. 스펙트럼 중심 (Spectral Centroid)
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
    features['spec_cent_mean'] = float(np.mean(spec_cent))
    features['spec_cent_std'] = float(np.std(spec_cent))

    # 5. 제로 크로싱 비율 (Zero-Crossing Rate)
    zcr = librosa.feature.zero_crossing_rate(y)
    features['zcr_mean'] = float(np.mean(zcr))
    features['zcr_std'] = float(np.std(zcr))
    
    # 모든 특징을 리스트가 아닌 단일 딕셔너리로 반환
    return features


@app.post("/extract_features/")
async def extract_features_from_audio(file: UploadFile = File(...)):
    """
    오디오 파일을 업로드받아 감정 인식을 위한 주요 특징 벡터를 추출합니다.
    """
    
    if not file.content_type.startswith("audio/"):
        raise HTTPException(status_code=400, detail="오디오 파일이 아닙니다.")

    try:
        # 오디오 파일 로드
        contents = await file.read()
        audio_stream = io.BytesIO(contents)
        y, sr = librosa.load(audio_stream, sr=None)
        
        # 특징 추출 함수 호출
        features = extract_features(y, sr)
        
        # 추출된 특징(dict)을 JSON으로 반환
        return features

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"특징 추출 중 오류 발생: {str(e)}")

@app.get("/")
def read_root():
    return {"message": "음성 특징 추출 API. /extract_features/ 엔드포인트로 오디오 파일을 POST하세요."}

# 서버 실행
if __name__ == "__main__":
    # 터미널에서 'python -m uvicorn main:app --reload' 명령어로 실행하세요.
    uvicorn.run(app, host="0.0.0.0", port=8000)