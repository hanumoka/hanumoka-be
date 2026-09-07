import com.hanumoka.buildlogic.applyHanumokaConventions

/**
 * 스프링을 모르는 순수 Kotlin 모듈이 쓰는 관례.
 * `contract/` 의 이벤트 스키마처럼 프레임워크가 필요 없는 것이 여기 해당한다.
 *
 * 나머지 관례 플러그인 둘도 이것을 물려받는다.
 */
plugins {
    id("org.jetbrains.kotlin.jvm")
}

applyHanumokaConventions()
