package com.hanumoka.be.app.smoke

import com.jayway.jsonpath.JsonPath
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

/**
 * 스모크 컨트롤러가 재는 것은 업무 규칙이 아니라 배관이다 —
 * 프로세스가 뜨고, WAS 가 요청을 받고, JSON 이 양방향으로 오가는가.
 *
 * ★ POST/PUT 이 여기서 특히 중요하다. 컴파일이 통과해도 JSON 이 못 들어오면 여기서만 드러난다.
 *   빌드는 초록불인 채로 깨질 수 있으므로 테스트가 없으면 실행해 볼 때까지 모른다.
 *   Jackson 3 와 Kotlin 의 관계는 JacksonKotlinModuleTest 가 따로 재고 있다.
 *
 * ★ 이 컨트롤러는 싱글턴 빈 안에 ConcurrentHashMap 으로 상태를 들고 있다.
 *   그래서 테스트끼리 상태를 공유한다 — 「전체 개수가 3이다」 같은 단정은 실행 순서에 따라 깨진다.
 *   각 테스트가 자기 항목을 만들고 그 항목만 확인한다. 도메인이 DB 로 가면
 *   트랜잭션 롤백으로 격리되므로 이 제약은 스모크 단계에서만 참는 것이다.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SmokeControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
) {

    @Test
    fun `POST creates an item and answers 201 with its id`() {
        val id = create("keyboard")

        mockMvc.get("$PATH/$id")
            .andExpect {
                status { isOk() }
                jsonPath("\$.id") { value(id.toInt()) }
                jsonPath("\$.name") { value("keyboard") }
            }
    }

    @Test
    fun `GET on a missing id answers 404`() {
        mockMvc.get("$PATH/999999")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `GET on the collection contains the created item`() {
        val id = create("monitor")

        val body = mockMvc.get(PATH)
            .andExpect { status { isOk() } }
            .andReturn().response.contentAsString

        // JsonPath 필터식(\$[?(@.id == n)])은 Spring 의 jsonPath 단정과 섞으면 결과 타입이 헷갈린다.
        // 목록을 꺼내 Kotlin 에서 확인하는 편이 무엇을 재는지 분명하다.
        val ids: List<Int> = JsonPath.read(body, "\$[*].id")
        assertThat(ids).contains(id.toInt())
    }

    @Test
    fun `PUT replaces the name and answers 200`() {
        val id = create("mouse")

        mockMvc.put("$PATH/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name":"trackball"}"""
        }.andExpect {
            status { isOk() }
            jsonPath("\$.name") { value("trackball") }
        }

        mockMvc.get("$PATH/$id")
            .andExpect { jsonPath("\$.name") { value("trackball") } }
    }

    @Test
    fun `PUT on a missing id answers 404`() {
        mockMvc.put("$PATH/999999") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name":"ghost"}"""
        }.andExpect { status { isNotFound() } }
    }

    @Test
    fun `DELETE answers 204 and the item is gone afterwards`() {
        val id = create("cable")

        mockMvc.delete("$PATH/$id")
            .andExpect { status { isNoContent() } }

        mockMvc.get("$PATH/$id")
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `POST without a name is rejected with 400`() {
        mockMvc.post(PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }.andExpect { status { isBadRequest() } }
    }

    /** POST 로 하나 만들고 그 id 를 돌려준다. 각 테스트가 자기 항목만 보게 하는 장치다. */
    private fun create(name: String): Long {
        val body = mockMvc.post(PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name":"$name"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("\$.name") { value(name) }
        }.andReturn().response.contentAsString

        return JsonPath.read<Int>(body, "\$.id").toLong()
    }

    private companion object {
        const val PATH = "/smoke/items"
    }
}
