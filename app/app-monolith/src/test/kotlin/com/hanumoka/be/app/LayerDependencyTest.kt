package com.hanumoka.be.app

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

/**
 * 층 의존 규칙의 **패키지 단위** 검사.
 *
 * 빌드 쪽 검사(`checkLayerDependencies`)와 역할이 다르다.
 * 그쪽은 「모듈이 모듈을 의존하는가」를 보고, 이쪽은 「클래스가 클래스를 참조하는가」를 본다.
 *
 * ★ 그래서 둘 다 필요하다. 같은 모듈 안에서 패키지를 잘못 쓰면 —
 *   예를 들어 주문 도메인 클래스를 `app` 모듈에 만들면 — 빌드 쪽 검사는 아무 말도 못 한다.
 *
 * ★ `app` 모듈에 두는 이유: 모든 도메인을 classpath 에 갖고 있는 유일한 모듈이라
 *   전체 그래프를 볼 수 있는 자리가 여기뿐이다.
 *
 * ★ `allowEmptyShould(true)` 가 붙어 있는 이유: 0단계에는 도메인 클래스가 아직 없다.
 *   ArchUnit 은 검사할 클래스가 하나도 없으면 기본적으로 실패한다 —
 *   「규칙이 오타 때문에 아무것도 못 잡고 있는 것」과 구별할 방법이 없기 때문이다.
 *   도메인이 생기면 이 인자를 지운다. 그때부터는 비어 있는 것이 곧 오타의 신호다.
 */
class LayerDependencyTest {

    private val classes = ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackages(ROOT)

    @Test
    fun `nobody knows the app layer`() {
        noClasses().that().resideOutsideOfPackage("$ROOT.app..")
            .should().dependOnClassesThat().resideInAPackage("$ROOT.app..")
            .because("도메인이 조립 지점을 알면 그 모듈은 이 프로세스 전용이 되어 다른 프로세스에 못 들어간다 (계약 규칙 5)")
            .allowEmptyShould(true)
            .check(classes)
    }

    @Test
    fun `inventory does not know order`() {
        noClasses().that().resideInAPackage("$ROOT.inventory..")
            .should().dependOnClassesThat().resideInAPackage("$ROOT.order..")
            .because("service 끼리 서로의 클래스를 직접 부르지 않는다 (계약 규칙 1)")
            .allowEmptyShould(true)
            .check(classes)
    }

    /**
     * ★ 반대 방향(주문 → 재고)은 **일부러 열어 둔 위반이라 검사하지 않는다.**
     *
     * 0단계는 비교할 기준선을 만드는 자리이므로 직접 호출로 시작한다.
     * 1단계에서 이벤트로 바꾸면 그 의존이 사라지고, **그때 이 자리에 대칭 규칙을 추가한다.**
     * 지금 넣으면 초록불을 유지할 수 없어서 검사 전체를 끄게 된다 —
     * 그것이 이 장치가 죽는 가장 흔한 경로다.
     */
    private companion object {
        const val ROOT = "com.hanumoka.be"
    }
}
