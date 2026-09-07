// 재고 도메인. 라이브러리 모듈이므로 Boot 플러그인도 main 도 없다.
plugins {
    id("hanumoka.spring-library")
}

dependencies {
    // 0단계의 재고에는 HTTP 표면이 없다. 주문이 직접 부른다. 그래서 webmvc 가 아니라 starter 다.
    implementation("org.springframework.boot:spring-boot-starter")
}
