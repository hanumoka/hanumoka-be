plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// ★ 저장소 전체에서 플러그인 버전이 적히는 유일한 곳이다.
//   루트 build.gradle.kts 의 plugins 블록을 없앤 대신 이 자리가 정본이 됐다.
//   여기 말고 버전이 적히는 자리가 생기면 그때가 gradle/libs.versions.toml 을 들일 때다.
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    // ★ kotlin.plugin.spring 은 kotlin-gradle-plugin 이 아니라 kotlin-allopen 에 들어 있다.
    //   플러그인 id 와 아티팩트 이름이 안 겹치는 자리라 관례 플러그인을 쓸 때 처음 걸린다.
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.3.21")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:4.1.0")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.7")
}
