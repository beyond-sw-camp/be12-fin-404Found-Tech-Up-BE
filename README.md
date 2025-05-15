

<br>
![Tech Up 프로젝트 이미지]()
<!-- <br>
<div align="center">
<img src="https://github.com/user-attachments/assets/3f6df515-9e11-4b63-b4e1-c35d22176721" style="width: 60%;">
</div> -->

<br>

## 🎁팀원 소개
> **[한화시스템 BEYOND SW캠프 12기] Final Project** 

> **404Fount팀 Tech-Up 프로젝트**

<table align="center">
 <tr>
    <td align="center"><a href="https://github.com/dyun23"><img src="https://github.com/user-attachments/assets/99536e16-063c-4785-92d2-44b909ab7c84" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/sue06004"><img src="https://github.com/user-attachments/assets/2dde57cc-aa14-410c-99f7-9d50841fde51" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/jimnyy"><img src="https://github.com/user-attachments/assets/5ff5cb30-f7aa-4ab2-a18d-a31087ab9f80" width="150px;" alt=""></td>
    <td align="center"><a href="https://github.com/dlrkdms125"><img src="https://github.com/user-attachments/assets/42bed0f6-7d78-4eec-8906-e843495fca4f" width="150px;" alt=""></td>
  </tr>
  <tr>
    <td align="center">🐥<a href="https://github.com/dyun23"><b>김정엽</b></td>
    <td align="center">🦊<a href="https://github.com/sue06004"><b>김무성</b></td>
    <td align="center">😼<a href="https://github.com/jimnyy"><b>오건하</b></td>
    <td align="center">🐰<a href="https://github.com/dlrkdms125"><b>황경윤</b></td>
  </tr>
  </table>
<br>


### 목차
- [🛠 기술 스택](#-기술-스택)
- [📚 Tech-Up 데모 사이트 링크](#-Tech-Up-데모-사이트-바로가기)
- [🎨 Tech-Up 서비스 소개](#-Tech-Up-서비스-소개)
- [🏗️ 시스템 아키텍처](#-시스템-아키텍처)
- [📚 프론트엔드 프로젝트 목표](#-프론트엔드-프로젝트-목표)
- [📈 프로젝트 설계](#-프로젝트-설계)
- [🚀 핵심 로직 상세 설명](#-핵심-로직-상세-설명)
- [📂 프로젝트 폴더 바로가기](#-프로젝트-폴더-바로가기)
<br><br>

## 🛠 기술 스택

#### &nbsp;　[ Frontend ]
&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white" style="border-radius: 5px;"/>
<img src="https://img.shields.io/badge/css3-1572B6?style=for-the-badge&logo=css3&logoColor=white" style="border-radius: 5px;"/>
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white" style="border-radius: 5px;"/>
![Vue.js](https://img.shields.io/badge/vuejs-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D)
<img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white" style="border-radius: 5px;"/>
<img src="https://img.shields.io/badge/pinia-gold?style=for-the-badge&logo=Pinia&logoColor=white" style="border-radius: 5px;"/>
![Nuxt.js]()

#### &nbsp;　[ CI/CD ]
&nbsp;&nbsp;&nbsp;&nbsp;![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white" style="border-radius: 5px;">

#### &nbsp;　[ Communication ]
&nbsp;&nbsp;&nbsp;&nbsp;<img src="https://img.shields.io/badge/discord-326CE5?style=for-the-badge&logo=discord&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white" style="border-radius: 5px;">
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white" style="border-radius: 5px;">
![JIRA]()
![Confluence]()
<br><br><br>


## 📚 Tech-Up 데모 사이트 바로가기
### [공개 버전 링크](https://techup.kro.kr)</a>
<br><br>
## 🎨 Tech-Up 서비스 소개
![프로젝트 이미지]()

#### 프로젝트 배경
 - 사용자 맞춤 부품 추천 쇼핑몰 <br>
 - 고객이 제공한 제품 정보 및 구매 기록을 바탕으로 연관된 상품을 알 수 있다.<br>
   고사양을 요하지 않는 일반 사용자들은 기기 전체를 바꾸는 것보다 필요한 부분만 간단히 업그레이드하길 원한다. 하지만 부품의 호환성, 업그레이드 범위, 관련된 추가 제품을 파악하는 일은 일반 사용자에게 쉽지 않다.<br>
   “Tech-Up”은 그런 사용자들을 위한 가벼운 업그레이드 추천 중심의 서비스이다.
<br>

- `연관성 높은 제품 추천` : 사용자가 등록하거나 사이트에서 구매한 자신의 제품에 관한 데이터를 분석하여 연관 제품을 제안하고, 사용자의 현재 제품 사양에 기반하여 간단한 업그레이드(예: RAM 용량 증가, SSD 용량 확장, CPU 코어 수 증가 등) 중심으로 추천하는 데 중점을 둔다. 단, 제품 간 호환성 자동 판단은 제공하지 않으며 사용자가 스스로 확인하도록 안내한다.<br>
- `즉각적인 추천 제품 구매` : 제품 간 비교 페이지에서 추천받은 제품은 즉시 장바구니에 담아 주문할 수 있다.<br>
- `커뮤니티 형성` : 회원 간의 부품 호환성에 관한 지식 공유를 통해 협력 문화를 조성한다.
<br>



#### 세부 기능
  - **`WIKI`**<br>
    유저들이 프로그래밍 언어별 정보, 최신 기술에 관한 지식을 자유롭게 공유하고, 이를 체계적으로 관리할 수 있는 서비스를 제공한다.
  - **`QnA`**<br>
    개발자들이 직면한 문제와 에러를 질문하고, 답변을 얻을 수 있는 서비스를 제공한다.
  - **`ErrorArchive`**<br>
    단순히 문제를 해결하는 것에 그치지 않고, 해결 과정을 블로그 형태로 정리하고, 공유한다.
  - **`채팅`**<br>
    게시글의 작성자에게 추가적인 질문사항이 있을 떄, 실시간 소통이 가능한 1:1채팅 서비스를 제공한다.
 - **`포인트 및 랭킹`**<br>
    서비스 사용을 유도하기 위해 특정 조건에 따라 포인트를 부여하고, 활동을 통해 본인의 등급 및 랭킹을 제공한다.    
<br>

## 🏗️ 시스템 아키텍처
![백엔드 시스템 아키텍처 이미지]()<br><br>


## 📚 백엔드 프로젝트 목표
- `API 설계 및 개발` : 클라이언트(프론트엔드)에서 호출할 수 있는 REST/GraphQL API 제공
- `비즈니스 로직 구현` : 사용자 요청을 처리하고 결과 반환
- `DB 연동 및 관리` : 데이터베이스 모델 설계, CRUD 기능 구현
- `보안 및 인증` : 로그인, 권한 관리, JWT/OAuth 등 인증 로직
- `성능 최적화` : 안정적인 서버 운영을 위한 로직 구성
- `예외 처리` : 안정적인 서버 운영을 위한 로직 구성
<br>

## 📈 프로젝트 설계

### [1. 기획서 바로가기]()

### [2. 요구사항 정의서]()

### [3. WBS]()

### [4. API 명세서 바로가기]()


<br><br>

<details>
<summary>회원가입</summary>

![회원가입 페이지지]()
> 이메일, 비밀번호, 닉네임로 회원 가입을 한다.  
> 이메일, 닉네임은 **중복이 불가능**하며, 비밀번호는 8자 이상의 영문 및 숫자여야 한다.
> 계정 생성 스패밍을 막기 위해 이메일 인증을 해야 가입할 수 있다.
</details>

<details>
<summary>로그인</summary>

![image]()
> 일반 로그인, 소셜(Kakao) 로그인

![로그인 페이지]()

> 소셜 로그인은 소셜로 로그인 한 후 로그인한 사용자 계정의 이름 및 이메일을 받아온다.
> 소셜 로그인 사용자는 소셜 로그인으로만 로그인할 수 있다.
</details>

<details>
<summary>검색</summary>

### 제품 검색
![통합검색]()
> 헤더에 있는 검색창으로 제품 검색이 가능하다.  
> 메인 페이지의 검색창은 카테고리별 제품 검색이 가능하다.
> 검색 결과는 5가지 옵션으로 정렬하며, 페이지네이션을 통해 다음 제품 목록을 받아온다.
---
### 제품 목록 탐색
![제품 목록 페이지]()
> 헤더의 **제품** 링크로 들어간 페이지에서 부품 카테고리(CPU, 그래픽 카드, RAM, SSD, 하드디스크) 및 가격대 별로 판매 중인 상품을 조회할 수 있다.
> 제품 상세보기에서는 제품에 대한 설명과 제품에 대한 리뷰를 볼 수 있다.
</details>

<summary>제품 추천</summary>

### 사용자가 소유한 제품 정보의 등록
![제품 등록 및 내 기기 정보 페이지 이미지]()

> 사용자는 마이페이지 내에서 사이드바의 제품 정보 등록 페이지 링크를 눌러 내 제품 등록 페이지로 이동할 수 있다.
> 자신의 부품 이름을 검색하여 찾은 부품을 내 부품으로 등록할 수 있다.
### 제공한 부품 정보를 바탕으로 제품 추천
![비교 페이지]()

> 페이지 상단의 양방향 화살표 로고를 누르면 제품 간 비교 페이지에 진입한다.
> 사용자가 소유한 제품, 사용자가 위시리스트에 담은 제품과 추천 알고리즘에 의해 선정된 연관 부품을 5가지 카테고리(CPU, 그래픽 카드, RAM, SSD, 하드디스크)로 구분하여 볼 수 있다.
</details>

<details>
<summary>제품 주문 및 환불</summary>

### 장바구니
![장바구니 캡쳐]()
> 장바구니에 구매하려는 여러 제품을 모아 한 번에 주문할 수 있다.
---
### 주문 및 결제
![주문 결제 과정 이미지 예시]()

> 주문 내역을 보고 결제를 할 수 있다.
> 주문에 사용 가능한 쿠폰을 받았다면 적용하여 할인을 받을 수 있다.
> 카카오페이가 지원된다.
---
### 주문 취소 및 환불
![사용자의 환불 신청 이미지]()

> 사용자는 주문 상세 페이지에서 환불 요청을 쉽게 할 수 있다.

![관리자의 환불 허가 과정 이미지]()

> 관리자는 환불이 요청된 제품에 한해 환불 처리를 할 수 있다.
</details>

<details>
<summary>관리자</summary>

관리자만이 들어갈 수 있는 페이지는 `/dashboard` URI를 통해 진입할 수 있으며, 익명 사용자 및 일반 사용자는 진입할 수 없다.

### 제품 등록과 삭제
![제품 등록/삭제 이미지]()

> ...
---
### 쿠폰과 선착순 쿠폰 발급 이벤트
![쿠폰 및 이벤트 관리 페이지]()

> ...
---
### 주문 관리
![사용자 목록과 주문 목록 페이지]()

> ...
</details>

<details>
<summary>커뮤니티</summary>

### 게시글 목록
![커뮤니티 페이지지]()

> ...
### 게시글 작성/수정/삭제
![에디터]()

> ...
</details>

## 🚀 핵심 로직 상세 설명
### [📃 프로젝트 Wiki](https://github.com/beyond-sw-camp/be12-fin-404Found-Tech-Up-BE/wiki) <br><br>


## 📂 백엔드, DevOps 프로젝트 폴더 바로가기
### [📃 Backend](https://github.com/beyond-sw-camp/be12-fin-404Found-Tech-Up-FE) <br>
### [📃 Devops]()
<br>
