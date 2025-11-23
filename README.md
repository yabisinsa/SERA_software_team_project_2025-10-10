<a href=" target="_blank">
<img src="" alt="배너" width="100%"/>
</a>

<br/>
<br/>

# 0. Getting Started (시작하기)
```bash
$ npm start
```
[서비스 링크]()

<br/>
<br/>

#  1. Project Name(프로젝트 이름)
 프로젝트이름: S.E.R.A
 프로젝트설명: 음성 감정 인식  AI

<br/>
<br/>

# 2. Team Members (팀원 및 팀 소개)
| 김형진 | 박나정 | 박기훈 | 손영주 | 안의진 | 윤시훈 | 임민하 | 지훈 |
|:------:|:------:|:------:|:------:|:------:|:------:|:------:|:------:|

| PL | FE | FE | FE | FE | FE | FE | FE |


<br/>
<br/>

# 3. Key Features (주요 기능)
- **회원가입**:
  - 회원가입 시 DB에 유저정보가 등록됩니다.



<br/>
<br/>

# 4. Tasks & Responsibilities (작업 및 역할 분담)
|  |  |  |  |  |  |  |  |
|-----------------|-----------------|-----------------|-----------------|-----------------|-----------------|-----------------|-----------------|
| 김형진    |  <img src="" alt="김형진" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 박기훈    |  <img src="" alt="박기훈" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 손영주    |  <img src="" alt="손영주" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 안의진    |  <img src="" alt="안의진" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 윤시훈    |  <img src="" alt="윤시훈" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 임민하    |  <img src="" alt="임민하" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 장나정    |  <img src="" alt="장나정" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
| 지훈    |  <img src="" alt="지훈" width="100"> | <ul><li>프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 개발</li></ul>     |
</ul>    |

<br/>
<br/>

# 5. Technology Stack (기술 스택)
## 5.1 Language
|  |  |
|-----------------|-----------------|
|     || 
| Python    |   |
| JAVA    |   | 

<br/>



# 6. Project Structure (프로젝트 구조)
```plaintext
project/
├── public/
│   ├── index.html           # HTML 템플릿 파일
│   └── favicon.ico          # 아이콘 파일
├── src/
│   ├── assets/              # 이미지, 폰트 등 정적 파일
│   ├── components/          # 재사용 가능한 UI 컴포넌트
│   ├── hooks/               # 커스텀 훅 모음
│   ├── pages/               # 각 페이지별 컴포넌트
│   ├── App.js               # 메인 애플리케이션 컴포넌트
│   ├── index.js             # 엔트리 포인트 파일
│   ├── index.css            # 전역 css 파일
│   ├── firebaseConfig.js    # firebase 인스턴스 초기화 파일
│   package-lock.json    # 정확한 종속성 버전이 기록된 파일로, 일관된 빌드를 보장
│   package.json         # 프로젝트 종속성 및 스크립트 정의
├── .gitignore               # Git 무시 파일 목록
└── README.md                # 프로젝트 개요 및 사용법

app/
├──src/
    ├──main/
         ├──java
         │     ├── AnalysisActivity.java       # 파일 수신 확인 및 분석 대기 화면
         │     ├── AnalysisItem.java           # 감정 분석 결과 데이터 모델
         │     ├── FileuploadActivity.java     # 오디오 파일 선택 및 업로드 화면
         │     ├── DetailAnalysisActivity.java # 감정 상세 분포 시각화 결과 화면
         │     ├── AnalysisAdapter.java        # 분석 기록 리스트 관리 및 연결 어댑터
         │     ├── BaseActivity.java           # 공통 배경 애니메이션 정의 부모 클래스
         │     ├── MainActivity.java           # 초기 진입 로그인 및 인증 처리 화면
         │     ├── SignUpActivity.java         # 신규 사용자 정보 입력 및 회원가입 화면
         │     ├── RecordingActivity.java      # 녹음 제어 및 상태 관리 메인 화면
         │     ├── MainViewModel.java          # 녹음 상태/타이머/데이터 로직 관리 뷰모델
         │     ├── ResultActivity.java         # 감정 분석 결과 시각화 및 저장/공유 화면
         │     ├── HistoryActivity.java        # 분석 기록 리스트 조회 화면
         │     ├── HistoryManager.java         # 분석 기록 데이터 관리 싱글톤 저장소
         │     ├── ProfileActivity.java        # 사용자 프로필 조회 및 로그아웃 화면
         │
         ├── res/
              ├── layout/
              │       ├── activity_main.xml             # 로그인 화면 레이아웃
              │       ├── activity_profile.xml          # 사용자 프로필 및 설정 화면 레이아웃
              │       ├── activity_fileupload.xml       # 파일 업로드 UI 레이아웃
              │       ├── activity_analysis.xml         # 분석 대기 및 진행 상태 화면 레이아웃
              │       ├── activity_detail_analysis.xml  # 감정 분석 상세 결과 시각화 레이아웃
              │       ├── activity_history.xml          # 분석 기록 리스트 화면 레이아웃
              │       ├── activity_recording.xml        # 녹음 및 분석 상태 관리 메인 레이아웃
              │       ├── activity_signup.xml           # 회원가입 입력 폼 레이아웃
              │       ├── activity_result.xml           # 분석 결과 및 인사이트 시각화 레이아웃
              │       ├── background_layout.xml         # 공통 배경 및 별 애니메이션 포함 레이아웃
              │       ├── list_item_analysis.xml        # 기록 리스트용 아이템 디자인
              │       ├── item_emotion_progress_bar.xml # 감정 분포 그래프용 아이템 디자인
              │
              ├── drawable  # UI 요소의 애니메이션 동작(이동, 회전, 투명도 등) 설정을 정의
              ├── anime     # 이미지, 아이콘, 도형(Shape) 등 화면에 그려지는 그래픽 리소스
              ├── value     # 문자열(String), 색상(Color), 테마(Style) 등 앱의 공통 상수 값을 관리
```

<br/>
<br/>

# 7. Development Workflow (개발 워크플로우)
## 브랜치 전략 (Branch Strategy)
우리의 브랜치 전략은 Git Flow를 기반으로 하며, 다음과 같은 브랜치를 사용합니다.

- master Branch
  - 배포 가능한 상태의 코드를 유지합니다.
  - 모든 배포는 이 브랜치에서 이루어집니다.
  
- {name} Branch
  - 팀원 각자의 개발 브랜치입니다.
  - 모든 기능 개발은 이 브랜치에서 이루어집니다.

<br/>
<br/>

# 8. Coding Convention
## 문장 종료
```
// 세미콜론(;)
```

<br/>



## 태그 네이밍
Styled-component태그 생성 시 아래 네이밍 규칙을 준수하여 의미 전달을 명확하게 한다.<br/>
태그명이 길어지더라도 의미 전달의 명확성에 목적을 두어 작성한다.<br/>

```

<br/>


```

<br/>

## 커밋 이모지
```
== 코드 관련
📝	코드 작성
🔥	코드 제거
🔨	코드 리팩토링
💄	UI / style 변경

== 문서&파일
📰	새 파일 생성
🔥	파일 제거
📚	문서 작성

== 버그
🐛	버그 리포트
🚑	버그를 고칠 때

== 기타
🐎	성능 향상
✨	새로운 기능 구현
💡	새로운 아이디어
🚀	배포
```

<br/>

## 커밋 예시
```
== ex1
✨Feat: "회원 가입 기능 구현"

SMS, 이메일 중복확인 API 개발

== ex2
📚chore: styled-components 라이브러리 설치

UI개발을 위한 라이브러리 styled-components 설치
```

<br/>
<br/>

# 10. 컨벤션 수행 결과
