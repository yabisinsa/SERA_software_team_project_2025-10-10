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
| 김형진    |  <img src="" alt="김형진" width="100"> | <ul><li>전체 프로젝트 계획 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>서버 개발</li></ul>     |
| 박기훈    |  <img src="" alt="박기훈" width="100"> | <ul><li>어플리케이션 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 앱 개발</li></ul>     |
| 손영주    |  <img src="" alt="손영주" width="100"> | <ul><li>어플리케이션 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 앱 개발</li></ul>     |
| 안의진    |  <img src="" alt="안의진" width="100"> | <ul><li>API서버 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>서버 개발</li></ul>     |
| 윤시훈    |  <img src="" alt="윤시훈" width="100"> | <ul><li>API서버 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>서버 개발</li></ul>     |
| 임민하    |  <img src="" alt="임민하" width="100"> | <ul><li>어플리케이션 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 앱 개발</li></ul>     |
| 장나정    |  <img src="" alt="장나정" width="100"> | <ul><li>어플리케이션 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>커스텀 앱 개발</li></ul>     |
| 지훈    |  <img src="" alt="지훈" width="100"> | <ul><li>API서버 개발 및 관리</li><li>팀 리딩 및 커뮤니케이션</li><li>서버 개발</li></ul>     |
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
## 6.1 Sever Structure (서버 구조)
```
SERA_software_team_project_2025-10-10-1/
│   ├── feature_extractor.py/     # 음성값 뽑아오기 파일
│   ├── emotion_model.pkl/        # 음성값으로 감정 분류 모델(sera모델)
│   ├── gender_predictor.py/      #성별 감지 파일 
│   ├── gender_predictor.pkl/     # 성별 감지 모델
│   ├── requirements.txt/         # 필수 라이브러리 목록 파일
│   ├── Dockerfile/               # 서버 환경 구축 설명서 파일
│   ├── .dockerignore/            # 불필요한 파일 제외 파일
├── .gitignore                    # Git 무시 파일 목록
└── README.md                     # 프로젝트 개요 및 사용법
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
✨날짜 시간 팀원 홍길동: "메시지"

SMS, 이메일 중복확인 API 개발

== ex2
📚chore: styled-components 라이브러리 설치

UI개발을 위한 라이브러리 styled-components 설치
```

<br/>
<br/>

# 10. 컨벤션 수행 결과
