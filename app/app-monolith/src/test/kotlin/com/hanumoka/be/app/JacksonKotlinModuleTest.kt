package com.hanumoka.be.app

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import tools.jackson.databind.ObjectMapper

/**
 * tools.jackson.module:jackson-module-kotlin 이 왜 의존성에 있는지를 재는 테스트다.
 *
 * ★ 이 테스트가 없으면 그 의존성은 「없어도 되는데 습관으로 넣은 것」과 구분되지 않는다.
 *   실제로 스모크 컨트롤러의 POST/PUT 은 이 모듈 없이도 돈다 —
 *   Boot Gradle 플러그인이 Kotlin 컴파일에 javaParameters 를 켜 주므로
 *   생성자 파라미터 이름이 바이트코드에 남고 Jackson 3 가 그것만으로 객체를 만든다.
 *
 *   차이는 Kotlin 의 기본값에서 난다. 모듈이 없으면 Jackson 은 Kotlin 의 기본값 생성자를
 *   모르므로 그 자리에 null 을 넣으려 하고 이렇게 실패한다 (2026-09-07 실측):
 *
 *     MismatchedInputException: Cannot map `null` into type `int`
 *
 *   즉 기본값이 무시된다. 조용히 틀리는 것이 아니라 예외로 뜨지만,
 *   기본값을 쓸 생각으로 만든 DTO 는 그 순간 전부 못 쓰게 된다.
 */
@SpringBootTest
class JacksonKotlinModuleTest @Autowired constructor(
    private val objectMapper: ObjectMapper,
) {

    data class OrderLine(val name: String, val quantity: Int = 1)

    @Test
    fun `a missing field falls back to the Kotlin default instead of a silent zero`() {
        val decoded = objectMapper.readValue("""{"name":"keyboard"}""", OrderLine::class.java)

        assertThat(decoded.quantity).isEqualTo(1)
    }
}
