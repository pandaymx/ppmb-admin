plugins {
    id("buildlogic.java-application-conventions")
    id("buildlogic.java-database-conventions")
    id("buildlogic.spring-cloud-conventions")
    alias(libs.plugins.spring.boot)
}

val libs = the<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation(project(":ppmb-common-api"))
    implementation(project(":ppmb-common-web"))

    implementation(libs.findLibrary("spring-boot-starter-web").get())

    runtimeOnly("com.h2database:h2")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
