plugins {
    id("buildlogic.spring-cloud-conventions")
    id("buildlogic.native-image-conventions")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(project(":ppmb-common:ppmb-common-web"))

    implementation(libs.spring.cloud.starter.gateway.server.webmvc)

    developmentOnly(libs.spring.boot.devtools)
}
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
