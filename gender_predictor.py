import uvicorn
from fastapi import FastAPI, File, UploadFile, HTTPException
import numpy as np
import io
import librosa
import pickle
import os

# 1. 특징 추출 모듈에서 함수 임포트
from feature_extractor import extract_features 

# --- 모델 파일 경로 ---
GENDER_MODEL_PATH = 'gender_model.pkl'
EMOTION_MODEL_PATH = 'emotion_model.pkl'

# --- 2. 모델 및 스케일러 로드 ---
def load_model(path):
    """모델 파일이 있는지 확인하고 로드합니다."""
    if os.path.exists(path):
        try:
            with open(path, 'rb') as f:
                model_data = pickle.load(f)
            print(f"성공: 모델 로드 ({path}) 완료.")
            return model_data
        except Exception as e:
            print(f"⚠️ 경고: 모델 로드 중 오류 발생 ({path}): {e}")
            return None
    else:
        print(f"⚠️ 경고: 모델 파일 '{path}'을(를) 찾을 수 없습니다. 해당 기능이 비활성화됩니다.")
        return None

# 전역 변수로 모델 로드
GENDER_MODEL_DATA = load_model(GENDER_MODEL_PATH)
EMOTION_MODEL_DATA = load_model(EMOTION_MODEL_PATH)

app = FastAPI()

@app.get("/")
def read_root():
    return {"message": "음성 특징 추출 및 예측 API. /docs 를 방문하세요."}


@app.post("/predict_gender/")
async def predict_gender_from_audio(file: UploadFile = File(...)):
    """
    오디오 파일을 업로드받아 특징을 추출하고 성별을 예측합니다.
    """
    if not file.content_type.startswith("audio/"):
        raise HTTPException(status_code=400, detail="오디오 파일이 아닙니다.")
    
    if not GENDER_MODEL_DATA:
         raise HTTPException(status_code=503, detail=f"성별 예측 모델({GENDER_MODEL_PATH})이 로드되지 않았습니다.")

    try:
        contents = await file.read()
        y, sr = librosa.load(io.BytesIO(contents), sr=None)
        
        features_dict, feature_vector = extract_features(y, sr)
        
        # --- 성별 예측 로직 ---
        X = np.array(feature_vector).reshape(1, -1)
        
        # 성별 모델은 Pipeline만 저장되어 있다고 가정
        pipeline = GENDER_MODEL_DATA 
        prediction_encoded = pipeline.predict(X)[0]
        
        # (참고) 성별 모델도 LabelEncoder를 저장했다면 로직 수정 필요
        # 현재는 'male', 'female'을 직접 반환한다고 가정
        features_dict['predicted_gender'] = str(prediction_encoded) 

        return features_dict

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"처리 중 오류 발생: {str(e)}")


@app.post("/predict_emotion/")
async def predict_emotion_from_audio(file: UploadFile = File(...)):
    """
    오디오 파일을 업로드받아 특징을 추출하고 감정을 예측합니다.
    (!!수정!!: 예측 확률(probabilities)을 포함하도록 변경)
    """
    if not file.content_type.startswith("audio/"):
        raise HTTPException(status_code=400, detail="오디오 파일이 아닙니다.")

    if not EMOTION_MODEL_DATA:
         raise HTTPException(status_code=503, detail=f"감정 예측 모델({EMOTION_MODEL_PATH})이 로드되지 않았습니다.")
         
    try:
        contents = await file.read()
        y, sr = librosa.load(io.BytesIO(contents), sr=None)
        
        features_dict, feature_vector = extract_features(y, sr)
        
        # --- 감정 예측 로직 (LabelEncoder 사용) ---
        X = np.array(feature_vector).reshape(1, -1)
        
        # pkl 파일에서 Pipeline과 LabelEncoder 분리
        pipeline = EMOTION_MODEL_DATA['pipeline']
        label_encoder = EMOTION_MODEL_DATA['label_encoder']
        
        # 모델은 숫자를 예측 (e.g., [1])
        prediction_encoded = pipeline.predict(X)
        
        # --- (!!추가!!) 확률 계산 ---
        # (e.g., [[0.1, 0.8, 0.05, ...]])
        prediction_proba = pipeline.predict_proba(X)[0] 
        
        # --- (!!추가!!) 확률을 텍스트 라벨과 매핑 ---
        # (e.g., {'happy': 0.8, 'sad': 0.1, ...})
        probabilities = {label: prob for label, prob in zip(label_encoder.classes_, prediction_proba)}
        # --- (!!추가 완료!!) ---

        # LabelEncoder를 사용해 숫자를 다시 텍스트로 변환 (e.g., 'happy')
        prediction_label = label_encoder.inverse_transform(prediction_encoded)[0]
        
        features_dict['predicted_emotion'] = str(prediction_label)
        features_dict['predicted_emotion_encoded'] = int(prediction_encoded[0])
        
        # --- (!!추가!!) 응답에 확률 추가 ---
        features_dict['probabilities'] = probabilities 
        # --- (!!추가 완료!!) ---

        return features_dict

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"처리 중 오류 발생: {str(e)}")