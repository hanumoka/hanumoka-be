package com.hanumoka.be.app

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

/**
 * 0단계에서 이 테스트가 재는 것은 하나다 — 이 프로세스가 뜨는가.
 *
 * ★ 도메인 모듈에는 이 테스트를 두지 않는다. 부팅 클래스가 그 모듈에 없기 때문이고,
 *   있게 만들면 도메인이 조립 지점을 알게 되어 계약 규칙 5를 깬다.
 *   도메인 모듈은 스프링 없는 순수 단위 테스트만 갖는다.
 */
@SpringBootTest
class MonolithApplicationTests {

    @Test
    fun contextLoads() {
    }
}
