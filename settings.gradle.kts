// ★ 툴체인 다운로드 저장소를 등록한다.
//
// 이것이 없으면 JDK 25 가 설치돼 있지 않은 기계에서 빌드가 서지 않는다 —
//   "Toolchain download repositories have not been configured."
//
// IntelliJ 의 Auto-download 스위치는 「금지하지 않았다」는 뜻일 뿐이고,
// **어디서 받을지**는 이 플러그인이 정한다. 허가와 주소는 다른 설정이다.
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "hanumoka-be"

include(
    ":app:app-monolith",
    ":service:service-order",
    ":service:service-inventory",
)
