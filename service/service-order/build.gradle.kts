// 주문 도메인. 라이브러리 모듈이므로 Boot 플러그인도 main 도 없다.
plugins {
    id("hanumoka.spring-library")
}

dependencies {
    // ★ 0단계가 일부러 남기는 위반이다 (계약 규칙 1).
    //   buildSrc 의 deliberateExceptions 에 등록돼 있어 checkLayerDependencies 를 통과한다.
    //   1단계에서 이벤트로 바꾸면 이 줄과 그 예외 항목이 함께 사라진다.
    implementation(project(":service:service-inventory"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
}
