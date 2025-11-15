# feature_extractor.py
import librosa
import numpy as np

def extract_features(y, sr):
    """
    오디오 데이터(y)와 샘플링 레이트(sr)로부터 특징 벡터를 추출합니다.
    (API 함수가 아닌 순수 특징 추출 함수로 사용하기 위해 feature_vector도 반환)
    """
    
    features = {}
    feature_vector = [] # 모델 예측을 위한 순서 있는 특징 리스트
    
    # 1. F0 (Pitch)
    f0, _, _ = librosa.pyin(y, fmin=librosa.note_to_hz('C2'), fmax=librosa.note_to_hz('C7'), sr=sr)
    valid_f0 = f0[~np.isnan(f0)]
    f0_mean = float(np.mean(valid_f0)) if len(valid_f0) > 0 else 0.0
    f0_std = float(np.std(valid_f0)) if len(valid_f0) > 0 else 0.0
    features['f0_mean'] = f0_mean
    features['f0_std'] = f0_std
    feature_vector.extend([f0_mean, f0_std])

    # 2. RMS
    rms = librosa.feature.rms(y=y)
    rms_mean = float(np.mean(rms))
    rms_std = float(np.std(rms))
    features['rms_mean'] = rms_mean
    features['rms_std'] = rms_std
    feature_vector.extend([rms_mean, rms_std])

    # 3. MFCC (20개)
    mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=20)
    mfccs_mean = np.mean(mfccs, axis=1)
    mfccs_std = np.std(mfccs, axis=1)
    
    for i in range(20):
        mean_val = float(mfccs_mean[i])
        std_val = float(mfccs_std[i])
        features[f'mfcc{i+1}_mean'] = mean_val
        features[f'mfcc{i+1}_std'] = std_val
        feature_vector.extend([mean_val, std_val])

    # 4. Spectral Centroid
    spec_cent = librosa.feature.spectral_centroid(y=y, sr=sr)
    spec_cent_mean = float(np.mean(spec_cent))
    spec_cent_std = float(np.std(spec_cent))
    features['spec_cent_mean'] = spec_cent_mean
    features['spec_cent_std'] = spec_cent_std
    feature_vector.extend([spec_cent_mean, spec_cent_std])

    # 5. Zero-Crossing Rate
    zcr = librosa.feature.zero_crossing_rate(y)
    zcr_mean = float(np.mean(zcr))
    zcr_std = float(np.std(zcr))
    features['zcr_mean'] = zcr_mean
    features['zcr_std'] = zcr_std
    feature_vector.extend([zcr_mean, zcr_std])
    
    return features, feature_vector