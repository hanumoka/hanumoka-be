import org.springframework.boot.gradle.plugin.SpringBootPlugin

/**
 * `service-*` · `platform-*` 가 쓰는 관례. **라이브러리 모듈이다.**
 *
 * ★ Boot 플러그인을 일부러 적용하지 않는다. 적용하면 `main` 을 요구하고,
 *   `main` 을 가지면 그 모듈이 프로세스가 되어 계약 규칙 5 를 깬다.
 */
plugins {
    id("hanumoka.kotlin-base")
    id("org.jetbrains.kotlin.plugin.spring")
    id("io.spring.dependency-management")
}

/**
 * ★ Boot 플러그인이 없으면 BOM 이 자동으로 안 들어온다 — 그것이 위 선택의 대가다.
 *   직접 가져오지 않으면 모든 의존성에 버전을 손으로 적어야 한다.
 *
 *   이 상수를 쓰면 buildSrc 가 선언한 Boot 플러그인 버전을 그대로 읽는다. 버전을 두 번 적지 않는다.
 */
dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
