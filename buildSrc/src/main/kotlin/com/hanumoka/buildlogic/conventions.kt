package com.hanumoka.buildlogic

// ★ 패키지 이름이 `buildlogic` 인 이유 — `build` 로 두면 소스가 Git 에 안 올라간다.
//   .gitignore 의 `build/` 는 앵커가 없어서 **경로 어디에 있든** 그 이름의 디렉터리를 다 무시한다.
//   com/hanumoka/build/ 도 거기 걸린다. 커밋은 조용히 되고 클론한 사람의 빌드만 깨진다.

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * 모든 모듈이 공유하는 바닥. 좌표 · 툴체인 · 저장소 · 테스트 러너 · 층 의존 규칙 검사.
 *
 * 관례 플러그인 셋(`hanumoka.kotlin-base` · `hanumoka.spring-library` · `hanumoka.spring-app`)이
 * 각자 자기 plugins 블록을 갖고 본문에서 이 함수를 부른다.
 * 플러그인이 아니라 함수로 둔 이유는 하나다 — 플러그인 적용 순서와 무관하게 부를 수 있다.
 */
fun Project.applyHanumokaConventions() {
    group = "com.hanumoka"
    version = "0.0.1-SNAPSHOT"

    repositories.mavenCentral()

    extensions.configure<KotlinJvmProjectExtension>("kotlin") {
        // ★ 「25 이상」이 아니라 정확히 25 를 찾는다. 없으면 settings.gradle.kts 의
        //   foojay 리졸버가 받아 온다 — 그 플러그인이 없으면 여기서 빌드가 선다.
        jvmToolchain(TOOLCHAIN_JAVA_VERSION)
        compilerOptions {
            // 생성자 파라미터에 붙인 애노테이션의 기본 대상.
            // 검증 애노테이션(@field:NotBlank 같은 것)을 쓰기 시작하면 이것이 값을 한다.
            freeCompilerArgs.add("-Xannotation-default-target=param-property")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    registerLayerDependencyCheck()
}

private const val TOOLCHAIN_JAVA_VERSION = 25

// ---------------------------------------------------------------------------
// 층 의존 규칙 — 문서가 아니라 빌드가 지킨다
//
// 규칙의 정본은 zm-docs 의 contracts.md 이고 이 파일은 그 집행이다.
// 여기서 보는 것은 모듈이 선언한 project(...) 의존뿐이다.
// 패키지 단위 위반은 app 모듈의 LayerDependencyTest(ArchUnit)가 따로 본다.
// ---------------------------------------------------------------------------

/**
 * ★ 0단계가 일부러 남기는 위반이다.
 *
 * service 끼리 직접 부르지 않는다는 규칙 1 을 어긴 채로 시작한다 —
 * 비교할 기준선을 먼저 만드는 것이 목적이기 때문이다.
 * 1단계에서 이벤트로 바꾸면 이 항목과 service-order 의 그 의존이 **함께** 사라진다.
 * 그 커밋의 diff 가 「경계를 설계했다」의 증거가 된다.
 */
private val DELIBERATE_EXCEPTIONS: Map<String, Set<String>> = mapOf(
    ":service:service-order" to setOf(":service:service-inventory"),
)

/**
 * 어느 층이 어느 층을 알아도 되는가.
 *
 * - `app` 은 모두를 알고 아무도 `app` 을 모른다 (규칙 5) — 어떤 값에도 "app" 이 없는 이유다.
 * - `service` 끼리는 모른다 (규칙 1) — "service" 의 값에 "service" 가 없는 이유다.
 * - `platform` 은 도메인을 모른다 (규칙 4) — "service" 도 "contract" 도 없는 이유다.
 */
private val ALLOWED_EDGES: Map<String, Set<String>> = mapOf(
    "app" to setOf("service", "platform", "contract"),
    "service" to setOf("platform", "contract"),
    "platform" to setOf("platform"),
    "contract" to emptySet(),
)

/** 모듈 경로에서 층을 읽는다. 층은 디렉터리가 정한다. */
private fun layerOf(path: String): String = when {
    path.startsWith(":app:") -> "app"
    path.startsWith(":service:") -> "service"
    path.startsWith(":platform:") -> "platform"
    path.startsWith(":contract") -> "contract"
    else -> "unknown"
}

private val DECLARING_CONFIGURATIONS = setOf("api", "implementation", "compileOnly", "runtimeOnly")

private fun Project.registerLayerDependencyCheck() {
    val task = tasks.register("checkLayerDependencies") {
        group = "verification"
        description = "이 모듈의 project 의존이 층 의존 규칙을 지키는지 검사한다."

        // 설정 시점에 값으로 뽑아 둔다. doLast 안에서 Project 를 잡으면 설정 캐시가 깨진다.
        val modulePath = this@registerLayerDependencyCheck.path
        val moduleLayer = layerOf(modulePath)
        val allowed = ALLOWED_EDGES[moduleLayer].orEmpty()
        val exempted = DELIBERATE_EXCEPTIONS[modulePath].orEmpty()
        val declared = configurations
            .filter { it.name in DECLARING_CONFIGURATIONS }
            .flatMap { it.dependencies }
            .filterIsInstance<ProjectDependency>()
            .map { it.path }
            .toSortedSet()

        doLast {
            val violations = declared.filter { target ->
                target !in exempted && layerOf(target) !in allowed
            }
            if (violations.isEmpty()) return@doLast

            val lines = violations.joinToString("\n") { target ->
                "  $modulePath ($moduleLayer)  ->  $target (${layerOf(target)})"
            }
            throw GradleException(
                "층 의존 규칙 위반:\n$lines\n\n" +
                    "'$moduleLayer' 층이 알아도 되는 층: ${allowed.sorted()}\n" +
                    "일부러 어기는 것이라면 buildSrc 의 DELIBERATE_EXCEPTIONS 에 이유와 함께 등록한다.",
            )
        }
    }

    tasks.named("check") { dependsOn(task) }
}
