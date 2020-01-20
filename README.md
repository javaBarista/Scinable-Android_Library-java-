# Scinable-Android_Library (java)
> Notification service 제공 및 분석용 라이브러리  

***  
Adnroid application을 위한 푸시 알림 제공 라이브러리, FCM을 이용한 토큰값 생성 후 비동기로 서버DB로 올린다.  
분석기능 추가, 해당기능을 회사의 웹분석용 javaScript code를 안드로이드에서 대응 가능하도록 java로 재 작성한 기능

<img src="https://user-images.githubusercontent.com/48575996/72703849-1d40a680-3b9a-11ea-9927-58d1bb2c6b64.png" width="30%"></img>

## Library 추가 방법

1. Android Studio 프로젝트 생성 (본인의 프로젝트가 있다면 해당과정 생략)  
2. 상단 메뉴바에 file -> project Structure 클릭  
3. 좌측 메뉴의 Modules 클릭 후 '+' 을 눌러 Import .jar/.aar Package 클릭  
4. 제공된 ".aar" 추가후 우측 하단의 apply 클릭  
5. Dependencies 메뉴로 이동 <All Modules> 하단의 app이란 이름의 파일 클릭  
6. Declared Dependencies 하단의 '+' 클릭 후 "3. Module Dependency" 클릭  
7. 푸시라이브러리 체크 후 'OK', 메인창 'applay' 클릭 후 'OK'    

## FCM 설정

1. Firebase Console 접속 -> [링크](https://firebase.google.com/?hl=ko)  
2. 시작하기 -> 프로젝트 추가 -> 원하는 프로젝트명 작성  
3. 앱 추가 Platfrom android 클릭 후 이동
4. 본인의 프로젝트명 입력 ex) com.example.push_Example
5. 제공되는 json 파일을 설명대로 본인의 프로젝트에 추가

## 어플 환경 설정

#### 프로젝트 단위의 build.gradle에서 dependencies에 추가사항  

```sh
classpath 'com.google.gms:google-services:4.2.0'
```

#### 앱 단위의 build.gradle에서 dependencies에 추가사항  

```sh
    implementation 'com.google.firebase:firebase-core:16.0.1'
    implementation 'com.google.firebase:firebase-messaging:17.3.4'
    implementation 'com.squareup.okhttp3:okhttp:3.2.0'
```

#### Kotlin
```sh
val pushclass = FCMRequest(applicationContext.packageName, this::class.java.simpleName, R.drawable.caulogo2, this)
```  

#### java
```sh
FCMRequest puchclass = new FCMRequest(getApplicationContext().getPackageName(), getClass().getSimpleName(), myLogo, this);
``` 

## 업데이트 내역
* 0.3.1
    * 오류 : 앱 강제종료후 재 실행시 번들정보에서 NullException 오류 발생
    * 수정 : bundle의 null값 여부 확인 후 정보를 가져오도록 조건문 추가
* 0.3.0
    * Analytics 기능 추가
    * 수정 사항 : 푸시 수신시 php에서 전송하는 광고와 채널 정보를 함께 받아 bundle에 저장 후 유저 어플로 넘긴다.
* 0.2.2
    * 푸시함에 Floaring Action 버튼 추가 읽은 푸시는 제외하고 안읽은 푸시만 볼 수 있도록 구현
    * Tread로 리스트 업테이트를 넘겨 비동기로 실행하도록 구현
    * 리스트마다 체크박스를 추가하여 읽은 리스트는 체크되어 읽은것과 안읽은것을 구분하도록 구현
* 0.2.1
    * 유저로 부터 홍보용 링크를 푸시로 함께 보내어 푸시함에서 링크를 클릭시 WebView를 이용해 해당 URI로 이동
    * PutExtra를 이용해 Link URI를 다음 웹뷰로 넘긴다.
* 0.2.0
    * 푸시함 추가 Recycler View를 이용하여 리스트 생성
    * Dialog를 커스텀하여 푸시 리스트 클릭시 본문과 이미지르 띄운다.
* 0.1.5
    * 이미지 및 긴 텍스트를 수신할 수 있도록 코드수정 조건문을 통해 분기실행(이미지 여부와 텍스트 길이 여부에 따라)
    * 이미지의 경우 URI를 받아 bitmap형식으로 변경하여 이미지 receive
    * 오류 : kitkat버전에서 largeicon이 없을시 smallicon이 largeicon으로 대체되며 이미지 확대 및 깨지는 현상 발생
    * 수정 : smallicon으로 들어온 이미지를 resize하여 bitmap형식으로 largeicon으로 지정
* 0.1.4
    * 이미지 및 긴 텍스트를 수신할 수 있도록 코드수정 조건문을 통해 분기실행(이미지 여부와 텍스트 길이 여부에 따라)
    * 수정 : 이미지의 경우 URI를 받아 bitmap형식으로 변경하여 이미지 receive
* 0.1.3
    * api version(19 ~ 29)테스트 진행 : 26 Oreo를 기준으로 상위버전에서 오류 발생  
    * 수정 : 채널을 생성하여 해결
* 0.1.2
    * `sharedpreferences`를 사용하여 사용자로부터 푸시클릭시 실행할 액티비티이름을 입력받아 저장  
    * receive 기능에서 intent에 바인딩
* 0.1.1
    * 버그 수정: 이미 사용중이던 어플에 라이브러리 추가시 기존에 어플을 사용중이던 고객은 토큰값이 생성 안되는 문제점 발생
      => 토큰의 정보를 찾아 NULL을 가르킬시 토큰을 재생성 하도록 구현
* 0.1.0
    * 생성된 토큰값을 비동기로 서버DB에 올리도록 코드 추가
    * 백/ 포 그라운드에서 푸시를 받을 수 있도록 수정
* 0.0.1
    * 가장 기본의 푸시서비스를 위한 코드작성

## 구현 정보

#### Notification Service  

* PendingIntent를 이용하여 푸시 클릭시 원하는 유저의 액티비티를 바인딩하여 실행할 수 있도록 구현  
* 중요도를 IMPORTANCE_DEFAULT로 하여 사용자의 푸시 수신 설정에 맞게 진동, 알림음으로 나타나도록 구현  
* api 26 부터는 채널을 사용하기 때문에 채널 미삭제시 푸시알림 무한반복 오류가 발생하여 deleteNotificationChannel를 이용해 채널을 삭제  
* 푸시함은 클릭시 클릭했다는 이벤트를 보여주기 위하여 클릭시 색상을 회색으로 약 3초정도 변경(Thread를 통해 딜레이 구현)  
* AsyncTask를 사용하여 비동기로 푸시함 실행시 서버DB에서 내용들을 가져와 리스트 생성  
* JSON 타입의 정보를 GSON을 이용하여 틀을 만들어 놓은 Class에 매핑 후 ArrayList로 저장 후 리스트에 바인딩  

#### EC Anaytics  

* 변수를 전부 정의하지 않고 hashMap을 통해 사용자가 호출할 때 적절한 키값으로 해당 값들을 저장하도록 구현  
* setter를 전부 private로 작성하고 push 메소드를 따로 구현하여 사용자로부터 메소드명과 변수를 가변인자로 받아  
  스위치문을 통해 적절한 메소드로 분기하여 실행하도록 구현하였다.  
* 웹에서 사용하는 쿠키 대신 sharedpreferences를 사용하여 가공된 값들을 저장, 동시에 만료시간 또한 함께 저장하여  
  getter로 값 호출시 우선적으로 milliSecond 형식으로 함께 저장된 만료시간을 현재의 시간과 비교하여 만료여부 판단 후 값을 가져오도록 구현  
* 만들어진 모든 값들을 사용자가 입력한 URI에 get형식으로 바인딩하여 OKHTTP를 비동기로 구현하여 request 실행   

## 참고 사항  
Analytics 작성 : google Anaytic 참고하여 작성  
Push Service : 구글 예제를 통하여 코드작성 및 채널, 알림등 작성
