import org.springframework.boot.gradle.plugin.SpringBootPlugin

// 주문 도메인. 라이브러리 모듈이므로 Boot 플러그인과 main 이 없다 (DEC-0034 규칙 5).
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
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

// Boot 플러그인이 없으면 BOM 이 자동으로 들어오지 않는다. 직접 가져와야 버전을 안 적는다.
dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    // ★ 0단계가 일부러 남기는 위반이다 (계약 규칙 1). 1단계에서 이 한 줄이 contract 의존으로 바뀐다.
    implementation(project(":service:service-inventory"))

    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
