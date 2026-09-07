package com.hanumoka.be.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 0단계의 유일한 프로세스.
 *
 * ★ scanBasePackages 를 명시하는 이유
 * 모듈 경계는 빌드 시점에만 있고, 컴포넌트 스캔은 패키지를 본다.
 * 기본값은 이 클래스의 패키지(com.hanumoka.be.app) 아래만 훑으므로 도메인 빈이 하나도 안 잡힌다.
 *
 * 상위 패키지(com.hanumoka.be)로 올려서 전부 훑게 할 수도 있지만 그러면
 * 「app 이 어떤 도메인을 이 프로세스에 넣을지 고른다」가 사라진다 — classpath 에 있는 것이 전부 들어온다.
 * 그래서 고르는 행위를 목록으로 남긴다. 이 목록과 build.gradle.kts 의 project(...) 목록이 짝이다.
 */
@SpringBootApplication(
    scanBasePackages = [
        "com.hanumoka.be.app",
        "com.hanumoka.be.order",
        "com.hanumoka.be.inventory",
    ],
)
class MonolithApplication

fun main(args: Array<String>) {
    runApplication<MonolithApplication>(*args)
}
