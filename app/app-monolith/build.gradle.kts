// 0단계의 유일한 프로세스. main · WAS · 환경 설정만 갖고 업무 규칙은 한 줄도 없다 (DEC-0034).
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.hanumoka"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(25)
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    // ★ 이 목록이 「이 프로세스에 무엇이 들어가는가」의 정본이다.
    //   프로세스를 가를 때 바뀌는 것은 여기와 scanBasePackages 뿐이고 도메인 코드는 안 바뀐다.
    implementation(project(":service:service-order"))
    implementation(project(":service:service-inventory"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // ★ Boot 4 는 Jackson 3 이다. 그룹이 com.fasterxml.jackson.module 이 아니라 tools.jackson.module 이다.
    //   ★ 이것이 없어도 스모크 컨트롤러의 POST/PUT 은 돈다 (2026-09-07 실측).
    //   Boot Gradle 플러그인이 Kotlin 컴파일에 javaParameters 를 켜서 생성자 파라미터 이름이 남고,
    //   Jackson 3 는 그것만으로 data class 를 만든다. 이 모듈이 사는 자리는 Kotlin 의 기본값이다 —
    //   근거를 JacksonKotlinModuleTest 가 갖는다. 그 테스트를 지우면 이 줄도 근거를 잃는다.
    implementation("tools.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // ★ Boot 4 에서 @AutoConfigureMockMvc 가 spring-boot-webmvc-test 모듈로 이사했다.
    //   starter-test 에는 없다. 패키지도 org.springframework.boot.webmvc.test.autoconfigure 로 바뀌었다.
    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
